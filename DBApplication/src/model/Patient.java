package model;

public class Patient {
    private int patient_id;
    private String p_lastname;
    private String p_firstname;
    private String birth_date;
    private String contact_no;
    private String sex;
    private String p_status;

    public Patient(int id)
    {
        this.patient_id = id;
    }

    public Patient(String ln, String fn, String bd, String contact, String s, String st)
    {
        p_lastname = ln;
        p_firstname = fn;
        birth_date = bd;
        contact_no = contact;
        sex = s;
        p_status = st;
    }

    public void setPatientID(int pID)
    {
        patient_id = pID;
    }

    public void setLastName(String ln)
    {
        p_lastname = ln;
    }

    public void setFirstName(String fn)
    {
        p_firstname = fn;
    }

    public void setContact(String c)
    {
        contact_no = c;
    }

    public void setSex(String s)
    {
        sex = s;
    }

    public void setStatus(String st)
    {
        p_status = st;
    }

    public void setBirthDate(String bd)
    {
        birth_date = bd;
    }


    public int getPatientID()
    {
        return patient_id;
    }

    public String getLastName()
    {
        return p_lastname;
    }

    public String getFirstName()
    {
        return p_firstname;
    }

    public String getContact()
    {
        return contact_no;
    }

    public String getSex()
    {
        return sex;
    }

    public String getStatus()
    {
        return p_status;
    }

    public String getBirthDate()
    {
        return birth_date;
    }
}
