import { DatabaseSync } from 'node:sqlite';
import { mkdirSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { legacyColumns, migrations as releasedMigrations } from './migrations.js';
import { seedDatabase } from './seed.js';

export function transaction(db, action) {
  db.exec('BEGIN IMMEDIATE');
  try {
    const result = action();
    db.exec('COMMIT');
    return result;
  } catch (error) {
    db.exec('ROLLBACK');
    throw error;
  }
}

function verifyLegacySchema(db) {
  for (const [table, expected] of Object.entries(legacyColumns)) {
    const actual = db.prepare(`PRAGMA table_info("${table}")`).all().map((column) => column.name);
    if (actual.join(',') !== expected.join(',')) {
      throw new Error(`Cannot adopt unversioned database: ${table} does not match the Wardline schema`);
    }
  }
}

/**
 * Apply ordered SQL upgrades atomically. SQLite's user_version is updated in
 * the same transaction as the DDL and initial synthetic data.
 * `migrationPlan` permits testing a failing future migration in isolation.
 */
export function migrateDatabase(db, { seed = false, migrationPlan = releasedMigrations } = {}) {
  for (const [index, migration] of migrationPlan.entries()) {
    if (migration.version !== index + 1 || typeof migration.sql !== 'string') {
      throw new Error('Migrations must have consecutive versions beginning with 1');
    }
  }
  if (migrationPlan.length === 0) throw new Error('No migrations registered');

  db.exec('PRAGMA foreign_keys = ON');
  const version = db.prepare('PRAGMA user_version').get().user_version;
  const latest = migrationPlan.at(-1).version;
  if (version > latest) {
    throw new Error(`Database schema v${version} is newer than supported v${latest}`);
  }

  // A version-zero Wardline database already has all 13 tables, even if its
  // user removed every record. Presence of any schema object disqualifies a
  // database from first-run seeding.
  const existingObjects = db.prepare(`
    SELECT name FROM sqlite_master
    WHERE type IN ('table', 'view', 'index', 'trigger') AND name NOT LIKE 'sqlite_%'
  `).all();
  const isNew = version === 0 && existingObjects.length === 0;

  transaction(db, () => {
    let applied = version;
    if (version === 0 && !isNew) {
      verifyLegacySchema(db);
      // Restore any missing baseline indexes without modifying existing rows.
      db.exec(migrationPlan[0].sql);
      db.exec('PRAGMA user_version = 1');
      applied = 1;
    }

    for (const migration of migrationPlan) {
      if (migration.version <= applied) continue;
      db.exec(migration.sql);
      db.exec(`PRAGMA user_version = ${migration.version}`);
    }

    if (seed && isNew) seedDatabase(db);
  });

  return { isNew, version: latest };
}

export function openDatabase({ file = process.env.HOSPITAL_DB_PATH || 'data/hospital.sqlite', seed = true } = {}) {
  if (file !== ':memory:') mkdirSync(dirname(resolve(file)), { recursive: true });
  const db = new DatabaseSync(file);
  try {
    migrateDatabase(db, { seed });
    return db;
  } catch (error) {
    db.close();
    throw error;
  }
}
