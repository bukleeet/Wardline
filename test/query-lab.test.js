import assert from 'node:assert/strict';
import { test } from 'node:test';
import { openDatabase } from '../src/database.js';
import { queryLab } from '../src/query-lab.js';
import { operationsReport, PHYSICIAN_WORKLOAD_SQL, RECORDED_RETURNS_SQL } from '../src/reports.js';

test('query lab explains real report statements and a patient timeline index without writing', () => {
  const db = openDatabase({ file: ':memory:' });
  try {
    const range = { from: '2026-09-01', to: '2026-09-30' };
    const baseline = db.prepare('SELECT total_changes() AS count').get().count;
    const result = queryLab(db, range);
    const report = operationsReport(db, range);

    assert.deepEqual([result.from, result.to], [range.from, range.to]);
    assert.equal(result.examples.length, 3);
    assert.deepEqual(result.examples.map(({ id }) => id),
      ['patient-timeline', 'physician-workload', 'recorded-returns']);
    assert.deepEqual(result.examples[0].bindings, { patient_id: 1 });
    assert.match(result.examples[0].plan.map((node) => node.detail).join(' '),
      /USING INDEX idx_admissions_patient_date_id/);
    assert.equal(result.examples[1].sql, PHYSICIAN_WORKLOAD_SQL.trim().replace(/^ {4}/gm, ''));
    assert.equal(result.examples[2].sql, RECORDED_RETURNS_SQL.trim().replace(/^ {4}/gm, ''));
    for (const example of result.examples) {
      assert.ok(example.sql.startsWith('SELECT') || example.sql.startsWith('WITH'));
      assert.ok(example.plan.length > 0);
      assert.ok(example.plan.every(({ detail, id, parent }) =>
        typeof detail === 'string' && Number.isInteger(id) && Number.isInteger(parent)));
    }
    assert.ok(report.physicians.length > 0 && report.returns.length > 0);
    assert.equal(db.prepare('SELECT total_changes() AS count').get().count, baseline);
  } finally { db.close(); }
});

test('query lab validates date boundaries and rejects SQL-shaped input', () => {
  const db = openDatabase({ file: ':memory:', seed: false });
  try {
    for (const range of [
      { from: '2026-04-05', to: '2026-04-01' },
      { from: '2026-02-29', to: '2026-03-01' },
      { from: '2026-01-01', to: '2027-01-02' },
      { from: '2026-01-01\' OR TRUE --', to: '2026-01-02' },
    ]) assert.throws(() => queryLab(db, range), { status: 400 });
  } finally { db.close(); }
});
