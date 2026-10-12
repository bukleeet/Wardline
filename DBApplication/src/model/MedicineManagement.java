package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MedicineManagement {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dbhospital_final";
    private static final String USER = "root";
    private static final String PASSWORD = "infom123";
    private Connection conn;
    PreparedStatement pstmt;

    public boolean createMedicineRecord(Medicine medicine)
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "INSERT INTO medicine (medicine_name, stock_qty) VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, medicine.getMedicineName());
            pstmt.setInt(2, medicine.getStockQty());


            pstmt.executeUpdate();
            System.out.println("Medicine Record inserted successfully!");

            pstmt.close();
            conn.close();
            return true;

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Medicine> viewMedicineRecord()
    {
        List<Medicine> medicines = new ArrayList<>();
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "SELECT * FROM medicine";
            pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();// used for queries that returns result
            while(rs.next())
            {
                Medicine medicine = new Medicine(rs.getInt("medicine_id"));
                medicine.setName(rs.getString("medicine_name"));
                medicine.setStock_qty(rs.getInt("stock_qty"));
                medicines.add(medicine);

                int mID = rs.getInt("medicine_id");
                String mName = rs.getString("medicine_name");
                int stock = rs.getInt("stock_qty");

                System.out.println(mID + ", " + mName + ", " + stock);
            }

            pstmt.close();
            rs.close();
            conn.close();

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return medicines;
    }

    public boolean updateMedicineRecord(Medicine medicine)
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "UPDATE medicine SET medicine_name = ?, stock_qty = ? WHERE medicine_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, medicine.getMedicineName());
            pstmt.setInt(2, medicine.getStockQty());
            pstmt.setInt(3,medicine.getMedicineID());


            int rowsAffected = pstmt.executeUpdate();

            if(rowsAffected > 0)
            {
                System.out.println("\n Medicine with id = " + medicine.getMedicineID() + " has been updated!");
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

    public boolean deleteMedicineRecord(int medicineID) //DELETE
    {
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "DELETE FROM medicine WHERE medicine_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, medicineID);

            int rowsAffected = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            if(rowsAffected > 0)
            {
                System.out.println("Medicine with id = " + medicineID + " deleted successfully!");
                return true;
            }
            else
            {
                System.out.println("Medicine deletion failed");
                return false;
            }

        }catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Object[]> medicineRelatedRecord(int id)
    {
        List<Object[]> records = new ArrayList<>();
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection to database successful!");

            String sql = "SELECT\n" +
                    "    m.medicine_name,\n" +
                    "    CONCAT(p.p_lastname, ', ', p.p_firstname) AS patient_name,\n" +
                    "    i.illness_name,\n" +
                    "    CONCAT(ph_diag.ph_lastname, ', ', ph_diag.ph_firstname) AS diagnosing_physician,\n" +
                    "    CONCAT(ph_assig.ph_lastname, ', ', ph_assig.ph_firstname) AS assigned_physician,\n" +
                    "    t.treatment_date,\n" +
                    "    t.treatment_procedure\n" +
                    "FROM treatment t\n" +
                    "    LEFT JOIN medicine m ON t.medicine_id = m.medicine_id\n" +
                    "    LEFT JOIN diagnosis d ON t.diagnosis_id = d.diagnosis_id\n" +
                    "    LEFT JOIN patient p ON d.patient_id = p.patient_id\n" +
                    "    LEFT JOIN illness i ON d.illness_id = i.illness_id\n" +
                    "    LEFT JOIN physician_schedule pa ON d.physicianSchedule_id = pa.physicianSchedule_id\n" +
                    "    LEFT JOIN physician ph_diag ON pa.physician_id = ph_diag.physician_id\n" + // for diagnosing physician (connected to the sched)
                    "    LEFT JOIN physician ph_assig ON t.assignedPhysician_id = ph_assig.physician_id\n" + // for assigned physician (connected to the treatment)
                    "WHERE m.medicine_id = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();// used for queries that returns result
            while(rs.next())
            {
                Object[] row = new Object[7];
                row[0] = rs.getString("medicine_name");
                row[1] = rs.getString("patient_name");
                row[2] = rs.getString("illness_name");
                row[3] = rs.getString("diagnosing_physician");
                row[4] = rs.getString("assigned_physician") != null ? rs.getString("assigned_physician") : "N/A"; // in case walang assigned physician
                row[5] = rs.getString("treatment_date");
                row[6] = rs.getString("treatment_procedure");
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
        Medicine medicine = new Medicine("Biogesic", 50);
        MedicineManagement mm = new MedicineManagement();

        Medicine updatedMed = new Medicine("Paracetamol", 100);
        updatedMed.setMedicineID(4002);

        mm.createMedicineRecord(medicine);
//        mm.updateMedicineRecord(updatedMed);
//        mm.deleteMedicineRecord(4002);
        mm.viewMedicineRecord();
        mm.medicineRelatedRecord(4001);
    }
}


