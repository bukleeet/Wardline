-- CORE RECORDS --
CREATE TABLE patient(
	patient_id INT AUTO_INCREMENT PRIMARY KEY,
    p_lastname VARCHAR(50) NOT NULL, 
    p_firstname VARCHAR(50) NOT NULL,
    sex ENUM('M', 'F') NOT NULL,
    birth_date DATE NOT NULL,
    contact_no VARCHAR(30) NOT NULL,	
    p_status ENUM('Admitted', 'Discharged') NOT NULL
);


CREATE TABLE nurse(
	nurse_id INT AUTO_INCREMENT PRIMARY KEY,
    n_firstname VARCHAR(50) NOT NULL,
    n_lastname VARCHAR(50) NOT NULL,
	contact_no VARCHAR(30) NOT NULL 
);

CREATE TABLE nurse_shift(
	nurseShift_id INT AUTO_INCREMENT PRIMARY KEY,
	nurse_id INT NOT NULL,
    shift_day ENUM('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    FOREIGN KEY (nurse_id) REFERENCES nurse(nurse_id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE (nurse_id, shift_day)
);

CREATE TABLE physician(
	physician_id INT AUTO_INCREMENT PRIMARY KEY,
    ph_firstname VARCHAR(50) NOT NULL,
    ph_lastname VARCHAR(50) NOT NULL,
	contact_no VARCHAR(30) NOT NULL,
    specialization VARCHAR(80) NOT NULL
);

CREATE TABLE physician_schedule(
	physicianSchedule_id INT AUTO_INCREMENT PRIMARY KEY,
	physician_id INT NOT NULL,
    schedule_day ENUM('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    FOREIGN KEY (physician_id) REFERENCES physician(physician_id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE (physician_id, schedule_day)
);

CREATE TABLE ward(
	ward_id INT AUTO_INCREMENT PRIMARY KEY, 
    ward_number INT NOT NULL,
    floor VARCHAR(20) NOT NULL,
    w_status ENUM('Available', 'Occupied') NOT NULL
);

CREATE TABLE illness(
	illness_id INT AUTO_INCREMENT PRIMARY KEY,
    illness_name VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,
    illness_description VARCHAR(255) NOT NULL
);

CREATE TABLE medicine(
	medicine_id INT AUTO_INCREMENT PRIMARY KEY,
    medicine_name VARCHAR(100) NOT NULL,
    stock_qty INT NOT NULL
);

-- TRANSACTIONS --
CREATE TABLE diagnosis(
	diagnosis_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    physicianSchedule_id INT NOT NULL,
    illness_id INT NOT NULL, 
    diagnosis_date DATE NOT NULL, 
    notes VARCHAR (255) NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES patient(patient_id) ON DELETE RESTRICT ON UPDATE CASCADE, 
    FOREIGN KEY (physicianSchedule_id) REFERENCES physician_schedule(physicianSchedule_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (illness_id) REFERENCES illness(illness_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE nurse_assignment(
	nurseAssignment_id INT AUTO_INCREMENT PRIMARY KEY,
    nurseShift_id INT NOT NULL,
    patient_id INT NOT NULL,
    date_assigned DATE,
    assigned_until DATE,
	FOREIGN KEY (nurseShift_id) REFERENCES nurse_shift(nurseShift_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES patient(patient_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    UNIQUE (nurseShift_id, patient_id, date_assigned) 
);

CREATE TABLE admission(
	admission_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    ward_id INT NOT NULL,
    admission_date DATE NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES patient(patient_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (ward_id) REFERENCES ward(ward_id) ON DELETE RESTRICT ON UPDATE CASCADE
);
	
CREATE TABLE treatment(
	treatment_id INT AUTO_INCREMENT PRIMARY KEY,
    nurseAssignment_id INT NOT NULL,
    diagnosis_id INT NOT NULL,
    medicine_id INT NULL,
    assignedPhysician_id INT,
    treatment_date DATE NOT NULL,
    treatment_procedure VARCHAR(100) NOT NULL,
    performed_by ENUM('Nurse', 'Diagnosing Physician', 'Assigned Physician') NOT NULL,
    remarks VARCHAR(255) NOT NULL,
    FOREIGN KEY (nurseAssignment_id) REFERENCES nurse_assignment(nurseAssignment_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (diagnosis_id) REFERENCES diagnosis(diagnosis_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicine(medicine_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (assignedPhysician_id) REFERENCES physician(physician_id) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE discharge(
	discharge_id INT AUTO_INCREMENT PRIMARY KEY,
    admission_id INT NOT NULL,
    discharge_date DATE NOT NULL,
    FOREIGN KEY (admission_id) REFERENCES admission(admission_id) ON DELETE CASCADE ON UPDATE CASCADE 	
);













