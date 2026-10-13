# Wardline

**A hospital operations dashboard and relational record management demo.**

Wardline reimagines an academic hospital database assignment as a local-first web application. It brings patient flow, ward utilization, medication stock, staffing, and clinical records into a responsive interface backed by SQLite. All demo data is synthetic, so the project can be explored without a hospital database, secrets, or external services.

> **Portfolio demonstration only.** All names and records are fictional. This is **not** a medical device, an electronic health record system, or a system for real patient information. It has no user authentication, authorization, encryption-at-rest, or audit trail; do not expose it to the public internet or enter real health data.

## Explore the app

- **Overview:** live admission totals, patient counts, bed occupancy, six-month admissions trend, ward utilization, medication reorder alerts, and recent admissions.
- **SQL insights:** choose a date range to inspect daily census and net patient flow, inclusive occupied bed-days, ranked ward utilization, peak ward census, days at or above capacity, recorded 30-day returns by discharge ward, average discharged length of stay, and physician workload. Computed on demand by parameterized SQL with recursive CTEs, event-stream aggregation, and window functions.
- **SQL Query Lab:** expand three inspectable examples to see the actual parameterized SQL, bound values, and live SQLite `EXPLAIN QUERY PLAN` output. The workload and recorded-return queries are exactly the statements that generate their corresponding reports; the third builds an indexed longitudinal clinical timeline across admissions, diagnoses and treatments using UNION ALL. Every example also shows up to five actual result rows. Planner output is diagnostic evidence, not a timing benchmark.
- **Record management:** create, browse, search, edit, and remove records across 13 resources, including patients, staff, conditions, medicines, admissions, diagnoses, and treatments.
- **Linked data:** searchable relationship pickers (name or exact `#ID`) and readable relation labels in tables, including historical records outside the initial option list.
- **Filtered CSV exports:** download all records matching the current section search, with readable linked-record labels and spreadsheet formula escaping (up to 5,000 records).
- **Data integrity:** foreign keys, transactions, input validation, ward capacity checks, nonoverlapping admission histories, admission/discharge chronology, and treatment-to-diagnosis consistency. A separate **SQL integrity audit** checks seven cross-record invariants even when database edits bypass the application.
- **Responsive navigation:** desktop sidebar, compact mobile navigation, keyboard shortcut (`/`) for patient search, skip-to-content link, labeled data tables, accessible dialogs, and empty/error states.

### Quick start

Requires **Node.js 24 or later**. No `npm install`, API keys, or external services are needed.

```bash
git clone https://github.com/bukleeet/Hospital-DB.git
cd Hospital-DB
npm start
```

Open **http://127.0.0.1:3000**. Wardline creates `data/hospital.sqlite` on first launch and inserts deterministic fake records. Data changes persist across restarts.

Useful commands:

```bash
npm run dev     # restart server when source changes
npm test        # run the Node test suite
npm run format:css # format the readable stylesheet
npm run check   # syntax, CSS formatting, and all tests
```

Configuration: `PORT` sets the HTTP port (default `3000`), and `HOSPITAL_DB_PATH` sets the SQLite file location (default `data/hospital.sqlite`). The default host is `127.0.0.1`; keep the demo bound to a local interface. To reset the demonstration, stop the server and delete `data/hospital.sqlite` and any adjacent `-wal`/`-shm` files. The initial synthetic dataset will be recreated at next launch.

Node currently marks `node:sqlite` experimental, so an experimental feature warning can appear in the console.

## Architecture

```text
Browser (HTML + CSS + vanilla JavaScript)
       | same-origin fetch /api/*
Node HTTP server (src/server.js)
       | request validation + route dispatch
Repository/service layer (src/repository.js)
       | catalog-driven validation + business invariants + parameterized SQL
SQLite database (src/database.js)
       | versioned SQL migrations, foreign keys, indexes, transactions
Deterministic demo fixtures (src/seed.js)
```

No runtime npm dependencies are required. The backend uses built-in Node HTTP and SQLite modules; the frontend uses native browser APIs. Resource definitions and form metadata live in `src/catalog.js` to keep backend validation and client forms aligned.

The database schema lives in [numbered SQL migrations](src/migrations/). On startup, Wardline upgrades an existing SQLite file transactionally using `PRAGMA user_version`. Older unversioned Wardline installations are adopted without changing their records, including intentionally emptied demo databases. To add a future schema change, create a new numbered SQL migration and register it in `src/migrations.js`; never modify a released migration. See [database lifecycle](docs/architecture.md#schema-migrations-and-query-plans).

See [Architecture and domain rules](docs/architecture.md) for the resource map, API contract, design decisions, and limitations.

### API examples

| Method | Route | Purpose |
| --- | --- | --- |
| `GET` | `/api/health` | Health check |
| `GET` | `/api/catalog` | Resource and field metadata |
| `GET` | `/api/dashboard` | Aggregate dashboard metrics |
| `GET` | `/api/reports/operations?from=2026-09-01&to=2026-09-30` | Operational SQL report (inclusive range, at most 366 days) |
| `GET` | `/api/reports/query-lab?from=2026-09-01&to=2026-09-30` | Fixed, read-only SQL with bound parameters, live query plans and bounded sample rows |
| `GET` | `/api/reports/integrity` | Read-only relational integrity audit across the entire database |
| `GET` | `/api/records/patients?search=amara&page=1&pageSize=10` | Search/paginate patient records |
| `GET` | `/api/records/patients/1` | Read one record |
| `GET` | `/api/lookup/wards?search=critical` | Search ward options for forms (100 results maximum) |
| `GET` | `/api/export/patients?search=amara` | Download matching records as UTF-8 CSV (5,000-row limit) |
| `POST` | `/api/records/patients` | Create a patient |
| `PATCH` | `/api/records/patients/1` | Update selected fields |
| `DELETE` | `/api/records/patients/1` | Remove an unreferenced patient |

The API accepts JSON for `POST` and `PATCH` and returns JSON responses. Invalid input produces `400`, unknown resources or IDs produce `404`, and conflicting relations return `409`. The browser and local API use the same HTTP origin; cross-origin requests with conflicting `Origin` or `Sec-Fetch-Site` values are rejected.

## Quality and testing

Tests use Node's built-in test runner and isolated SQLite databases. They cover seeded consistency, search and pagination, CRUD, validation, foreign-key behavior, bed capacity, clinical and scheduling rules, dashboard aggregation, and HTTP request handling. Migration tests additionally exercise actual file persistence, unversioned upgrade compatibility, transactional rollback, rejection of future schema versions, and SQLite query plans. HTTP tests invoke the real request listener without opening a socket, which also works in restricted CI environments.

The SQL reporting fixtures also check occupied-bed-day overlap across date boundaries, inclusive discharge dates, date-complete daily census, carried admissions, peak ward census, capacity pressure, 30-day return boundaries, next stays outside the report window, empty wards, length of stay, workload join fanout, leap days, and range validation. Independently computed ward census bed-days are reconciled with interval-based occupied bed-days. The report is a concrete example of nontrivial relational analytics beyond basic CRUD.

`test/report-invariants.test.js` strengthens these hand-built examples with **45 deterministic reporting scenarios** (15 generated patient histories × 3 date windows). It checks SQLite's daily census, admissions/discharges, net flow, ward occupancy peaks and capacity thresholds, occupied bed-days, and 30-day return cohorts against an independent calendar-based JavaScript oracle. The fixtures deliberately span leap day, report boundaries, open stays, and even over-capacity states; failures identify their seed, window, and ward so they can be reproduced with `node --test test/report-invariants.test.js`.

The integrity audit independently queries the underlying tables for overlapping stays, over-capacity wards, invalid discharge chronology, mismatched treatment/diagnosis patients, inverted nurse-assignment dates, and invalid physician or nurse shift times. It returns counts and up to three example records per rule. Its interval-overlap check uses an ordered running maximum to catch nested historical stays. The audit is **read-only**, runs over the **entire database** regardless of the selected insights date range, and diagnoses only its seven named rules; a clean result is not proof of all possible data correctness. Tests inject direct SQL anomalies that bypass application validation and verify that the audit catches them without modifying records.

To assess the SQL work, open **SQL insights -> From SQL to evidence.** The Query Lab exposes three fixed statements, current parameter bindings, SQLite access plans, and up to five executed rows per example. Compare the indexed UNION ALL patient history with the CTE/window-function plans for physician workload and 30-day returns. No arbitrary SQL is accepted. See [SQL engineering deep dive](docs/sql-deep-dive.md) for the algorithms, invariants, and interview-ready discussion.

Run `npm run check` before committing. The GitHub Actions workflow repeats these checks on pushes and pull requests. Use conventional commit subjects, such as `feat: add admission dashboard` or `fix: prevent capacity overbooking`.

### Reproducible SQL benchmark

Run the actual operational report and the three SQL Query Lab statements against a larger, **deterministic synthetic dataset**:

```bash
npm run benchmark:sql
npm run benchmark:sql -- --patients=8000 --stays=6 --seed=42 --iterations=9
npm run benchmark:sql -- --patients=8000 --stays=6 --seed=42 --iterations=9 --json
```

The default fixture contains 4,000 fake patients, 20,000 admissions, 8,000 diagnoses, 8,000 treatments, 24 wards and 120 physicians. All data lives in an isolated in-memory SQLite database and is discarded after each run. Dates, relationships and patient stay sequences are generated from a fixed seed; no hospital data is imported. The report covers January 1–March 31, 2024.

The runner applies the production migrations, fills the fixture in one transaction, executes `ANALYZE`, warms up each query, then reports measured execution times, median, nearest-rank p95, min/max, returned row counts, runtime versions and actual `EXPLAIN QUERY PLAN` steps. The composite operational report is timed end-to-end; its constituent statements live in `src/reports.js`. SQL query plans are emitted for the three independently benchmarked Query Lab statements. This is a repeatable local experiment, not evidence of production throughput. Compare identical seed, shape and environment when discussing results.

## Where the original assignment lives

The repository also retains the original Java Swing/MySQL coursework in `DBApplication/` and the top-level SQL scripts as historical reference. Wardline is a separate, modern implementation of the same broad domain and does **not** migrate or depend on the legacy database. The source files in `src/`, `public/`, `test/`, and `docs/` are the active application.

### Scope and next steps

Wardline is built for a local, single-user demo. Production usage would require identity and role controls, encryption, audit logging, migration governance and backups, observability, accessibility testing in real browsers, and a formal privacy/security review. No such production guarantees are claimed here.
