package model;

import java.sql.Date;

public class Treatment {
    private int treatment_id;
    private int nurseAssignment_id;
    private int diagnosis_id;
    private int medicine_id;
    private java.sql.Date treatment_date;
    private String treatment_procedure;
    private String remarks;
    private Integer assignedPhysician_id;
    private String performed_by;

    public Treatment(int naID, int dID, int mID, java.sql.Date date, String procedure, String remarks, Integer apID, String performedBy)
    {
        nurseAssignment_id = naID;
        diagnosis_id = dID;
        medicine_id = mID;
        treatment_date = date;
        treatment_procedure = procedure;
        this.remarks = remarks;
        assignedPhysician_id = apID;
        performed_by = performedBy;
    }

    public Treatment(int id)
    {
        treatment_id = id;
    }


    public void setTreatmentID(int id)
    {
        treatment_id = id;
    }

    public void setNurseAssignment_id(int nurseAssignment_id)
    {
        this.nurseAssignment_id = nurseAssignment_id;
    }

    public void setDiagnosis_id(int diagnosis_id)
    {
        this.diagnosis_id = diagnosis_id;
    }

    public void setMedicine_id(int medicine_id)
    {
        this.medicine_id = medicine_id;
    }

    public void setTreatment_date(java.sql.Date treatment_date)
    {
        this.treatment_date = treatment_date;
    }

    public void setTreatment_procedure(String treatment_procedure)
    {
        this.treatment_procedure = treatment_procedure;
    }


    public void setRemarks(String remarks)
    {
        this.remarks = remarks;
    }

    public void setAssignedPhysicianID(Integer id)
    {
        assignedPhysician_id = id;
    }

    public void setPerformed_by(String performed_by)
    {
        this.performed_by = performed_by;
    }



    public int getTreatmentID()
    {
        return treatment_id;
    }

    public int getNurseAssignment_id()
    {
        return nurseAssignment_id;
    }

    public int getDiagnosisID()
    {
        return diagnosis_id;
    }

    public int getMedicineID()
    {
        return medicine_id;
    }

    public java.sql.Date getTreatmentDate()
    {
        return treatment_date;
    }

    public String getProcedure()
    {
        return treatment_procedure;
    }

    public String getRemarks()
    {
        return remarks;
    }

    public Integer getAssignedPhysician()
    {
        return assignedPhysician_id;
    }

    public String getPerformedBy()
    {
        return performed_by;
    }
}
