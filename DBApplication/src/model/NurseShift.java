package model;

public class NurseShift {
    private int nurseShift_id;
    private int nurse_id;
    private String shift_day;
    private String start_time;
    private String end_time;

    public NurseShift(int nurseID, String day, String start, String end)
    {
        nurse_id = nurseID;
        shift_day = day;
        start_time = start;
        end_time = end;
    }

    public NurseShift(int id)
    {
        nurseShift_id = id;
    }

    public void setNurseShiftID(int nsID)
    {
        nurseShift_id = nsID;
    }

    public void setNurse_id(int nurse_id)
    {
        this.nurse_id = nurse_id;
    }

    public void setShift_day(String shift_day)
    {
        this.shift_day = shift_day;
    }

    public void setStart_time(String start_time)
    {
        this.start_time = start_time;
    }

    public void setEnd_time(String end_time)
    {
        this.end_time = end_time;
    }

    public int getNurseShiftID()
    {
        return nurseShift_id;
    }

    public int getNurseID()
    {
        return nurse_id;
    }

    public String getShiftDay()
    {
        return this.shift_day;
    }

    public String getStartTime()
    {
        return this.start_time;
    }

    public String getEndTime()
    {
        return this.end_time;
    }

}
