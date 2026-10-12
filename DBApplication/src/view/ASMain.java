package view;

import controller.*;

public class ASMain{
    public static void main(String[] args){
        ASGui gui = new ASGui();
        WelcomeController welcomeController= new WelcomeController(gui);
        PatientController patientController = new PatientController(gui);
        NurseController nurseController = new NurseController(gui);
        PhysicianController physicianController = new PhysicianController(gui);
        WardController wardController = new WardController(gui);
        IllnessController illnessController = new IllnessController(gui);
        MedicineController medicineController = new MedicineController(gui);
        DiagnosisController diagnosisController = new DiagnosisController(gui);
        NurseAssignmentController nurseAssignmentController = new NurseAssignmentController(gui);
        AdmissionController admissionController = new AdmissionController(gui);
        TreatmentController treatmentController = new TreatmentController(gui);
        DischargeController dischargeController = new DischargeController(gui);
        SearchController searchController = new SearchController(gui);
        CreateController createController = new CreateController(gui);
        ReportController reportController = new ReportController(gui);
    }
}