package model;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class DischargeManagement {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dbhospital_final";
    private static final String USER = "root";
    private static final String PASSWORD = "infom123";
    private Connection conn;
    PreparedStatement pstmt;

    public boolean createDischargeRecord(Discharge discharge)
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            // no discahrge id since it auto increments
            String sql = "INSERT INTO discharge (admission_id, discharge_date) VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, discharge.getAdmissionID());
            pstmt.setString(2, discharge.getDischargeDate());

            pstmt.executeUpdate();
            System.out.println("Discharge Record inserted successfully!");

            pstmt.close();
            conn.close();
            return true;

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Discharge> viewDischargeRecord() //READ
    {
        List<Discharge> discharges = new ArrayList<>();
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "SELECT * FROM discharge";
            pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();// used for queries that returns result
            while(rs.next())
            {
                Discharge d = new Discharge(rs.getInt("discharge_id"));
                d.setAdmission_id(rs.getInt("admission_id"));
                d.setDischarge_date(rs.getString("discharge_date"));
                discharges.add(d);

                int discharge_id = rs.getInt("discharge_id");
                int admission_id = rs.getInt("admission_id");
                String discharge_date = rs.getString("discharge_date");

                System.out.println(discharge_id + ", " + admission_id + ", " + discharge_date);
            }

            pstmt.close();
            rs.close();
            conn.close();

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return discharges;
    }

    public boolean updateDischargeRecord(Discharge discharge)
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "UPDATE discharge SET admission_id = ?, discharge_date = ? WHERE discharge_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, discharge.getAdmissionID());
            pstmt.setString(2, discharge.getDischargeDate());
            pstmt.setInt(3, discharge.getDischargeID());

            int rowsAffected = pstmt.executeUpdate();

            if(rowsAffected > 0)
            {
                System.out.println("Discharge with id = " + discharge.getDischargeID() + " has been updated!");
                pstmt.close();
                conn.close();
                return true;
            }
            else
            {
                System.out.println("Discharge record update failed.");
                pstmt.close();
                conn.close();
                return false;
            }


        }catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteDischargeRecord(int discharge_id) //DELETE
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "DELETE FROM discharge WHERE discharge_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, discharge_id); //1 is position placeholder of ? in sql variable. since 1 lang ung ?, it starts at 1

            int rowsAffected = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            if(rowsAffected > 0)
            {
                System.out.println("Discharge record with id = " + discharge_id + " deleted successfully!");
                return true;
            }
            else
            {
                System.out.println("Discharge record deletion failed");
                return false;
            }

        }catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static void main(String[] args)
    {
        DischargeManagement dischargeManagement = new DischargeManagement();
//        Discharge d = new Discharge(1, 8001, "2025-11-10");
//        dischargeManagement.createDischargeRecord(d);
        dischargeManagement.viewDischargeRecord();

    }
}
