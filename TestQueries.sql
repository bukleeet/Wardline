INSERT INTO nurse(nurse_id, n_firstname, n_lastname, contact_no)
VALUES (1001, 'Emil', 'Castagnier', '+63 9638215147');

INSERT INTO nurse_shift(nurseShift_id, nurse_id, shift_day, start_time, end_time)
VALUES(2, 1001, 'Monday', '08:00', '20:00');	 

INSERT INTO patient(patient_id, p_lastname, p_firstname, contact_no)
VALUES (100, 'Mikasa', 'Ackerman', '+63 9163257790');

INSERT INTO patient(p_firstname, p_lastname, contact_no)
VALUES('Levi', 'Ackerman', '+63 9105633254'); 

INSERT INTO physician(physician_id, ph_firstname, ph_lastname, contact_no, specialization)
VALUES(2001, 'Maria', 'Santos', '+63 9171234567', 'Cardiology');

INSERT INTO physician_schedule(physicianSchedule_id, physician_id, schedule_day, start_time, end_time)
VALUES(3, 2001, 'Monday', '08:00', '20:00');

INSERT INTO illness(illness_id, illness_name, category, illness_description)
VALUES (3001, 'Coronary Artery Disease', 'Heart Disease', 'A condition caused by buildup of plaque in the arteries supplying blood to the heart');

INSERT INTO medicine(medicine_id, medicine_name, stock_qty)
VALUES(4001, 'Aspirin', 50);

INSERT INTO ward(ward_id, floor)
VALUES(501, '3rd Floor');

INSERT INTO diagnosis(diagnosis_id, patient_id, physicianSchedule_id, illness_id, diagnosis_date, notes)
VALUES(6001, 101, 3, 3001, '2025-09-11', 'Monitor patient closely');	

INSERT INTO nurse_assignment(nurseAssignment_id, nurseShift_id, patient_id, date_assigned, assigned_until)
VALUES (7001, 2, 101, '2025-11-09', null);

INSERT INTO admission(admission_id, patient_id, ward_id, admission_date)
VALUES (8001, 101, 1, '2025-11-09');

INSERT INTO treatment(treatment_id, nurseAssignment_id, diagnosis_id, medicine_id, treatment_date, treatment_procedure, remarks, performed_by)
VALUES (9001, 7001, 6001, 4001, '2025-11-09', 'Administered medicine', 'See if patient condition alleviates', 'Nurse');



INSERT INTO discharge(discharge_id, admission_id, discharge_date)
VALUES (1, 8001, '2025-11-10');


# -------------
SELECT * FROM patient;
SELECT * FROM nurse;
SELECT * FROM physician;
SELECT * FROM ward;
SELECT * FROM illness;
SELECT * FROM medicine;
SELECT * FROM nurse_shift;
SELECT * FROM physician_schedule;
SELECT * FROM diagnosis;
SELECT * FROM nurse_assignment;
SELECT * FROM admission;
SELECT * FROM treatment;
SELECT * FROM discharge; 

DELETE FROM treatment WHERE treatment_id = 9001;
DELETE FROM discharge WHERE discharge_id = 3;
DELETE FROM admission WHERE admission_id = 8004;

INSERT INTO discharge(discharge_id, admission_id, discharge_date)
VALUES (3, 8002, '2025-11-11');

-- Test Queries --
DELETE FROM nurse WHERE nurse_id = 1001; #should fail (restrict) if nurse assignment exist == SUCCESS

DELETE FROM medicine WHERE medicine_id = 4001; #Should set medicine_id in treatment to NULL (set null) == SUCCESS

DELETE FROM admission WHERE admission_id = 8001; #Should also delete from discharge == SUCCESS

DELETE FROM physician WHERE physician_id = 2001; #Delete a physician still linked to a diagnosis (should RESTRICT) -- this should fail == SUCCESS

INSERT INTO nurse_assignment(nurseAssignment_id, nurseShift_id, patient_id, date_assigned, assigned_until)
VALUES (7002, 2, 100, '2025-11-09', null); #Should fail: same nurseShift_id + patient_id + date_assigned (unique constraint)

DELETE FROM diagnosis WHERE diagnosis_id = 6001; #Delete a diagnosis that is linked to treatment: Should fail because treatment references it (ON DELETE RESTRICT) == SUCCESS

DELETE FROM ward WHERE ward_id = 501; #Delete a ward that has admissions: Should fail (ON DELETE RESTRICT on admission) == SUCCESS

DELETE FROM nurse_shift WHERE nurseShift_id = 2; #Delete a nurse_shift linked to nurse_assignment: Should fail (ON DELETE RESTRICT on nurse_assignment). == SUCCESS

INSERT INTO treatment(nurseAssignment_id, diagnosis_id, treatment_date)
VALUES (9999, 6001, '2025-11-11');#Try inserting treatment with a nonexistent nurseAssignment_id: should fail == SUCCESS

INSERT INTO admission(patient_id, ward_id, admission_date)
VALUES (101, 501, '2025-11-10'); #inserting another patient on the same ward: should fail == SUCCESS

INSERT INTO nurse_assignment(nurseShift_id, patient_id, date_assigned, assigned_until)
VALUES (2, 101, '2025-11-10', null); #Assign the same nurse to different patients on the same shift (should succeed). == SUCCESS

SELECT a.admission_id, a.patient_id, a.ward_id, a.admission_date
FROM admission a
LEFT JOIN discharge d ON a.admission_id = d.admission_id
WHERE d.discharge_id IS NULL; #wala pang discharge == occupied ung ward


SELECT a.admission_id, a.patient_id, a.ward_id, a.admission_date
FROM admission a
LEFT JOIN discharge d ON a.admission_id = d.admission_id
WHERE a.ward_id != 501 AND d.discharge_id IS NULL; #we have to make sure ward_id !-= id when updating an admission record 

INSERT INTO discharge(admission_id, discharge_date)
VALUES (8001, "2025-11-10");

ALTER TABLE treatment
ADD assignedPhysician_id INT,
ADD CONSTRAINT fk_assignedPhysician
FOREIGN KEY (assignedPhysician_id) REFERENCES physician(physician_id) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE treatment
ADD performed_by ENUM('Nurse', 'Diagnosing Physician', 'Assigned Physician');

ALTER TABLE treatment
DROP performed_by;	


ALTER TABLE ward RENAME COLUMN ward_number TO ward_no;

ALTER TABLE ward RENAME COLUMN ward_number TO ward_no;

-- REPORTS --
#Physician Workload Report
SELECT COUNT(DISTINCT d.patient_id) AS TotalPatients
FROM treatment t 
	LEFT JOIN diagnosis d ON t.diagnosis_id = d.diagnosis_id
    LEFT JOIN physician_schedule ps ON d.physicianSchedule_id = ps.physicianSchedule_id
WHERE t.treatment_date = '2025-11-09'
	AND ((t.performed_by = 'Diagnosing Physician' AND ps.physician = 1)
    OR (t.performed_by = 'Assigned Physician' AND t.assignedPhysician_id = 1));



#Viewing patient record, which includes the illness of the patient, treatment record, and physician 
#and nurse record related to that patient, and the ward the patient is occupying
SELECT CONCAT(p.p_lastname, ', ', p.p_firstname) AS PatientName, i.illness_name, t.treatment_procedure, w.ward_no, CONCAT(ph.ph_lastname, ', ', ph.ph_firstname) AS Assigned_Physician, CONCAT(n.n_lastname, ', ', n.n_firstname) AS Assigned_Nurse
FROM patient p 
	LEFT JOIN diagnosis d ON p.patient_id = d.patient_id
    LEFT JOIN illness i ON d.illness_id = i.illness_id
    LEFT JOIN treatment t ON d.diagnosis_id = t.diagnosis_id
	LEFT JOIN nurse_assignment na ON t.nurseAssignment_id = na.nurseAssignment_id
    LEFT JOIN nurse_shift ns ON na.nurseShift_id = ns.nurseShift_id
    LEFT JOIN admission a ON p.patient_id = a.patient_id
    LEFT JOIN ward w ON a.ward_id = w.ward_id
    LEFT JOIN physician_schedule ps ON d.physicianSchedule_id = ps.physicianSchedule_id
    LEFT JOIN physician ph ON ps.physician_id = ph.physician_id
    LEFT JOIN nurse n ON ns.nurse_id = n.nurse_id
WHERE p.patient_id = 101;	

#Viewing an illness record, the patients that have been diagnosed with that illness and the treatment
SELECT 
    i.illness_name,
    d.diagnosis_date,
    CONCAT (p.p_lastname, ', ', p.p_firstname) AS patient_name,
    t.treatment_date,
    t.treatment_procedure
FROM diagnosis d
JOIN patient p ON d.patient_id = p.patient_id
LEFT JOIN treatment t ON d.diagnosis_id = t.diagnosis_id
LEFT JOIN illness i ON d.illness_id = i.illness_id
WHERE d.illness_id = 3001;

SELECT
    m.medicine_name,
    CONCAT(p.p_lastname, ', ', p.p_firstname) AS patient_name,
    i.illness_name,
    CONCAT(ph.ph_lastname, ', ', ph.ph_firstname) AS physician_name,
    t.treatment_date,
    t.treatment_procedure,
    t.remarks
FROM treatment t
    LEFT JOIN medicine m ON t.medicine_id = m.medicine_id
    LEFT JOIN diagnosis d ON t.diagnosis_id = d.diagnosis_id
    LEFT JOIN patient p ON d.patient_id = p.patient_id
    LEFT JOIN illness i ON d.illness_id = i.illness_id
    LEFT JOIN physician_schedule pa ON d.physicianSchedule_id = pa.physicianSchedule_id
    LEFT JOIN physician ph ON pa.physician_id = ph.physician_id
WHERE m.medicine_id = 4001;

SELECT
    CONCAT(ph.ph_lastname, ', ', ph.ph_firstname) AS physician_name,
    ph.specialization,
    CONCAT(p.p_lastname, ', ', p.p_firstname) AS patient_name,
    i.illness_name,
    t.treatment_id,
    t.treatment_date,
    t.treatment_procedure,
    t.remarks
FROM physician ph
    LEFT JOIN treatment t ON ph.physician_id = t.assignedPhysician_id
    LEFT JOIN diagnosis d ON t.diagnosis_id = d.diagnosis_id
    LEFT JOIN patient p ON d.patient_id = p.patient_id
    LEFT JOIN illness i ON d.illness_id = i.illness_id
    LEFT JOIN physician_schedule ps ON d.physicianSchedule_id = ps.physicianSchedule_id
    LEFT JOIN physician dp ON ps.physician_id = dp.physician_id
WHERE (performed_by = 'Assigned Physician' OR performed_by = 'Diagnosing Physician') AND ph.physician_id = 2001;


SELECT
	CONCAT(pd.ph_lastname, ', ', pd.ph_firstname) AS physician_name,
    CONCAT(p.p_lastname, ', ', p.p_firstname) AS patient_name,
    i.illness_name,
    t.treatment_id,
    t.treatment_date,
    t.treatment_procedure
FROM treatment t
LEFT JOIN nurse_assignment na ON t.nurseAssignment_id = na.nurseAssignment_id
LEFT JOIN patient p ON na.patient_id = p.patient_id
LEFT JOIN diagnosis d ON t.diagnosis_id = d.diagnosis_id
LEFT JOIN physician_schedule ps ON d.physicianSchedule_id = ps.physicianSchedule_id
LEFT JOIN physician pd ON ps.physician_id = pd.physician_id
LEFT JOIN physician pa ON t.assignedPhysician_id = pa.physician_id
LEFT JOIN illness i ON d.illness_id = i.illness_id
WHERE t.performed_by LIKE '%Physician%' AND (pa.physician_id = 2001 OR pd.physician_id = 2001);


#WARD:
SELECT
    w.ward_no,
    w.floor,
    CONCAT (p.p_lastname, ', ', p.p_firstname) AS admitted_patient,
    CONCAT (n.n_lastname, ', ', n_firstname) AS assigned_nurse
FROM ward w
LEFT JOIN admission a ON w.ward_id = a.ward_id
LEFT JOIN  patient p ON a.patient_id = p.patient_id
LEFT JOIN  nurse_assignment na ON na.patient_id = p.patient_id
LEFT JOIN  nurse_shift ns ON na.nurseShift_id = ns.nurseShift_id
LEFT JOIN  nurse n ON ns.nurse_id = n.nurse_id
WHERE w.ward_id = ?;

#Viewing a specific nurse record and the list of patients assigned to that nurse, 
#the wards that the nurse is handling, and treatments involved

SELECT CONCAT(n.n_lastname, ', ', n.n_firstname) AS nurse_name,
	   CONCAT(p.p_lastname, ', ', p.p_firstname) AS patient_name,
       w.ward_no, w.floor,
	   t.treatment_procedure,
       t.treatment_date
FROM nurse n
	LEFT JOIN nurse_shift ns ON n.nurse_id = ns.nurse_id
    LEFT JOIN nurse_assignment na ON ns.nurseShift_id = na.nurseShift_id
    LEFT JOIN patient p ON na.patient_id = p.patient_id
    LEFT JOIN treatment t ON na.nurseAssignment_id = t.nurseAssignment_id
    LEFT JOIN admission a ON p.patient_id = a.patient_id
    LEFT JOIN ward w ON a.ward_id = w.ward_id
WHERE  n.nurse_id = 1001;










