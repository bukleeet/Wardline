INSERT INTO nurse(nurse_id, n_firstname, n_lastname, contact_no)
VALUES (1001, 'Emil', 'Castagnier', '+63 9638215147');

INSERT INTO nurse_shift(nurseShift_id, nurse_id, shift_day, start_time, end_time)
VALUES(2, 1001, 'Monday', '08:00', '20:00');	 

INSERT INTO patient(patient_id, p_lastname, p_firstname, contact_no)
VALUES (100, 'Mikasa', 'Ackerman', '+63 9163257790');

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
VALUES(6001, 100, 3, 3001, '2025-09-11', 'Monitor patient closely');

INSERT INTO nurse_assignment(nurseAssignment_id, nurseShift_id, patient_id, date_assigned, assigned_until)
VALUES (7001, 2, 100, '2025-11-09', null);

INSERT INTO admission(admission_id, patient_id, ward_id, admission_date)
VALUES (8001, 100, 501, '2025-11-09');

INSERT INTO treatment(treatment_id, nurseAssignment_id, diagnosis_id, medicine_id, treatment_date, treatment_procedure, remarks)
VALUES (9001, 7001, 6001, 4001, '2025-11-09', 'Administered medicine', 'See if patient condition alleviates');

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






