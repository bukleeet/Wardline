package controller;

import model.*;
import view.ASGui;
import view.UpdatePatientDialog; // Correct Import
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PatientController implements ActionListener{
    private PatientManagement patientManagement; //model
    private ASGui asgui; //view

    public PatientController(ASGui gui)
    {
        patientManagement = new PatientManagement();
        this.asgui = gui;
        asgui.setActionListeners(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == asgui.getPatientButton())
        {
            asgui.setButtonValue(0);
            asgui.setCreateButtonText("Add Patient");
            asgui.showOnlyTabs("Patients", "Patient Related Records");
            asgui.setTableLabel("Patient Records");
            asgui.setPathLabel("Home   /   Patient   /");
            System.out.println("Patient Button clicked!");
            //---------Creating the table for viewing record
            List<Patient> patients = patientManagement.viewPatientRecords();
            Object[][] data = new Object[patients.size()][7];
            for(int i = 0; i < patients.size(); i++)
            {
                Patient p = patients.get(i);
                data[i][0] = false;
                data[i][1] = p.getPatientID();
                data[i][2] = new Object[]{ asgui.getProfileIcon(), p.getLastName() + ", " + p.getFirstName()};
                data[i][3] = p.getSex();
                data[i][4] = p.getBirthDate();
                data[i][5] = p.getContact();
                data[i][6] = p.getStatus();
            }

            String[] attributes = {" ", "ID", "Patient Name", "Sex", "Birthdate", "Contact Number", "Status"};

            Map<Integer, Integer> colWidths = Map.of(
                    0, 106, 1, 150, 2, 260, 3, 120, 4, 220, 5, 220, 6, 150
            );

            JTable patientTable = asgui.createTable(data, attributes, 2, 0, 6, colWidths);
            JScrollPane patientScrollPane = new JScrollPane(patientTable);
            asgui.setPatientScrollPane(patientScrollPane);
            asgui.setCurrentPatientTable(patientTable);
            int tabIndex = asgui.getTabIndex("Patients");
            if(tabIndex != -1) {
                asgui.getTabbedPane().setComponentAt(tabIndex, patientScrollPane);
                asgui.getTabbedPane().setSelectedIndex(tabIndex);
            } else {
                System.err.println("Tab 'Patient' not found!");
            }

        }
        else if(e.getSource() == asgui.getDeleteButton()) 
        {
            if (!"Patient Records".equals(asgui.getTableLabel().getText())) return;

            JTable table = asgui.getCurrentPatientTable();
            if(table == null) {
                JOptionPane.showMessageDialog(asgui, "No table data visible to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Object> selectedIDs = new ArrayList<>();
            for(int i = 0; i < table.getRowCount(); i++) {
                Boolean checked = (Boolean) table.getValueAt(i, 0);
                if(Boolean.TRUE.equals(checked)) {
                    selectedIDs.add(table.getValueAt(i, 1)); // patient ID
                }
            }

            if(selectedIDs.isEmpty()) {
                JOptionPane.showMessageDialog(asgui, "No rows selected for deletion.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(asgui,
                    "Are you sure you want to delete the selected " + selectedIDs.size() + " patient record(s)?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if(confirm == JOptionPane.YES_OPTION) {
                int deletedCount = 0;
                for(Object id : selectedIDs) {
                    try {
                        if(patientManagement.deletePatientRecord((int) id)) {
                            deletedCount++;
                        }
                    } catch(Exception ex) {
                        System.err.println("Error deleting patient ID " + id + ": " + ex.getMessage());
                    }
                }
                JOptionPane.showMessageDialog(asgui, deletedCount + " patient record(s) deleted successfully.", "Deletion Complete", JOptionPane.INFORMATION_MESSAGE);
                asgui.getPatientButton().doClick(); // refresh table
            }
        }
        else if(e.getSource() == asgui.getUpdateButton()) 
        {
            if (!"Patient Records".equals(asgui.getTableLabel().getText())) return;

            JTable table = asgui.getCurrentPatientTable();
            if(table == null) return;

            int checkedRow = -1;
            for(int i = 0; i < table.getRowCount(); i++) {
                Boolean checked = (Boolean) table.getValueAt(i, 0);
                if(Boolean.TRUE.equals(checked)) {
                    if(checkedRow != -1) {
                        JOptionPane.showMessageDialog(asgui, "Please check only one row to update.", "Multiple Selection", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    checkedRow = i;
                }
            }

            if(checkedRow == -1) {
                JOptionPane.showMessageDialog(asgui, "Please check a row to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Extract data
            int patientID = (int) table.getValueAt(checkedRow, 1);
            Object[] nameObj = (Object[]) table.getValueAt(checkedRow, 2);
            String fullName = (String) nameObj[1];
            String[] names = fullName.split(", ");
            String lastName = names[0].trim();
            String firstName = names.length > 1 ? names[1].trim() : "";
            String sex = (String) table.getValueAt(checkedRow, 3);
            String birthDate = (String) table.getValueAt(checkedRow, 4);
            String contact = (String) table.getValueAt(checkedRow, 5);
            String status = (String) table.getValueAt(checkedRow, 6);

            Patient selectedPatient = new Patient(lastName, firstName, birthDate, contact, sex, status);
            selectedPatient.setPatientID(patientID);

            UpdatePatientDialog updateDialog = new UpdatePatientDialog(asgui, selectedPatient);
            updateDialog.setVisible(true);

        }
    }
}