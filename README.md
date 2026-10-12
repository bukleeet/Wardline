# Wardline

**A hospital operations dashboard and relational record management demo.**

Wardline reimagines an academic hospital database assignment as a local-first web application. It brings patient flow, ward utilization, medication stock, staffing, and clinical records into a responsive interface backed by SQLite. All demo data is synthetic, so the project can be explored without a hospital database, secrets, or external services.

> **Portfolio demonstration only.** All names and records are fictional. This is **not** a medical device, an electronic health record system, or a system for real patient information. It has no user authentication, authorization, encryption-at-rest, or audit trail; do not expose it to the public internet or enter real health data.

## Explore the app

- **Overview:** live admission totals, patient counts, bed occupancy, six-month admissions trend, ward utilization, medication reorder alerts, and recent admissions.
- **Record management:** create, browse, search, edit, and remove records across 13 resources, including patients, staff, conditions, medicines, admissions, diagnoses, and treatments.
- **Linked data:** relationship pickers for related records and readable relation labels in tables.
- **Data integrity:** foreign keys, transactions, input validation, ward capacity checks, admission/discharge chronology, and treatment-to-diagnosis consistency.
- **Responsive navigation:** desktop sidebar, compact mobile navigation, keyboard shortcut (`/`) for patient search, accessible dialogs, and empty/error states.

### Quick start

Requires **Node.js 24 or later**. No `npm install`, API keys, or external services are needed.

```bash
git clone <your-repository-url>
cd Hospital-DB
npm start
```

Open **http://127.0.0.1:3000**. Wardline creates `data/hospital.sqlite` on first launch and inserts deterministic fake records. Data changes persist across restarts.

Useful commands:

```bash
npm run dev     # restart server when source changes
npm test        # run the Node test suite
npm run check   # syntax checks plus all tests
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
       | foreign keys, indexes, transactions
Deterministic demo fixtures (src/seed.js)
```

No runtime npm dependencies are required. The backend uses built-in Node HTTP and SQLite modules; the frontend uses native browser APIs. Resource definitions and form metadata live in `src/catalog.js` to keep backend validation and client forms aligned.

See [Architecture and domain rules](docs/architecture.md) for the resource map, API contract, design decisions, and limitations.

### API examples

| Method | Route | Purpose |
| --- | --- | --- |
| `GET` | `/api/health` | Health check |
| `GET` | `/api/catalog` | Resource and field metadata |
| `GET` | `/api/dashboard` | Aggregate dashboard metrics |
| `GET` | `/api/records/patients?search=amara&page=1&pageSize=10` | Search/paginate patient records |
| `GET` | `/api/records/patients/1` | Read one record |
| `GET` | `/api/lookup/wards` | Ward options for forms |
| `POST` | `/api/records/patients` | Create a patient |
| `PATCH` | `/api/records/patients/1` | Update selected fields |
| `DELETE` | `/api/records/patients/1` | Remove an unreferenced patient |

The API accepts JSON for `POST` and `PATCH` and returns JSON responses. Invalid input produces `400`, unknown resources or IDs produce `404`, and conflicting relations return `409`. The browser and local API use the same HTTP origin; cross-origin requests with conflicting `Origin` or `Sec-Fetch-Site` values are rejected.

## Quality and testing

Tests use Node's built-in test runner and fresh, in-memory SQLite databases. They cover seeded consistency, search and pagination, CRUD, validation, foreign-key behavior, bed capacity, clinical and scheduling rules, dashboard aggregation, and HTTP request handling. HTTP tests invoke the real request listener without opening a socket, which also works in restricted CI environments.

Run `npm run check` before committing. The GitHub Actions workflow repeats these checks on pushes and pull requests. Use conventional commit subjects, such as `feat: add admission dashboard` or `fix: prevent capacity overbooking`.

## Where the original assignment lives

The repository also retains the original Java Swing/MySQL coursework in `DBApplication/` and the top-level SQL scripts as historical reference. Wardline is a separate, modern implementation of the same broad domain and does **not** migrate or depend on the legacy database. The source files in `src/`, `public/`, `test/`, and `docs/` are the active application.

### Scope and next steps

Wardline is built for a local, single-user demo. Production usage would require identity and role controls, encryption, audit logging, migration tooling, observability, accessibility testing in real browsers, and a formal privacy/security review. No such production guarantees are claimed here.
