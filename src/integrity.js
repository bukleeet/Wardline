// Read-only diagnostics for invariants that span multiple rows or tables.
// The application enforces these during normal writes; this audit can also
// expose inconsistencies imported or introduced directly through SQLite.
const rules = [
  ['admission_overlap', 'Overlapping patient stays'],
  ['ward_over_capacity', 'Active ward capacity exceeded'],
  ['discharge_chronology', 'Discharge before admission'],
  ['treatment_patient', 'Treatment and diagnosis patient mismatch'],
  ['assignment_chronology', 'Nurse assignment ends before start'],
  ['physician_hours', 'Invalid physician schedule hours'],
  ['nurse_hours', 'Invalid nurse shift hours'],
];

const integritySql = `
  WITH ordered_stays AS (
    SELECT a.id, a.patient_id, a.admitted_on,
      MAX(COALESCE(d.discharged_on, '9999-12-31')) OVER (
        PARTITION BY a.patient_id ORDER BY a.admitted_on, a.id
        ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING
      ) AS previous_end
    FROM admissions a
    LEFT JOIN discharges d ON d.admission_id = a.id
  ),
  active_ward_counts AS (
    SELECT w.id, w.capacity, COUNT(a.id) AS occupants
    FROM wards w
    LEFT JOIN admissions a ON a.ward_id = w.id
      AND NOT EXISTS (SELECT 1 FROM discharges d WHERE d.admission_id = a.id)
    GROUP BY w.id
  ),
  violations AS (
    SELECT 'admission_overlap' AS code, id AS record_id,
      'Admission #' || id || ' overlaps a prior stay for patient #' || patient_id AS detail
    FROM ordered_stays WHERE previous_end >= admitted_on

    UNION ALL
    SELECT 'ward_over_capacity', id,
      'Ward #' || id || ' has ' || occupants || ' active stays for ' || capacity || ' beds'
    FROM active_ward_counts WHERE occupants > capacity

    UNION ALL
    SELECT 'discharge_chronology', d.id,
      'Discharge #' || d.id || ' (' || d.discharged_on || ') precedes admission #' || a.id ||
      ' (' || a.admitted_on || ')'
    FROM discharges d JOIN admissions a ON a.id = d.admission_id
    WHERE d.discharged_on < a.admitted_on

    UNION ALL
    SELECT 'treatment_patient', t.id,
      'Treatment #' || t.id || ' patient #' || t.patient_id ||
      ' differs from diagnosis #' || d.id || ' patient #' || d.patient_id
    FROM treatments t JOIN diagnoses d ON d.id = t.diagnosis_id
    WHERE t.patient_id <> d.patient_id

    UNION ALL
    SELECT 'assignment_chronology', id,
      'Assignment #' || id || ' ends ' || ends_on || ' before ' || assigned_on
    FROM nurse_assignments WHERE ends_on IS NOT NULL AND ends_on < assigned_on

    UNION ALL
    SELECT 'physician_hours', id,
      'Schedule #' || id || ' starts at ' || starts_at || ' and ends at ' || ends_at
    FROM physician_schedules WHERE starts_at >= ends_at

    UNION ALL
    SELECT 'nurse_hours', id,
      'Shift #' || id || ' starts at ' || starts_at || ' and ends at ' || ends_at
    FROM nurse_shifts WHERE starts_at >= ends_at
  ),
  ranked AS (
    SELECT code, record_id, detail,
      COUNT(*) OVER (PARTITION BY code) AS violation_count,
      ROW_NUMBER() OVER (PARTITION BY code ORDER BY record_id) AS example_rank
    FROM violations
  )
  SELECT code, record_id, detail, violation_count
  FROM ranked WHERE example_rank <= 3
  ORDER BY code, record_id
`;

export function integrityReport(db) {
  const checks = rules.map(([code, label]) => ({ code, label, count: 0, examples: [] }));
  const byCode = new Map(checks.map((check) => [check.code, check]));

  for (const row of db.prepare(integritySql).all()) {
    const check = byCode.get(row.code);
    check.count = row.violation_count;
    check.examples.push({ id: row.record_id, detail: row.detail });
  }

  return {
    checked: checks.length,
    total: checks.reduce((sum, check) => sum + check.count, 0),
    checks,
  };
}
