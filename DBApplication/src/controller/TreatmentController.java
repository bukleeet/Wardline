package controller;
import model.Treatment;
import model.TreatmentManagement;

import view.ASGui;
import view.UpdateTreatmentDialog;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.sql.Date;

public class TreatmentController implements ActionListener{
    private TreatmentManagement treatmentManagement;
    private ASGui asgui;

    public TreatmentController(ASGui gui)
    {
        treatmentManagement = new TreatmentManagement();
        this.asgui = gui;
        gui.setActionListeners(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == asgui.getTreatmentButton())
        {
            asgui.setButtonValue(10);
            asgui.setCreateButtonText("Add Treatment");
            asgui.showOnlyTabs("Treatments");
            asgui.setTableLabel("Treatment Records");
            asgui.setPathLabel("Home   /   Treatment   /");
            System.out.println("Treatment Button clicked!");
            List<Treatment> treatments = treatmentManagement.viewTreatmentRecords();
            Object[][] data = new Object[treatments.size()][10];
            for(int i = 0; i < treatments.size(); i++)
            {
                Treatment t = treatments.get(i);
                data[i][0] = false;
                data[i][1] = t.getTreatmentID();
                data[i][2] = t.getNurseAssignment_id();
                data[i][3] = t.getDiagnosisID();
                data[i][4] = t.getMedicineID();
                data[i][5] = t.getAssignedPhysician();
                data[i][6] = t.getPerformedBy();
                data[i][7] = t.getTreatmentDate();
                data[i][8] = t.getProcedure();
                data[i][9] = t.getRemarks();
            }

            String[] attributes = {" ", "Treatment ID", "Nurse Assignment ID", "Diagnosis ID", "Medicine ID", "AP ID", "Performed by", "Treatment Date", "Treatment Procedure", "Remarks"};

            Map<Integer, Integer> colWidths = Map.of(
                    0, 106, 1, 120, 2, 120, 3, 120, 4, 120, 5, 120, 6, 120, 7, 120, 8, 200, 9, 200
            );

            JTable treatmentTable = asgui.createTable(data, attributes, -1, 0, -1, colWidths);
            JScrollPane treatmentScrollPane = new JScrollPane(treatmentTable);
            asgui.setTreatmentTable(treatmentTable);
            asgui.setTreatmentScrollPane(treatmentScrollPane);

            int tabIndex = asgui.getTabIndex("Treatments");
            if(tabIndex != -1) {
                asgui.getTabbedPane().setComponentAt(tabIndex, treatmentScrollPane);
                asgui.getTabbedPane().setSelectedIndex(tabIndex);
            } else {
                System.err.println("Tab 'Treatments' not found!");
            }
        }
		else if(e.getSource() == asgui.getDeleteButton()) 
		{
            if (!"Treatment Records".equals(asgui.getTableLabel().getText())) return;

            JTable table = asgui.getTreatmentTable();
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
                    "Are you sure you want to delete the selected " + selectedIDs.size() + " treatment record(s)?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if(confirm == JOptionPane.YES_OPTION) {
                int deletedCount = 0;
                for(Object id : selectedIDs) {
                    try {
                        if(treatmentManagement.deleteTreatmentRecord((int) id)) {
                            deletedCount++;
                        }
                    } catch(Exception ex) {
                        System.err.println("Error deleting tretment ID " + id + ": " + ex.getMessage());
                    }
                }
                JOptionPane.showMessageDialog(asgui, deletedCount + " treatment record(s) deleted successfully.", "Deletion Complete", JOptionPane.INFORMATION_MESSAGE);
                asgui.getPatientButton().doClick(); // refresh table
            }
		}
		else if(e.getSource() == asgui.getUpdateButton()) 
		{
            if (!"Treatment Records".equals(asgui.getTableLabel().getText())) return;

            JTable table = asgui.getTreatmentTable(); // get the stored reference
            if (table == null) return;

            int checkedRow = -1;
            for (int i = 0; i < table.getRowCount(); i++) {
                Boolean checked = (Boolean) table.getValueAt(i, 0);
                if (Boolean.TRUE.equals(checked)) {
                    if (checkedRow != -1) {
                        JOptionPane.showMessageDialog(asgui, "Please check only one row to update.", "Multiple Selection", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    checkedRow = i;
                }
            }

            if (checkedRow == -1) {
                JOptionPane.showMessageDialog(asgui, "Please check a row to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Extract data from the checked row
                int treatmentID = (int) table.getValueAt(checkedRow, 1);
                int nurseAssignmentID = Integer.parseInt(table.getValueAt(checkedRow, 2).toString());
                int diagnosisID = Integer.parseInt(table.getValueAt(checkedRow, 3).toString());
                int medicineID = Integer.parseInt(table.getValueAt(checkedRow, 4).toString());

                Integer assignedPhysicianID = null;
                if (table.getValueAt(checkedRow, 5) != null && !table.getValueAt(checkedRow, 5).toString().trim().isEmpty()) {
                    try {
                        assignedPhysicianID = Integer.parseInt(table.getValueAt(checkedRow, 5).toString());
                    } catch (NumberFormatException nfe) {
                        assignedPhysicianID = null;
                    }
                }

                String performedBy = (String) table.getValueAt(checkedRow, 6);

                Object dateObj = table.getValueAt(checkedRow, 7);
                java.sql.Date treatmentDate;

                if (dateObj instanceof java.sql.Date) {
                    treatmentDate = (java.sql.Date) dateObj;  // already correct type
                } else if (dateObj instanceof java.util.Date) {
                    treatmentDate = new java.sql.Date(((java.util.Date) dateObj).getTime());
                } else if (dateObj instanceof String) {
                    try {
                        treatmentDate = java.sql.Date.valueOf(((String) dateObj).trim());
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(asgui,
                                "Invalid treatment date. Use yyyy-MM-dd format.",
                                "Invalid Date", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(asgui,
                            "Unknown date format in table.",
                            "Invalid Date", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String procedure = (String) table.getValueAt(checkedRow, 8);
                String remarks = (String) table.getValueAt(checkedRow, 9);

                Treatment selectedTreatment = new Treatment(nurseAssignmentID, diagnosisID, medicineID, treatmentDate, procedure, remarks, assignedPhysicianID, performedBy);
                selectedTreatment.setTreatmentID(treatmentID);

                UpdateTreatmentDialog updateDialog = new UpdateTreatmentDialog(asgui, selectedTreatment);
                updateDialog.setVisible(true);

                // Refresh the table
                asgui.getTreatmentButton().doClick();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(asgui, "Error processing selected Treatment data.", "Data Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }



        }
    }
}