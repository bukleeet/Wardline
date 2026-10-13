#!/usr/bin/env node
/**
 * Disposable, synthetic SQLite workload. Reuses the application's actual SQL.
 * Benchmarks are opt-in; CI runs only a tiny command-line smoke test.
 */
import { performance } from 'node:perf_hooks';
import { openDatabase } from '../src/database.js';
import { queryLab } from '../src/query-lab.js';
import { operationsReport, PHYSICIAN_WORKLOAD_SQL, RECORDED_RETURNS_SQL } from '../src/reports.js';

const WINDOW = Object.freeze({ from: '2024-01-01', to: '2024-03-31' });
const DEFAULTS = Object.freeze({ patients: 4000, stays: 5, seed: 20241014, warmup: 2, iterations: 7 });
const LIMITS = Object.freeze({ patients: 20000, stays: 8, seed: 4294967295, warmup: 20, iterations: 100 });
const BASE_DAY = Date.UTC(2023, 0, 1);
const DAY_MS = 86400000;

function parseOptions(args) {
  const options = { ...DEFAULTS, json: false };
  for (const arg of args) {
    if (arg === '--json') {
      options.json = true;
      continue;
    }
    const match = /^--(patients|stays|seed|warmup|iterations)=(\d+)$/.exec(arg);
    if (!match) throw new Error('Unknown option: ' + arg);
    const [, name, digits] = match;
    const value = Number(digits);
    const minimum = name === 'warmup' || name === 'seed' ? 0 : 1;
    if (!Number.isSafeInteger(value) || value < minimum || value > LIMITS[name]) {
      throw new Error('--' + name + ' must be an integer from ' + minimum + ' to ' + LIMITS[name]);
    }
    options[name] = value;
  }
  return options;
}

function randomSequence(seed) {
  let state = seed >>> 0;
  return () => {
    state = (Math.imul(1664525, state) + 1013904223) >>> 0;
    return state;
  };
}

function dateAt(offset) {
  return new Date(BASE_DAY + offset * DAY_MS).toISOString().slice(0, 10);
}

function generateFixture(db, { patients, stays, seed }) {
  const random = randomSequence(seed);
  const addWard = db.prepare('INSERT INTO wards (name, floor, category, capacity) VALUES (?, ?, ?, ?)');
  const addPhysician = db.prepare('INSERT INTO physicians (first_name, last_name, specialty, contact_no) VALUES (?, ?, ?, ?)');
  const addPatient = db.prepare(
    'INSERT INTO patients (first_name, last_name, date_of_birth, sex, contact_no) VALUES (?, ?, ?, ?, ?)'
  );
  const addAdmission = db.prepare(
    'INSERT INTO admissions (patient_id, ward_id, admitted_on, reason) VALUES (?, ?, ?, ?)'
  );
  const addDischarge = db.prepare(
    'INSERT INTO discharges (admission_id, discharged_on, disposition) VALUES (?, ?, ?)'
  );
  const addDiagnosis = db.prepare(
    'INSERT INTO diagnoses (patient_id, physician_id, illness_id, diagnosed_on) VALUES (?, ?, ?, ?)'
  );
  const addTreatment = db.prepare(
    'INSERT INTO treatments (patient_id, physician_id, performed_on, procedure_name) VALUES (?, ?, ?, ?)'
  );

  db.exec('BEGIN');
  try {
    for (let ward = 1; ward <= 24; ward += 1) {
      addWard.run('Synthetic ward ' + ward, String(ward % 6 + 1), 'Benchmark', 800);
    }
    for (let physician = 1; physician <= 120; physician += 1) {
      addPhysician.run('Demo', 'Clinician' + physician, 'Synthetic', '0');
    }
    db.prepare('INSERT INTO illnesses (name, category) VALUES (?, ?)').run('Synthetic condition', 'Benchmark');

    for (let patient = 1; patient <= patients; patient += 1) {
      addPatient.run('Demo', 'Patient' + patient, '1990-01-01', patient % 2 ? 'F' : 'M', '0');
      let start = 20 + Number(random() % 200);
      for (let stay = 0; stay < stays; stay += 1) {
        const ward = 1 + Number(random() % 24);
        const duration = 1 + Number(random() % 18);
        const admissionId = Number(addAdmission.run(patient, ward, dateAt(start), 'Synthetic load').lastInsertRowid);
        // Only a final stay can remain open. No patient has overlapping stays.
        if (stay !== stays - 1 || random() % 7 !== 0) {
          addDischarge.run(admissionId, dateAt(start + duration), 'Recovered');
        }
        start += duration + 2 + Number(random() % 70);
      }
      for (let event = 0; event < 2; event += 1) {
        addDiagnosis.run(patient, 1 + Number(random() % 120), 1, dateAt(150 + Number(random() % 520)));
        addTreatment.run(patient, 1 + Number(random() % 120),
          dateAt(150 + Number(random() % 520)), 'Synthetic procedure');
      }
    }
    db.exec('COMMIT');
  } catch (error) {
    db.exec('ROLLBACK');
    throw error;
  }
  // Populate planner statistics for the generated data before explaining queries.
  db.exec('ANALYZE');
}

function datasetCounts(db) {
  const tables = ['patients', 'wards', 'physicians', 'admissions', 'discharges', 'diagnoses', 'treatments'];
  return Object.fromEntries(tables.map((table) => [
    table, db.prepare('SELECT COUNT(*) AS count FROM ' + table).get().count,
  ]));
}

function measure(execute, warmup, iterations) {
  for (let i = 0; i < warmup; i += 1) execute();
  let rowsPerRun;
  const samples = [];
  for (let i = 0; i < iterations; i += 1) {
    const start = performance.now();
    const rows = execute();
    const elapsed = performance.now() - start;
    if (rowsPerRun !== undefined && rows !== rowsPerRun) {
      throw new Error('Query returned an inconsistent number of rows');
    }
    rowsPerRun = rows;
    samples.push(elapsed);
  }
  const sorted = [...samples].sort((a, b) => a - b);
  const center = Math.floor(sorted.length / 2);
  const median = sorted.length % 2 ? sorted[center] : (sorted[center - 1] + sorted[center]) / 2;
  const round = (value) => Number(value.toFixed(3));
  return {
    rowsPerRun,
    samplesMs: samples.map(round),
    medianMs: round(median),
    p95Ms: round(sorted[Math.ceil(0.95 * sorted.length) - 1]),
    minMs: round(sorted[0]),
    maxMs: round(sorted.at(-1)),
  };
}

function benchmark(db, settings) {
  const timeline = queryLab(db, WINDOW).examples.find((example) => example.id === 'patient-timeline');
  if (!timeline) throw new Error('Patient timeline example missing from Query Lab');
  const workload = db.prepare(PHYSICIAN_WORKLOAD_SQL);
  const returns = db.prepare(RECORDED_RETURNS_SQL);
  const patientHistory = db.prepare(timeline.sql);
  const cases = [
    {
      id: 'operations-report',
      sql: null,
      execute: () => {
        const result = operationsReport(db, WINDOW);
        return result.wards.length + result.daily.length + result.physicians.length + result.returns.length;
      },
    },
    { id: 'physician-workload', sql: PHYSICIAN_WORKLOAD_SQL, bindings: WINDOW,
      execute: () => workload.all(WINDOW).length },
    { id: 'recorded-returns', sql: RECORDED_RETURNS_SQL, bindings: WINDOW,
      execute: () => returns.all(WINDOW).length },
    { id: 'patient-timeline', sql: timeline.sql, bindings: timeline.bindings,
      execute: () => patientHistory.all(timeline.bindings).length },
  ];
  return cases.map(({ id, sql, bindings, execute }) => ({
    id,
    ...measure(execute, settings.warmup, settings.iterations),
    // The composite report executes several statements; it has no single plan.
    plan: sql ? db.prepare('EXPLAIN QUERY PLAN ' + sql).all(bindings)
      .map(({ id: node, parent, detail }) => ({ id: node, parent, detail })) : null,
  }));
}

function asText(result) {
  const { settings, runtime, dataset, queries } = result;
  const lines = [
    'Wardline SQL benchmark (synthetic, in-memory)',
    'Node ' + runtime.node + ' / SQLite ' + runtime.sqlite + ' / ' + runtime.platform,
    'Seed ' + settings.seed + ' / ' + settings.patients + ' patients / ' + settings.stays + ' stays each',
    'Window ' + WINDOW.from + '..' + WINDOW.to + ' / warmups ' + settings.warmup +
      ' / measured runs ' + settings.iterations,
    'Rows: ' + Object.entries(dataset).map(([table, count]) => table + '=' + count).join(', '),
  ];
  for (const item of queries) {
    lines.push('', item.id + ' | result rows ' + item.rowsPerRun +
      ' | median ' + item.medianMs + ' ms | p95 ' + item.p95Ms +
      ' ms | min ' + item.minMs + ' ms | max ' + item.maxMs + ' ms');
    lines.push('  Samples (ms): ' + item.samplesMs.join(', '));
    lines.push(...(item.plan === null
      ? ['  Composite report: individual SQL statements are in src/reports.js']
      : item.plan.map((step) => '  [' + step.id + ' -> ' + step.parent + '] ' + step.detail)));
  }
  lines.push('', 'These timings are local observations, not production performance guarantees.');
  return lines.join('\n');
}

try {
  const { json, ...settings } = parseOptions(process.argv.slice(2));
  const db = openDatabase({ file: ':memory:', seed: false });
  try {
    generateFixture(db, settings);
    const result = {
      settings,
      runtime: {
        node: process.version,
        sqlite: db.prepare('SELECT sqlite_version() AS version').get().version,
        platform: process.platform + '/' + process.arch,
      },
      window: WINDOW,
      dataset: datasetCounts(db),
      queries: benchmark(db, settings),
    };
    process.stdout.write(json ? JSON.stringify(result, null, 2) + '\n' : asText(result) + '\n');
  } finally {
    db.close();
  }
} catch (error) {
  process.stderr.write('SQL benchmark failed: ' + error.message + '\n');
  process.exitCode = 1;
}
