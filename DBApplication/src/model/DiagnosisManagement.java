package model;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class DiagnosisManagement {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dbhospital_final";
    private static final String USER = "root";
    private static final String PASSWORD = "infom123";
    private Connection conn;
    PreparedStatement pstmt;

    public boolean createDiagnosisRecord(Diagnosis diagnosis)
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String getPhysicianSched = "SELECT schedule_day FROM physician_schedule ps " +
                    "WHERE physicianSchedule_id = ?";
            pstmt = conn.prepareStatement(getPhysicianSched);
            pstmt.setInt(1, diagnosis.getPhysicianSchedule_id());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String scheduleDay = rs.getString("schedule_day"); //get physician's schedule day
                String sDiagnosisDate = diagnosis.getDiagnosis_date().toLocalDate().getDayOfWeek().toString();

                if (!scheduleDay.equalsIgnoreCase(sDiagnosisDate)) {
                    System.out.println("Cannot create Diagnosis Record. Diagnosis date is not within the assigned physician's schedule");
                    rs.close();
                    pstmt.close();
                    conn.close();
                    return false;
                }
            }
            rs.close();
            pstmt.close();

            String sql = "INSERT INTO diagnosis (patient_id, physicianSchedule_id, illness_id, diagnosis_date, notes) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, diagnosis.getPatient_id());
            pstmt.setInt(2, diagnosis.getPhysicianSchedule_id());
            pstmt.setInt(3, diagnosis.getIllness_id());
            pstmt.setDate(4, diagnosis.getDiagnosis_date());
            pstmt.setString(5, diagnosis.getNotes());

            pstmt.executeUpdate();
            System.out.println("Diagnosis Record inserted successfully!");

            pstmt.close();
            conn.close();
            return true;

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Diagnosis> viewPatientDiagnosis() //READ
    {
        List<Diagnosis> diagnoses = new ArrayList<>();
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "SELECT * FROM diagnosis";
            pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();// used for queries that returns result
            while(rs.next())
            {
                Diagnosis diagnosis = new Diagnosis(rs.getInt("diagnosis_id"));
                diagnosis.setPatient_id(rs.getInt("patient_id"));
                diagnosis.setPhysicianSchedule_id(rs.getInt("physicianSchedule_id"));
                diagnosis.setIllness_id(rs.getInt("illness_id"));
                diagnosis.setDiagnosis_date(rs.getDate("diagnosis_date"));
                diagnosis.setNotes(rs.getString("notes"));

                diagnoses.add(diagnosis);

                int diagnosisID = rs.getInt("diagnosis_id");
                int pID = rs.getInt("patient_id");
                int phySchedID = rs.getInt("physicianSchedule_id");
                int illID = rs.getInt("illness_id");
                java.sql.Date diagnosis_date = rs.getDate("diagnosis_date");
                String notes = rs.getString("notes");

                System.out.println(diagnosisID + ", " + pID + ", " + phySchedID + ", " + illID + ", " + diagnosis_date + ", " + notes);
            }

            pstmt.close();
            rs.close();
            conn.close();

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return diagnoses;
    }

    public boolean updateDiagnosisRecord(Diagnosis diagnosis)
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String getPhysicianSched = "SELECT schedule_day FROM physician_schedule ps " +
                    "WHERE physicianSchedule_id = ?";
            pstmt = conn.prepareStatement(getPhysicianSched);
            pstmt.setInt(1, diagnosis.getPhysicianSchedule_id());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String scheduleDay = rs.getString("schedule_day"); //get physician's schedule day
                java.sql.Date diagnosisDate = diagnosis.getDiagnosis_date();
                String sDiagnosisDate = diagnosisDate.toLocalDate().getDayOfWeek().toString();

                if (!scheduleDay.equalsIgnoreCase(sDiagnosisDate)) {
                    System.out.println("Cannot update Diagnosis Record. Diagnosis date is not within the assigned physician's schedule");
                    rs.close();
                    pstmt.close();
                    conn.close();
                    return false;
                }
            }
            rs.close();
            pstmt.close();


            String sql = "UPDATE diagnosis SET patient_id = ?, physicianSchedule_id = ?, illness_id = ?, diagnosis_date = ?, notes = ? WHERE diagnosis_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, diagnosis.getPatient_id());
            pstmt.setInt(2, diagnosis.getPhysicianSchedule_id());
            pstmt.setInt(3, diagnosis.getIllness_id());
            pstmt.setDate(4, diagnosis.getDiagnosis_date());
            pstmt.setString(5, diagnosis.getNotes());
            pstmt.setInt(6, diagnosis.getDiagnosis_id());

            int rowsAffected = pstmt.executeUpdate();

            if(rowsAffected > 0)
            {
                System.out.println("Diagnosis with id = " + diagnosis.getDiagnosis_id() + " has been updated!");
                pstmt.close();
                conn.close();
                return true;
            }
            else
            {
                System.out.println("Diagnosis record update failed.");
                pstmt.close();
                conn.close();
                return false;
            }


        }catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteDiagnosisRecord(int diagnosis_id) //DELETE
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "DELETE FROM diagnosis WHERE diagnosis_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, diagnosis_id); //1 is position placeholder of ? in sql variable. since 1 lang ung ?, it starts at 1

            int rowsAffected = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            if(rowsAffected > 0)
            {
                System.out.println("Diagnosis record with id = " + diagnosis_id + " deleted successfully!");
                return true;
            }
            else
            {
                System.out.println("Diagnosis deletion failed");
                return false;
            }

        }catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static void main(String[] args)
    {
        DiagnosisManagement diagnosisManagement = new DiagnosisManagement();

//        diagnosisManagement.createDischargeRecord()
        diagnosisManagement.viewPatientDiagnosis();

    }
}