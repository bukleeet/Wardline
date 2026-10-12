package model;

public class Discharge {
    private int discharge_id;
    private int admission_id;
    private String discharge_date;

    public Discharge(int admission_id, String discharge_date)
    {
        this.admission_id = admission_id;
        this.discharge_date = discharge_date;
    }

    public Discharge(int id)
    {
        discharge_id = id;
    }

    public void setDischargeID(int id)
    {
        discharge_id = id;
    }

    public void setAdmission_id(int admission_id)
    {
        this.admission_id = admission_id;
    }

    public void setDischarge_date(String discharge_date)
    {
        this.discharge_date = discharge_date;
    }

    public int getDischargeID() {
        return discharge_id;
    }

    public int getAdmissionID() { return admission_id; }

    public String getDischargeDate() {
        return discharge_date;
    }
}
