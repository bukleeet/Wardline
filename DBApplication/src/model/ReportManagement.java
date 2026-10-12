package model;

import model.MakePDF;

import java.sql.*;

public class ReportManagement {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dbhospital_final";
    private static final String USER = "root";
    private static final String PASSWORD = "infom123";
    private Connection conn;
    PreparedStatement pstmt;

   public void getPhysiciansWorkloadReport(String type, int physicianID, String dateOrMonth, Integer year)
    {
        //int totalPatients = 0;

        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");
            String sql = "";


            if(type.equalsIgnoreCase("day"))
            {
                sql = "SELECT COUNT(DISTINCT p.patient_id) AS total_patients, CONCAT(dp.ph_lastname, ', ', dp.ph_firstname) AS physician_name\n" +
                        "FROM treatment t\n" +
                        "LEFT JOIN diagnosis d ON t.diagnosis_id = d.diagnosis_id\n" +
                        "LEFT JOIN physician_schedule ps ON d.physicianSchedule_id = ps.physicianSchedule_id\n" +
                        "LEFT JOIN physician dp ON ps.physician_id = dp.physician_id\n" +
                        "LEFT JOIN patient p ON d.patient_id = p.patient_id\n" +
                        "LEFT JOIN physician ap ON t.assignedPhysician_id = ap.physician_id\n" +
                        "WHERE treatment_date = ? AND (dp.physician_id = ? OR ap.physician_id = ?)" +
                        "GROUP BY dp.physician_id";

                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, dateOrMonth);
                pstmt.setInt(2, physicianID);
                pstmt.setInt(3, physicianID);

            }
            else if(type.equalsIgnoreCase("month"))
            {
                sql = "SELECT COUNT(DISTINCT p.patient_id) AS total_patients, CONCAT(dp.ph_lastname, ', ', dp.ph_firstname) AS physician_name\n" +
                        "FROM treatment t\n" +
                        "LEFT JOIN diagnosis d ON t.diagnosis_id = d.diagnosis_id\n" +
                        "LEFT JOIN physician_schedule ps ON d.physicianSchedule_id = ps.physicianSchedule_id\n" +
                        "LEFT JOIN physician dp ON ps.physician_id = dp.physician_id\n" +
                        "LEFT JOIN patient p ON d.patient_id = p.patient_id\n" +
                        "LEFT JOIN physician ap ON t.assignedPhysician_id = ap.physician_id\n" +
                        "WHERE (MONTH(t.treatment_date) = ? AND YEAR(t.treatment_date) = ?) AND (dp.physician_id = ? OR ap.physician_id = ?)" +
                        "GROUP BY dp.physician_id ";

                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, Integer.parseInt(dateOrMonth));
                pstmt.setInt(2, year);
                pstmt.setInt(3, physicianID);
                pstmt.setInt(4, physicianID);
            }
            else
            {
                sql = "SELECT COUNT(DISTINCT p.patient_id) AS total_patients, CONCAT(dp.ph_lastname, ', ', dp.ph_firstname) AS physician_name\n" +
                        "FROM treatment t\n" +
                        "LEFT JOIN diagnosis d ON t.diagnosis_id = d.diagnosis_id\n" +
                        "LEFT JOIN physician_schedule ps ON d.physicianSchedule_id = ps.physicianSchedule_id\n" +
                        "LEFT JOIN physician dp ON ps.physician_id = dp.physician_id\n" +
                        "LEFT JOIN patient p ON d.patient_id = p.patient_id\n" +
                        "LEFT JOIN physician ap ON t.assignedPhysician_id = ap.physician_id\n" +
                        "WHERE YEAR(treatment_date) = ? AND (dp.physician_id = ? OR ap.physician_id = ?) " +
                        "GROUP BY dp.physician_id ";

                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, year);
                pstmt.setInt(2, physicianID);
                pstmt.setInt(3, physicianID);
            }

            ResultSet rs = pstmt.executeQuery();// used for queries that returns result
            MakePDF.exportToPDF(rs, "Physician_Workload_Report.pdf");


            pstmt.close();
            rs.close();
            conn.close();

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void getIllnessOccurrenceReport (String type, String illness, String dateOrMonth, Integer year){
        int totalPatients = 0;

        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");
            String sql = "";


            if(type.equalsIgnoreCase("day"))
            {
                sql = "SELECT COUNT(DISTINCT patient_id) AS total_patients," +
                        "CONCAT(p_lastname, ', ', p_firstname) AS patient_name," +
                        "illness_name " +
                        "FROM illness_occurrence_view " +
                        "WHERE illness_name = ? " +
                        "AND treatment_date = ? " +
                        "GROUP BY illness_name, p_lastname, p_firstname;";

                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, illness);
                pstmt.setString(2, dateOrMonth);


            }
            else if(type.equalsIgnoreCase("month"))
            {
                sql = "SELECT COUNT(DISTINCT patient_id) AS total_patients," +
                        "CONCAT(p_lastname, ', ', p_firstname) AS patient_name," +
                        "illness_name " +
                        "FROM illness_occurrence_view " +
                        "WHERE illness_name = ? " +
                        "AND MONTH(treatment_date) = ? " +
                        "AND YEAR(treatment_date) = ? " +
                        "GROUP BY illness_name, p_lastname, p_firstname;";

                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, illness);
                pstmt.setInt(2, Integer.parseInt(dateOrMonth));
                pstmt.setInt(3, year);
            }
            else
            {
                sql = "SELECT COUNT(DISTINCT patient_id) AS total_patients," +
                        "CONCAT(p_lastname, ', ', p_firstname) AS patient_name," +
                        "illness_name " +
                        "FROM illness_occurrence_view " +
                        "WHERE illness_name = ? " +
                        "AND YEAR(treatment_date) = ? " +
                        "GROUP BY illness_name, p_lastname, p_firstname;";

                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, illness);
                pstmt.setInt(2, year);
            }

            ResultSet rs = pstmt.executeQuery();// used for queries that returns result
            MakePDF.exportToPDF(rs, "illness_occurrence_report.pdf");

            if(rs.next())
            {
                totalPatients = rs.getInt("Total_Patients");
                System.out.println(totalPatients);
            }

            pstmt.close();
            rs.close();
            conn.close();

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

//        return totalPatients;
    }

    public void getTreatmentStatistics(String type, int treatmentID, String dateOrMonth, Integer year)
    {
        // int totalPatients = 0;

        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "";

            if (type.equalsIgnoreCase("day"))
            {
                sql = "SELECT COUNT(DISTINCT patient_id) AS Total_Patients, " +
                        "GROUP_CONCAT(CONCAT(p_lastname, ', ', p_firstname) SEPARATOR '\n ') AS patient_name " +
                        "FROM treatment_stats_view " +
                        "WHERE treatment_id = ? " +
                        "AND treatment_date = ? " +
                        "GROUP BY p_lastname, p_firstname;";

                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, treatmentID);
                pstmt.setString(2, dateOrMonth);

            }
            else if (type.equalsIgnoreCase("month"))
            {
                sql = "SELECT COUNT(DISTINCT patient_id) AS Total_Patients, " +
                        "GROUP_CONCAT(CONCAT(p_lastname, ', ', p_firstname) SEPARATOR '\n ') AS patient_name " +
                        "FROM treatment_stats_view " +
                        "WHERE treatment_id = ? " +
                        "AND MONTH(treatment_date) = ? " +
                        "AND YEAR(treatment_date) = ? " +
                        "GROUP BY p_lastname, p_firstname;";

                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, treatmentID);
                pstmt.setInt(2, Integer.parseInt(dateOrMonth)); // month
                pstmt.setInt(3, year);

            }
            else
            {
                sql = "SELECT COUNT(DISTINCT patient_id) AS Total_Patients, " +
                        "GROUP_CONCAT(CONCAT(p_lastname, ', ', p_firstname) SEPARATOR '\n ') AS patient_name " +
                        "FROM treatment_stats_view " +
                        "WHERE treatment_id = ? " +
                        "AND YEAR(treatment_date) = ? " +
                        "GROUP BY p_lastname, p_firstname;";

                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, treatmentID);
                pstmt.setInt(2, year);
            }

            ResultSet rs = pstmt.executeQuery();
            MakePDF.exportToPDF(rs, "treatment_stats_report.pdf");

//            if (rs.next())
//            {
//                totalPatients = rs.getInt("Total_Patients");
//                System.out.println(totalPatients);
//            }


            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // return totalPatients;
    }

    public void getAdmissionRateReport (String type, String dateOrMonth, Integer year){
        //int totalPatients = 0;

        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");
            String sql = "";


            if(type.equalsIgnoreCase("day"))
            {
                sql = "SELECT COUNT(DISTINCT patient_id) AS total_patients," +
                        "GROUP_CONCAT(CONCAT(p_lastname, ', ', p_firstname) SEPARATOR '\n ') AS patient_name " +
                        "FROM admission_rate_view " +
                        "WHERE diagnosis_date = ?; ";

                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, dateOrMonth);


            }
            else if(type.equalsIgnoreCase("month"))
            {
                sql = "SELECT COUNT(DISTINCT patient_id) AS total_patients," +
                        "GROUP_CONCAT(CONCAT(p_lastname, ', ', p_firstname) SEPARATOR '\n ') AS patient_name " +
                        "FROM admission_rate_view " +
                        "WHERE MONTH(diagnosis_date) = ? " +
                        "AND YEAR(diagnosis_date) = ?; ";

                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, Integer.parseInt(dateOrMonth));
                pstmt.setInt(2, year);
            }
            else
            {
                sql = "SELECT COUNT(DISTINCT patient_id) AS total_patients," +
                        "GROUP_CONCAT(CONCAT(p_lastname, ', ', p_firstname) SEPARATOR '\n ') AS patient_name " +
                        "FROM admission_rate_view " +
                        "WHERE YEAR(diagnosis_date) = ?; ";

                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, year);
            }

            ResultSet rs = pstmt.executeQuery();// used for queries that returns result
            MakePDF.exportToPDF(rs, "admission_rate_report.pdf");

//            if(rs.next())
//            {
//                totalPatients = rs.getInt("Total_Patients");
//                System.out.println(totalPatients);
//            }

            pstmt.close();
            rs.close();
            conn.close();

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

//        return totalPatients;
    }

    public static void main(String[] args) throws SQLException {
        ReportManagement rm = new ReportManagement();

        //rm.getIllnessOccurrenceReport("year", "coronary artery disease", "none", 2025);
        //rm.getPhysiciansWorkloadReport("year", 1, "none", 2025);
        //rm.getTreatmentStatistics("year", 1, "none", 2025);
        rm.getAdmissionRateReport("year", "none", 2025);
    }

}
