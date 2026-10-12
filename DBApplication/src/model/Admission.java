package model;

public class Admission {
    private int admission_id;
    private int patient_id;
    private int ward_id;
    private String admission_date;

    public Admission(int pID, int wID, String date)
    {
        patient_id = pID;
        ward_id = wID;
        admission_date = date;
    }

    public Admission(int id)
    {
        admission_id = id;
    }

    public void setAdmissionID(int id)
    {
        admission_id = id;
    }

    public void setPatient_id(int patient_id)
    {
        this.patient_id = patient_id;
    }

    public void setWard_id(int ward_id)
    {
        this.ward_id = ward_id;
    }

    public void setAdmission_date(String admission_date)
    {
        this.admission_date = admission_date;
    }

    public int getAdmissionID()
    {
        return admission_id;
    }

    public int getPatientID()
    {
        return patient_id;
    }

    public int getWardID()
    {
        return ward_id;
    }

    public String getAdmissionDate()
    {
        return admission_date;
    }

}
