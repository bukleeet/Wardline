package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NurseManagement {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dbhospital_final";
    private static final String USER = "root";
    private static final String PASSWORD = "infom123";
    private Connection conn;
    PreparedStatement pstmt;

    public boolean registerNurse(Nurse nurse) //CREATE
    {
        try{
            //This is where we will put codes that will interact w/ database
            //1. connect to database
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            //2. Prepare SQL Statement --> store in PreparedStatement (dont forget to put alias for column name so u can fetch the value)
            String sql = "INSERT INTO nurse (n_lastname, n_firstname, contact_no) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, nurse.getLastName());
            pstmt.setString(2, nurse.getFirstName());
            pstmt.setString(3, nurse.getContactNo());

            pstmt.executeUpdate(); //used for queries that modifies the table
            System.out.println("Nurse Record inserted successfully!");


            //3. After executing query, store result in ResultSet
            //4. To get result, use while(rst.next)

            pstmt.close();
            conn.close();
            return true;

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Nurse> viewNurseRecords() //READ
    {
        List<Nurse> nurses = new ArrayList<>();
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "SELECT * FROM nurse";
            pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();// used for queries that returns result
            while(rs.next())
            {
                Nurse nurse = new Nurse(rs.getInt("nurse_id"));

                int nurseID = rs.getInt("nurse_id");
                String ln = rs.getString("n_lastname");
                String fn = rs.getString("n_firstname");
                String no = rs.getString("contact_no");
                nurses.add(nurse);

                System.out.println(nurseID + ", " + ln + ", " + fn + ", " + no);

                nurse.setLastName(rs.getString("n_lastname"));
                nurse.setFirstName(rs.getString("n_firstname"));
                nurse.setContact(rs.getString("contact_no"));

            }

            pstmt.close();
            rs.close();
            conn.close();

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return nurses;
    }

    public boolean updateNurseRecord(Nurse nurse)
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "UPDATE nurse SET n_firstname = ?, n_lastname = ?, contact_no = ? WHERE nurse_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nurse.getFirstName());
            pstmt.setString(2, nurse.getLastName());
            pstmt.setString(3, nurse.getContactNo());
            pstmt.setInt(4, nurse.getNurseID());

            int rowsAffected = pstmt.executeUpdate();

            if(rowsAffected > 0)
            {
                System.out.println("Nurse with id = " + nurse.getNurseID() + " has been updated!");
                pstmt.close();
                conn.close();
                return true;
            }
            else
            {
                System.out.println("Nurse record update failed.");
                pstmt.close();
                conn.close();
                return false;
            }


        }catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteNurseRecord(int nurseID) //DELETE
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "DELETE FROM nurse WHERE nurse_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, nurseID); //1 is position placeholder of ? in sql variable. since 1 lang ung ?, it starts at 1

            int rowsAffected = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            if(rowsAffected > 0)
            {
                System.out.println("Nurse with id = " + nurseID + " deleted successfully!");
                return true;
            }
            else
            {
                System.out.println("Nurse deletion failed");
                return false;
            }

        }catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Object[]> nurseRelatedRecord(int id)
    {
        List<Object[]> records = new ArrayList<>();
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "SELECT CONCAT(n.n_lastname, ', ', n.n_firstname) AS nurse_name,\n" +
                    "\t   CONCAT(p.p_lastname, ', ', p.p_firstname) AS patient_name,\n" +
                    "       w.ward_number, w.floor,\n" +
                    "\t   t.treatment_procedure,\n" +
                    "       t.treatment_date\n" +
                    "FROM nurse n\n" +
                    "\tLEFT JOIN nurse_shift ns ON n.nurse_id = ns.nurse_id\n" +
                    "    LEFT JOIN nurse_assignment na ON ns.nurseShift_id = na.nurseShift_id\n" +
                    "    LEFT JOIN patient p ON na.patient_id = p.patient_id\n" +
                    "    LEFT JOIN treatment t ON na.nurseAssignment_id = t.nurseAssignment_id\n" +
                    "    LEFT JOIN admission a ON p.patient_id = a.patient_id\n" +
                    "    LEFT JOIN ward w ON a.ward_id = w.ward_id\n" +
                    "WHERE  n.nurse_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();// used for queries that returns result
            while(rs.next())
            {
                Object[] row = new Object[6];
                row[0] = rs.getString("nurse_name");
                row[1] = rs.getString("patient_name");
                row[2] = rs.getString("ward_number");
                row[3] = rs.getString("floor");
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


    public static void main(String[] args){
        Nurse nurse = new Nurse("Marta", "Lualdi", "+63 9645138314");
        NurseManagement nurseMgmt = new NurseManagement();

        nurseMgmt.registerNurse(nurse);
//        nurseMgmt.deleteNurseRecord(1002);
        nurseMgmt.viewNurseRecords();

    }
}
