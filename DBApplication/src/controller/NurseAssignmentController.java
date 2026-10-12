package controller;
import model.NurseAssignment;
import model.NurseAssignmentManagement;

import view.ASGui;
import view.UpdateNurseAssignmentDialog;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.sql.Date;

public class NurseAssignmentController implements ActionListener{
    private NurseAssignmentManagement nurseAssignmentManagement;
    private ASGui asgui;

    public NurseAssignmentController(ASGui gui)
    {
        nurseAssignmentManagement = new NurseAssignmentManagement();
        this.asgui = gui;
        gui.setActionListeners(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == asgui.getnAssignmentButton())
        {
            asgui.setButtonValue(9);
            asgui.setCreateButtonText("Add Assignment");
            asgui.showOnlyTabs("Nurse Assignments");
            asgui.setTableLabel("Nurse Assignment Records");
            asgui.setPathLabel("Home   /   Nurse Assignment   /");
            // ... (VIEW logic) ...
            List<NurseAssignment> nurseAssignments = nurseAssignmentManagement.viewNurseAssignments();
            Object[][] data = new Object[nurseAssignments.size()][6];
            for(int i = 0; i < nurseAssignments.size(); i++)
            {
                NurseAssignment na = nurseAssignments.get(i);
                data[i][0] = false;
                data[i][1] = na.getNurseAssignmentID();
                data[i][2] = na.getNurseShiftID();
                data[i][3] = na.getPatientID();
                data[i][4] = na.getDateAssigned();
                data[i][5] = na.getAssignedUntil();
            }

            String[] attributes = {" ", "Nurse Assignment ID", "Nurse Shift ID", "Patient ID", "Date Assigned", "Assigned Until"};
            Map<Integer, Integer> colWidths = Map.of(0, 106, 1, 150, 2, 242, 3, 242, 4, 242, 5, 242);
            JTable nurseAssignmentTable = asgui.createTable(data, attributes, -1, 0, -1, colWidths);
            JScrollPane nurseAssignmentScrollPane = new JScrollPane(nurseAssignmentTable);

            asgui.setNurseAssignmentTable(nurseAssignmentTable);
            asgui.setNurseAssignmentScrollPane(nurseAssignmentScrollPane);

            int tabIndex = asgui.getTabIndex("Nurse Assignments");
            if(tabIndex != -1) {
                asgui.getTabbedPane().setComponentAt(tabIndex, nurseAssignmentScrollPane);
                asgui.getTabbedPane().setSelectedIndex(tabIndex);
            } else {
                System.err.println("Tab 'Nurse Assignments' not found!");
            }
        }
		else if(e.getSource() == asgui.getDeleteButton()) 
		{
			if (!asgui.getTableLabel().getText().equals("Nurse Assignment Records")) return;
			System.out.println("Delete Button clicked for Nurse Assignment!");
			
			// ... (rest of Delete logic) ...
		}
		else if(e.getSource() == asgui.getUpdateButton()) 
		{
			if (!asgui.getTableLabel().getText().equals("Nurse Assignment Records")) return;

			System.out.println("Update Button clicked for Nurse Assignment!");
            JTable table = asgui.getNurseAssignmentTable();
            if(table == null) {
                JOptionPane.showMessageDialog(asgui, "No table data visible to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

			List<Object> selectedData = asgui.getSelectedRowData(table);

			if (selectedData != null) {
				if (table.getModel().getColumnCount() != 6) {
					JOptionPane.showMessageDialog(asgui, "Update function available only for Nurse Assignment Records view.", "Update Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try {
					// Extract data: [0:Chk] [1:NA ID] [2:NS ID] [3:P ID] [4:Date Assigned] [5:Assigned Until]
					int naID = (int) selectedData.get(1);
					int nsID = Integer.parseInt(selectedData.get(2).toString()); 
					int pID = Integer.parseInt(selectedData.get(3).toString());
					
					// Parse dates (handling null for Assigned Until)
					Date dateAssigned = Date.valueOf(selectedData.get(4).toString());
                    Date assignedUntil = null;
                    if (selectedData.get(5) != null && !selectedData.get(5).toString().isEmpty()) {
                        assignedUntil = Date.valueOf(selectedData.get(5).toString());
                    }
					
					NurseAssignment selectedAssignment = new NurseAssignment(nsID, pID, dateAssigned, assignedUntil);
					selectedAssignment.setAssignmentID(naID);
					
					UpdateNurseAssignmentDialog updateDialog = new UpdateNurseAssignmentDialog(asgui, selectedAssignment);
					updateDialog.setVisible(true);

					asgui.getnAssignmentButton().doClick(); // Refresh

				} catch (Exception ex) {
					JOptionPane.showMessageDialog(asgui, "Error processing selected Assignment data.", "Data Error", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
		}
        else if(e.getSource() == asgui.getUpdateButton()) {
            if (!"Nurse Assignment Records".equals(asgui.getTableLabel().getText())) return;

            JTable table = asgui.getNurseAssignmentTable();
            if (table == null) return;

            int checkedRow = -1;
            for (int i = 0; i < table.getRowCount(); i++) {
                Boolean checked = (Boolean) table.getValueAt(i, 0);
                if (Boolean.TRUE.equals(checked)) {
                    if (checkedRow != -1) {
                        JOptionPane.showMessageDialog(asgui, "Please select only one row to update.", "Multiple Selection", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    checkedRow = i;
                }
            }
            if (checkedRow == -1) {
                JOptionPane.showMessageDialog(asgui, "Please select a row to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int naID = (int) table.getValueAt(checkedRow, 1);
                int nsID = Integer.parseInt(table.getValueAt(checkedRow, 2).toString());
                int pID = Integer.parseInt(table.getValueAt(checkedRow, 3).toString());

                Date dateAssigned = Date.valueOf(table.getValueAt(checkedRow, 4).toString());
                Date assignedUntil = null;
                Object assignedUntilObj = table.getValueAt(checkedRow, 5);
                if (assignedUntilObj != null && !assignedUntilObj.toString().trim().isEmpty()) {
                    assignedUntil = Date.valueOf(assignedUntilObj.toString());
                }

                NurseAssignment selectedAssignment = new NurseAssignment(nsID, pID, dateAssigned, assignedUntil);
                selectedAssignment.setAssignmentID(naID);

                UpdateNurseAssignmentDialog updateDialog = new UpdateNurseAssignmentDialog(asgui, selectedAssignment);
                updateDialog.setVisible(true);

                // Refresh table
                asgui.getnAssignmentButton().doClick();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(asgui, "Error processing selected Assignment data.", "Data Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }

    }
}