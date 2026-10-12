import assert from 'node:assert/strict';
import { mkdtempSync, rmSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { test } from 'node:test';
import { openDatabase } from '../src/database.js';

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
