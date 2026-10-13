import { PHYSICIAN_WORKLOAD_SQL, RECORDED_RETURNS_SQL, reportWindow } from './reports.js';

// These are fixed, read-only statements. Never accept SQL from the browser.
const EXAMPLES = Object.freeze([
  {
    id: 'patient-timeline',
    title: 'Longitudinal clinical timeline',
    technique: 'UNION ALL + indexed joins',
    explanation: 'Three independently indexed patient lookups combine admissions, diagnoses and treatments into a single chronological event stream. UNION ALL preserves multiple events on the same date; source IDs keep their order deterministic.',
    sql: `SELECT a.admitted_on AS event_date,
       'Admission' AS event_type,
       a.id AS event_id,
       w.name || ' · ' || COALESCE(a.reason, 'Admitted') AS description
FROM admissions AS a
JOIN wards AS w ON w.id = a.ward_id
WHERE a.patient_id = :patient_id
UNION ALL
SELECT d.diagnosed_on AS event_date,
       'Diagnosis' AS event_type,
       d.id AS event_id,
       i.name || ' · Dr. ' || p.last_name AS description
FROM diagnoses AS d
JOIN illnesses AS i ON i.id = d.illness_id
JOIN physicians AS p ON p.id = d.physician_id
WHERE d.patient_id = :patient_id
UNION ALL
SELECT t.performed_on AS event_date,
       'Treatment' AS event_type,
       t.id AS event_id,
       t.procedure_name || ' · Dr. ' || p.last_name AS description
FROM treatments AS t
JOIN physicians AS p ON p.id = t.physician_id
WHERE t.patient_id = :patient_id
ORDER BY event_date DESC, event_type, event_id DESC`,
    parameters: () => ({ patient_id: 1 }),
    context: 'Three clinical resources for synthetic patient #1; preview shows the five newest events.',
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

const PREVIEW_LIMIT = 5;

/** Explain and sample curated, parameterized, read-only SQL. Never accept browser-authored SQL. */
export function queryLab(db, range = {}) {
  const { from, to } = reportWindow(range);
  return {
    from, to,
    examples: EXAMPLES.map(({ id, title, technique, explanation, sql, parameters, context }) => {
      const bindings = parameters({ from, to });
      const plan = db.prepare(`EXPLAIN QUERY PLAN ${sql}`).all(bindings).map(({ id: node, parent, detail }) => ({
        id: node, parent, detail,
      }));
      // Limit the result inside SQLite; reports can span up to 366 calendar days.
      const statement = db.prepare(`SELECT * FROM (${sql}) AS result_preview LIMIT ${PREVIEW_LIMIT}`);
      const rows = statement.all(bindings);
      return {
        id, title, technique, explanation, context,
        sql: sql.trim().replace(/^ {4}/gm, ''), bindings, plan,
        preview: { limit: PREVIEW_LIMIT, columns: statement.columns().map(({ name }) => name), rows },
      };
    }),
  };
}
