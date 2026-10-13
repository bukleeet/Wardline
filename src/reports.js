import { AppError } from './repository.js';

function validDate(value) {
  if (!/^\d{4}-\d{2}-\d{2}$/.test(value ?? '')) return false;
  const date = new Date(`${value}T00:00:00Z`);
  return !Number.isNaN(date.getTime()) && date.toISOString().slice(0, 10) === value;
}

export function operationsReport(db, { from, to } = {}) {
  if (!validDate(from) || !validDate(to) || from > to) {
    throw new AppError(400, 'Provide valid from and to dates (YYYY-MM-DD) in chronological order');
  }
  const days = Math.round((Date.parse(`${to}T00:00:00Z`) - Date.parse(`${from}T00:00:00Z`)) / 86400000) + 1;
  if (days > 366) throw new AppError(400, 'Report range must not exceed 366 days');
  const params = { from, to };
  // Bed days count both admission and discharge dates. Preaggregate each
  // relation separately to prevent fanout when reporting physician workload.
  const wards = db.prepare(`
    WITH intervals AS (
      SELECT a.ward_id, a.admitted_on AS started, COALESCE(d.discharged_on, :to) AS ended
      FROM admissions a LEFT JOIN discharges d ON d.admission_id = a.id
      WHERE a.admitted_on <= :to AND (d.discharged_on IS NULL OR d.discharged_on >= :from)
    ), occupied AS (
      SELECT ward_id, SUM(julianday(MIN(ended, :to)) - julianday(MAX(started, :from)) + 1) AS bed_days,
             COUNT(*) AS admissions_touching_window
      FROM intervals GROUP BY ward_id
    )
    SELECT w.id, w.name, w.category, w.capacity,
           COALESCE(o.bed_days, 0) AS occupied_bed_days,
           w.capacity * :days AS available_bed_days,
           ROUND(100.0 * COALESCE(o.bed_days, 0) / (w.capacity * :days), 1) AS utilization_pct,
           COALESCE(o.admissions_touching_window, 0) AS admissions_touching_window,
           DENSE_RANK() OVER (ORDER BY COALESCE(o.bed_days, 0) * 1.0 / w.capacity DESC) AS utilization_rank
    FROM wards w LEFT JOIN occupied o ON o.ward_id = w.id
    ORDER BY utilization_pct DESC, w.name
  `).all({ ...params, days });
  const daily = db.prepare(`
    WITH RECURSIVE days(day) AS (
      SELECT :from UNION ALL SELECT date(day, '+1 day') FROM days WHERE day < :to
    ), admission_counts AS (
      SELECT admitted_on AS day, COUNT(*) AS count FROM admissions
      WHERE admitted_on BETWEEN :from AND :to GROUP BY admitted_on
    ), discharge_counts AS (
      SELECT discharged_on AS day, COUNT(*) AS count FROM discharges
      WHERE discharged_on BETWEEN :from AND :to GROUP BY discharged_on
    ), opening_census AS (
      SELECT COUNT(*) AS occupied FROM admissions a
      LEFT JOIN discharges d ON d.admission_id = a.id
      WHERE a.admitted_on < :from AND (d.discharged_on IS NULL OR d.discharged_on >= :from)
    ), events AS (
      SELECT days.day, COALESCE(ac.count, 0) AS admissions,
             COALESCE(dc.count, 0) AS discharges
      FROM days LEFT JOIN admission_counts ac ON ac.day = days.day
      LEFT JOIN discharge_counts dc ON dc.day = days.day
    ), shifted AS (
      SELECT *, LAG(discharges, 1, 0) OVER (ORDER BY day) AS previous_discharges
      FROM events
    )
    SELECT day, admissions, discharges,
           (SELECT occupied FROM opening_census) +
             SUM(admissions - previous_discharges) OVER (ORDER BY day ROWS UNBOUNDED PRECEDING) AS census,
           SUM(admissions - discharges) OVER (ORDER BY day ROWS UNBOUNDED PRECEDING) AS net_flow
    FROM shifted ORDER BY day
  `).all(params);
  const physicians = db.prepare(`
    WITH diagnosis_counts AS (
      SELECT physician_id, COUNT(*) AS diagnoses FROM diagnoses
      WHERE diagnosed_on BETWEEN :from AND :to GROUP BY physician_id
    ), treatment_counts AS (
      SELECT physician_id, COUNT(*) AS treatments, COUNT(DISTINCT patient_id) AS treated_patients
      FROM treatments WHERE performed_on BETWEEN :from AND :to AND physician_id IS NOT NULL
      GROUP BY physician_id
    )
    SELECT p.id, p.first_name || ' ' || p.last_name AS name, p.specialty,
           COALESCE(d.diagnoses, 0) AS diagnoses,
           COALESCE(t.treatments, 0) AS treatments,
           COALESCE(t.treated_patients, 0) AS treated_patients,
           RANK() OVER (ORDER BY COALESCE(d.diagnoses, 0) + COALESCE(t.treatments, 0) DESC) AS workload_rank
    FROM physicians p LEFT JOIN diagnosis_counts d ON d.physician_id = p.id
    LEFT JOIN treatment_counts t ON t.physician_id = p.id
    ORDER BY workload_rank, p.last_name, p.first_name
  `).all(params);
  const discharges = db.prepare(`
    SELECT COUNT(*) AS count,
           ROUND(AVG(julianday(d.discharged_on) - julianday(a.admitted_on) + 1), 1) AS avg_stay_days
    FROM discharges d JOIN admissions a ON a.id = d.admission_id
    WHERE d.discharged_on BETWEEN :from AND :to
  `).get(params);
  return { from, to, days, wards, daily, physicians, summary: {
    admissions: daily.reduce((sum, row) => sum + row.admissions, 0),
    discharges: discharges.count,
    avgStayDays: discharges.avg_stay_days,
    occupiedBedDays: wards.reduce((sum, ward) => sum + ward.occupied_bed_days, 0),
    availableBedDays: wards.reduce((sum, ward) => sum + ward.available_bed_days, 0),
  } };
}
