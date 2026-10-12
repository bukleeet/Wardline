package model;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class TreatmentManagement {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dbhospital_final";
    private static final String USER = "root";
    private static final String PASSWORD = "infom123";
    private Connection conn;
    PreparedStatement pstmt;

    public boolean createTreatmentRecord(Treatment treatment)
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");
            //ensure treatment date is consistent w/ nurse, physician from diagnosis, or assigned physician
            if(treatment.getPerformedBy().equalsIgnoreCase("Assigned Physician") && treatment.getAssignedPhysician() != null) //if there is an assigned physician, compare that physicians schedule to treatment date
            {
                String getPhysicianSched = "SELECT schedule_day FROM physician_schedule ps " +
                                            "WHERE physician_id = ?";
                pstmt = conn.prepareStatement(getPhysicianSched);
                pstmt.setInt(1, treatment.getAssignedPhysician());
                ResultSet rs = pstmt.executeQuery();

                if(rs.next())
                {
                    String scheduleDay = rs.getString("schedule_day"); //get assigned physician's schedule day
                    java.sql.Date treatmendDate = treatment.getTreatmentDate();
                    String sTreatDate = treatmendDate.toLocalDate().getDayOfWeek().toString();

                    if(!scheduleDay.equalsIgnoreCase(sTreatDate))
                    {
                        System.out.println("Cannot create treatment record. Treatment date is not within the assigned physician's schedule");
                        rs.close();
                        pstmt.close();
                        conn.close();
                        return false;
                    }
                }
                rs.close();
                pstmt.close();
            }
            else if(treatment.getPerformedBy().equalsIgnoreCase("Diagnosing Physician") && treatment.getAssignedPhysician() == null) //otherwise, compare diagnosing physician or nurse schedule
            {
                String getPhysicianSched = "SELECT schedule_day FROM physician_schedule ps " +
                        "JOIN diagnosis d ON ps.physicianSchedule_id = d.physicianSchedule_id " +
                        "WHERE d.diagnosis_id = ?";
                pstmt = conn.prepareStatement(getPhysicianSched);
                pstmt.setInt(1, treatment.getDiagnosisID());
                ResultSet rs = pstmt.executeQuery();

                if(rs.next())
                {
                    String scheduleDay = rs.getString("schedule_day"); //get assigned physician's schedule day
                    java.sql.Date treatmendDate = treatment.getTreatmentDate();
                    String sTreatDate = treatmendDate.toLocalDate().getDayOfWeek().toString();

                    if(!scheduleDay.equalsIgnoreCase(sTreatDate))
                    {
                        System.out.println("Cannot create treatment record. Treatment date is not within the diagnosing physician's schedule");
                        rs.close();
                        pstmt.close();
                        conn.close();
                        return false;
                    }
                }
                rs.close();
                pstmt.close();
            }
            else
            {
                String getNurseSched = "SELECT shift_day FROM nurse_shift ns " +
                        "JOIN nurse_assignment na ON ns.nurseShift_id = na.nurseShift_id " +
                        "WHERE na.nurseAssignment_id = ?";
                pstmt = conn.prepareStatement(getNurseSched);
                pstmt.setInt(1, treatment.getNurseAssignment_id());
                ResultSet rs = pstmt.executeQuery();

                if(rs.next())
                {
                    String shiftDay = rs.getString("shift_day"); //get assigned physician's schedule day
                    java.sql.Date treatmendDate = treatment.getTreatmentDate();
                    String sTreatDate = treatmendDate.toLocalDate().getDayOfWeek().toString();

                    if(!shiftDay.equalsIgnoreCase(sTreatDate))
                    {
                        System.out.println("Cannot create treatment record. Treatment date is not within the assigned nurse's schedule");
                        rs.close();
                        pstmt.close();
                        conn.close();
                        return false;
                    }
                }
                rs.close();
                pstmt.close();
            }


            String sql = "INSERT INTO treatment (nurseAssignment_id, diagnosis_id, medicine_id, treatment_date, treatment_procedure, remarks, assignedPhysician_id, performed_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, treatment.getNurseAssignment_id());
            pstmt.setInt(2, treatment.getDiagnosisID());
            pstmt.setInt(3, treatment.getMedicineID());
            pstmt.setDate(4, treatment.getTreatmentDate());
            pstmt.setString(5, treatment.getProcedure());
            pstmt.setString(6, treatment.getRemarks());
            if(treatment.getAssignedPhysician() != null)
                pstmt.setInt(7, treatment.getAssignedPhysician());
            else
                pstmt.setNull(7, Types.INTEGER); //sets value to null

            pstmt.setString(8, treatment.getPerformedBy());

            pstmt.executeUpdate();
            System.out.println("Treatment Record inserted successfully!");

            pstmt.close();
            conn.close();
            return true;

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    public List<Treatment> viewTreatmentRecords() //READ
    {
        List<Treatment> treatments = new ArrayList<>();
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "SELECT * FROM treatment";
            pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();// used for queries that returns result
            while(rs.next())
            {
                Treatment t = new Treatment(rs.getInt("treatment_id"));
                t.setNurseAssignment_id(rs.getInt("nurseAssignment_id"));
                t.setDiagnosis_id(rs.getInt("diagnosis_id"));
                t.setMedicine_id(rs.getInt("medicine_id"));
                t.setTreatment_date(rs.getDate("treatment_date"));
                t.setTreatment_procedure(rs.getString("treatment_procedure"));
                t.setRemarks(rs.getString("remarks"));
                t.setAssignedPhysicianID((Integer) rs.getObject("assignedPhysician_id"));
                t.setPerformed_by(rs.getString("performed_by"));

                treatments.add(t);

                int tID = rs.getInt("treatment_id");
                int naID = rs.getInt("nurseAssignment_id");
                int dID = rs.getInt("diagnosis_id");
                int mID = rs.getInt("medicine_id");
                String date = rs.getString("treatment_date");
                String procedure = rs.getString("treatment_procedure");
                String remarks = rs.getString("remarks");
                Integer pID = (Integer) rs.getObject("assignedPhysician_id");
                String performed = rs.getString("performed_by");
                System.out.println(tID + ", " + naID + ", " + dID + ", " + mID + ", " + date + ", " + procedure + ", " + remarks + ", " + pID + ", " + performed);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return treatments;
    }

    public boolean updateTreatmentRecord(Treatment treatment)
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            //ensure treatment date is consistent w/ nurse, physician from diagnosis, or assigned physician
            if(treatment.getPerformedBy().equalsIgnoreCase("Assigned Physician") && treatment.getAssignedPhysician() != null) //if there is an assigned physician, compare that physicians schedule to treatment date
            {
                String getPhysicianSched = "SELECT schedule_day FROM physician_schedule ps " +
                        "WHERE physician_id = ?";
                pstmt = conn.prepareStatement(getPhysicianSched);
                pstmt.setInt(1, treatment.getAssignedPhysician());
                ResultSet rs = pstmt.executeQuery();

                if(rs.next())
                {
                    String scheduleDay = rs.getString("schedule_day"); //get assigned physician's schedule day
                    java.sql.Date treatmendDate = treatment.getTreatmentDate();
                    String sTreatDate = treatmendDate.toLocalDate().getDayOfWeek().toString();

                    if(!scheduleDay.equalsIgnoreCase(sTreatDate))
                    {
                        System.out.println("Cannot update treatment record. Treatment date is not within the assigned physician's schedule");
                        rs.close();
                        pstmt.close();
                        conn.close();
                        return false;
                    }
                }
                rs.close();
                pstmt.close();

            }
            else if(treatment.getPerformedBy().equalsIgnoreCase("Diagnosing Physician") && treatment.getAssignedPhysician() == null) //otherwise, compare diagnosing physician or nurse schedule
            {
                String getPhysicianSched = "SELECT schedule_day FROM physician_schedule ps " +
                        "JOIN diagnosis d ON ps.physicianSchedule_id = d.physicianSchedule_id " +
                        "WHERE d.diagnosis_id = ?";
                pstmt = conn.prepareStatement(getPhysicianSched);
                pstmt.setInt(1, treatment.getDiagnosisID());
                ResultSet rs = pstmt.executeQuery();

                if(rs.next())
                {
                    String scheduleDay = rs.getString("schedule_day"); //get assigned physician's schedule day
                    java.sql.Date treatmendDate = treatment.getTreatmentDate();
                    String sTreatDate = treatmendDate.toLocalDate().getDayOfWeek().toString();

                    if(!scheduleDay.equalsIgnoreCase(sTreatDate))
                    {
                        System.out.println("Cannot update treatment record. Treatment date is not within the diagnosing physician's schedule");
                        rs.close();
                        pstmt.close();
                        conn.close();
                        return false;
                    }
                }
                rs.close();
                pstmt.close();
            }
            else
            {
                String getNurseSched = "SELECT shift_day FROM nurse_shift ns " +
                        "JOIN nurse_assignment na ON ns.nurseShift_id = na.nurseShift_id " +
                        "WHERE na.nurseAssignment_id = ?";
                pstmt = conn.prepareStatement(getNurseSched);
                pstmt.setInt(1, treatment.getNurseAssignment_id());
                ResultSet rs = pstmt.executeQuery();

                if(rs.next())
                {
                    String shiftDay = rs.getString("shift_day"); //get assigned physician's schedule day
                    java.sql.Date treatmendDate = treatment.getTreatmentDate();
                    String sTreatDate = treatmendDate.toLocalDate().getDayOfWeek().toString();

                    if(!shiftDay.equalsIgnoreCase(sTreatDate))
                    {
                        System.out.println("Cannot update treatment record. Treatment date is not within the assigned nurse's schedule");
                        rs.close();
                        pstmt.close();
                        conn.close();
                        return false;
                    }
                }
                rs.close();
                pstmt.close();
            }


            String sql = "UPDATE treatment SET nurseAssignment_id = ?, diagnosis_id = ?, medicine_id = ?, treatment_date = ?, treatment_procedure = ?, remarks = ?, assignedPhysician_id = ?, performed_by = ? WHERE treatment_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, treatment.getNurseAssignment_id());
            pstmt.setInt(2, treatment.getDiagnosisID());
            pstmt.setInt(3, treatment.getMedicineID());
            pstmt.setDate(4, treatment.getTreatmentDate());
            pstmt.setString(5, treatment.getProcedure());
            pstmt.setString(6, treatment.getRemarks());
            if(treatment.getAssignedPhysician() != null)
                pstmt.setInt(7, treatment.getAssignedPhysician());
            else
                pstmt.setNull(7, Types.INTEGER);
            pstmt.setString(8, treatment.getPerformedBy());
            pstmt.setInt(9, treatment.getTreatmentID());


            int rowsAffected = pstmt.executeUpdate();

            if(rowsAffected > 0)
            {
                System.out.println("Treatment record with id = " + treatment.getTreatmentID() + " has been updated!");
                pstmt.close();
                conn.close();
                return true;
            }
            else
            {
                System.out.println("Treatment record update failed.");
                pstmt.close();
                conn.close();
                return false;
            }


        }catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteTreatmentRecord(int tID) //DELETE
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "DELETE FROM treatment WHERE treatment_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, tID); //1 is position placeholder of ? in sql variable. since 1 lang ung ?, it starts at 1

            int rowsAffected = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            if(rowsAffected > 0)
            {
                System.out.println("Treatment record with id = " + tID + " deleted successfully!");
                return true;
            }
            else
            {
                System.out.println("Treatment assignment deletion failed");
                return false;
            }

        }catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static void main(String[] args)
    {
        Treatment treatment = new Treatment(7001, 6001, 4001, Date.valueOf("2025-11-11"), "Administered medicine", "See if patient condition alleviates", null, "Nurse");
        TreatmentManagement tm = new TreatmentManagement();
        treatment.setTreatmentID(9001);

//        tm.createTreatmentRecord(treatment);
        tm.updateTreatmentRecord(treatment);
        tm.viewTreatmentRecords();

    }
}
