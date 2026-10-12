-- Insert Values
USE dbhospital_final;

/*
Patient ID starts at 1001
PhysicianID starts at 2001
Nurse ID starts at 3001
Ward ID starts at 4001
Medicine ID starts at 5001
Illness ID starts at 6001
Physician sched starts at 7001
Nurse shift starts at 8001
*/
-- CORE RECORDS
INSERT INTO patient(patient_id, p_lastname, p_firstname, sex, birth_date, contact_no, p_status)
VALUES
    (1001, 'Santos', 'Maria', 'F', '1990-05-12', '09171234567', 'Active'),
    (1002, 'Reyes', 'Juan', 'M', '1985-09-23', '09987654321', 'Active'),
    (1003, 'Cruz', 'Angela', 'F', '2001-02-14', '09170011223', 'Active'),
    (1004, 'Dela Cruz', 'Mark', 'M', '1998-12-02', '09223334455', 'Active'),
    (1005, 'Garcia', 'Luis', 'M', '1975-03-30', '09175556677', 'Inactive'),
    (1006, 'Torres', 'Sophia', 'F', '2003-07-19', '09334445566', 'Active'),
    (1007, 'Flores', 'Daniel', 'M', '1993-11-08', '09449998877', 'Active'),
    (1008, 'Gonzales', 'Isabel', 'F', '1989-04-25', '09556667788', 'Inactive'),
    (1009, 'Navarro', 'Carlos', 'M', '1996-01-17', '09171112223', 'Active'),
    (10010, 'Lim', 'Patricia', 'F', '2000-10-05', '09221112223', 'Active');

INSERT INTO physician(physician_id, ph_lastname, ph_firstname, specialization, contact_no) VALUES
	(2001, 'Laforteza', 'Sophia', 'Cardiology', '+63 994 133 4589'),
	(2002, 'Jeung', 'Yoonchae', 'Pediatrics', '+63 926 369 7532'),
	(2003, 'Bannerman', 'Manon', 'Dermatology', '+63 939 748 3415'),
	(2004, 'Edwards', 'Martin', 'Neurology', '+63 919 149 2346'),
	(2005, 'Llorente', 'Daniela', 'Psychiatry', '+63 973 188 852'),
	(2006, 'Ahn', 'Geonho', 'Orthopedics', '+63 992 266 293'),
	(2007, 'Rajagopalan', 'Lara', 'Urology', '+63 923 284 0621'),
	(2008, 'Kim', 'Juhoon', 'Oncology', '+63 984 054 0043'),
	(2009, 'Skiendel', 'Lara', 'OB-GYN', '+63 938 279 7640'),
	(2010, 'Valdez', 'Rhea', 'Pulmonology', '+63 927 427 1054');

INSERT INTO nurse(nurse_id, n_firstname, n_lastname, contact_no)
VALUES
	(3001, 'Maria', 'Santos', '+63 917 555 1234'),
    (3002, 'Jose', 'Reyes', '+63 998 777 5678'),
    (3003, 'Elena', 'Cruz', '+63 920 111 9012'),
    (3004, 'Ricardo', 'Gonzalez', '+63 932 444 3456'),
    (3005, 'Sofia', 'Garcia', '+63 947 888 7890'),
    (3006, 'Antonio', 'Lim', '+63 908 222 1100'),
    (3007, 'Katrina', 'Ramos', '+63 999 666 4321'),
    (3008, 'Paolo', 'Pascual', '+63 976 333 8765'),
    (3009, 'Isabella', 'Torres', '+63 918 000 5432'),
    (30010, 'Gabriel', 'Diaz', '+63 925 999 2109');

INSERT INTO ward(ward_id, ward_number, floor, w_status)
VALUES 
	(4001, 101, '1st Floor', 'Available'),
	(4002, 102, '1st Floor', 'Available'),
	(4003, 103, '1st Floor', 'Available'),
	(4004, 104, '1st Floor', 'Available'),
	(4005, 201, '2nd Floor', 'Available'),
    (4006, 202, '2nd Floor', 'Available'),
    (4007, 203, '2nd Floor', 'Available'),
    (4008, 204, '2nd Floor', 'Available'),
    (4009, 301, '3rd Floor', 'Available'),
    (40010, 302, '3rd Floor', 'Available'),
    (40011, 303, '3rd Floor', 'Available'),
    (40012, 304, '3rd Floor', 'Available');

INSERT INTO medicine(medicine_id, medicine_name, stock_qty)
VALUES
    (5001, 'Paracetamol 500mg', 120),
    (5002, 'Amoxicillin 500mg', 80),
    (5003, 'Ibuprofen 400mg', 150),
    (5004, 'Metformin 850mg', 60),
    (5005, 'Losartan 50mg', 90),
    (5006, 'Salbutamol Inhaler', 40),
    (5007, 'Omeprazole 20mg', 100),
    (5008, 'Cetirizine 10mg', 200),
    (5009, 'Hydration IV Fluid (PNSS)', 50),
    (50010, 'Insulin Glargine', 30);

INSERT INTO illness(illness_id, illness_name, category, illness_description)
VALUES
	(6001, 'Influenza', 'Infectious', 'A contagious respiratory illness caused by influenza viruses'),
    (6002, 'Type 2 Diabetes', 'Metabolic', 'A chronic condition where the body becomes resistant to insulin'),
    (6003, 'Hypertension', 'Cardiovascular', 'Persistent high blood pressure that can lead to heart attack, stroke, and kidney damage'),
    (6004, 'Asthma', 'Respiratory', 'A chronic lung condition that causes airway inflammation and narrowing'),
    (6005, 'Osteoarthritis', 'Musculoskeletal', 'Degeneration of joint cartilage causing pain, stiffness, and reduced mobility'),
    (6006, 'Kidney Disease', 'Renal', 'Gradual loss of kidney function over time' ),
    (6007, 'Anxiety Disorder', 'Mental Health', 'A condition marked by excessive worry, tension, and physical symptoms like restlessness or rapid heartbeat' ),
    (6008, 'Migraine', 'Neurological', 'A recurring headache disorder characterized by intense throbbing pain'),
    (6009, 'Pneumonia', 'Infectious', 'An infection that inflames the air sacs in the lungs, causing cough, fever, chest pain, and difficulty breathing'),
    (60010, 'Irritable Bowel Syndrome', 'Gastrointestinal', 'A functional digestive disorder causing abdominal pain, bloating, and altered bowel habits without structural damage');

INSERT INTO physician_schedule(physicianSchedule_id, physician_id, schedule_day, start_time, end_time) 
VALUES
	(7001, 2001, 'Monday', '08:00', '12:00'),
	(7002, 2002, 'Tuesday', '09:00', '15:00'),
	(7003, 2003, 'Wednesday', '08:00', '14:00'),
	(7004, 2004, 'Thursday', '10:00', '16:00'),
	(7005, 2005, 'Friday', '08:00', '12:00'),
	(7006, 2006, 'Monday', '13:00', '17:00'),
	(7007, 2007, 'Tuesday', '10:00', '14:00'),
	(7008, 2008, 'Wednesday', '09:00', '13:00'),
	(7009, 2009, 'Thursday', '08:00', '12:00'),
	(7010, 2010, 'Friday', '13:00:00', '18:00');

INSERT INTO nurse_shift(nurseShift_id, nurse_id, shift_day, start_time, end_time)
VALUES
	(8001, 3001, 'Monday', '08:00',	'10:00'),
    (8002, 3002, 'Tuesday', '14:00', '16:00'),
    (8003, 3003, 'Wednesday', '21:00', '23:00'),
    (8004, 3004, 'Thursday', '01:00', '03:00'),
    (8005, 3005, 'Friday', '12:00', '17:00'),
    (8006, 3006, 'Monday', '05:00', '06:00'),
    (8007, 3007, 'Tuesday', '18:00', '20:00'),
    (8008, 3008, 'Wednesday', '09:00', '13:00'),
    (8009, 3009, 'Thursday', '22:00', '00:00'),
    (80010, 30010, 'Friday', '04:00', '07:00');

-- TRANSACTIONS
/*
Diagnosis starts at 101
Nurse assignment starts at 201
Admission starts at 301
Treatment starts at 401
Discharge starts at 501
*/
INSERT INTO diagnosis(diagnosis_id, patient_id, physicianSchedule_id, illness_id, diagnosis_date, notes)
VALUES
	(101, 1001, 7001, 6001, '2029-06-11', 'Acute respiratory viral illness. Symptoms: fever, cough, body aches. Isolate and rest advised.'),
    (102, 1002, 7002, 6002, '2026-03-31', 'Chronic metabolic disorder. Body is insulin resistant. Requires diet, exercise, and glucose monitoring.'),
    (103, 1003, 7003, 6003, '2028-11-22', 'Persistent high blood pressure. Risk of $\text{MI/CVA}$. Start/adjust $\text{BP}$ meds and lifestyle changes.'),
    (104, 1004, 7004, 6004, '2027-01-28', 'Chronic airway inflammation. Presents with wheezing and dyspnea. Managed with bronchodilators/corticosteroids.'),
    (105, 1005, 7005, 6005, '2030-08-09', 'Joint cartilage degeneration causing $\text{pain/stiffness}$. Focus on $\text{pain}$ management and $\text{PT}$.'),
    (106, 1006, 7006, 6006, '2032-12-06', 'Progressive loss of renal function. Monitor $\text{GFR}$ and $\text{Cr}$. Address underlying causes.'),
    (107, 1007, 7007, 6007, '2029-01-09', 'Excessive worry/tension. Physical symptoms present. Recommend therapy and pharmacotherapy.'),
    (108, 1008, 7008, 6008, '2031-05-21', 'Recurrent, throbbing headache disorder. Triggers identified. Acute and prophylactic treatment initiated.'),
    (109, 1009, 7009, 6009, '2026-10-15', 'Lung infection/inflammation. Presents with $\text{fever/cough}$. $\text{CXR}$ ordered. Initiate antibiotics.'),
    (110, 1010, 7010, 6010, '2028-04-14', 'Functional $\text{GI}$ disorder; $\text{abd pain/bloating/altered}$ $\text{bowel habits}$. Symptom management via diet/meds.');
    
INSERT INTO admission(admission_id, patient_id, ward_id, admission_date)
VALUES
	(301, 1001, 4001, '2029-06-13'),
    (302, 1002, 4002, '2026-04-02'),
    (303, 1003, 4003, '2028-11-24'),
    (304, 1004, 4004, '2027-01-30'),
    (305, 1005, 4005, '2030-08-11'),
    (306, 1006, 4006, '2032-12-08'),
    (307, 1007, 4007, '2029-01-11'),
    (308, 1008, 4008, '2031-05-23'),
    (309, 1009, 4009, '2026-10-17'),
    (310, 1010, 4010, '2028-04-16');
    