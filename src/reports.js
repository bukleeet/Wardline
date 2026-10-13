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
  // An event-sourced daily census avoids expanding each admission into one
  // row per occupied date. Discharges decrement occupancy the following day
  // because hospital stays use inclusive calendar dates.
  const wardPressure = db.prepare(`
    WITH RECURSIVE calendar(day) AS (
      SELECT :from UNION ALL SELECT date(day, '+1 day') FROM calendar WHERE day < :to
    ), opening AS (
      SELECT a.ward_id, COUNT(*) AS occupied FROM admissions a
      LEFT JOIN discharges d ON d.admission_id = a.id
      WHERE a.admitted_on < :from AND (d.discharged_on IS NULL OR d.discharged_on >= :from)
      GROUP BY a.ward_id
    ), pulses AS (
      SELECT ward_id, admitted_on AS day, 1 AS change FROM admissions
      WHERE admitted_on BETWEEN :from AND :to
      UNION ALL
      SELECT a.ward_id, date(d.discharged_on, '+1 day') AS day, -1 AS change
      FROM discharges d JOIN admissions a ON a.id = d.admission_id
      WHERE d.discharged_on >= :from AND d.discharged_on < :to
    ), daily_changes AS (
      SELECT ward_id, day, SUM(change) AS change FROM pulses GROUP BY ward_id, day
    ), census AS (
      SELECT w.id AS ward_id, w.capacity, calendar.day,
             COALESCE(o.occupied, 0) + SUM(COALESCE(dc.change, 0)) OVER (
               PARTITION BY w.id ORDER BY calendar.day ROWS UNBOUNDED PRECEDING
             ) AS occupied
      FROM wards w CROSS JOIN calendar
      LEFT JOIN opening o ON o.ward_id = w.id
      LEFT JOIN daily_changes dc ON dc.ward_id = w.id AND dc.day = calendar.day
    ), ranked AS (
      SELECT *, ROW_NUMBER() OVER (
        PARTITION BY ward_id ORDER BY occupied DESC, day ASC
      ) AS peak_position FROM census
    )
    SELECT ward_id, MAX(occupied) AS peak_census,
           MIN(CASE WHEN peak_position = 1 THEN day END) AS peak_day,
           SUM(CASE WHEN occupied >= capacity THEN 1 ELSE 0 END) AS days_at_capacity,
           SUM(CASE WHEN occupied > capacity THEN 1 ELSE 0 END) AS days_over_capacity,
           SUM(occupied) AS census_bed_days
    FROM ranked GROUP BY ward_id
  `).all(params);
  const pressureByWard = new Map(wardPressure.map(({ ward_id, ...pressure }) => [ward_id, pressure]));
  for (const ward of wards) Object.assign(ward, pressureByWard.get(ward.id));
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
  // Compute the next stay across the patient's *entire* admission history,
  // before restricting the discharge cohort to the requested date range.
  // Otherwise, returns that start after :to would be incorrectly missed.
  const returns = db.prepare(`
    WITH sequenced_stays AS (
      SELECT id, patient_id, ward_id, admitted_on,
             LEAD(admitted_on) OVER (
               PARTITION BY patient_id ORDER BY admitted_on, id
             ) AS next_admitted_on
      FROM admissions
    ), discharged_stays AS (
      SELECT s.ward_id, d.discharged_on,
             CASE WHEN julianday(s.next_admitted_on) - julianday(d.discharged_on)
                         BETWEEN 1 AND 30 THEN 1 ELSE 0 END AS returned_within_30_days
      FROM sequenced_stays s JOIN discharges d ON d.admission_id = s.id
      WHERE d.discharged_on BETWEEN :from AND :to
    ), by_ward AS (
      SELECT ward_id, COUNT(*) AS discharged_stays,
             SUM(returned_within_30_days) AS returns_within_30_days
      FROM discharged_stays GROUP BY ward_id
    )
    SELECT w.id AS ward_id, w.name,
           COALESCE(b.discharged_stays, 0) AS discharged_stays,
           COALESCE(b.returns_within_30_days, 0) AS returns_within_30_days,
           CASE WHEN b.discharged_stays > 0
             THEN ROUND(100.0 * b.returns_within_30_days / b.discharged_stays, 1)
             ELSE NULL END AS return_pct
    FROM wards w LEFT JOIN by_ward b ON b.ward_id = w.id
    ORDER BY returns_within_30_days DESC, discharged_stays DESC, w.name
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
    returnsWithin30Days: returns.reduce((sum, ward) => sum + ward.returns_within_30_days, 0),
    occupiedBedDays: wards.reduce((sum, ward) => sum + ward.occupied_bed_days, 0),
    availableBedDays: wards.reduce((sum, ward) => sum + ward.available_bed_days, 0),
  }, returns };
}
