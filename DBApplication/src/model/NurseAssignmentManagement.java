package model;

import java.sql.*;
import java.time.*;
import java.util.List;
import java.util.ArrayList;

public class NurseAssignmentManagement {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dbhospital_final";
    private static final String USER = "root";
    private static final String PASSWORD = "infom123";
    private Connection conn;
    PreparedStatement pstmt;


    public boolean assignNurseToPatient(NurseAssignment na) //di ko alam if mas maganda ba object or values na mismo ung parameter. will stick to object for now
    { //THIS IS CREATE
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");
            //(1) Get the day of the nurse shift first
            String getNurseShift = "SELECT shift_day FROM nurse_shift WHERE nurseShift_id = ?";
            pstmt = conn.prepareStatement(getNurseShift);
            pstmt.setInt(1, na.getNurseShiftID());
            ResultSet rs = pstmt.executeQuery();

            if(rs.next())
            {
                String shiftDay = rs.getString("shift_day"); //(1) get nurse's day shift in String
                String dayUntil = null;

                //(2) Get assigned_date's and assigned_until's day of week
                java.sql.Date assignedDate = na.getDateAssigned();
                String day = assignedDate.toLocalDate().getDayOfWeek().toString(); //just need to convert it to local date to get the day of the week, then convert to string

                if(na.getAssignedUntil() != null) //because assigned until can be null
                {
                    java.sql.Date assignedUntil = na.getAssignedUntil();
                    dayUntil = assignedUntil.toLocalDate().getDayOfWeek().toString();
                }


                //(3) Compare assigned date (converted) and shiftDay
                if(!shiftDay.equalsIgnoreCase(day) || (dayUntil != null && !shiftDay.equalsIgnoreCase(dayUntil)))
                {
                    System.out.println("Cannot assign nurse. The date does not match their shift date");
                    rs.close();
                    pstmt.close();
                    conn.close();
                    return false;
                }
                rs.close();
                pstmt.close();
            }
            else
            {
                System.out.println("No shift found for nurseShiftID " + na.getNurseShiftID());
                rs.close();
                pstmt.close();
                conn.close();
                return false;
            }

            //if it doesnt fail, meaning consistent ung date assigned sa day ng nurse shift, proceed with insert

            String sql = "INSERT INTO nurse_assignment (nurseShift_id, patient_id, date_assigned, assigned_until) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, na.getNurseShiftID());
            pstmt.setInt(2, na.getPatientID());
            pstmt.setDate(3, na.getDateAssigned());
            if(na.getAssignedUntil() != null)
                pstmt.setDate(4, na.getAssignedUntil());
            else
                pstmt.setNull(4, Types.DATE);

            pstmt.executeUpdate();
            System.out.println("Nurse Assignment Record inserted successfully!");
            pstmt.close();
            conn.close();
            return true;

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<NurseAssignment> viewNurseAssignments() //READ
    {
        List<NurseAssignment> nurseAssignments = new ArrayList<>();
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "SELECT * FROM nurse_assignment";
            pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();// used for queries that returns result
            while(rs.next())
            {
                NurseAssignment na = new NurseAssignment(rs.getInt("nurseAssignment_id"));
                na.setNurseShift_id(rs.getInt("nurseShift_id"));
                na.setPatient_id(rs.getInt("patient_id"));
                na.setDate_assigned(rs.getDate("date_assigned"));
                na.setAssigned_until(rs.getDate("assigned_until"));

                nurseAssignments.add(na);

                int naID = rs.getInt("nurseAssignment_id");
                int nsID = rs.getInt("nurseShift_id");
                int pID = rs.getInt("patient_id");
                String dateAssigned = rs.getString("date_assigned");
                String assignedUntil = rs.getString("assigned_until");

                System.out.println(naID + ", " + nsID + ", " + pID + ", " + dateAssigned + ", " + assignedUntil);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return nurseAssignments;
    }

    public boolean updateNurseAssignment(NurseAssignment na)
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            //(1) Get the day of the nurse shift first
            String getNurseShift = "SELECT shift_day FROM nurse_shift WHERE nurseShift_id = ?";
            pstmt = conn.prepareStatement(getNurseShift);
            pstmt.setInt(1, na.getNurseShiftID());
            ResultSet rs = pstmt.executeQuery();

            if(rs.next())
            {
                String shiftDay = rs.getString("shift_day"); //(1) get nurse's day shift in String
                String dayUntil = null;

                //(2) Get assigned_date's and assigned_until's day of week
                java.sql.Date assignedDate = na.getDateAssigned();
                String day = assignedDate.toLocalDate().getDayOfWeek().toString(); //just need to convert it to local date to get the day of the week, then convert to string

                if(na.getAssignedUntil() != null) //because assigned until can be null
                {
                    java.sql.Date assignedUntil = na.getAssignedUntil();
                    dayUntil = assignedUntil.toLocalDate().getDayOfWeek().toString();
                }


                //(3) Compare assigned date (converted) and shiftDay
                if(!shiftDay.equalsIgnoreCase(day) || (dayUntil != null && !shiftDay.equalsIgnoreCase(dayUntil)))
                {
                    System.out.println("Cannot update nurse assignment record. The date does not match their shift date");
                    rs.close();
                    pstmt.close();
                    conn.close();
                    return false;
                }
                rs.close();
                pstmt.close();
            }
            else
            {
                System.out.println("No shift found for nurseShiftID " + na.getNurseShiftID());
                rs.close();
                pstmt.close();
                conn.close();
                return false;
            }

            //if it doesnt fail, meaning consistent ung date assigned sa day ng nurse shift, proceed with update

            String sql = "UPDATE nurse_assignment SET nurseShift_id = ?, patient_id = ?, date_assigned = ?, assigned_until = ? WHERE nurseAssignment_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, na.getNurseShiftID());
            pstmt.setInt(2, na.getPatientID());
            pstmt.setDate(3, na.getDateAssigned());
            if(na.getAssignedUntil() != null)
                pstmt.setDate(4, na.getAssignedUntil());
            else
                pstmt.setNull(4, Types.DATE);

            pstmt.setInt(5, na.getNurseAssignmentID());

            int rowsAffected = pstmt.executeUpdate();

            if(rowsAffected > 0)
            {
                System.out.println("Nurse Assignment with id = " + na.getNurseAssignmentID() + " has been updated!");
                pstmt.close();
                conn.close();
                return true;
            }
            else
            {
                System.out.println("Nurse Assignment record update failed.");
                pstmt.close();
                conn.close();
                return false;
            }


        }catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteNurseAssignment(int naID) //DELETE
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "DELETE FROM nurse_assignment WHERE nurseAssignment_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, naID); //1 is position placeholder of ? in sql variable. since 1 lang ung ?, it starts at 1

            int rowsAffected = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            if(rowsAffected > 0)
            {
                System.out.println("Nurse assignment record with id = " + naID + " deleted successfully!");
                return true;
            }
            else
            {
                System.out.println("Nurse assignment deletion failed");
                return false;
            }

        }catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    public static void main(String[] args)
    {
        NurseAssignment na = new NurseAssignment(6, 103, Date.valueOf("2025-11-17"), null);
        NurseAssignmentManagement nam = new NurseAssignmentManagement();
        NurseAssignment updateNa = new NurseAssignment(6, 103, Date.valueOf("2025-11-10"), Date.valueOf("2025-11-24"));
        updateNa.setAssignmentID(7002);
//        nam.assignNurseToPatient(na);
        nam.updateNurseAssignment(updateNa);
//        nam.deleteNurseAssignment(updateNa.getNurseAssignmentID());
        nam.viewNurseAssignments();
    }

}
