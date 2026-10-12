CREATE OR REPLACE VIEW physicians_workload_view AS
SELECT 
    d.patient_id,
    t.treatment_date,
    ps.physician_id,
    t.performed_by,
    t.assignedPhysician_id
FROM treatment t
LEFT JOIN diagnosis d ON t.diagnosis_id = d.diagnosis_id
LEFT JOIN physician_schedule ps ON d.physicianSchedule_id = ps.physicianSchedule_id;

CREATE OR REPLACE VIEW illness_occurrence_view AS
SELECT
	d.patient_id,
    i.illness_name,
    t.treatment_date
FROM diagnosis d
LEFT JOIN illness i ON d.illness_id = i.illness_id
LEFT JOIN treatment t ON d.diagnosis_id = t.diagnosis_id;

CREATE OR REPLACE VIEW treatment_stats_view AS
SELECT
	d.patient_id,
    t.treatment_procedure
FROM diagnosis d
LEFT JOIN treatment t ON d.diagnosis_id = t.diagnosis_id;
