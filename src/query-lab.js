import { PHYSICIAN_WORKLOAD_SQL, RECORDED_RETURNS_SQL, reportWindow } from './reports.js';

// These are fixed, read-only statements. Never accept SQL from the browser.
const EXAMPLES = Object.freeze([
  {
    id: 'patient-timeline',
    title: 'Indexed patient timeline',
    technique: 'Composite timeline index',
    explanation: 'The (patient_id, admitted_on, id) index supports patient filtering and chronological reads without an extra sort. Fetching ward_id still requires a table lookup.',
    sql: `SELECT id, ward_id, admitted_on
FROM admissions
WHERE patient_id = :patient_id
ORDER BY admitted_on, id`,
    parameters: () => ({ patient_id: 1 }),
    context: 'Representative record lookup using a synthetic patient ID of 1.',
  },
  {
    id: 'physician-workload',
    title: 'Physician workload',
    technique: 'Pre-aggregation + RANK()',
    explanation: 'Diagnoses and treatments are grouped separately before joining physicians. This avoids multiplying counts through a many-to-many fanout.',
    sql: PHYSICIAN_WORKLOAD_SQL,
    parameters: ({ from, to }) => ({ from, to }),
    context: 'Exact SQL used by the workload table in this report.',
  },
  {
    id: 'recorded-returns',
    title: 'Recorded 30-day returns',
    technique: 'LEAD() over full patient history',
    explanation: 'The next visit is determined across the entire history before filtering discharges to the selected period, preserving returns that begin after the range ends.',
    sql: RECORDED_RETURNS_SQL,
    parameters: ({ from, to }) => ({ from, to }),
    context: 'Exact SQL used by the 30-day returns table in this report.',
  },
]);

/** Return actual SQLite planner output for curated query paths, without running the data queries. */
export function queryLab(db, range = {}) {
  const { from, to } = reportWindow(range);
  return {
    from, to,
    examples: EXAMPLES.map(({ id, title, technique, explanation, sql, parameters, context }) => {
      const bindings = parameters({ from, to });
      const plan = db.prepare(`EXPLAIN QUERY PLAN ${sql}`).all(bindings).map(({ id: node, parent, detail }) => ({
        id: node, parent, detail,
      }));
      return { id, title, technique, explanation, context, sql: sql.trim().replace(/^ {4}/gm, ''), bindings, plan };
    }),
  };
}
