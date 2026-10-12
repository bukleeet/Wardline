package model;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class NurseShiftManagement {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dbhospital_final";
    private static final String USER = "root";
    private static final String PASSWORD = "infom123";
    private Connection conn;
    PreparedStatement pstmt;

    public boolean createNurseShift(NurseShift nurseShift)
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "INSERT INTO nurse_shift (nurse_id, shift_day, start_time, end_time) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, nurseShift.getNurseID());
            pstmt.setString(2, nurseShift.getShiftDay());
            pstmt.setString(3, nurseShift.getStartTime());
            pstmt.setString(4, nurseShift.getEndTime());

            pstmt.executeUpdate();
            System.out.println("Nurse Shift Record inserted successfully!");

            pstmt.close();
            conn.close();
            return true;

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<NurseShift> viewNurseShifts()
    {
        List<NurseShift> nurseShifts = new ArrayList<>();
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "SELECT * FROM nurse_shift";
            pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();// used for queries that returns result
            while(rs.next())
            {
                NurseShift ns = new NurseShift(rs.getInt("nurseShift_id"));
                ns.setNurse_id(rs.getInt("nurse_id"));
                ns.setShift_day(rs.getString("shift_day"));
                ns.setStart_time(rs.getString("start_time"));
                ns.setEnd_time(rs.getString("end_time"));
                nurseShifts.add(ns);

                int nsID = rs.getInt("nurseShift_id");
                int nID = rs.getInt("nurse_id");
                String sday = rs.getString("shift_day");
                String sTime = rs.getString("start_time");
                String eTime = rs.getString("end_time");

                System.out.println(nsID + ", " + nID + ", " + sday + ", " + sTime + ", " + eTime);
            }

            pstmt.close();
            rs.close();
            conn.close();

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return nurseShifts;
    }

    public boolean updateNurseShift(NurseShift ns)
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "UPDATE nurse_shift SET shift_day = ?, start_time = ?, end_time = ?  WHERE nurseShift_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ns.getShiftDay());
            pstmt.setString(2, ns.getStartTime());
            pstmt.setString(3, ns.getEndTime());
            pstmt.setInt(4, ns.getNurseShiftID());

            int rowsAffected = pstmt.executeUpdate();

            if(rowsAffected > 0)
            {
                System.out.println("\nNurseShift with id = " + ns.getNurseShiftID() + " has been updated!");
                pstmt.close();
                conn.close();
                return true;
            }
            else
            {
                System.out.println("NurseShift record update failed.");
                pstmt.close();
                conn.close();
                return false;
            }

        }catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteNurseShift(int nurseShiftID) //DELETE
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "DELETE FROM nurse_shift WHERE nurseShift_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, nurseShiftID);

            int rowsAffected = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            if(rowsAffected > 0)
            {
                System.out.println("NurseShift with id = " + nurseShiftID + " deleted successfully!");
                return true;
            }
            else
            {
                System.out.println("Nurse Shift deletion failed");
                return false;
            }

        }catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }



    public static void main(String[] args)
    {
        NurseShift ns = new NurseShift(1, "Monday", "07:00", "19:00");
        NurseShiftManagement nsm = new NurseShiftManagement();
        NurseShift updateNs = new NurseShift(1, "Tuesday", "09:00", "19:00");

        nsm.createNurseShift(updateNs);
//        nsm.updateNurseShift(updateNs);
//        nsm.deleteNurseShift(5);
        nsm.viewNurseShifts();


    }

}
