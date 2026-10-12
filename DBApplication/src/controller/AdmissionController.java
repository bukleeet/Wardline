package controller;
import model.Admission;
import model.AdmissionManagement;


import view.ASGui;
import view.UpdateAdmissionDialog;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;


public class AdmissionController implements ActionListener{
    private AdmissionManagement admissionManagement;
    private ASGui asgui;

    public AdmissionController(ASGui gui)
    {
        admissionManagement = new AdmissionManagement();
        this.asgui = gui;
        gui.setActionListeners(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == asgui.getAdmissionButton())
        {
            asgui.setButtonValue(6);
            asgui.setCreateButtonText("Add Admission");
            asgui.showOnlyTabs("Admissions");
            asgui.setTableLabel("Admission Records");
            asgui.setPathLabel("Home   /   Admission   /");
            System.out.println("Admission Button clicked!");
            List<Admission> admissions = admissionManagement.viewPatientAdmission();
            Object[][] data = new Object[admissions.size()][5];
            for(int i = 0; i < admissions.size(); i++)
            {
                Admission a = admissions.get(i);
                data[i][0] = false;
                data[i][1] = a.getAdmissionID();
                data[i][2] = a.getPatientID();
                data[i][3] = a.getWardID();
                data[i][4] = a.getAdmissionDate();
            }

            String[] attributes = {" ", "Admission ID", "Patient ID", "Ward ID", "Admission Date"};

            Map<Integer, Integer> colWidths = Map.of(
                    0, 106,  // checkbox
                    1, 150,  // Admission ID
                    2, 323,  // Patient ID
                    3, 323,  //Ward ID
                    4, 323 //Date
            ); //1226 total = 106 checkbox, 150 ID, 970 left

            JTable admissionTable = asgui.createTable(data, attributes, -1, 0, -1, colWidths);
            JScrollPane admissionSrollPane = new JScrollPane(admissionTable);

            asgui.setAdmissionTable(admissionTable);
            asgui.setAdmissionSrollPane(admissionSrollPane);


            int tabIndex = asgui.getTabIndex("Admissions");
            if(tabIndex != -1) {
                asgui.getTabbedPane().setComponentAt(tabIndex, admissionSrollPane);
                asgui.getTabbedPane().setSelectedIndex(tabIndex);
            } else {
                System.err.println("Tab 'Admissions' not found!");
            }
        }
		else if(e.getSource() == asgui.getDeleteButton())
		{
            // ROUTING CHECK (This prevents duplicate pop-ups)
			if (!asgui.getTableLabel().getText().equals("Admission Records")) return;
            
			System.out.println("Delete Button clicked for Admission!");
            JTable table = asgui.getAdmissionTable();
            if(table == null) {
                JOptionPane.showMessageDialog(asgui, "No table data visible to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Object> selectedIDs = asgui.getSelectedRowIDs(table);
            if (selectedIDs.isEmpty()) {
                JOptionPane.showMessageDialog(asgui, "No rows selected for deletion.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(asgui, 
                "Are you sure you want to delete the selected " + selectedIDs.size() + " admission record(s)?", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                int deletedCount = 0;
                for (Object id : selectedIDs) {
                    try {
                        if (admissionManagement.deleteAdmissionRecord((int) id)) {
                            deletedCount++;
                        }
                    } catch (Exception ex) {
                        System.err.println("Error deleting Admission ID " + id + ": " + ex.getMessage());
                    }
                }

                JOptionPane.showMessageDialog(asgui, deletedCount + " admission record(s) deleted successfully.", "Deletion Complete", JOptionPane.INFORMATION_MESSAGE);
                asgui.getAdmissionButton().doClick(); // Refresh
            }
		}
		else if(e.getSource() == asgui.getUpdateButton())
		{
            // ROUTING CHECK
			if (!asgui.getTableLabel().getText().equals("Admission Records")) return;
			
			// IMPLEMENTED UPDATE LOGIC
			System.out.println("Update Button clicked for Admission!");
            JTable table = asgui.getAdmissionTable();
            if(table == null) {
                JOptionPane.showMessageDialog(asgui, "No table data visible to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

			List<Object> selectedData = asgui.getSelectedRowData(table);

			if (selectedData != null) {
				if (table.getModel().getColumnCount() != 5) {
					JOptionPane.showMessageDialog(asgui, "Update function available only for Admission Records view.", "Update Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try {
					// Extract data
					int admissionID = (int) selectedData.get(1);
					int patientID = Integer.parseInt(selectedData.get(2).toString()); 
					int wardID = Integer.parseInt(selectedData.get(3).toString());
					String admissionDate = (String) selectedData.get(4);
					
					// Create Admission object
					Admission selectedAdmission = new Admission(patientID, wardID, admissionDate);
					selectedAdmission.setAdmissionID(admissionID);
					
					// Open the Update Dialog
					UpdateAdmissionDialog updateDialog = new UpdateAdmissionDialog(asgui, selectedAdmission);
					updateDialog.setVisible(true);

					// Refresh the table
					asgui.getAdmissionButton().doClick();

				} catch (Exception ex) {
					JOptionPane.showMessageDialog(asgui, "Error processing selected Admission data.", "Data Error", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
		}
    }
}
