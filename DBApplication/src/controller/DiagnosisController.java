// File: C:\Users\Bullet\Downloads\Github\CCINFOM-DB-Application\DBApplication\src\controller\DiagnosisController.java (FINAL CODE)

package controller;
import model.Diagnosis;
import model.DiagnosisManagement;

import view.ASGui;
import view.UpdateDiagnosisDialog;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.List;
import java.util.Map;

public class DiagnosisController implements ActionListener{
    private DiagnosisManagement diagnosisManagement;
    private ASGui asgui;

    public DiagnosisController(ASGui gui)
    {
        diagnosisManagement = new DiagnosisManagement();
        this.asgui = gui;
        asgui.setActionListeners(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == asgui.getDiagnosisButton())
        {
            asgui.setButtonValue(8);
            asgui.setCreateButtonText("Add Diagnosis");
            asgui.showOnlyTabs("Diagnosis");
            asgui.setTableLabel("Diagnosis Records");
            asgui.setPathLabel("Home   /   Diagnosis   /");
            System.out.println("Diagnosis Button clicked!");
            List<Diagnosis> diagnoses = diagnosisManagement.viewPatientDiagnosis();
            Object[][] data = new Object[diagnoses.size()][7];
            for(int i = 0; i < diagnoses.size(); i++)
            {
                Diagnosis d = diagnoses.get(i);
                data[i][0] = false;
                data[i][1] = d.getDiagnosis_id();
                data[i][2] = d.getPatient_id();
                data[i][3] = d.getPhysicianSchedule_id();
                data[i][4] = d.getIllness_id();
                data[i][5] = d.getDiagnosis_date(); // Using the Date object directly if possible, or toString()
                data[i][6] = d.getNotes();
            }

            String[] attributes = {" ", "Diagnosis ID", "Patient ID", "Physician Schedule ID", "Illness ID", "Diagnosis Date", "Notes"};

            Map<Integer, Integer> colWidths = Map.of(
                    0, 106, 1, 150, 2, 194, 3, 194, 4, 194, 5, 194, 6, 194
            );

            JTable diagnsosisTable = asgui.createTable(data, attributes, -1, 0, -1, colWidths);
            JScrollPane diagnosisScrollPane = new JScrollPane(diagnsosisTable);

            asgui.setDiagnTable(diagnsosisTable);
            asgui.setDiagnScrollPane(diagnosisScrollPane);

            int tabIndex = asgui.getTabIndex("Diagnosis");
            if(tabIndex != -1) {
                asgui.getTabbedPane().setComponentAt(tabIndex, diagnosisScrollPane);
                asgui.getTabbedPane().setSelectedIndex(tabIndex);
            } else {
                System.err.println("Tab 'Diagnosis' not found!");
            }
        }
		else if(e.getSource() == asgui.getDeleteButton()) 
		{
			if (!asgui.getTableLabel().getText().equals("Diagnosis Records")) return;
			System.out.println("Delete Button clicked for Diagnosis!");
            JTable table = asgui.getDiagnosisTable();
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
				"Are you sure you want to delete the selected " + selectedIDs.size() + " diagnosis record(s)?", 
				"Confirm Deletion", JOptionPane.YES_NO_OPTION);

			if (confirm == JOptionPane.YES_OPTION) {
				int deletedCount = 0;
				for (Object id : selectedIDs) {
					try {
						if (diagnosisManagement.deleteDiagnosisRecord((int) id)) {
							deletedCount++;
						}
					} catch (Exception ex) {
						System.err.println("Error deleting Diagnosis ID " + id + ": " + ex.getMessage());
					}
				}

				JOptionPane.showMessageDialog(asgui, deletedCount + " diagnosis record(s) deleted successfully.", "Deletion Complete", JOptionPane.INFORMATION_MESSAGE);
				asgui.getDiagnosisButton().doClick(); // Refresh
			}
		}
		else if(e.getSource() == asgui.getUpdateButton()) 
		{
			if (!asgui.getTableLabel().getText().equals("Diagnosis Records")) return;
			System.out.println("Update Button clicked for Diagnosis!");
            JTable table = asgui.getDiagnosisTable();
            if(table == null) {
                JOptionPane.showMessageDialog(asgui, "No table data visible to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
			List<Object> selectedData = asgui.getSelectedRowData(table);

			if (selectedData != null) {
				if (table.getModel().getColumnCount() != 7) {
					JOptionPane.showMessageDialog(asgui, "Update function available only for Diagnosis Records view.", "Update Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try {
					// Extract data: [0:Chk] [1:Diagnosis ID] [2:Patient ID] [3:PhysicianSchedule ID] [4:Illness ID] [5:Diagnosis Date] [6:Notes]
					int diagnosisID = (int) selectedData.get(1);
					int patientID = Integer.parseInt(selectedData.get(2).toString()); 
					int phySchedID = Integer.parseInt(selectedData.get(3).toString());
					int illnessID = Integer.parseInt(selectedData.get(4).toString());
					
                    // Converting via String.valueOf and then Date.valueOf ensures the correct sql.Date object is passed.
					Date diagnosisDate = Date.valueOf(selectedData.get(5).toString()); 
					
					String notes = (String) selectedData.get(6);
					
					Diagnosis selectedDiagnosis = new Diagnosis(phySchedID, patientID, illnessID, diagnosisDate, notes);
					selectedDiagnosis.setDiagnosis_id(diagnosisID);
					
					UpdateDiagnosisDialog updateDialog = new UpdateDiagnosisDialog(asgui, selectedDiagnosis);
					updateDialog.setVisible(true);

					asgui.getDiagnosisButton().doClick();

				} catch (Exception ex) {
					JOptionPane.showMessageDialog(asgui, "Error processing selected Diagnosis data. Ensure IDs and Date (YYYY-MM-DD) are correctly selected.", "Data Error", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
		}
    }
}