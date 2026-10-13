# SQL engineering deep dive

Wardline is a practical SQL portfolio project: its operational reports are computed from normalized records at query time, and its integrity checks operate independently of the application write path. This guide identifies the concrete decisions, tradeoffs, and tests a reviewer can examine. All fixtures are synthetic; none of these metrics should be interpreted as clinical decision support.

## Start with observable evidence

1. Run `npm start` with Node.js 24+, open **SQL insights**, and choose a date range.
2. Expand **From SQL to evidence.** Compare the SQL with its bound values, SQLite `EXPLAIN QUERY PLAN` steps, and up to five executed result rows. The workload and recorded-return examples are the exact strings run by the report, exported from `src/reports.js`. The timeline example illustrates the indexes in migration `002_patient_timeline_indexes.sql`.
3. Read `src/reports.js`, `src/integrity.js`, `src/repository.js`, and `src/migrations/*.sql`; then run `npm run check` to exercise real SQLite fixtures and HTTP handlers.

The Query Lab accepts only a date window. **It does not accept submitted SQL**, and it runs `EXPLAIN QUERY PLAN` and a bounded five-row preview on three fixed, parameterized statements. Bound values are supplied separately from SQL syntax. Plans describe a chosen access path on this schema and dataset; they do **not** measure elapsed time, memory, or throughput. Preview rows demonstrate what SQLite returned, not the completeness of a full report.

## Why the SQL is structured this way

| Design problem | Implemented solution | Verification |
| --- | --- | --- |
| Multiple diagnoses and treatments inflate a physician's counts when joined together | Aggregate each relation separately in `diagnosis_counts` and `treatment_counts` CTEs, then join the aggregate rows to physicians; use `RANK()` to order activity | `test/reports.test.js` checks 2 diagnoses + 2 treatments = 2 of each, rather than 4 |
| A discharge at the end of the selected month may be followed by an admission next month | Apply `LEAD(admitted_on) OVER (PARTITION BY patient_id ORDER BY admitted_on, id)` to **all** admissions before filtering the discharge cohort | `test/reports.test.js` checks a next stay beyond the reporting window and the 30-day boundary |
| A stay crosses a reporting boundary | Clip overlapping intervals to `[from, to]` using SQLite `MIN`/`MAX`; add 1 for inclusive discharge day | Interval and daily-census bed-day totals agree across boundary and same-day fixtures |
| Dense daily census needs dates with no admissions or discharges | Build a bounded recursive calendar, aggregate event pulses per day, carry in the opening census, then apply a windowed cumulative sum | Tests cover no-event days, previously admitted patients, leap days, peaks, and capacity thresholds |
| An earlier long stay can contain a later stay that is not adjacent in discharge order | Compare each start date to a **running maximum** of prior interval ends instead of only `LAG()` | `test/integrity.test.js` injects overlapping historical stays directly in SQLite |
| Record edits should not leave cross-record contradictions | Use schema constraints, whitelisted resources/fields, prepared statements, and transactions with business-rule validation | `test/repository.test.js` covers conflicting admissions, capacity changes, relations, and chronology |
| Treatments may have no assigned physician | Unite admissions, diagnoses, and treatments with `UNION ALL`; use a `LEFT JOIN` for the optional treatment physician so valid events are never silently dropped | `test/clinical-timeline.test.js` checks unassigned treatments, patient isolation, and read-only previews |

### Independent report oracle

The hand-built SQL test cases above exercise specific edge conditions. `test/report-invariants.test.js` adds 45 deterministic scenarios across 15 generated admission histories and three report windows, including February 29, 2024. An independent JavaScript implementation expands each stay into occupied calendar days, computes daily admissions and discharges, finds ward occupancy peaks and capacity thresholds, then walks each patient's consecutive stays to count observed returns within 30 days. Those totals are checked against the SQL report's recursive CTE, window function, interval aggregation, and `LEAD()` outputs. Unlike comparing two SQL queries that could share an incorrect assumption, this reference calculation follows a separate algorithm. Seed and date-range labels make failures reproducible.

Run only this contract with `node --test test/report-invariants.test.js` or include it in `npm run check`. These synthetic scenarios strengthen correctness evidence, but they do not substitute for production workload benchmarks or clinical validation.

### A join fanout example

If physician A has two diagnosis rows and two treatment rows, directly joining both child tables by physician creates **four** joined rows. `COUNT(diagnosis.id)` and `COUNT(treatment.id)` would each incorrectly report four. The implemented report produces one aggregate row per physician per child relation before joining them. It deliberately keeps `COUNT(DISTINCT patient_id)` within the treatment aggregate to count distinct treated patients. Both subqueries use the same bound report window.

### A temporal boundary example

Suppose a stay is discharged on September 29 and the same patient returns on October 10. If September's date filter is applied to admissions **before** `LEAD()`, the October admission disappears and the return is missed. Wardline sequences the entire history first and filters discharge dates afterward. It counts only the next recorded admission that starts **1–30 calendar days** after discharge, associates the count with the discharge ward, and retains wards with no discharges via a left join. This is an observed-return metric: missing follow-up records and unknown return reasons limit its meaning.

### Calendar semantics

This model stores dates without times and intentionally counts both admission and discharge calendar days as occupied. Discharge creates a **-1 pulse on the following day**, while admission creates a **+1 pulse on its first day**. The running daily ward census includes an opening count of stays already in progress; the integral (sum of census over days) is tested against independently calculated overlapping bed-day intervals. Both calculations are based on the current ward capacity, as the schema does not retain capacity history.

## Index design and explain plans

The second migration adds `idx_admissions_patient_date_id` on `(patient_id, admitted_on, id)` plus patient/date indexes for diagnoses and treatments. The clinical timeline performs independent patient-indexed reads, then uses `UNION ALL` to retain every event before sorting newest first. The treatment-to-physician relationship is optional, so the timeline uses a `LEFT JOIN` and labels missing clinicians explicitly. The Query Lab shows the actual planner choice, which currently includes `SEARCH admissions USING INDEX idx_admissions_patient_date_id (patient_id=?)` for the synthetic fixture. Since the query selects `ward_id`, the index is **not covering**; table-row access can still be required.

The windowed aggregate queries have broader work to do, including scans, joins, temporary sorting, and materialized intermediary results depending on SQLite's planner choices. Adding indexes to every column would increase storage and write costs and is not automatically an improvement. A proper performance experiment would increase synthetic data size, check distributions with `ANALYZE`, compare query plans, run repeatable warm/cold timing tests, and inspect writes and memory under a stated workload. The small deterministic seed is useful for correctness and explainability, but no production speed-up is claimed.

## Reproducible performance experiment

```bash
npm run benchmark:sql
npm run benchmark:sql -- --patients=8000 --stays=6 --seed=42 --iterations=9 --json
```

The opt-in benchmark (`scripts/benchmark-sql.mjs`) applies the real migrations to a disposable in-memory SQLite database. A seeded pseudo-random generator inserts valid foreign-key relationships and nonoverlapping per-patient admission histories alongside diagnoses and treatments. It records the exact row counts, Node/SQLite versions, date range, seed, warm-up count and timed iterations. After `ANALYZE`, it executes the **actual** production physician-workload and recorded-return SQL strings, the live Query Lab clinical timeline statement, and the composite `operationsReport` function. The three individual statements also produce `EXPLAIN QUERY PLAN` nodes and details; the composite report has multiple queries, so it has no single plan.

The benchmark uses repeated wall-clock observations via `performance.now()` with median and nearest-rank p95; all rows are materialized for each timed query, and statements for the three single-query workloads are prepared outside their timed runs. The composite report times its normal query preparation and all SQL statements, so its numbers are **not directly comparable** to single-query timings. It includes no network or browser rendering time. Output row counts are checked for stability across measured iterations. The small benchmark CLI smoke test in `test/benchmark-sql.test.js` verifies data counts, query selection, explain output and invalid-argument rejection; it does not test performance thresholds.

Query plans and timings can change with SQLite/Node versions, hardware, OS caches, workload distribution, and parameter values. Run the command on the same machine with identical flags for useful comparisons, record the raw samples, and avoid claiming a speed-up without a controlled comparison. The generator intentionally models a synthetic load profile rather than a real hospital's occupancy or case mix. This experiment demonstrates query inspection and measurement technique, not production scale or clinical relevance.

## Schema evolution and safety

`src/migrations.js` runs ordered SQL files inside `BEGIN IMMEDIATE`, advancing `PRAGMA user_version` only after an upgrade succeeds. Initial seeding occurs within the fresh-database transaction. The runner recognizes valid unversioned Wardline databases, including empty ones, and refuses incompatible or newer schemas without writing changes. Released migrations are immutable: new schema changes require a new numbered SQL file and tests for both fresh installs and upgrades. `test/database.test.js` covers persistence, rollback, and index selection.

User text is always bound as SQL parameters. Identifiers cannot be bound in SQLite, so resource and field names are selected only through the application's validated catalog. The API's Query Lab is deliberately fixed to reviewed statements; exposing free-form SQL execution in a web endpoint would defeat that boundary. `src/integrity.js` is a separate read-only diagnostic that catches seven named violations even when writes bypass repository validation; a clean audit is not a blanket data-integrity guarantee.

## Talking through the work in an interview

A useful walkthrough is to trace **the SQL question → query design → edge case → test → observed plan**. For example: show how joins multiply counts, explain pre-aggregation, open the physician workload SQL in the Query Lab, and point to the fixture that would fail with a naïve join. Then show the date-boundary return fixture, discuss why the full admission history must be sequenced first, and distinguish query-plan inspection from benchmarking. Conclude by explaining transactional migrations and why raw-SQL integrity audits complement application validation. These are demonstrable engineering decisions in the repository, rather than claims based on a UI screenshot.
