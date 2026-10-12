package model;

import model.MakePDF;
import java.sql.*;


public class Test  {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dbhospital_final";
    private static final String USER = "root";
    private static final String PASSWORD = "infom123";

        public static void main(String[] args) throws SQLException {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM patient");
                ResultSet rs = pstmt.executeQuery();

                // Export to PDF
                MakePDF.exportToPDF(rs, "patient_report.pdf");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }