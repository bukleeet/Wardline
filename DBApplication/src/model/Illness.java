package model;

public class Illness {
    private int illness_id;
    private String illness_name;
    private String category;
    private String illness_description;

    public Illness (String illness_name, String category, String illness_description) {
        this.illness_name = illness_name;
        this.category = category;
        this.illness_description = illness_description;
    }

    public Illness(int id)
    {
        illness_id = id;
    }

    public void setIllnessName(String n)
    {
        illness_name = n;
    }

	public void setIllness_id(int id) 
    {
        this.illness_id = id;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public void setIllness_description(String illness_description)
    {
        this.illness_description = illness_description;
    }

    public int getIllness_id() {
        return illness_id;
    }

    public String getIllness_name() {
        return illness_name;
    }

    public String getCategory() {
        return category;
    }

    public String getIllness_description() {
        return illness_description;
    }
}
