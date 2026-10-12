package model;

public class PhysicianSchedule {
    private int physicianSchedule_id;
    private int physician_id;
    private String schedule_day;
    private String start_time;
    private String end_time;

    public PhysicianSchedule(int physicianID, String day, String start, String end)
    {
        physician_id = physicianID;
        schedule_day = day;
        start_time = start;
        end_time = end;
    }

    public PhysicianSchedule(int id)
    {
        physicianSchedule_id = id;
    }

    public void setPhysicianScheduleID(int phsID)
    {
        physicianSchedule_id = phsID;
    }

    public void setPhysician_id(int physician_id)
    {
        this.physician_id = physician_id;
    }

    public void setSchedule_day(String schedule_day)
    {
        this.schedule_day = schedule_day;
    }

    public void setStart_time(String start_time)
    {
        this.start_time = start_time;
    }

    public void setEnd_time(String end_time)
    {
        this.end_time = end_time;
    }

    public int getPhysicianScheduleID()
    {
        return physicianSchedule_id;
    }

    public int getPhysicianID()
    {
        return physician_id;
    }

    public String getDay()
    {
        return schedule_day;
    }

    public String getStartTime()
    {
        return start_time;
    }

    public String getEndTime()
    {
        return end_time;
    }
}
