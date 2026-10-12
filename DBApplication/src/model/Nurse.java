package model;

public class Nurse {

    private int nurse_id;
    private String n_lastname;
    private String n_firstname;
    private String contact_no;

    public Nurse(String ln, String fn, String contact_no)
    {
        n_lastname = ln;
        n_firstname = fn;
        this.contact_no = contact_no;
    }

    public Nurse(int id)
    {
        nurse_id = id;
    }

    public void setNurse_id(int id)
    {
        nurse_id = id;
    }

    public void setLastName(String n_lastname)
    {
        this.n_lastname = n_lastname;
    }

    public void setFirstName(String fn)
    {
        n_firstname = fn;
    }

    public void setContact(String contact_no)
    {
        this.contact_no = contact_no;
    }

	public void setNurseID(int id) 
    {
        nurse_id = id;
    }
	
    public int getNurseID()
    {
        return nurse_id;
    }

    public String getLastName()
    {
        return n_lastname;
    }

    public String getFirstName()
    {
        return n_firstname;
    }

    public  String getContactNo()
    {
        return contact_no;
    }
}
