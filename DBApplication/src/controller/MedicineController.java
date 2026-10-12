package controller;
import model.Medicine;
import model.MedicineManagement;

import view.ASGui;
import view.UpdateMedicineDialog;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class MedicineController implements ActionListener{
    private MedicineManagement medicineManagement;
    private ASGui asgui;

    public MedicineController(ASGui gui)
    {
        medicineManagement = new MedicineManagement();
        this.asgui = gui;
        gui.setActionListeners(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == asgui.getMedicineButton())
        {
            asgui.setButtonValue(4);
            asgui.setCreateButtonText("Add Medicine");
            asgui.showOnlyTabs("Medicines", "Medicine Related Records");
            asgui.setTableLabel("Medicine Records");
            asgui.setPathLabel("Home   /   Medicine   /");
            System.out.println("Medicine button clicked!");
            List<Medicine> medicines = medicineManagement.viewMedicineRecord();
            
            // Data preparation for JTable
            Object[][] data = new Object[medicines.size()][4];
            for(int i = 0; i < medicines.size(); i++) {
                Medicine m = medicines.get(i);
                data[i][0] = false; // Checkbox column
                data[i][1] = m.getMedicineID();
                data[i][2] = m.getMedicineName();
                data[i][3] = m.getStockQty();
            }
            String[] attributes = {" ", "Medicine ID", "Medicine name", "Stock_qty"};
            Map<Integer, Integer> colWidths = Map.of(0, 106, 1, 373, 2, 373, 3, 373);
            
            // VIEW Call: Create and display table
            JTable medicineTable = asgui.createTable(data, attributes, -1, 0, -1, colWidths);
            JScrollPane medicineScrollPane = new JScrollPane(medicineTable);

            asgui.setMedicineTable(medicineTable);
            asgui.setMedicineScrollPane(medicineScrollPane);

            int tabIndex = asgui.getTabIndex("Medicines");
            if(tabIndex != -1) {
                asgui.getTabbedPane().setComponentAt(tabIndex, medicineScrollPane);
                asgui.getTabbedPane().setSelectedIndex(tabIndex);
            } else {
                System.err.println("Tab 'Medicines' not found!");
            }
        }
		else if(e.getSource() == asgui.getDeleteButton()) 
		{
			if (!asgui.getTableLabel().getText().equals("Medicine Records")) return;
			System.out.println("Delete Button clicked for Medicine!");
            JTable table = asgui.getMedicineTable();
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
				"Are you sure you want to delete the selected " + selectedIDs.size() + " medicine record(s)?", 
				"Confirm Deletion", JOptionPane.YES_NO_OPTION);

			if (confirm == JOptionPane.YES_OPTION) {
				int deletedCount = 0;
				for (Object id : selectedIDs) {
					try {
						// Assuming the delete method takes the ID (Integer)
						if (medicineManagement.deleteMedicineRecord((int) id)) {
							deletedCount++;
						}
					} catch (Exception ex) {
						System.err.println("Error deleting Medicine ID " + id + ": " + ex.getMessage());
					}
				}

				JOptionPane.showMessageDialog(asgui, deletedCount + " medicine record(s) deleted successfully.", "Deletion Complete", JOptionPane.INFORMATION_MESSAGE);
				asgui.getMedicineButton().doClick(); // Refresh the table
			}
		}
		else if(e.getSource() == asgui.getUpdateButton()) 
		{
            if (!asgui.getTableLabel().getText().equals("Medicine Records")) return;

			System.out.println("Update Button clicked for Medicine!");
            JTable table = asgui.getMedicineTable();
            if(table == null) {
                JOptionPane.showMessageDialog(asgui, "No table data visible to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

			List<Object> selectedData = asgui.getSelectedRowData(table);

			// Check if a single row is selected and data is available
			if (selectedData != null && !selectedData.isEmpty()) {
				// We expect 4 columns: Checkbox, ID, Name, StockQty
				if (table.getModel().getColumnCount() != 4) {
					JOptionPane.showMessageDialog(asgui, "Update function available only for Medicine Records view.", "Update Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try {
					// Index 1 is Medicine ID, Index 2 is Name, Index 3 is Stock Qty
					int medicineID = (int) selectedData.get(1);
					String name = (String) selectedData.get(2);
					int stockQty = Integer.parseInt(selectedData.get(3).toString());
					
					// Create a Medicine object with the current data
					Medicine selectedMedicine = new Medicine(name, stockQty);
					selectedMedicine.setMedicineID(medicineID);
					
					// Open the Update Dialog
					UpdateMedicineDialog updateDialog = new UpdateMedicineDialog(asgui, selectedMedicine);
					updateDialog.setVisible(true);
					
					// Refresh the table after the dialog closes (assuming the dialog handles persistence)
					asgui.getMedicineButton().doClick(); 
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(asgui, "Error processing selected Medicine data.", "Data Error", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
			else
			{
				JOptionPane.showMessageDialog(asgui, "Please select one row to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
			}
		}
    }
}