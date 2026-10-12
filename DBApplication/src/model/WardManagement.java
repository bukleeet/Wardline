package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WardManagement {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dbhospital_final";
    private static final String USER = "root";
    private static final String PASSWORD = "infom123";
    private Connection conn;
    PreparedStatement pstmt;

    public boolean createWard(Ward ward) //create
    {
        try {
            //This is where we will put codes that will interact w/ database
            //1. connect to database
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            //2. Prepare SQL Statement --> store in PreparedStatement (dont forget to put alias for column name so u can fetch the value)
            String sql = "INSERT INTO ward (floor, ward_number, w_status) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, ward.getFloor());
            pstmt.setInt(2, ward.getWardNo());
            pstmt.setString(3, ward.getStatus());

            pstmt.executeUpdate();
            System.out.println("Record inserted successfully!");


            //3. After executing query, store result in ResultSet
            //4. To get result, use while(rst.next)

            pstmt.close();
            conn.close();
            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Ward> viewWardRecords() // read
    {
        List<Ward> wards = new ArrayList<>();
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "SELECT * FROM ward";
            pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Ward ward = new Ward(rs.getInt("ward_id"));
                ward.setWardNo(rs.getInt("ward_number"));
                ward.setFloor(rs.getString("floor"));
                ward.setStatus(rs.getString("w_status"));
                wards.add(ward);

                int ward_id = rs.getInt("ward_id");
                int ward_no = rs.getInt("ward_number");
                String floor = rs.getString("floor");
                String status = rs.getString("w_status");

                System.out.println(ward_id + " " + ward_no + " " + floor + " " + status);

            }

            pstmt.close();
            rs.close();
            conn.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return wards;
    }

    public boolean updateWard(Ward ward) {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "UPDATE ward SET floor = ?, ward_number = ?, w_status = ? WHERE ward_id = ?";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, ward.getFloor()); // new floor
            pstmt.setInt(2, ward.getWardNo());
            pstmt.setString(3, ward.getStatus());
            pstmt.setInt(4, ward.getWard_id()); // new ward id
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("model.Ward updated successfully!");
                pstmt.close();
                conn.close();
                return true;
            } else {
                System.out.println("model.Ward ID not found.");
                pstmt.close();
                conn.close();
                return false;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteWard(Ward ward) {
        // SQL commands (check and delete)

        // business rule: check first if there are pateints in the ward
        String check_sql = "SELECT COUNT(*) AS patientCount FROM admission WHERE ward_id = ?";
        String delete_sql = "DELETE FROM ward  WHERE ward_id = ?";


        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");
            pstmt = conn.prepareStatement(check_sql);

            pstmt.setInt(1, ward.getWard_id());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt("patientCount") > 0) {
                System.out.println("There is a patient assigned. This ward cannot be deleted.");
                return false;
            }

            // close the check before the delete
            pstmt.close();

            // if no patients
            pstmt = conn.prepareStatement(delete_sql);
            pstmt.setInt(1, ward.getWard_id());
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("model.Ward deleted successfully!");
                pstmt.close();
                return true;
            } else {
                System.out.println("Ward_ID not found.");
                pstmt.close();
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        } finally {
            // close everything so no leak
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
            }
        }
    }


    public List<Object[]> wardRelatedRecord(int id)
    {
        List<Object[]> records = new ArrayList<>();
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "SELECT\n" +
                    "    w.ward_number,\n" +
                    "    w.floor,\n" +
                    "    CONCAT (p.p_lastname, ', ', p.p_firstname) AS admitted_patient,\n" +
                    "    CONCAT (n.n_lastname, ', ', n.n_firstname) AS assigned_nurse\n" +
                    "FROM ward w\n" +
                    "LEFT JOIN admission a ON w.ward_id = a.ward_id\n" +
                    "LEFT JOIN  patient p ON a.patient_id = p.patient_id\n" +
                    "LEFT JOIN  nurse_assignment na ON na.patient_id = p.patient_id\n" +
                    "LEFT JOIN  nurse_shift ns ON na.nurseShift_id = ns.nurseShift_id\n" +
                    "LEFT JOIN  nurse n ON ns.nurse_id = n.nurse_id\n" +
                    "WHERE w.ward_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();// used for queries that returns result
            while(rs.next())
            {
                Object[] row = new Object[4];
                row[0] = rs.getInt("ward_number");
                row[1] = rs.getString("floor");
                row[2] = rs.getString("admitted_patient");
                row[3] = rs.getString("assigned_nurse");
                records.add(row);
            }

            pstmt.close();
            rs.close();
            conn.close();

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return records;
    }


    public static void main(String[] args){
        WardManagement wardManagement = new WardManagement();

       // Test create ward
       Ward ward = new model.Ward("3rd Floor", 302, "Available");
       wardManagement.createWard(ward); // put ward in the database

       // Test update ward
//        wardManagement.updateWard(3, "1st Floor");

       // Test delete ward
//       wardManagement.deleteWard(3);
//        // model.Ward record
        wardManagement.viewWardRecords();
    }


}
