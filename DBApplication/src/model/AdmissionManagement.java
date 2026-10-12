package model;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class AdmissionManagement {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dbhospital_final";
    private static final String USER = "root";
    private static final String PASSWORD = "infom123";
    private Connection conn;
    PreparedStatement pstmt;

    public boolean createAdmissionRecord(Admission admission)
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String getWardOccupancy = "SELECT a.admission_id, a.patient_id, a.ward_id, a.admission_id " +
                                       "FROM admission a LEFT JOIN discharge d ON a.admission_id = d.admission_id " +
                                        "WHERE a.ward_id = ? AND d.discharge_id IS NULL";

            pstmt = conn.prepareStatement(getWardOccupancy);
            pstmt.setInt(1, admission.getWardID());
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) //if it has a result, ward is occupied, cannot create admission
            {
                System.out.println("Cannot create admission record, the specified ward is currently occupied!");
                return false;
            }

            //if it doesnt fail, ward is unoccupied, insert the record successfully
            String sql = "INSERT INTO admission (patient_id, ward_id, admission_date) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, admission.getPatientID());
            pstmt.setInt(2, admission.getWardID());
            pstmt.setString(3, admission.getAdmissionDate());

            pstmt.executeUpdate();
            System.out.println("Admission Record inserted successfully!");

            pstmt.close();
            conn.close();
            return true;

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Admission> viewPatientAdmission() //READ
    {
        List<Admission> admissions = new ArrayList<>();
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "SELECT * FROM admission";
            pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();// used for queries that returns result
            while(rs.next())
            {
                Admission a = new Admission(rs.getInt("admission_id"));
                a.setPatient_id(rs.getInt("patient_id"));
                a.setWard_id(rs.getInt("ward_id"));
                a.setAdmission_date(rs.getString("admission_date"));

                admissions.add(a);

                int admissionID = rs.getInt("admission_id");
                int pID = rs.getInt("patient_id");
                int wID = rs.getInt("ward_id");
                String admissionDate = rs.getString("admission_date");

                System.out.println(admissionID + ", " + pID + ", " + wID + ", " + admissionDate);
            }

            pstmt.close();
            rs.close();
            conn.close();

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return admissions;
    }

    public boolean updateAdmissionRecord(Admission admission)
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String getWardOccupancy = "SELECT a.admission_id, a.patient_id, a.ward_id, a.admission_id " +
                    "FROM admission a LEFT JOIN discharge d ON a.admission_id = d.admission_id " +
                    "WHERE a.ward_id != ? AND d.discharge_id IS NULL AND a.admission_id = ?";

            pstmt = conn.prepareStatement(getWardOccupancy);
            pstmt.setInt(1, admission.getWardID());
            pstmt.setInt(2, admission.getAdmissionID());
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) //if it has a result, ward is occupied, cannot create admission
            {
                System.out.println("Cannot update admission record, the specified ward is currently occupied!");
                return false;
            }

            rs.close();
            pstmt.close();

            String sql = "UPDATE admission SET patient_id = ?, ward_id = ?, admission_date = ? WHERE admission_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, admission.getPatientID());
            pstmt.setInt(2, admission.getWardID());
            pstmt.setString(3, admission.getAdmissionDate());
            pstmt.setInt(4, admission.getAdmissionID());

            int rowsAffected = pstmt.executeUpdate();

            if(rowsAffected > 0)
            {
                System.out.println("Admission with id = " + admission.getAdmissionID() + " has been updated!");
                pstmt.close();
                conn.close();
                return true;
            }
            else
            {
                System.out.println("Admission record update failed.");
                pstmt.close();
                conn.close();
                return false;
            }

        }catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteAdmissionRecord(int admissionID) //DELETE
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "DELETE FROM admission WHERE admission_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, admissionID); //1 is position placeholder of ? in sql variable. since 1 lang ung ?, it starts at 1

            int rowsAffected = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            if(rowsAffected > 0)
            {
                System.out.println("Admission record with id = " + admissionID + " deleted successfully!");
                return true;
            }
            else
            {
                System.out.println("Admission deletion failed");
                return false;
            }

        }catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }




    public static void main(String[] args)
    {
        AdmissionManagement am = new AdmissionManagement();
        Admission a = new Admission(101, 501, "2025-11-11");
        Admission updateA = new Admission(103, 501, "2025-11-12");
//        am.createAdmissionRecord(a);
        updateA.setAdmissionID(8005);
        am.updateAdmissionRecord(updateA);
//        am.deleteAdmissionRecord(8003);
        am.viewPatientAdmission();
    }
}
