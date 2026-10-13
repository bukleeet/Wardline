import assert from 'node:assert/strict';
import { mkdtempSync, rmSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { DatabaseSync } from 'node:sqlite';
import { test } from 'node:test';
import { migrateDatabase, openDatabase } from '../src/database.js';
import { migrations } from '../src/migrations.js';

const versionOf = (db) => db.prepare('PRAGMA user_version').get().user_version;

function withDatabaseFile(action) {
  const directory = mkdtempSync(join(tmpdir(), 'wardline-migration-'));
  try {
    action(join(directory, 'hospital.sqlite'));
  } finally {
    rmSync(directory, { recursive: true, force: true });
  }
}

test('new SQLite files migrate to the latest version, seed once, and persist data', () => {
  withDatabaseFile((file) => {
    const first = openDatabase({ file });
    try {
      assert.equal(versionOf(first), migrations.at(-1).version);
      assert.equal(first.prepare('SELECT COUNT(*) AS n FROM patients').get().n, 32);
      first.prepare('UPDATE patients SET first_name = ? WHERE id = 1').run('Updated');
      assert.equal(first.prepare('PRAGMA foreign_keys').get().foreign_keys, 1);
    } finally {
      first.close();
    }

    const reopened = openDatabase({ file });
    try {
      assert.equal(versionOf(reopened), migrations.at(-1).version);
      assert.equal(reopened.prepare('SELECT COUNT(*) AS n FROM patients').get().n, 32);
      assert.equal(reopened.prepare('SELECT first_name FROM patients WHERE id = 1').get().first_name, 'Updated');
    } finally {
      reopened.close();
    }
  });
});

test('adopts populated and deliberately empty unversioned Wardline databases', () => {
  for (const populated of [true, false]) {
    withDatabaseFile((file) => {
      const old = new DatabaseSync(file);
      try {
        old.exec(migrations[0].sql);
        if (populated) {
          old.prepare(`INSERT INTO patients (first_name, last_name, date_of_birth, sex, contact_no)
                       VALUES (?, ?, ?, ?, ?)`).run('Legacy', 'Patient', '1990-01-01', 'F', '000');
        }
        assert.equal(versionOf(old), 0);
      } finally {
        old.close();
      }

      const upgraded = openDatabase({ file });
      try {
        assert.equal(versionOf(upgraded), 2);
        assert.equal(upgraded.prepare('SELECT COUNT(*) AS n FROM patients').get().n, populated ? 1 : 0);
        if (populated) {
          assert.equal(upgraded.prepare('SELECT first_name FROM patients').get().first_name, 'Legacy');
        }
        const index = upgraded.prepare(`SELECT name FROM sqlite_master
          WHERE type = 'index' AND name = 'idx_admissions_patient_date_id'`).get();
        assert.ok(index);
      } finally {
        upgraded.close();
      }
    });
  }
});

test('rejects an unrelated or partially created unversioned schema', () => {
  withDatabaseFile((file) => {
    const foreign = new DatabaseSync(file);
    foreign.exec('CREATE TABLE patients (id INTEGER PRIMARY KEY, name TEXT)');
    foreign.close();
    assert.throws(() => openDatabase({ file }), /Cannot adopt unversioned database: patients/);
    const inspected = new DatabaseSync(file);
    try {
      assert.equal(versionOf(inspected), 0);
      assert.equal(inspected.prepare("SELECT COUNT(*) AS n FROM sqlite_master WHERE type = 'table'").get().n, 1);
    } finally {
      inspected.close();
    }
  });
});

test('rolls back failed migrations without changing the previous version or schema', () => {
  const db = openDatabase({ file: ':memory:', seed: false });
  try {
    const badMigration = { version: 3, sql: `
      CREATE TABLE accidental (id INTEGER PRIMARY KEY);
      INSERT INTO missing_table VALUES (1);
    ` };
    assert.throws(() => migrateDatabase(db, {
      migrationPlan: [...migrations, badMigration],
    }), /no such table: missing_table/);
    assert.equal(versionOf(db), 2);
    assert.equal(db.prepare("SELECT COUNT(*) AS n FROM sqlite_master WHERE name = 'accidental'").get().n, 0);
    assert.equal(db.prepare('PRAGMA foreign_keys').get().foreign_keys, 1);
  } finally {
    db.close();
  }
});

test('refuses a database from a newer app without writing to it', () => {
  withDatabaseFile((file) => {
    const future = new DatabaseSync(file);
    future.exec('CREATE TABLE future_data (value TEXT); PRAGMA user_version = 99;');
    future.close();
    assert.throws(() => openDatabase({ file }), /schema v99 is newer than supported v2/);
    const inspected = new DatabaseSync(file);
    try {
      assert.equal(versionOf(inspected), 99);
      assert.ok(inspected.prepare("SELECT name FROM sqlite_master WHERE name = 'future_data'").get());
    } finally {
      inspected.close();
    }
  });
});

test('patient timeline indexes support date-ordered relational lookups', () => {
  const db = openDatabase({ file: ':memory:', seed: false });
  try {
    const queries = [
      ['admissions', 'admitted_on', 'idx_admissions_patient_date_id'],
      ['diagnoses', 'diagnosed_on', 'idx_diagnoses_patient_date'],
      ['treatments', 'performed_on', 'idx_treatments_patient_date'],
    ];
    for (const [table, dateColumn, expectedIndex] of queries) {
      const details = db.prepare(`EXPLAIN QUERY PLAN SELECT id FROM ${table}
        WHERE patient_id = ? AND ${dateColumn} BETWEEN ? AND ? ORDER BY ${dateColumn}`)
        .all(1, '2026-01-01', '2026-12-31').map((row) => row.detail).join(' ');
      assert.match(details, new RegExp(`USING (COVERING )?INDEX ${expectedIndex}`));
    }
    assert.throws(() => db.prepare(`INSERT INTO admissions (patient_id, ward_id, admitted_on)
      VALUES (999, 999, '2026-01-01')`).run(), /FOREIGN KEY constraint failed/);
  } finally {
    db.close();
  }
});

test('seeds a new database once and preserves an intentionally cleared database', () => {
  const directory = mkdtempSync(join(tmpdir(), 'wardline-seed-'));
  const file = join(directory, 'hospital.sqlite');
  let db;

  try {
    db = openDatabase({ file });
    assert.equal(db.prepare('SELECT COUNT(*) AS total FROM patients').get().total, 32);

    // Clear records in foreign-key dependency order, as a local demo reset
    // might do when a user wants to start with an empty workspace.
    for (const table of [
      'treatments', 'nurse_assignments', 'diagnoses', 'discharges', 'admissions',
      'physician_schedules', 'nurse_shifts', 'patients', 'physicians', 'nurses',
      'wards', 'illnesses', 'medicines',
    ]) {
      db.exec(`DELETE FROM ${table}`);
    }
    db.close();
    db = null;

    db = openDatabase({ file });
    assert.equal(db.prepare('SELECT COUNT(*) AS total FROM patients').get().total, 0);
    assert.equal(db.prepare('SELECT COUNT(*) AS total FROM wards').get().total, 0);
  } finally {
    db?.close();
    rmSync(directory, { recursive: true, force: true });
  }
});
