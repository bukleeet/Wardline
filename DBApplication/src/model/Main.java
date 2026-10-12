package model;

import java.sql.Connection;
 import java.sql.DriverManager;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
//import java.sql.*;

public class Main {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/dbhospital_final";
    private static final String USER = "root";
    private static final String PASSWORD = "infom123";

    private static Connection conn = null;

    //1. Establish a connection
    public static void connectDB()
    {
        try {
            //driver manager - 
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connected to database!");
        } catch (SQLException ex) {
            System.getLogger(Main.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
//
//    public static void getCountryLanguage(String countryCode, String official){
//        StringBuilder query = new StringBuilder();
//        query.append (" SELECT *               ");
//        query.append (" FROM   CountryLanguage");
//        query.append (" WHERE  CountryCode = ? "); //? means parameter holders
//
//        if(official.equals("T") || official.equals("F"))
//        {
//            query.append(" AND IsOfficial = ?");
//        }
//
//        try{
//            PreparedStatement stmt; //PreparedStatement
//            stmt = conn.prepareCall(query.toString());
//            stmt.setString(1, countryCode); //parameter index always start at index 1
//
//            if(official.equals("T") || official.equals("F"))
//            {
//                stmt.setString(2, official);
//            }
//
//            ResultSet rs; //this returns the result of the query
//            rs = stmt.executeQuery(); //returns true if there is at least 1 record in the query
//
//            //stmt.executeUpdate(); For INSERT, DELETE, and UPDATE instructions. Tracking number of rows updated
//
//            while(rs.next())
//            {
//                String code = rs.getString("CountryCode");
//                String language = rs.getString("Language");
//                String isOfficial = rs.getString ("IsOfficial");
//                double percentage = rs.getDouble("Percentage");
//                System.out.println(code + ", " + language + ", " + isOfficial + ", " + percentage);
//            }
//
//            rs.close();
//            stmt.close();
//            conn.close();
//        } catch(SQLException ex) {
//            System.out.println(ex.getMessage());
//        }
//    }

    public static void main(String[] args) {
      System.out.println("Testing DB Connection...");
      connectDB();
     // getCountryLanguage("PHL", "");

    }
}