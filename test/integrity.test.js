import assert from 'node:assert/strict';
import { test } from 'node:test';
import { openDatabase } from '../src/database.js';
import { integrityReport } from '../src/integrity.js';

test('integrity audit returns all seven checks for clean databases, including synthetic seed', () => {
  for (const seed of [false, true]) {
    const db = openDatabase({ file: ':memory:', seed });
    try {
      const report = integrityReport(db);
      assert.equal(report.checked, 7);
      assert.equal(report.checks.length, 7);
      assert.equal(new Set(report.checks.map(({ code }) => code)).size, 7);
      assert.equal(report.total, 0);
      assert.ok(report.checks.every(({ count, examples }) => count === 0 && examples.length === 0));
    } finally { db.close(); }
  }
});

test('integrity audit finds direct-SQL violations, full counts and capped examples without writing', () => {
  const db = openDatabase({ file: ':memory:', seed: false });
  try {
    db.exec(`
      INSERT INTO wards VALUES (1, 'Small', '1', 'General', 1);
      INSERT INTO wards VALUES (2, 'Other', '2', 'General', 10);
      INSERT INTO patients VALUES (1, 'A', 'One', '2000-01-01', 'F', '0');
      INSERT INTO patients VALUES (2, 'B', 'Two', '2000-01-01', 'F', '0');
      INSERT INTO patients VALUES (3, 'C', 'Three', '2000-01-01', 'F', '0');
      INSERT INTO patients VALUES (4, 'D', 'Four', '2000-01-01', 'F', '0');
      INSERT INTO physicians VALUES (1, 'Dr', 'One', 'General', '0');
      INSERT INTO nurses VALUES (1, 'N', 'One', '0');
      INSERT INTO illnesses VALUES (1, 'Condition', 'General', '');

      INSERT INTO admissions VALUES (1, 1, 2, '2026-01-01', '');
      INSERT INTO admissions VALUES (2, 1, 2, '2026-01-03', '');
      INSERT INTO admissions VALUES (3, 1, 2, '2026-01-05', '');
      INSERT INTO admissions VALUES (4, 1, 2, '2026-01-07', '');
      INSERT INTO admissions VALUES (5, 1, 2, '2026-01-09', '');
      INSERT INTO admissions VALUES (6, 2, 1, '2026-02-01', '');
      INSERT INTO admissions VALUES (7, 3, 1, '2026-02-01', '');
      INSERT INTO admissions VALUES (8, 4, 2, '2026-02-01', '');
      INSERT INTO discharges VALUES (1, 1, '2026-01-15', 'Recovered', '');
      INSERT INTO discharges VALUES (2, 2, '2026-01-04', 'Recovered', '');
      INSERT INTO discharges VALUES (3, 3, '2026-01-06', 'Recovered', '');
      INSERT INTO discharges VALUES (4, 4, '2026-01-08', 'Recovered', '');
      INSERT INTO discharges VALUES (5, 5, '2026-01-10', 'Recovered', '');
      INSERT INTO discharges VALUES (8, 8, '2026-01-31', 'Recovered', '');

      INSERT INTO diagnoses VALUES (1, 1, 1, 1, '2026-01-01', '');
      INSERT INTO treatments VALUES (1, 2, 1, 1, NULL, NULL, '2026-01-02', 'Procedure', '');
      INSERT INTO nurse_assignments VALUES (1, 1, 1, '2026-02-12', '2026-02-11');
      INSERT INTO physician_schedules VALUES (1, 1, 'Monday', '17:00', '09:00');
      INSERT INTO nurse_shifts VALUES (1, 1, 'Tuesday', '08:00', '08:00');
    `);

    const before = db.prepare('SELECT total_changes() AS count').get().count;
    const report = integrityReport(db);
    const after = db.prepare('SELECT total_changes() AS count').get().count;
    assert.equal(after, before, 'diagnostics must be read-only');
    assert.equal(report.checked, 7);
    assert.equal(report.total, 10);
    const byCode = Object.fromEntries(report.checks.map((check) => [check.code, check]));
    assert.equal(byCode.admission_overlap.count, 4);
    assert.deepEqual(byCode.admission_overlap.examples.map(({ id }) => id), [2, 3, 4]);
    assert.match(byCode.admission_overlap.examples[0].detail, /patient #1/);
    for (const code of [
      'ward_over_capacity', 'discharge_chronology', 'treatment_patient',
      'assignment_chronology', 'physician_hours', 'nurse_hours',
    ]) {
      assert.equal(byCode[code].count, 1, code);
      assert.equal(byCode[code].examples.length, 1, code);
      assert.ok(byCode[code].examples[0].detail.length > 12, code);
    }
    assert.equal(byCode.ward_over_capacity.examples[0].id, 1);
    assert.equal(byCode.discharge_chronology.examples[0].id, 8);
  } finally { db.close(); }
});

test('admissions starting on an earlier discharge date are flagged as overlapping', () => {
  const db = openDatabase({ file: ':memory:', seed: false });
  try {
    db.exec(`
      INSERT INTO wards VALUES (1, 'A', '1', 'General', 4);
      INSERT INTO patients VALUES (1, 'A', 'One', '2000-01-01', 'F', '0');
      INSERT INTO admissions VALUES (1, 1, 1, '2026-01-01', '');
      INSERT INTO admissions VALUES (2, 1, 1, '2026-01-03', '');
      INSERT INTO discharges VALUES (1, 1, '2026-01-03', 'Recovered', '');
      INSERT INTO discharges VALUES (2, 2, '2026-01-05', 'Recovered', '');
    `);
    const overlap = integrityReport(db).checks.find(({ code }) => code === 'admission_overlap');
    assert.equal(overlap.count, 1);
    assert.equal(overlap.examples[0].id, 2);
  } finally { db.close(); }
});
