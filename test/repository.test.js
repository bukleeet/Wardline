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

test('searches bounded relation choices by label or exact ID beyond the initial page', () => {
  const insert = db.prepare(`INSERT INTO patients
    (first_name, last_name, date_of_birth, sex, contact_no) VALUES (?, 'Example', '1998-05-12', 'F', '100200')`);
  for (let index = 0; index < 125; index += 1) insert.run(`Aaron${String(index).padStart(3, '0')}`);
  const older = createRecord(db, 'patients', person('ZuluArchive'));
  const initial = lookup(db, 'patients');
  assert.equal(initial.length, 100);
  assert.ok(!initial.some((entry) => entry.id === older.id));
  assert.deepEqual(lookup(db, 'patients', { search: `#${older.id}` }).map((row) => ({ ...row })), [
    { id: older.id, label: 'ZuluArchive Example' },
  ]);
  assert.deepEqual(lookup(db, 'patients', { search: 'zuluARCHIVE' }).map((row) => ({ ...row })), [
    { id: older.id, label: 'ZuluArchive Example' },
  ]);
  assert.equal(lookup(db, 'patients', { search: 'Aaron' }).length, 100);
  assert.deepEqual(lookup(db, 'patients', { search: "' OR 1=1 --" }), []);
  assert.deepEqual(lookup(db, 'patients', { search: 'NoSuchPatient' }), []);

  for (const relation of ['admissions', 'diagnoses']) {
    const entry = lookup(db, relation)[0];
    assert.deepEqual(lookup(db, relation, { search: `#${entry.id}` }), [entry]);
    assert.ok(lookup(db, relation, { search: entry.label.split(' · ')[1].toLowerCase() })
      .some((result) => result.id === entry.id));
  }
  assert.throws(() => lookup(db, 'patients', { search: 'x'.repeat(151) }), /Search is too long/);
  assert.throws(() => lookup(db, 'patients', { search: 5 }), /Search is too long/);
  assert.throws(() => lookup(db, 'patients', { search: '#999999999999999999' }), /Invalid record ID/);
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

test('rejects capacity reductions and ward transfers that would overbook beds', () => {
  const ward = createRecord(db, 'wards', { name: 'Capacity test', floor: '1', category: 'General', capacity: 2 });
  const fullWard = createRecord(db, 'wards', { name: 'Full ward', floor: '2', category: 'General', capacity: 1 });
  const a = createRecord(db, 'patients', person('CapacityA'));
  const b = createRecord(db, 'patients', person('CapacityB'));
  const c = createRecord(db, 'patients', person('CapacityC'));
  const first = createRecord(db, 'admissions', { patient_id: a.id, ward_id: ward.id, admitted_on: '2026-01-01', reason: null });
  createRecord(db, 'admissions', { patient_id: b.id, ward_id: ward.id, admitted_on: '2026-01-02', reason: null });
  createRecord(db, 'admissions', { patient_id: c.id, ward_id: fullWard.id, admitted_on: '2026-01-02', reason: null });

  assert.throws(() => updateRecord(db, 'wards', ward.id, { capacity: 1 }), { status: 409 });
  assert.throws(() => updateRecord(db, 'admissions', first.id, { ward_id: fullWard.id }), { status: 409 });
  assert.equal(getRecord(db, 'wards', ward.id).capacity, 2);
  assert.equal(getRecord(db, 'admissions', first.id).ward_id, ward.id);
  assert.equal(dashboard(db).wards.find((item) => item.id === ward.id).occupied, 2);
});

test('preserves discharge chronology and only reopens admissions when safe', () => {
  const ward = createRecord(db, 'wards', { name: 'Reopening test', floor: '1', category: 'General', capacity: 2 });
  const personRecord = createRecord(db, 'patients', person('Reopening'));
  const admission = createRecord(db, 'admissions', {
    patient_id: personRecord.id, ward_id: ward.id, admitted_on: '2026-04-01', reason: null,
  });
  const discharge = createRecord(db, 'discharges', {
    admission_id: admission.id, discharged_on: '2026-04-03', disposition: 'Recovered', notes: null,
  });

  assert.throws(() => updateRecord(db, 'admissions', admission.id, { admitted_on: '2026-04-04' }), /after discharge/);
  assert.throws(() => updateRecord(db, 'discharges', discharge.id, { discharged_on: '2026-03-31' }), /precede admission/);
  assert.equal(getRecord(db, 'admissions', admission.id).admitted_on, '2026-04-01');
  assert.equal(getRecord(db, 'discharges', discharge.id).discharged_on, '2026-04-03');

  const second = createRecord(db, 'admissions', {
    patient_id: personRecord.id, ward_id: ward.id, admitted_on: '2026-04-05', reason: null,
  });
  assert.throws(() => deleteRecord(db, 'discharges', discharge.id), /admitted elsewhere/);
  assert.ok(getRecord(db, 'discharges', discharge.id));

  deleteRecord(db, 'admissions', second.id);
  const activeBefore = dashboard(db).metrics.activeAdmissions;
  deleteRecord(db, 'discharges', discharge.id);
  assert.equal(dashboard(db).metrics.activeAdmissions, activeBefore + 1);
  assert.throws(() => getRecord(db, 'discharges', discharge.id), { status: 404 });
});

test('prevents overlapping historical patient admissions throughout record changes', () => {
  const ward = createRecord(db, 'wards', { name: 'History ward', floor: '1', category: 'General', capacity: 3 });
  const patient = createRecord(db, 'patients', person('History'));
  const otherPatient = createRecord(db, 'patients', person('OtherHistory'));
  const admit = (personId, admittedOn) => createRecord(db, 'admissions', {
    patient_id: personId, ward_id: ward.id, admitted_on: admittedOn, reason: null,
  });
  const discharge = (admissionId, dischargedOn) => createRecord(db, 'discharges', {
    admission_id: admissionId, discharged_on: dischargedOn, disposition: 'Recovered', notes: null,
  });

  const first = admit(patient.id, '2026-01-01');
  const firstDischarge = discharge(first.id, '2026-01-05');
  const second = admit(patient.id, '2026-01-10');
  const secondDischarge = discharge(second.id, '2026-01-12');

  assert.throws(() => admit(patient.id, '2026-01-04'), /overlap/);
  assert.throws(() => admit(patient.id, '2026-01-11'), /overlap/);
  assert.throws(() => updateRecord(db, 'admissions', second.id, { admitted_on: '2026-01-05' }), /overlap/);
  assert.throws(() => updateRecord(db, 'discharges', firstDischarge.id, { discharged_on: '2026-01-10' }), /overlap/);
  assert.throws(() => deleteRecord(db, 'discharges', firstDischarge.id), /overlap/);
  assert.equal(getRecord(db, 'discharges', firstDischarge.id).discharged_on, '2026-01-05');
  assert.equal(getRecord(db, 'admissions', second.id).admitted_on, '2026-01-10');
  assert.equal(listRecords(db, 'admissions', { search: 'History' }).total, 2);

  // Nonoverlapping dates and different patients remain valid.
  const third = admit(patient.id, '2026-01-13');
  discharge(third.id, '2026-01-14');
  const sameDatesOtherPatient = admit(otherPatient.id, '2026-01-10');
  discharge(sameDatesOtherPatient.id, '2026-01-12');
  assert.equal(getRecord(db, 'discharges', secondDischarge.id).discharged_on, '2026-01-12');
  assert.equal(db.prepare('PRAGMA foreign_key_check').all().length, 0);
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
  assert.throws(() => updateRecord(db, 'treatments', linkedTreatment.id, { patient_id: otherPatient.id }), /Diagnosis must belong/);
  assert.equal(getRecord(db, 'treatments', linkedTreatment.id).patient_id, diagnosis.patient_id);
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
