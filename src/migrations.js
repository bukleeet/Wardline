import { readFileSync } from 'node:fs';

// Never edit a released migration. Add the next numbered SQL file instead.
export const migrations = Object.freeze([
  { version: 1, name: 'initial', sql: readFileSync(new URL('./migrations/001_initial.sql', import.meta.url), 'utf8') },
  { version: 2, name: 'patient timeline indexes', sql: readFileSync(new URL('./migrations/002_patient_timeline_indexes.sql', import.meta.url), 'utf8') },
]);

// A version-zero database with these exact tables/columns is a pre-migration
// Wardline installation. Count zero rows as valid: users can clear demo data.
export const legacyColumns = Object.freeze({
  patients: ['id', 'first_name', 'last_name', 'date_of_birth', 'sex', 'contact_no'],
  physicians: ['id', 'first_name', 'last_name', 'specialty', 'contact_no'],
  nurses: ['id', 'first_name', 'last_name', 'contact_no'],
  wards: ['id', 'name', 'floor', 'category', 'capacity'],
  illnesses: ['id', 'name', 'category', 'description'],
  medicines: ['id', 'name', 'category', 'unit', 'stock_qty', 'reorder_level'],
  admissions: ['id', 'patient_id', 'ward_id', 'admitted_on', 'reason'],
  discharges: ['id', 'admission_id', 'discharged_on', 'disposition', 'notes'],
  diagnoses: ['id', 'patient_id', 'physician_id', 'illness_id', 'diagnosed_on', 'notes'],
  treatments: ['id', 'patient_id', 'diagnosis_id', 'physician_id', 'nurse_id', 'medicine_id', 'performed_on', 'procedure_name', 'notes'],
  nurse_assignments: ['id', 'nurse_id', 'patient_id', 'assigned_on', 'ends_on'],
  physician_schedules: ['id', 'physician_id', 'weekday', 'starts_at', 'ends_at'],
  nurse_shifts: ['id', 'nurse_id', 'weekday', 'starts_at', 'ends_at'],
});
