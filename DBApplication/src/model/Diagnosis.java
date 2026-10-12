package model;

import java.sql.Date;

public class Diagnosis {
    private int diagnosis_id;
    private int physicianSchedule_id;
    private int patient_id;
    private int illness_id;
    private java.sql.Date diagnosis_date;
    private String notes;

    public Diagnosis(int phySchedID, int pID, int illID, java.sql.Date diagnosis_date, String notes) {
        this.physicianSchedule_id = phySchedID;
        this.patient_id = pID;
        this.illness_id = illID;
        this.diagnosis_date = diagnosis_date;
        this.notes = notes;
    }

    public Diagnosis(int id)
    {
        diagnosis_id = id;
    }

    public void setDiagnosis_id(int diagnosis_id) {
        this.diagnosis_id = diagnosis_id;
    }

    public void setPhysicianSchedule_id(int physicianSchedule_id)
    {
        this.physicianSchedule_id = physicianSchedule_id;
    }

    public void setPatient_id(int patient_id)
    {
        this.patient_id = patient_id;
    }

    public void setIllness_id(int illness_id)
    {
        this.illness_id = illness_id;
    }

    public void setDiagnosis_date(java.sql.Date diagnosis_date)
    {
        this.diagnosis_date = diagnosis_date;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public int getDiagnosis_id() {
        return diagnosis_id;
    }

    public int getPhysicianSchedule_id() {
        return physicianSchedule_id;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public int getIllness_id() {
        return illness_id;
    }

    public java.sql.Date getDiagnosis_date() {
        return diagnosis_date;
    }

    public String getNotes() {
        return notes;
    }
}