-- Cover patient timeline filters used in operational analytics and record review.
CREATE INDEX IF NOT EXISTS idx_admissions_patient_date_id
  ON admissions(patient_id, admitted_on, id);
CREATE INDEX IF NOT EXISTS idx_diagnoses_patient_date
  ON diagnoses(patient_id, diagnosed_on);
CREATE INDEX IF NOT EXISTS idx_treatments_patient_date
  ON treatments(patient_id, performed_on);
