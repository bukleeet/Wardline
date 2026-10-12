package controller;

import model.*;
import view.ASGui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class SearchController implements ActionListener {

    private ASGui asgui;
    private PatientManagement patientManagement;
    private IllnessManagement illnessManagement;
    private WardManagement wardManagement;
    private NurseManagement nurseManagement;
    private PhysicianManagement physicianManagement;
    private MedicineManagement medicineManagement;

    public SearchController(ASGui gui) {
        this.asgui = gui;
        this.patientManagement = new PatientManagement();
        this.illnessManagement = new IllnessManagement();
        this.wardManagement = new WardManagement();
        this.nurseManagement = new NurseManagement();
        this.physicianManagement = new PhysicianManagement();
        this.medicineManagement = new MedicineManagement();
        asgui.setActionListeners(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == asgui.getSearchButton()) {
            String query = asgui.getSearchTextField().getText().trim();
            if (query.isEmpty()) return;

            int selectedIndex = asgui.getTabbedPane().getSelectedIndex();
            String selectedTab = asgui.getTabbedPane().getTitleAt(selectedIndex);

            if (selectedTab.equalsIgnoreCase("Patient Related Records")) {
                int patientID;

                try {
                    patientID = Integer.parseInt(query); // ensure numeric ID
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(asgui,
                            "Please enter a valid numeric patient ID",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Fetch related records from model
                List<Object[]> relatedRecords = patientManagement.patientRelatedRecord(patientID);

                if (relatedRecords.isEmpty()) {
                    JOptionPane.showMessageDialog(asgui,
                            "No records found for Patient ID " + patientID,
                            "No Results",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Convert to table data
                Object[][] data = new Object[relatedRecords.size()][6];
                for (int i = 0; i < relatedRecords.size(); i++) {
                    Object[] row = relatedRecords.get(i);
                    System.arraycopy(row, 0, data[i], 0, row.length);
                }

                String[] attributes = {
                        "Patient Name", "Illness", "Treatment Procedure", "Ward No", "Assigned Physician", "Assigned Nurse"
                };

                Map<Integer, Integer> colWidths = Map.of(
                        0, 300,
                        1, 150,
                        2, 200,
                        3, 120,
                        4, 200,
                        5, 200
                );

                // Create table
                JTable relatedTable = asgui.createTable(data, attributes, -1, -1, -1, colWidths);
                JScrollPane relatedScrollPane = new JScrollPane(relatedTable);

                // Replace content in the selected tab
                asgui.getTabbedPane().setComponentAt(selectedIndex, relatedScrollPane);
            }
            else if(selectedTab.equalsIgnoreCase("Illness Related Records"))
            {
                int illnessID;

                try {
                    illnessID = Integer.parseInt(query); // ensure numeric ID
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(asgui,
                            "Please enter a valid numeric illness ID",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Fetch related records from model
                List<Object[]> relatedRecords = illnessManagement.illnessRelatedRecord(illnessID);

                if (relatedRecords.isEmpty()) {
                    JOptionPane.showMessageDialog(asgui,
                            "No records found for Illness ID " + illnessID,
                            "No Results",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Convert to table data
                Object[][] data = new Object[relatedRecords.size()][5];
                for (int i = 0; i < relatedRecords.size(); i++) {
                    Object[] row = relatedRecords.get(i);
                    System.arraycopy(row, 0, data[i], 0, row.length);
                }

                String[] attributes = {
                        "Illness Name", "Diagnosis Date", "Patient Name", "Treatment Date", "Treatment Procedure"
                };

                Map<Integer, Integer> colWidths = Map.of(
                        0, 245,
                        1, 245,
                        2, 245,
                        3, 245,
                        4, 245
                );

                // Create table
                JTable relatedTable = asgui.createTable(data, attributes, -1, -1, -1, colWidths);
                JScrollPane relatedScrollPane = new JScrollPane(relatedTable);

                // Replace content in the selected tab
                asgui.getTabbedPane().setComponentAt(selectedIndex, relatedScrollPane);
            }
            else if(selectedTab.equalsIgnoreCase("Medicine Related Records"))
            {
                int medicineID;

                try {
                    medicineID = Integer.parseInt(query); // ensure numeric ID
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(asgui,
                            "Please enter a valid numeric medicine ID",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Fetch related records from model
                List<Object[]> relatedRecords = medicineManagement.medicineRelatedRecord(medicineID);

                if (relatedRecords.isEmpty()) {
                    JOptionPane.showMessageDialog(asgui,
                            "No records found for Medicine ID " + medicineID,
                            "No Results",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Convert to table data
                Object[][] data = new Object[relatedRecords.size()][6];
                for (int i = 0; i < relatedRecords.size(); i++) {
                    Object[] row = relatedRecords.get(i);
                    System.arraycopy(row, 0, data[i], 0, row.length);
                }

                String[] attributes = {
                        "Medicine Name", "Patient Name", "Illness Name", "Physician Name", "Treatment Date", "Treatment Procedure"
                };

                Map<Integer, Integer> colWidths = Map.of(
                        0, 204,
                        1, 204,
                        2, 204,
                        3, 204,
                        4, 204,
                        5, 204
                );

                // Create table
                JTable relatedTable = asgui.createTable(data, attributes, -1, -1, -1, colWidths);
                JScrollPane relatedScrollPane = new JScrollPane(relatedTable);

                // Replace content in the selected tab
                asgui.getTabbedPane().setComponentAt(selectedIndex, relatedScrollPane);
            }
            else if(selectedTab.equalsIgnoreCase("Physician Related Records"))
            {
                int physicianID;

                try {
                    physicianID = Integer.parseInt(query); // ensure numeric ID
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(asgui,
                            "Please enter a valid numeric medicine ID",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Fetch related records from model
                List<Object[]> relatedRecords = physicianManagement.physicianRelatedRecord(physicianID);

                if (relatedRecords.isEmpty()) {
                    JOptionPane.showMessageDialog(asgui,
                            "No records found for Physician ID " + physicianID,
                            "No Results",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Convert to table data
                Object[][] data = new Object[relatedRecords.size()][6];
                for (int i = 0; i < relatedRecords.size(); i++) {
                    Object[] row = relatedRecords.get(i);
                    System.arraycopy(row, 0, data[i], 0, row.length);
                }

                String[] attributes = {
                        "Physician Name", "Patient Name", "Illness Name", "Treatment ID", "Treatment Date", "Treatment Procedure"
                };

                Map<Integer, Integer> colWidths = Map.of(
                        0, 204,
                        1, 204,
                        2, 204,
                        3, 204,
                        4, 204,
                        5, 204
                );

                // Create table
                JTable relatedTable = asgui.createTable(data, attributes, -1, -1, -1, colWidths);
                JScrollPane relatedScrollPane = new JScrollPane(relatedTable);

                // Replace content in the selected tab
                asgui.getTabbedPane().setComponentAt(selectedIndex, relatedScrollPane);
            }
            else if(selectedTab.equalsIgnoreCase("Ward Related Records"))
            {
                int wardID;

                try {
                    wardID = Integer.parseInt(query); // ensure numeric ID
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(asgui,
                            "Please enter a valid numeric ward ID",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Fetch related records from model
                List<Object[]> relatedRecords = wardManagement.wardRelatedRecord(wardID);

                if (relatedRecords.isEmpty()) {
                    JOptionPane.showMessageDialog(asgui,
                            "No records found for Ward ID " + wardID,
                            "No Results",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Convert to table data
                Object[][] data = new Object[relatedRecords.size()][4];
                for (int i = 0; i < relatedRecords.size(); i++) {
                    Object[] row = relatedRecords.get(i);
                    System.arraycopy(row, 0, data[i], 0, row.length);
                }

                String[] attributes = {
                        "Ward no", "Floor", "Admitted Patient", "Assigned Nurse"
                };

                Map<Integer, Integer> colWidths = Map.of(
                        0, 306,
                        1, 306,
                        2, 306,
                        3, 306
                );

                // Create table
                JTable relatedTable = asgui.createTable(data, attributes, -1, -1, -1, colWidths);
                JScrollPane relatedScrollPane = new JScrollPane(relatedTable);

                // Replace content in the selected tab
                asgui.getTabbedPane().setComponentAt(selectedIndex, relatedScrollPane);
            }
            else if(selectedTab.equalsIgnoreCase("Nurse Related Records"))
            {
                int nurseID;

                try {
                    nurseID = Integer.parseInt(query); // ensure numeric ID
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(asgui,
                            "Please enter a valid numeric nurse ID",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Fetch related records from model
                List<Object[]> relatedRecords = nurseManagement.nurseRelatedRecord(nurseID);

                if (relatedRecords.isEmpty()) {
                    JOptionPane.showMessageDialog(asgui,
                            "No records found for Ward ID " + nurseID,
                            "No Results",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Convert to table data
                Object[][] data = new Object[relatedRecords.size()][6];
                for (int i = 0; i < relatedRecords.size(); i++) {
                    Object[] row = relatedRecords.get(i);
                    System.arraycopy(row, 0, data[i], 0, row.length);
                }

                String[] attributes = {
                        "Nurse name", "Patient name", "Ward no", "Floor", "Treatment Date", "Treatment Procedure"
                };

                Map<Integer, Integer> colWidths = Map.of(
                        0, 204,
                        1, 204,
                        2, 204,
                        3, 204,
                        4, 204,
                        5, 204
                );

                // Create table
                JTable relatedTable = asgui.createTable(data, attributes, -1, -1, -1, colWidths);
                JScrollPane relatedScrollPane = new JScrollPane(relatedTable);
                // Replace content in the selected tab
                asgui.getTabbedPane().setComponentAt(selectedIndex, relatedScrollPane);
            }
        }
    }
}
