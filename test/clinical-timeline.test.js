import assert from 'node:assert/strict';
import { test } from 'node:test';
import { openDatabase } from '../src/database.js';
import { queryLab } from '../src/query-lab.js';

test('the curated clinical timeline unifies indexed events and exposes bounded query results', () => {
  const db = openDatabase({ file: ':memory:' });
  try {
    const before = db.prepare('SELECT total_changes() AS n').get().n;
    const lab = queryLab(db, { from: '2026-09-01', to: '2026-09-30' });
    const timeline = lab.examples.find(({ id }) => id === 'patient-timeline');
    const details = timeline.plan.map(({ detail }) => detail).join(' ');
    for (const index of ['idx_admissions_patient_date_id', 'idx_diagnoses_patient_date',
      'idx_treatments_patient_date']) assert.ok(details.includes(index), details);
    assert.match(timeline.sql, /UNION ALL/);
    assert.deepEqual(timeline.preview.columns, ['event_date', 'event_type', 'event_id', 'description']);
    assert.equal(timeline.preview.rows.length, 5);
    assert.deepEqual(timeline.preview.rows,
      [...timeline.preview.rows].sort((a, b) =>
        b.event_date.localeCompare(a.event_date) ||
        a.event_type.localeCompare(b.event_type) ||
        b.event_id - a.event_id));
    assert.ok(new Set(timeline.preview.rows.map(({ event_type }) => event_type)).size > 1);
    for (const example of lab.examples) {
      assert.equal(example.preview.limit, 5);
      assert.ok(example.preview.rows.length <= 5);
      assert.ok(example.preview.columns.length > 0);
      for (const row of example.preview.rows) assert.deepEqual(Object.keys(row), example.preview.columns);
    }
    assert.equal(db.prepare('SELECT total_changes() AS n').get().n, before);
  } finally {
    db.close();
  }
});

test('the timeline handles empty clinical history and empty report windows', () => {
  const db = openDatabase({ file: ':memory:', seed: false });
  try {
    const lab = queryLab(db, { from: '2026-09-01', to: '2026-09-30' });
    assert.deepEqual(lab.examples[0].preview.rows, []);
    assert.ok(lab.examples.every(({ preview }) => preview.rows.length <= 5));
  } finally {
    db.close();
  }
});

test('clinical timeline retains optional clinicians, same-day events, and patient isolation', () => {
  const db = openDatabase({ file: ':memory:', seed: false });
  try {
    db.exec(`
      INSERT INTO patients (id, first_name, last_name, date_of_birth, sex, contact_no)
      VALUES (1, 'First', 'Patient', '1999-01-01', 'F', '0'),
             (2, 'Second', 'Patient', '1999-01-01', 'M', '0');
      INSERT INTO wards (id, name, floor, category, capacity)
      VALUES (1, 'Ward', '1', 'General', 10);
      INSERT INTO physicians (id, first_name, last_name, specialty, contact_no)
      VALUES (1, 'Alex', 'Smith', 'General', '0');
      INSERT INTO illnesses (id, name, category)
      VALUES (1, 'Condition', 'General');
      INSERT INTO admissions (id, patient_id, ward_id, admitted_on, reason)
      VALUES (1, 1, 1, '2026-09-10', 'Observation');
      INSERT INTO diagnoses (id, patient_id, physician_id, illness_id, diagnosed_on)
      VALUES (1, 1, 1, 1, '2026-09-10'), (2, 1, 1, 1, '2026-09-10');
      INSERT INTO treatments (id, patient_id, performed_on, procedure_name)
      VALUES (1, 1, '2026-09-10', 'Care'), (2, 2, '2026-09-11', 'Other');
    `);
    const previous = db.prepare('SELECT total_changes() AS count').get().count;
    const timeline = queryLab(db, { from: '2026-09-01', to: '2026-09-30' }).examples[0];
    assert.deepEqual(timeline.preview.rows.map((row) => ({ ...row })), [
      { event_date: '2026-09-10', event_type: 'Admission', event_id: 1,
        description: 'Ward · Observation' },
      { event_date: '2026-09-10', event_type: 'Diagnosis', event_id: 2,
        description: 'Condition · Dr. Smith' },
      { event_date: '2026-09-10', event_type: 'Diagnosis', event_id: 1,
        description: 'Condition · Dr. Smith' },
      { event_date: '2026-09-10', event_type: 'Treatment', event_id: 1,
        description: 'Care / Unassigned clinician' },
    ]);
    assert.equal(db.prepare('SELECT total_changes() AS count').get().count, previous);
  } finally {
    db.close();
  }
});
