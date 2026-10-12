package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhysicianManagement {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dbhospital_final";
    private static final String USER = "root";
    private static final String PASSWORD = "infom123";
    private Connection conn;
    PreparedStatement pstmt;

    public boolean createPhysicianRecord(Physician physician)
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "INSERT INTO physician (ph_firstname, ph_lastname, contact_no, specialization) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, physician.getFirstName());
            pstmt.setString(2, physician.getLastName());
            pstmt.setString(3, physician.getContact());
            pstmt.setString(4, physician.getSpecialization());

            pstmt.executeUpdate();
            System.out.println("Physician Record inserted successfully!");

            pstmt.close();
            conn.close();
            return true;

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Physician> viewPhysicianRecords() //READ
    {
        List<Physician> physicians = new ArrayList<>();
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "SELECT * FROM physician";
            pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();// used for queries that returns result
            while(rs.next())
            {
                Physician physician = new Physician(rs.getInt("physician_id"));
                physician.setLastName(rs.getString("ph_lastname"));
                physician.setFirstName(rs.getString("ph_firstname"));
                physician.setContact(rs.getString("contact_no"));
                physician.setSpecialization(rs.getString("specialization"));

                int physicianID = rs.getInt("physician_id");
                String ln = rs.getString("ph_lastname");
                String fn = rs.getString("ph_firstname");
                String no = rs.getString("contact_no");
                String sp = rs.getString("specialization");

                System.out.println(physicianID + ", " + ln + ", " + fn + ", " + no + ", " + sp);
                physicians.add(physician);
            }

            pstmt.close();
            rs.close();
            conn.close();

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return physicians;
    }

    public boolean updatePhysicianRecord(Physician physician)
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "UPDATE physician SET ph_firstname = ?, ph_lastname = ?, contact_no = ?, specialization = ? WHERE physician_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, physician.getFirstName());
            pstmt.setString(2, physician.getLastName());
            pstmt.setString(3, physician.getContact());
            pstmt.setString(4, physician.getSpecialization());
            pstmt.setInt(5, physician.getPhysicianID());

            int rowsAffected = pstmt.executeUpdate();

            if(rowsAffected > 0)
            {
                System.out.println("Physician with id = " + physician.getPhysicianID() + " has been updated!");
                pstmt.close();
                conn.close();
                return true;
            }
            else
            {
                System.out.println("Physician record update failed.");
                pstmt.close();
                conn.close();
                return false;
            }


        }catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deletePhysicianRecord(int physicianID) //DELETE
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "DELETE FROM physician WHERE physician_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, physicianID); //1 is position placeholder of ? in sql variable. since 1 lang ung ?, it starts at 1

            int rowsAffected = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            if(rowsAffected > 0)
            {
                System.out.println("physician with id = " + physicianID + " deleted successfully!");
                return true;
            }
            else
            {
                System.out.println("Physician deletion failed");
                return false;
            }

        }catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Object[]> physicianRelatedRecord(int id)
    {
        List<Object[]> records = new ArrayList<>();
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "SELECT\n" +
                    "\tCONCAT(pd.ph_lastname, ', ', pd.ph_firstname) AS physician_name,\n" +
                    "    CONCAT(p.p_lastname, ', ', p.p_firstname) AS patient_name,\n" +
                    "    i.illness_name,\n" +
                    "    t.treatment_id,\n" +
                    "    t.treatment_date,\n" +
                    "    t.treatment_procedure\n" +
                    "FROM treatment t\n" +
                    "LEFT JOIN nurse_assignment na ON t.nurseAssignment_id = na.nurseAssignment_id\n" +
                    "LEFT JOIN patient p ON na.patient_id = p.patient_id\n" +
                    "LEFT JOIN diagnosis d ON t.diagnosis_id = d.diagnosis_id\n" +
                    "LEFT JOIN physician_schedule ps ON d.physicianSchedule_id = ps.physicianSchedule_id\n" +
                    "LEFT JOIN physician pd ON ps.physician_id = pd.physician_id\n" +
                    "LEFT JOIN physician pa ON t.assignedPhysician_id = pa.physician_id\n" +
                    "LEFT JOIN illness i ON d.illness_id = i.illness_id\n" +
                    "WHERE t.performed_by LIKE '%Physician%' AND (pa.physician_id = ? OR pd.physician_id = ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.setInt(2, id);


            ResultSet rs = pstmt.executeQuery();// used for queries that returns result
            while(rs.next())
            {
                Object[] row = new Object[6];
                row[0] = rs.getString("physician_name");
                row[1] = rs.getString("patient_name");
                row[2] = rs.getString("illness_name");
                row[3] = rs.getString("treatment_id");
                row[4] = rs.getString("treatment_date");
                row[5] = rs.getString("treatment_procedure");
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




    public static void main(String[] args)
    {
        Physician ph = new Physician("Meredith", "Gray", "+639166825678", "General");
        PhysicianManagement pm = new PhysicianManagement();

//        Physician updatedPh = new Physician("Meredith", "Grey", "+63 9116685678", "General");
//        updatedPh.setPhysician_id(2002);

        pm.createPhysicianRecord(ph);
//        pm.updatePhysicianRecord(updatedPh);
//        pm.deletePhysicianRecord(2002);
        pm.viewPhysicianRecords();
    }
}
