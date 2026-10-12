package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;


public class PhysicianScheduleManagement {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dbhospital_final";
    private static final String USER = "root";
    private static final String PASSWORD = "infom123";
    private Connection conn;
    PreparedStatement pstmt;

    public boolean createPhysicianSchedule(PhysicianSchedule ps)
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "INSERT INTO physician_schedule (physician_id, schedule_day, start_time, end_time) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, ps.getPhysicianID());
            pstmt.setString(2, ps.getDay());
            pstmt.setString(3, ps.getStartTime());
            pstmt.setString(4, ps.getEndTime());

            pstmt.executeUpdate();
            System.out.println("Physician Schedule Record inserted successfully!");

            pstmt.close();
            conn.close();
            return true;

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<PhysicianSchedule> viewPhysicianSchedule()
    {
        List<PhysicianSchedule> physicianSchedules = new ArrayList<>();

        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "SELECT * FROM physician_schedule";
            pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();// used for queries that returns result
            while(rs.next())
            {
                PhysicianSchedule ps = new PhysicianSchedule(rs.getInt("physicianSchedule_id"));
                ps.setPhysician_id(rs.getInt("physician_id"));
                ps.setSchedule_day(rs.getString("schedule_day"));
                ps.setStart_time(rs.getString("start_time"));
                ps.setEnd_time(rs.getString("end_time"));

                physicianSchedules.add(ps);

                int phsID = rs.getInt("physicianSchedule_id");
                int phID = rs.getInt("physician_id");
                String sday = rs.getString("schedule_day");
                String sTime = rs.getString("start_time");
                String eTime = rs.getString("end_time");

                System.out.println(phsID + ", " + phID + ", " + sday + ", " + sTime + ", " + eTime);
            }
            pstmt.close();
            rs.close();
            conn.close();

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return physicianSchedules;
    }

    public boolean updatePhysicianSchedule(PhysicianSchedule ps)
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "UPDATE physician_schedule SET schedule_day = ?, start_time = ?, end_time = ?  WHERE physicianSchedule_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ps.getDay());
            pstmt.setString(2, ps.getStartTime());
            pstmt.setString(3, ps.getEndTime());
            pstmt.setInt(4, ps.getPhysicianScheduleID());

            int rowsAffected = pstmt.executeUpdate();

            if(rowsAffected > 0)
            {
                System.out.println("\nPhysicianSchedule with id = " + ps.getPhysicianScheduleID() + " has been updated!");
                pstmt.close();
                conn.close();
                return true;
            }
            else
            {
                System.out.println("Physician Schedule record update failed.");
                pstmt.close();
                conn.close();
                return false;
            }

        }catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deletePhysicianSchedule(int psID) //DELETE
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "DELETE FROM physician_schedule WHERE physicianSchedule_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, psID);

            int rowsAffected = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            if(rowsAffected > 0)
            {
                System.out.println("Physician Schedule with id = " + psID + " deleted successfully!");
                return true;
            }
            else
            {
                System.out.println("Physician Schedule deletion failed");
                return false;
            }

        }catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static void main(String[] args)
    {
        //PhysicianSchedule ps = new PhysicianSchedule(2001, "Wednesday", "08:00", "20:00");
        PhysicianScheduleManagement psm = new PhysicianScheduleManagement();
        //PhysicianSchedule updateps = new PhysicianSchedule(2001, "Thursday", "10:00", "20:00");

//        psm.createPhysicianSchedule(ps);
//        updateps.setPhysicianScheduleID(4);
//        psm.updatePhysicianSchedule(updateps);

//        psm.deletePhysicianSchedule(updateps.getPhysicianScheduleID());
        psm.viewPhysicianSchedule();

    }



}
