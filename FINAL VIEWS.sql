USE dbhospital_final;

CREATE OR REPLACE VIEW physicians_workload_view AS
SELECT 
    d.patient_id,
	p.p_lastname,
    p.p_firstname,
    t.treatment_date,
    ps.physician_id,
    i.illness_name,
    t.performed_by,
    t.treatment_procedure,
    t.assignedPhysician_id
FROM treatment t
JOIN diagnosis d ON t.diagnosis_id = d.diagnosis_id
JOIN patient p ON d.patient_id = p.patient_id
JOIN illness i ON d.illness_id = i.illness_id
JOIN physician_schedule ps ON d.physicianSchedule_id = ps.physicianSchedule_id;

CREATE OR REPLACE VIEW illness_occurrence_view AS
SELECT
	d.patient_id,
    i.illness_name,
    t.treatment_date,
    p.p_lastname,
    p.p_firstname
FROM diagnosis d
JOIN illness i ON d.illness_id = i.illness_id
JOIN patient p ON d.patient_id = p.patient_id
JOIN treatment t ON d.diagnosis_id = t.diagnosis_id;

CREATE OR REPLACE VIEW treatment_stats_view AS
SELECT
	d.patient_id,
    t.treatment_id,
    t.treatment_procedure,
    t.treatment_date,
    p.p_lastname,
    p.p_firstname
FROM diagnosis d
JOIN treatment t ON d.diagnosis_id = t.diagnosis_id
JOIN patient p ON d.patient_id = p.patient_id;

CREATE OR REPLACE VIEW admission_rate_view AS
SELECT
	d.patient_id,
    d.diagnosis_date,
	p.p_lastname,
    p.p_firstname
FROM diagnosis d
JOIN patient p ON d.patient_id = p.patient_id;
    
	