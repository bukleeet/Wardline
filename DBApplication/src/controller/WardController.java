package controller;

import model.Ward;
import model.WardManagement;

import view.ASGui;
import view.UpdateWardDialog;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class WardController implements ActionListener {
    private WardManagement wardManagement;
    private ASGui asgui;

    public WardController(ASGui gui)
    {
        wardManagement = new WardManagement();
        this.asgui = gui;
        gui.setActionListeners(this);
    }

    @Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == asgui.getWardButton())
		{
            asgui.setButtonValue(3);
            asgui.setCreateButtonText("Add Ward");
            asgui.showOnlyTabs("Wards", "Ward Related Records");
			asgui.setTableLabel("Ward Records");
            asgui.setPathLabel("Home   /   Ward   /");
			System.out.println("Ward Button clicked!");
			List<Ward> wards = wardManagement.viewWardRecords();
			Object[][] data = new Object[wards.size()][5];
			for(int i = 0; i < wards.size(); i++)
			{
				Ward w = wards.get(i);

				data[i][0] = false;
				data[i][1] = w.getWard_id();
				data[i][2] = w.getWardNo();
				data[i][3] = w.getFloor();
				data[i][4] = w.getStatus();
			}

			String[] attributes = {" ", "Ward ID", "Ward number", "Floor", "Status"};

			Map<Integer, Integer> colWidths = Map.of(
					0, 106, 1, 150, 2, 323, 3, 323, 4, 323
			);

            JTable wardTable = asgui.createTable(data, attributes, -1, 0, -1, colWidths);
            JScrollPane wardScrollPane = new JScrollPane(wardTable);
            asgui.setWardTable(wardTable);
            asgui.setWardScrollPane(wardScrollPane);

            int tabIndex = asgui.getTabIndex("Wards");
            System.out.println("Tab index for 'Wards' = " + tabIndex);
            if(tabIndex != -1) {
                asgui.getTabbedPane().setComponentAt(tabIndex, wardScrollPane);
                asgui.getTabbedPane().setSelectedIndex(tabIndex);
            } else {
                System.err.println("Tab 'Wards' not found!");
            }
        }
		else if(e.getSource() == asgui.getDeleteButton()) 
		{
			if (!asgui.getTableLabel().getText().equals("Ward Records")) return;

			System.out.println("Delete Button clicked for Ward!");
            JTable table = asgui.getWardTable();
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
				"Are you sure you want to delete the selected " + selectedIDs.size() + " ward record(s)?",
				"Confirm Deletion", JOptionPane.YES_NO_OPTION);

			if (confirm == JOptionPane.YES_OPTION) {
				int deletedCount = 0;
				for (Object id : selectedIDs) {
					try {
						Ward wardToDelete = new Ward((int) id);
						if (wardManagement.deleteWard(wardToDelete)) {
							deletedCount++;
						}
					} catch (Exception ex) {
						System.err.println("Error deleting Ward ID " + id + ": " + ex.getMessage());
					}
				}

				JOptionPane.showMessageDialog(asgui, deletedCount + " ward record(s) deleted successfully.", "Deletion Complete", JOptionPane.INFORMATION_MESSAGE);
				asgui.getWardButton().doClick(); // Refresh
			}
		}
		else if(e.getSource() == asgui.getUpdateButton())
		{
			if (!asgui.getTableLabel().getText().equals("Ward Records")) return;

			System.out.println("Update Button clicked for Ward!");
            JTable table = asgui.getWardTable();
            if(table == null) {
                JOptionPane.showMessageDialog(asgui, "No table data visible to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

			List<Object> selectedData = asgui.getSelectedRowData(table);

			if (selectedData != null) {
				if (table.getModel().getColumnCount() != 5) {
					JOptionPane.showMessageDialog(asgui, "Update function available only for Ward Records view.", "Update Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try {
					// Extract data: [0:Chk] [1:ID] [2:Ward No] [3:Floor] [4:Status]
					int wardID = (int) selectedData.get(1);
					int wardNo = Integer.parseInt(selectedData.get(2).toString());
					String floor = (String) selectedData.get(3);
					String status = (String) selectedData.get(4);
					
					// Create Ward object
					Ward selectedWard = new Ward(floor, wardNo, status);
					selectedWard.setWard_id(wardID);
					
					// Open the Update Dialog
					UpdateWardDialog updateDialog = new UpdateWardDialog(asgui, selectedWard);
					updateDialog.setVisible(true);

					// Refresh the table
					asgui.getWardButton().doClick();

				} catch (Exception ex) {
					JOptionPane.showMessageDialog(asgui, "Error processing selected Ward data.", "Data Error", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
		}
	}
}