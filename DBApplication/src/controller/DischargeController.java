package controller;
import model.Discharge;
import model.DischargeManagement;

import view.ASGui;
import view.UpdateDischargeDialog;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class DischargeController implements ActionListener{
    private DischargeManagement dischargeManagement;
    private ASGui asgui;

    public DischargeController(ASGui gui)
    {
        dischargeManagement = new DischargeManagement();
        this.asgui = gui;
        asgui.setActionListeners(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == asgui.getDischargeButton())
        {
            asgui.setButtonValue(7);
            asgui.setCreateButtonText("Add Discharge");
            asgui.showOnlyTabs("Discharges");
            asgui.setTableLabel("Discharge Records");
            asgui.setPathLabel("Home   /   Discharge   /");
            System.out.println("DischargeAilment Button clicked!");
            List<Discharge> discharges = dischargeManagement.viewDischargeRecord();
            Object[][] data = new Object[discharges.size()][4];
            for(int i = 0; i < discharges.size(); i++)
            {
                Discharge d = discharges.get(i);

                data[i][0] = false;
                data[i][1] = d.getDischargeID();
                data[i][2] = d.getAdmissionID();
                data[i][3] = d.getDischargeDate();
            }

            String[] attributes = {" ", "Discharge ID", "Admission ID", "Discharge Date"};

            Map<Integer, Integer> colWidths = Map.of(
                    0, 106,  // checkbox
                    1, 373,  // Discharge ID
                    2, 373,  // Admission ID
                    3, 373  //Date
            ); //1226 total = 106 checkbox, 150 ID, 970 left

            JTable dischargeTable = asgui.createTable(data, attributes, -1, 0, -1, colWidths);
            JScrollPane dischargeScrollPane = new JScrollPane(dischargeTable);

            asgui.setDischargeTable(dischargeTable);
            asgui.setDischargeScrollPane(dischargeScrollPane);

            int tabIndex = asgui.getTabIndex("Discharges");
            if(tabIndex != -1) {
                asgui.getTabbedPane().setComponentAt(tabIndex, dischargeScrollPane);
                asgui.getTabbedPane().setSelectedIndex(tabIndex);
            } else {
                System.err.println("Tab 'Discharges' not found!");
            }
        }
		else if(e.getSource() == asgui.getDeleteButton()) 
		{
			if (!asgui.getTableLabel().getText().equals("Discharge Records")) return;
			System.out.println("Delete Button clicked for Discharge!");
            JTable table = asgui.getDischargeTable();
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
				"Are you sure you want to delete the selected " + selectedIDs.size() + " discharge record(s)?", 
				"Confirm Deletion", JOptionPane.YES_NO_OPTION);

			if (confirm == JOptionPane.YES_OPTION) {
				int deletedCount = 0;
				for (Object id : selectedIDs) {
					try {
						if (dischargeManagement.deleteDischargeRecord((int) id)) {
							deletedCount++;
						}
					} catch (Exception ex) {
						System.err.println("Error deleting Discharge ID " + id + ": " + ex.getMessage());
					}
				}

				JOptionPane.showMessageDialog(asgui, deletedCount + " discharge record(s) deleted successfully.", "Deletion Complete", JOptionPane.INFORMATION_MESSAGE);
				asgui.getDischargeButton().doClick(); // Refresh
			}
		}
		else if(e.getSource() == asgui.getUpdateButton()) 
		{
			if (!asgui.getTableLabel().getText().equals("Discharge Records")) return;
			System.out.println("Update Button clicked for Discharge!");
            JTable table = asgui.getDischargeTable();
            if(table == null) {
                JOptionPane.showMessageDialog(asgui, "No table data visible to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

			List<Object> selectedData = asgui.getSelectedRowData(table);

			if (selectedData != null) {
				// Check column count (4 columns including checkbox)
				if (table.getModel().getColumnCount() != 4) {
					JOptionPane.showMessageDialog(asgui, "Update function available only for Discharge Records view.", "Update Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					// Extract data
					int dischargeID = (int) selectedData.get(1);
					int admissionID = Integer.parseInt(selectedData.get(2).toString()); 
					String dischargeDate = (String) selectedData.get(3);
					
					// Create Discharge object
					Discharge selectedDischarge = new Discharge(admissionID, dischargeDate);
					selectedDischarge.setDischargeID(dischargeID);
					
					// Open the Update Dialog
					UpdateDischargeDialog updateDialog = new UpdateDischargeDialog(asgui, selectedDischarge);
					updateDialog.setVisible(true);

					// Refresh the table
					asgui.getDischargeButton().doClick();

				} catch (Exception ex) {
					JOptionPane.showMessageDialog(asgui, "Error processing selected Discharge data.", "Data Error", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
		}
    }
}
