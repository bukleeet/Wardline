import { DatabaseSync } from 'node:sqlite';
import { mkdirSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { seedDatabase } from './seed.js';

const schema = `
  PRAGMA foreign_keys = ON;
  CREATE TABLE IF NOT EXISTS patients (
    id INTEGER PRIMARY KEY, first_name TEXT NOT NULL, last_name TEXT NOT NULL,
    date_of_birth TEXT NOT NULL, sex TEXT NOT NULL CHECK (sex IN ('F','M')),
    contact_no TEXT NOT NULL
  );
  CREATE TABLE IF NOT EXISTS physicians (
    id INTEGER PRIMARY KEY, first_name TEXT NOT NULL, last_name TEXT NOT NULL,
    specialty TEXT NOT NULL, contact_no TEXT NOT NULL
  );
  CREATE TABLE IF NOT EXISTS nurses (
    id INTEGER PRIMARY KEY, first_name TEXT NOT NULL, last_name TEXT NOT NULL,
    contact_no TEXT NOT NULL
  );
  CREATE TABLE IF NOT EXISTS wards (
    id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE, floor TEXT NOT NULL,
    category TEXT NOT NULL, capacity INTEGER NOT NULL CHECK (capacity > 0)
  );
  CREATE TABLE IF NOT EXISTS illnesses (
    id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE, category TEXT NOT NULL,
    description TEXT
  );
  CREATE TABLE IF NOT EXISTS medicines (
    id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE, category TEXT NOT NULL,
    unit TEXT NOT NULL, stock_qty INTEGER NOT NULL CHECK (stock_qty >= 0),
    reorder_level INTEGER NOT NULL CHECK (reorder_level >= 0)
  );
  CREATE TABLE IF NOT EXISTS admissions (
    id INTEGER PRIMARY KEY, patient_id INTEGER NOT NULL REFERENCES patients(id) ON DELETE RESTRICT,
    ward_id INTEGER NOT NULL REFERENCES wards(id) ON DELETE RESTRICT,
    admitted_on TEXT NOT NULL, reason TEXT
  );
  CREATE TABLE IF NOT EXISTS discharges (
    id INTEGER PRIMARY KEY, admission_id INTEGER NOT NULL UNIQUE REFERENCES admissions(id) ON DELETE RESTRICT,
    discharged_on TEXT NOT NULL, disposition TEXT NOT NULL, notes TEXT
  );
  CREATE TABLE IF NOT EXISTS diagnoses (
    id INTEGER PRIMARY KEY, patient_id INTEGER NOT NULL REFERENCES patients(id) ON DELETE RESTRICT,
    physician_id INTEGER NOT NULL REFERENCES physicians(id) ON DELETE RESTRICT,
    illness_id INTEGER NOT NULL REFERENCES illnesses(id) ON DELETE RESTRICT,
    diagnosed_on TEXT NOT NULL, notes TEXT
  );
  CREATE TABLE IF NOT EXISTS treatments (
    id INTEGER PRIMARY KEY, patient_id INTEGER NOT NULL REFERENCES patients(id) ON DELETE RESTRICT,
    diagnosis_id INTEGER REFERENCES diagnoses(id) ON DELETE RESTRICT,
    physician_id INTEGER REFERENCES physicians(id) ON DELETE RESTRICT,
    nurse_id INTEGER REFERENCES nurses(id) ON DELETE RESTRICT,
    medicine_id INTEGER REFERENCES medicines(id) ON DELETE RESTRICT,
    performed_on TEXT NOT NULL, procedure_name TEXT NOT NULL, notes TEXT
  );
  CREATE TABLE IF NOT EXISTS nurse_assignments (
    id INTEGER PRIMARY KEY, nurse_id INTEGER NOT NULL REFERENCES nurses(id) ON DELETE RESTRICT,
    patient_id INTEGER NOT NULL REFERENCES patients(id) ON DELETE RESTRICT,
    assigned_on TEXT NOT NULL, ends_on TEXT
  );
  CREATE TABLE IF NOT EXISTS physician_schedules (
    id INTEGER PRIMARY KEY, physician_id INTEGER NOT NULL REFERENCES physicians(id) ON DELETE RESTRICT,
    weekday TEXT NOT NULL, starts_at TEXT NOT NULL, ends_at TEXT NOT NULL,
    UNIQUE(physician_id, weekday)
  );
  CREATE TABLE IF NOT EXISTS nurse_shifts (
    id INTEGER PRIMARY KEY, nurse_id INTEGER NOT NULL REFERENCES nurses(id) ON DELETE RESTRICT,
    weekday TEXT NOT NULL, starts_at TEXT NOT NULL, ends_at TEXT NOT NULL,
    UNIQUE(nurse_id, weekday)
  );
  CREATE INDEX IF NOT EXISTS idx_admissions_patient ON admissions(patient_id);
  CREATE INDEX IF NOT EXISTS idx_admissions_date ON admissions(admitted_on);
  CREATE INDEX IF NOT EXISTS idx_discharges_admission ON discharges(admission_id);
  CREATE INDEX IF NOT EXISTS idx_diagnoses_patient ON diagnoses(patient_id);
  CREATE INDEX IF NOT EXISTS idx_treatments_date ON treatments(performed_on);
`;

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

export function openDatabase({ file = process.env.HOSPITAL_DB_PATH || 'data/hospital.sqlite', seed = true } = {}) {
  if (file !== ':memory:') mkdirSync(dirname(resolve(file)), { recursive: true });
  const db = new DatabaseSync(file);
  try {
    // Seed only a new database. Existing demo records can be intentionally
    // deleted, and an empty patient table must remain empty after a restart.
    const isNew = !db.prepare("SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = 'patients'").get();
    db.exec(schema);
    if (seed && isNew) {
      transaction(db, () => seedDatabase(db));
    }
    return db;
  } catch (error) {
    db.close();
    throw error;
  }
}
