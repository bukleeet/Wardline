import assert from 'node:assert/strict';
import { afterEach, beforeEach, test } from 'node:test';
import { openDatabase } from '../src/database.js';
import { catalog } from '../src/catalog.js';
import { createRecord, dashboard, deleteRecord, getRecord, listRecords, lookup, updateRecord } from '../src/repository.js';

let db;
beforeEach(() => { db = openDatabase({ file: ':memory:' }); });
afterEach(() => db.close());

const person = (first_name = 'Jamie') => ({ first_name, last_name: 'Example', date_of_birth: '1998-05-12', sex: 'F', contact_no: '+63 900 123 4567' });

test('seeds a complete, internally consistent synthetic data model', () => {
  assert.equal(Object.keys(catalog).length, 13);
  assert.equal(lookup(db, 'patients').length, 32);
  assert.equal(lookup(db, 'physicians').length, 10);
  assert.equal(lookup(db, 'wards').length, 8);
  assert.match(lookup(db, 'diagnoses')[0].label, /^Diagnosis #/);
  assert.ok(listRecords(db, 'treatments').rows.length > 0);
  assert.equal(db.prepare('PRAGMA foreign_key_check').all().length, 0);
  assert.equal(db.prepare(`SELECT COUNT(*) AS n FROM discharges d JOIN admissions a ON a.id = d.admission_id WHERE d.discharged_on < a.admitted_on`).get().n, 0);
  assert.equal(dashboard(db).metrics.activeAdmissions, 16);
});

test('supports create, search, read, update, and delete with pagination', () => {
  const created = createRecord(db, 'patients', person('UniquePatient'));
  assert.equal(created.first_name, 'UniquePatient');
  assert.equal(getRecord(db, 'patients', created.id).last_name, 'Example');
  assert.equal(listRecords(db, 'patients', { search: 'uniquepatient' }).total, 1);
  assert.equal(listRecords(db, 'patients', { page: 2, pageSize: 10 }).rows.length, 10);
  assert.equal(updateRecord(db, 'patients', created.id, { contact_no: '555-0199' }).contact_no, '555-0199');
  assert.deepEqual(deleteRecord(db, 'patients', created.id), { deleted: true, id: created.id });
  assert.throws(() => getRecord(db, 'patients', created.id), { status: 404 });
});

test('rejects invalid or malicious inputs before reaching SQL', () => {
  assert.throws(() => listRecords(db, 'patients; DROP TABLE patients'), { status: 404 });
  assert.throws(() => lookup(db, 'missing_table'), { status: 404 });
  assert.throws(() => createRecord(db, 'patients', { ...person(), first_name: '<x>', unauthorized: 1 }), /Unknown field/);
  assert.throws(() => createRecord(db, 'patients', { ...person(), date_of_birth: '2025-02-30' }), /valid date/);
  assert.throws(() => createRecord(db, 'patients', { ...person(), contact_no: '' }), /required/);
  assert.throws(() => listRecords(db, 'patients', { page: 0 }), /positive integer/);
  assert.throws(() => listRecords(db, 'patients', { pageSize: 101 }), /between 1 and 100/);
  assert.throws(() => updateRecord(db, 'patients', 1, { id: 999 }), /Unknown field/);
  assert.equal(listRecords(db, 'patients').total, 32);
});

test('preserves relational integrity and prevents deleting referenced records', () => {
  assert.throws(() => createRecord(db, 'admissions', { patient_id: 999999, ward_id: 1, admitted_on: '2026-01-01', reason: '' }), { status: 409 });
  assert.throws(() => deleteRecord(db, 'patients', 1), { status: 409 });
});

test('enforces one active admission per patient and ward capacity', () => {
  const ward = createRecord(db, 'wards', { name: 'Test ward', floor: '1', category: 'General', capacity: 1 });
  const first = createRecord(db, 'patients', person('First'));
  const second = createRecord(db, 'patients', person('Second'));
  const admission = createRecord(db, 'admissions', { patient_id: first.id, ward_id: ward.id, admitted_on: '2026-01-08', reason: 'Observation' });
  assert.throws(() => createRecord(db, 'admissions', { patient_id: first.id, ward_id: 1, admitted_on: '2026-01-09', reason: null }), /already has an active admission/);
  assert.throws(() => createRecord(db, 'admissions', { patient_id: second.id, ward_id: ward.id, admitted_on: '2026-01-08', reason: null }), /at capacity/);
  assert.throws(() => updateRecord(db, 'wards', ward.id, { capacity: 0 }), /integer from 1/);
  assert.throws(() => createRecord(db, 'discharges', { admission_id: admission.id, discharged_on: '2026-01-07', disposition: 'Recovered', notes: null }), /precede admission/);
  const discharge = createRecord(db, 'discharges', { admission_id: admission.id, discharged_on: '2026-01-10', disposition: 'Recovered', notes: null });
  createRecord(db, 'admissions', { patient_id: second.id, ward_id: ward.id, admitted_on: '2026-01-11', reason: null });
  assert.throws(() => deleteRecord(db, 'discharges', discharge.id), /ward is full/);
});

test('prevents illogical clinical and scheduling changes', () => {
  assert.throws(() => createRecord(db, 'physician_schedules', { physician_id: 1, weekday: 'Sunday', starts_at: '17:00', ends_at: '08:00' }), /End time/);
  assert.throws(() => createRecord(db, 'nurse_assignments', { nurse_id: 1, patient_id: 1, assigned_on: '2026-04-20', ends_on: '2026-04-15' }), /Assignment end/);
  const diagnosis = getRecord(db, 'diagnoses', 1);
  assert.throws(() => createRecord(db, 'treatments', {
    patient_id: 2, diagnosis_id: diagnosis.id, physician_id: null, nurse_id: null,
    medicine_id: null, performed_on: '2026-04-20', procedure_name: 'Checkup', notes: null,
  }), /Diagnosis must belong/);
  assert.throws(() => createRecord(db, 'physician_schedules', { physician_id: 1, weekday: 'Monday', starts_at: '08:00', ends_at: '16:00' }), { status: 409 });
});

test('preserves linked treatment patients and discharge ownership', () => {
  const diagnosis = getRecord(db, 'diagnoses', 1);
  const otherPatient = createRecord(db, 'patients', person('Unrelated'));
  const linkedTreatment = createRecord(db, 'treatments', {
    patient_id: diagnosis.patient_id, diagnosis_id: diagnosis.id, physician_id: null,
    nurse_id: null, medicine_id: null, performed_on: '2026-04-20',
    procedure_name: 'Review', notes: null,
  });
  assert.equal(linkedTreatment.diagnosis_id_label, `Diagnosis #${diagnosis.id}`);
  assert.throws(() => updateRecord(db, 'diagnoses', diagnosis.id, { patient_id: otherPatient.id }), { status: 409 });
  assert.equal(getRecord(db, 'diagnoses', diagnosis.id).patient_id, diagnosis.patient_id);

  const [first, second] = db.prepare('SELECT id FROM discharges ORDER BY id LIMIT 2').all();
  const replacementAdmission = db.prepare('SELECT admission_id FROM discharges WHERE id = ?').get(second.id).admission_id;
  assert.throws(() => updateRecord(db, 'discharges', first.id, { admission_id: replacementAdmission }), /cannot be reassigned/);
});

test('aggregates charts, ward availability, and low-stock alerts', () => {
  const data = dashboard(db);
  assert.equal(data.wards.reduce((sum, ward) => sum + ward.occupied, 0), data.metrics.activeAdmissions);
  assert.equal(data.months.length, 6);
  assert.equal(data.recent.length, 7);
  assert.equal(data.metrics.lowStock, data.lowStock.length);
  assert.ok(data.topConditions.length > 0);
});
