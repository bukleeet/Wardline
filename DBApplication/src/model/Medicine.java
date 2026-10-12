package model;

public class Medicine {
    private int medicine_id;
    private String medicine_name;
    private int stock_qty;

    public Medicine(String med_name, int stock)
    {
        medicine_name = med_name;
        stock_qty = stock;
    }

    public Medicine(int id)
    {
        medicine_id = id;
    }


    public void setMedicineID(int medID)
    {
        medicine_id = medID;
    }

    public void setName(String n)
    {
        medicine_name = n;
    }

    public void setStock_qty(int s)
    {
        stock_qty = s;
    }

    public int getMedicineID()
    {
        return medicine_id;
    }

    public String getMedicineName()
    {
        return medicine_name;
    }

    public int getStockQty()
    {
        return stock_qty;
    }
}
