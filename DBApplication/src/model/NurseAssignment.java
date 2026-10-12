package model;

import java.sql.Date;

public class NurseAssignment {
    private int nurseAssignment_id;
    private int nurseShift_id;
    private int patient_id;
    private java.sql.Date date_assigned;
    private java.sql.Date assigned_until;

    public NurseAssignment(int nsID, int pID, java.sql.Date assignedDate, java.sql.Date assignedUntil)
    {
        nurseShift_id = nsID;
        patient_id = pID;
        date_assigned = assignedDate;
        assigned_until = assignedUntil;
    }

    public NurseAssignment(int id)
    {
        nurseAssignment_id = id;
    }

    public void setAssignmentID(int naID)
    {
        nurseAssignment_id = naID;
    }

    public void setPatient_id(int patient_id)
    {
        this.patient_id = patient_id;
    }

    public void setNurseShift_id(int nurseShift_id)
    {
        this.nurseShift_id = nurseShift_id;
    }

    public void setDate_assigned(java.sql.Date date_assigned)
    {
        this.date_assigned = date_assigned;
    }

    public void setAssigned_until(java.sql.Date assigned_until)
    {
        this.assigned_until = assigned_until;
    }

    public int getNurseAssignmentID()
    {
         return nurseAssignment_id;
    }

    public int getNurseShiftID()
    {
        return nurseShift_id;
    }

    public int getPatientID()
    {
        return patient_id;
    }

    public java.sql.Date getDateAssigned()
    {
        return date_assigned;
    }

    public java.sql.Date getAssignedUntil()
    {
        return assigned_until;
    }
}
