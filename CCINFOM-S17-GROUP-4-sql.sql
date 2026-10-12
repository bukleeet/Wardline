-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: dbhospital_final
-- ------------------------------------------------------
-- Server version	8.0.37

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admission`
--

DROP TABLE IF EXISTS `admission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admission` (
  `admission_id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int NOT NULL,
  `ward_id` int NOT NULL,
  `admission_date` date NOT NULL,
  PRIMARY KEY (`admission_id`),
  KEY `patient_id` (`patient_id`),
  KEY `ward_id` (`ward_id`),
  CONSTRAINT `admission_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `admission_ibfk_2` FOREIGN KEY (`ward_id`) REFERENCES `ward` (`ward_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=311 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admission`
--

LOCK TABLES `admission` WRITE;
/*!40000 ALTER TABLE `admission` DISABLE KEYS */;
INSERT INTO `admission` VALUES (301,1001,4001,'2025-06-11'),(302,1002,4002,'2025-03-31'),(303,1003,4003,'2025-11-02'),(304,1004,4004,'2025-01-28'),(305,1005,4005,'2025-08-09'),(306,1006,4006,'2025-12-06'),(307,1007,4007,'2025-01-09'),(308,1008,4008,'2025-05-21'),(309,1009,4009,'2025-10-15'),(310,1010,4010,'2025-04-14');
/*!40000 ALTER TABLE `admission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `admission_rate_view`
--

DROP TABLE IF EXISTS `admission_rate_view`;
/*!50001 DROP VIEW IF EXISTS `admission_rate_view`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `admission_rate_view` AS SELECT 
 1 AS `patient_id`,
 1 AS `diagnosis_date`,
 1 AS `p_lastname`,
 1 AS `p_firstname`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `diagnosis`
--

DROP TABLE IF EXISTS `diagnosis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `diagnosis` (
  `diagnosis_id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int NOT NULL,
  `physicianSchedule_id` int NOT NULL,
  `illness_id` int NOT NULL,
  `diagnosis_date` date NOT NULL,
  `notes` varchar(255) NOT NULL,
  PRIMARY KEY (`diagnosis_id`),
  KEY `patient_id` (`patient_id`),
  KEY `physicianSchedule_id` (`physicianSchedule_id`),
  KEY `illness_id` (`illness_id`),
  CONSTRAINT `diagnosis_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `diagnosis_ibfk_2` FOREIGN KEY (`physicianSchedule_id`) REFERENCES `physician_schedule` (`physicianSchedule_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `diagnosis_ibfk_3` FOREIGN KEY (`illness_id`) REFERENCES `illness` (`illness_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `diagnosis`
--

LOCK TABLES `diagnosis` WRITE;
/*!40000 ALTER TABLE `diagnosis` DISABLE KEYS */;
INSERT INTO `diagnosis` VALUES (101,1001,7001,6001,'2025-06-11','Acute respiratory viral illness. Symptoms: fever, cough, body aches. Isolate and rest advised.'),(102,1002,7002,6002,'2025-03-31','Chronic metabolic disorder. Body is insulin resistant. Requires diet, exercise, and glucose monitoring.'),(103,1003,7003,6003,'2025-11-22','Persistent high blood pressure. Risk of $	ext{MI/CVA}$. Start/adjust $	ext{BP}$ meds and lifestyle changes.'),(104,1004,7004,6004,'2025-01-28','Chronic airway inflammation. Presents with wheezing and dyspnea. Managed with bronchodilators/corticosteroids.'),(105,1005,7005,6005,'2025-08-09','Joint cartilage degeneration causing $	ext{pain/stiffness}$. Focus on $	ext{pain}$ management and $	ext{PT}$.'),(106,1006,7006,6006,'2025-12-06','Progressive loss of renal function. Monitor $	ext{GFR}$ and $	ext{Cr}$. Address underlying causes.'),(107,1007,7007,6007,'2025-01-09','Excessive worry/tension. Physical symptoms present. Recommend therapy and pharmacotherapy.'),(108,1008,7008,6008,'2025-05-21','Recurrent, throbbing headache disorder. Triggers identified. Acute and prophylactic treatment initiated.'),(109,1009,7009,6009,'2025-10-15','Lung infection/inflammation. Presents with ordered. Initiate antibiotics.'),(110,1010,7010,6010,'2025-04-14','Functional disorder; {abd pain/bloating/altered} Symptom management via diet/meds.');
/*!40000 ALTER TABLE `diagnosis` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `discharge`
--

DROP TABLE IF EXISTS `discharge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discharge` (
  `discharge_id` int NOT NULL AUTO_INCREMENT,
  `admission_id` int NOT NULL,
  `discharge_date` date NOT NULL,
  PRIMARY KEY (`discharge_id`),
  KEY `admission_id` (`admission_id`),
  CONSTRAINT `discharge_ibfk_1` FOREIGN KEY (`admission_id`) REFERENCES `admission` (`admission_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=511 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discharge`
--

LOCK TABLES `discharge` WRITE;
/*!40000 ALTER TABLE `discharge` DISABLE KEYS */;
INSERT INTO `discharge` VALUES (501,301,'2025-06-13'),(502,302,'2025-04-02'),(503,303,'2025-11-04'),(504,304,'2025-01-30'),(505,305,'2025-08-11'),(506,306,'2025-12-14'),(507,307,'2025-01-11'),(508,308,'2025-05-07'),(509,309,'2025-10-11'),(510,310,'2025-04-16');
/*!40000 ALTER TABLE `discharge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `illness`
--

DROP TABLE IF EXISTS `illness`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `illness` (
  `illness_id` int NOT NULL AUTO_INCREMENT,
  `illness_name` varchar(100) NOT NULL,
  `category` varchar(100) NOT NULL,
  `illness_description` varchar(255) NOT NULL,
  PRIMARY KEY (`illness_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6011 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `illness`
--

LOCK TABLES `illness` WRITE;
/*!40000 ALTER TABLE `illness` DISABLE KEYS */;
INSERT INTO `illness` VALUES (6001,'Influenza','Infectious','A contagious respiratory illness caused by influenza viruses'),(6002,'Type 2 Diabetes','Metabolic','A chronic condition where the body becomes resistant to insulin'),(6003,'Hypertension','Cardiovascular','Persistent high blood pressure that can lead to heart attack, stroke, and kidney damage'),(6004,'Asthma','Respiratory','A chronic lung condition that causes airway inflammation and narrowing'),(6005,'Osteoarthritis','Musculoskeletal','Degeneration of joint cartilage causing pain, stiffness, and reduced mobility'),(6006,'Kidney Disease','Renal','Gradual loss of kidney function over time'),(6007,'Anxiety Disorder','Mental Health','A condition marked by excessive worry, tension, and physical symptoms like restlessness or rapid heartbeat'),(6008,'Migraine','Neurological','A recurring headache disorder characterized by intense throbbing pain'),(6009,'Pneumonia','Infectious','An infection that inflames the air sacs in the lungs, causing cough, fever, chest pain, and difficulty breathing'),(6010,'Irritable Bowel Syndrome','Gastrointestinal','A functional digestive disorder causing abdominal pain, bloating, and altered bowel habits without structural damage');
/*!40000 ALTER TABLE `illness` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `illness_occurrence_view`
--

DROP TABLE IF EXISTS `illness_occurrence_view`;
/*!50001 DROP VIEW IF EXISTS `illness_occurrence_view`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `illness_occurrence_view` AS SELECT 
 1 AS `patient_id`,
 1 AS `illness_name`,
 1 AS `treatment_date`,
 1 AS `p_lastname`,
 1 AS `p_firstname`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `medicine`
--

DROP TABLE IF EXISTS `medicine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicine` (
  `medicine_id` int NOT NULL AUTO_INCREMENT,
  `medicine_name` varchar(100) NOT NULL,
  `stock_qty` int NOT NULL,
  PRIMARY KEY (`medicine_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5011 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medicine`
--

LOCK TABLES `medicine` WRITE;
/*!40000 ALTER TABLE `medicine` DISABLE KEYS */;
INSERT INTO `medicine` VALUES (5001,'Paracetamol 500mg',120),(5002,'Amoxicillin 500mg',80),(5003,'Ibuprofen 400mg',150),(5004,'Metformin 850mg',60),(5005,'Losartan 50mg',90),(5006,'Salbutamol Inhaler',40),(5007,'Omeprazole 20mg',100),(5008,'Cetirizine 10mg',200),(5009,'Hydration IV Fluid (PNSS)',50),(5010,'Insulin Glargine',30);
/*!40000 ALTER TABLE `medicine` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nurse`
--

DROP TABLE IF EXISTS `nurse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nurse` (
  `nurse_id` int NOT NULL AUTO_INCREMENT,
  `n_firstname` varchar(50) NOT NULL,
  `n_lastname` varchar(50) NOT NULL,
  `contact_no` varchar(30) NOT NULL,
  PRIMARY KEY (`nurse_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3011 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nurse`
--

LOCK TABLES `nurse` WRITE;
/*!40000 ALTER TABLE `nurse` DISABLE KEYS */;
INSERT INTO `nurse` VALUES (3001,'Maria','Santos','+63 917 555 1234'),(3002,'Jose','Reyes','+63 998 777 5678'),(3003,'Elena','Cruz','+63 920 111 9012'),(3004,'Ricardo','Gonzalez','+63 932 444 3456'),(3005,'Sofia','Garcia','+63 947 888 7890'),(3006,'Antonio','Lim','+63 908 222 1100'),(3007,'Katrina','Ramos','+63 999 666 4321'),(3008,'Paolo','Pascual','+63 976 333 8765'),(3009,'Isabella','Torres','+63 918 000 5432'),(3010,'Gabriel','Diaz','+63 925 999 2109');
/*!40000 ALTER TABLE `nurse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nurse_assignment`
--

DROP TABLE IF EXISTS `nurse_assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nurse_assignment` (
  `nurseAssignment_id` int NOT NULL AUTO_INCREMENT,
  `nurseShift_id` int NOT NULL,
  `patient_id` int NOT NULL,
  `date_assigned` date DEFAULT NULL,
  `assigned_until` date DEFAULT NULL,
  PRIMARY KEY (`nurseAssignment_id`),
  UNIQUE KEY `nurseShift_id` (`nurseShift_id`,`patient_id`,`date_assigned`),
  KEY `patient_id` (`patient_id`),
  CONSTRAINT `nurse_assignment_ibfk_1` FOREIGN KEY (`nurseShift_id`) REFERENCES `nurse_shift` (`nurseShift_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `nurse_assignment_ibfk_2` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=211 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nurse_assignment`
--

LOCK TABLES `nurse_assignment` WRITE;
/*!40000 ALTER TABLE `nurse_assignment` DISABLE KEYS */;
INSERT INTO `nurse_assignment` VALUES (201,8005,1001,'2025-06-11',NULL),(202,8003,1002,'2025-03-31',NULL),(203,8001,1003,'2025-11-22',NULL),(204,8004,1004,'2025-01-28',NULL),(205,8001,1005,'2025-08-09',NULL),(206,8006,1006,'2025-12-06',NULL),(207,8011,1007,'2025-01-09',NULL),(208,8010,1008,'2025-05-21',NULL),(209,8005,1009,'2025-10-15',NULL),(210,8008,1010,'2025-04-14',NULL);
/*!40000 ALTER TABLE `nurse_assignment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nurse_shift`
--

DROP TABLE IF EXISTS `nurse_shift`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nurse_shift` (
  `nurseShift_id` int NOT NULL AUTO_INCREMENT,
  `nurse_id` int NOT NULL,
  `shift_day` enum('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday') NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  PRIMARY KEY (`nurseShift_id`),
  UNIQUE KEY `nurse_id` (`nurse_id`,`shift_day`),
  CONSTRAINT `nurse_shift_ibfk_1` FOREIGN KEY (`nurse_id`) REFERENCES `nurse` (`nurse_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8028 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nurse_shift`
--

LOCK TABLES `nurse_shift` WRITE;
/*!40000 ALTER TABLE `nurse_shift` DISABLE KEYS */;
INSERT INTO `nurse_shift` VALUES (8001,3001,'Monday','08:00:00','20:00:00'),(8002,3001,'Tuesday','08:00:00','20:00:00'),(8003,3001,'Wednesday','08:00:00','20:00:00'),(8004,3002,'Tuesday','02:00:00','16:00:00'),(8005,3002,'Wednesday','02:00:00','16:00:00'),(8006,3002,'Thursday','02:00:00','16:00:00'),(8007,3003,'Wednesday','21:00:00','23:00:00'),(8008,3003,'Thursday','21:00:00','23:00:00'),(8009,3003,'Friday','21:00:00','23:00:00'),(8010,3004,'Thursday','01:00:00','12:00:00'),(8011,3004,'Friday','01:00:00','12:00:00'),(8012,3004,'Saturday','01:00:00','12:00:00'),(8013,3005,'Friday','12:00:00','24:00:00'),(8014,3005,'Saturday','12:00:00','24:00:00'),(8015,3005,'Sunday','12:00:00','24:00:00'),(8016,3006,'Monday','05:00:00','17:00:00'),(8017,3006,'Tuesday','05:00:00','17:00:00'),(8018,3006,'Wednesday','05:00:00','17:00:00'),(8019,3007,'Tuesday','18:00:00','05:00:00'),(8020,3007,'Wednesday','18:00:00','05:00:00'),(8021,3007,'Thursday','18:00:00','05:00:00'),(8022,3008,'Wednesday','09:00:00','22:00:00'),(8023,3008,'Thursday','09:00:00','22:00:00'),(8024,3009,'Thursday','22:00:00','10:00:00'),(8025,3009,'Friday','22:00:00','10:00:00'),(8026,3010,'Friday','04:00:00','16:00:00'),(8027,3010,'Saturday','04:00:00','16:00:00');
/*!40000 ALTER TABLE `nurse_shift` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient`
--

DROP TABLE IF EXISTS `patient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patient` (
  `patient_id` int NOT NULL AUTO_INCREMENT,
  `p_lastname` varchar(50) NOT NULL,
  `p_firstname` varchar(50) NOT NULL,
  `sex` enum('M','F') NOT NULL,
  `birth_date` date NOT NULL,
  `contact_no` varchar(30) NOT NULL,
  `p_status` enum('Admitted','Discharged') NOT NULL,
  PRIMARY KEY (`patient_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1011 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient`
--

LOCK TABLES `patient` WRITE;
/*!40000 ALTER TABLE `patient` DISABLE KEYS */;
INSERT INTO `patient` VALUES (1001,'Santos','Maria','F','1990-05-12','+63 9171234567','Discharged'),(1002,'Reyes','Juan','M','1985-09-23','+63 9987654321','Discharged'),(1003,'Cruz','Angela','F','2001-02-14','+63 9170011223','Discharged'),(1004,'Dela Cruz','Mark','M','1998-12-02','+63 9223334455','Discharged'),(1005,'Garcia','Luis','M','1975-03-30','+63 9175556677','Discharged'),(1006,'Torres','Sophia','F','2003-07-19','+63 9334445566','Discharged'),(1007,'Flores','Daniel','M','1993-11-08','+63 9449998877','Discharged'),(1008,'Gonzales','Isabel','F','1989-04-25','+63 9556667788','Discharged'),(1009,'Navarro','Carlos','M','1996-01-17','+63 9171112223','Discharged'),(1010,'Lim','Patricia','F','2000-10-05','+63 9221112223','Discharged');
/*!40000 ALTER TABLE `patient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `physician`
--

DROP TABLE IF EXISTS `physician`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `physician` (
  `physician_id` int NOT NULL AUTO_INCREMENT,
  `ph_firstname` varchar(50) NOT NULL,
  `ph_lastname` varchar(50) NOT NULL,
  `contact_no` varchar(30) NOT NULL,
  `specialization` varchar(80) NOT NULL,
  PRIMARY KEY (`physician_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2011 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `physician`
--

LOCK TABLES `physician` WRITE;
/*!40000 ALTER TABLE `physician` DISABLE KEYS */;
INSERT INTO `physician` VALUES (2001,'Sophia','Laforteza','+63 994 133 4589','Cardiology'),(2002,'Yoonchae','Jeung','+63 926 369 7532','Pediatrics'),(2003,'Manon','Bannerman','+63 939 748 3415','Dermatology'),(2004,'Martin','Edwards','+63 919 149 2346','Neurology'),(2005,'Daniela','Llorente','+63 973 188 852','Psychiatry'),(2006,'Geonho','Ahn','+63 992 266 293','Orthopedics'),(2007,'Lara','Rajagopalan','+63 923 284 0621','Urology'),(2008,'Juhoon','Kim','+63 984 054 0043','Oncology'),(2009,'Lara','Skiendel','+63 938 279 7640','OB-GYN'),(2010,'Rhea','Valdez','+63 927 427 1054','Pulmonology');
/*!40000 ALTER TABLE `physician` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `physician_schedule`
--

DROP TABLE IF EXISTS `physician_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `physician_schedule` (
  `physicianSchedule_id` int NOT NULL AUTO_INCREMENT,
  `physician_id` int NOT NULL,
  `schedule_day` enum('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday') NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  PRIMARY KEY (`physicianSchedule_id`),
  UNIQUE KEY `physician_id` (`physician_id`,`schedule_day`),
  CONSTRAINT `physician_schedule_ibfk_1` FOREIGN KEY (`physician_id`) REFERENCES `physician` (`physician_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7021 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `physician_schedule`
--

LOCK TABLES `physician_schedule` WRITE;
/*!40000 ALTER TABLE `physician_schedule` DISABLE KEYS */;
INSERT INTO `physician_schedule` VALUES (7001,2001,'Monday','08:00:00','12:00:00'),(7002,2001,'Tuesday','08:00:00','12:00:00'),(7003,2002,'Tuesday','09:00:00','15:00:00'),(7004,2002,'Wednesday','09:00:00','15:00:00'),(7005,2003,'Wednesday','08:00:00','14:00:00'),(7006,2003,'Thursday','08:00:00','14:00:00'),(7007,2004,'Thursday','10:00:00','16:00:00'),(7008,2004,'Friday','10:00:00','16:00:00'),(7009,2005,'Friday','08:00:00','12:00:00'),(7010,2005,'Saturday','08:00:00','12:00:00'),(7011,2006,'Monday','13:00:00','17:00:00'),(7012,2006,'Tuesday','13:00:00','17:00:00'),(7013,2007,'Tuesday','10:00:00','14:00:00'),(7014,2007,'Wednesday','10:00:00','14:00:00'),(7015,2008,'Wednesday','09:00:00','13:00:00'),(7016,2008,'Thursday','09:00:00','13:00:00'),(7017,2009,'Thursday','08:00:00','12:00:00'),(7018,2009,'Friday','08:00:00','12:00:00'),(7019,2010,'Friday','13:00:00','18:00:00'),(7020,2010,'Saturday','13:00:00','18:00:00');
/*!40000 ALTER TABLE `physician_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `physicians_workload_view`
--

DROP TABLE IF EXISTS `physicians_workload_view`;
/*!50001 DROP VIEW IF EXISTS `physicians_workload_view`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `physicians_workload_view` AS SELECT 
 1 AS `patient_id`,
 1 AS `p_lastname`,
 1 AS `p_firstname`,
 1 AS `treatment_date`,
 1 AS `physician_id`,
 1 AS `illness_name`,
 1 AS `performed_by`,
 1 AS `treatment_procedure`,
 1 AS `assignedPhysician_id`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `treatment`
--

DROP TABLE IF EXISTS `treatment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `treatment` (
  `treatment_id` int NOT NULL AUTO_INCREMENT,
  `nurseAssignment_id` int NOT NULL,
  `diagnosis_id` int NOT NULL,
  `medicine_id` int DEFAULT NULL,
  `assignedPhysician_id` int DEFAULT NULL,
  `treatment_date` date NOT NULL,
  `treatment_procedure` varchar(100) NOT NULL,
  `performed_by` enum('Nurse','Diagnosing Physician','Assigned Physician') NOT NULL,
  `remarks` varchar(255) NOT NULL,
  PRIMARY KEY (`treatment_id`),
  KEY `nurseAssignment_id` (`nurseAssignment_id`),
  KEY `diagnosis_id` (`diagnosis_id`),
  KEY `medicine_id` (`medicine_id`),
  KEY `assignedPhysician_id` (`assignedPhysician_id`),
  CONSTRAINT `treatment_ibfk_1` FOREIGN KEY (`nurseAssignment_id`) REFERENCES `nurse_assignment` (`nurseAssignment_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `treatment_ibfk_2` FOREIGN KEY (`diagnosis_id`) REFERENCES `diagnosis` (`diagnosis_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `treatment_ibfk_3` FOREIGN KEY (`medicine_id`) REFERENCES `medicine` (`medicine_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `treatment_ibfk_4` FOREIGN KEY (`assignedPhysician_id`) REFERENCES `physician` (`physician_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=412 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `treatment`
--

LOCK TABLES `treatment` WRITE;
/*!40000 ALTER TABLE `treatment` DISABLE KEYS */;
INSERT INTO `treatment` VALUES (401,201,101,5001,NULL,'2025-06-12','Administered Paracetamol for fever control.','Nurse','Temperature decreased within the hour.'),(402,202,102,5004,NULL,'2025-04-01','Given Metformin for glucose regulation.','Nurse','-'),(403,203,103,5005,NULL,'2025-11-03','Adjusted Losartan dosage based on blood pressure reading.','Diagnosing Physician','Improvement observed after evaluation.'),(404,204,104,5006,NULL,'2025-01-29','Administered Salbutamol inhalation for asthma','Nurse','Breathing improved post therapy.'),(405,205,105,5003,2003,'2025-08-10','Provided Ibuprofen for joint inflammation.','Assigned Physician','Pain reduced; mobility improved.'),(406,206,106,5009,NULL,'2025-12-07','Initiated IV hydration (PNSS).','Nurse','Hydration status stable.'),(407,207,107,5008,NULL,'2025-01-10','Evaluated anxiety symptoms and administered Cetirizine.','Diagnosing Physician','Patient calmer after treatment.'),(409,209,109,5002,NULL,'2025-10-16','Administered first dose of Amoxicillin.','Nurse','No allergic reaction observed.'),(410,210,110,5007,2006,'2025-04-15','Given Omeprazole for gastrointestinal discomfort','Assigned Physician','Symptoms improved after treatment.'),(411,201,101,5001,2001,'2025-06-12','Follow-up consultation performed by diagnosing physician.','Assigned Physician','Symptoms improving.');
/*!40000 ALTER TABLE `treatment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `treatment_stats_view`
--

DROP TABLE IF EXISTS `treatment_stats_view`;
/*!50001 DROP VIEW IF EXISTS `treatment_stats_view`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `treatment_stats_view` AS SELECT 
 1 AS `patient_id`,
 1 AS `treatment_id`,
 1 AS `treatment_procedure`,
 1 AS `treatment_date`,
 1 AS `p_lastname`,
 1 AS `p_firstname`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `ward`
--

DROP TABLE IF EXISTS `ward`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ward` (
  `ward_id` int NOT NULL AUTO_INCREMENT,
  `ward_number` int NOT NULL,
  `floor` varchar(20) NOT NULL,
  `w_status` enum('Available','Occupied') NOT NULL,
  PRIMARY KEY (`ward_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4013 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ward`
--

LOCK TABLES `ward` WRITE;
/*!40000 ALTER TABLE `ward` DISABLE KEYS */;
INSERT INTO `ward` VALUES (4001,101,'1st Floor','Available'),(4002,102,'1st Floor','Available'),(4003,103,'1st Floor','Available'),(4004,104,'1st Floor','Available'),(4005,201,'2nd Floor','Available'),(4006,202,'2nd Floor','Available'),(4007,203,'2nd Floor','Available'),(4008,204,'2nd Floor','Available'),(4009,301,'3rd Floor','Available'),(4010,302,'3rd Floor','Available'),(4011,303,'3rd Floor','Available'),(4012,304,'3rd Floor','Available');
/*!40000 ALTER TABLE `ward` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `admission_rate_view`
--

/*!50001 DROP VIEW IF EXISTS `admission_rate_view`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `admission_rate_view` AS select `d`.`patient_id` AS `patient_id`,`d`.`diagnosis_date` AS `diagnosis_date`,`p`.`p_lastname` AS `p_lastname`,`p`.`p_firstname` AS `p_firstname` from (`diagnosis` `d` join `patient` `p` on((`d`.`patient_id` = `p`.`patient_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `illness_occurrence_view`
--

/*!50001 DROP VIEW IF EXISTS `illness_occurrence_view`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `illness_occurrence_view` AS select `d`.`patient_id` AS `patient_id`,`i`.`illness_name` AS `illness_name`,`t`.`treatment_date` AS `treatment_date`,`p`.`p_lastname` AS `p_lastname`,`p`.`p_firstname` AS `p_firstname` from (((`diagnosis` `d` join `illness` `i` on((`d`.`illness_id` = `i`.`illness_id`))) join `patient` `p` on((`d`.`patient_id` = `p`.`patient_id`))) join `treatment` `t` on((`d`.`diagnosis_id` = `t`.`diagnosis_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `physicians_workload_view`
--

/*!50001 DROP VIEW IF EXISTS `physicians_workload_view`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `physicians_workload_view` AS select `d`.`patient_id` AS `patient_id`,`p`.`p_lastname` AS `p_lastname`,`p`.`p_firstname` AS `p_firstname`,`t`.`treatment_date` AS `treatment_date`,`ps`.`physician_id` AS `physician_id`,`i`.`illness_name` AS `illness_name`,`t`.`performed_by` AS `performed_by`,`t`.`treatment_procedure` AS `treatment_procedure`,`t`.`assignedPhysician_id` AS `assignedPhysician_id` from ((((`treatment` `t` join `diagnosis` `d` on((`t`.`diagnosis_id` = `d`.`diagnosis_id`))) join `patient` `p` on((`d`.`patient_id` = `p`.`patient_id`))) join `illness` `i` on((`d`.`illness_id` = `i`.`illness_id`))) join `physician_schedule` `ps` on((`d`.`physicianSchedule_id` = `ps`.`physicianSchedule_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `treatment_stats_view`
--

/*!50001 DROP VIEW IF EXISTS `treatment_stats_view`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `treatment_stats_view` AS select `d`.`patient_id` AS `patient_id`,`t`.`treatment_id` AS `treatment_id`,`t`.`treatment_procedure` AS `treatment_procedure`,`t`.`treatment_date` AS `treatment_date`,`p`.`p_lastname` AS `p_lastname`,`p`.`p_firstname` AS `p_firstname` from ((`diagnosis` `d` join `treatment` `t` on((`d`.`diagnosis_id` = `t`.`diagnosis_id`))) join `patient` `p` on((`d`.`patient_id` = `p`.`patient_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-19 20:03:12
