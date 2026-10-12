package controller;
import model.PhysicianManagement;
import model.Physician;
import model.PhysicianSchedule;
import model.PhysicianScheduleManagement;

import view.ASGui;
import view.UpdatePhysicianDialog;
import view.UpdatePhysicianScheduleDialog;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class PhysicianController implements ActionListener{
    private PhysicianManagement physicianManagement;
    private PhysicianScheduleManagement physicianScheduleManagement;
    private ASGui asgui;
    public PhysicianController(ASGui gui)
    {
        physicianManagement = new PhysicianManagement();
        physicianScheduleManagement = new PhysicianScheduleManagement();
        this.asgui = gui;
        gui.setActionListeners(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == asgui.getPhysicianButton())
        {
            asgui.setButtonValue(1);
            asgui.setCreateButtonText("Add Physician");
            asgui.showOnlyTabs("Physician", "Physician Related Records");
            asgui.setTableLabel("Physician Records");
            asgui.setPathLabel("Home   /   Physician   /");
            System.out.println("Physician Button clicked!");
            List<Physician> physicians = physicianManagement.viewPhysicianRecords();
            Object[][] data = new Object[physicians.size()][5];
            for(int i = 0; i < physicians.size(); i++)
            {
                Physician p = physicians.get(i);

                data[i][0] = false;
                data[i][1] = p.getPhysicianID();
                data[i][2] = new Object[]{asgui.getProfileIcon(), p.getLastName() + ", " + p.getFirstName()};
                data[i][3] = p.getContact();
                data[i][4] = p.getSpecialization();
            }

            String[] attributes = {" ", "Physician ID", "Physician name", "Contact number", "Specialization"};

            Map<Integer, Integer> colWidths = Map.of(
                    0, 106,  // checkbox
                    1, 150,  // ID
                    2, 323,  // Name
                    3, 323,  //contact
                    4, 323 //specialization
            ); //1226 total = 106 checkbox, 150 ID, 970 left

            JTable physicianTable = asgui.createTable(data, attributes, 2, 0, -1, colWidths);
            JScrollPane physicianScrollPane = new JScrollPane(physicianTable);
            asgui.setPhysicianScrollPane(physicianScrollPane);
            asgui.setCurrentPhysicianTable(physicianTable);

            int tabIndex = asgui.getTabIndex("Physician");
            if(tabIndex != -1) {
                asgui.getTabbedPane().setComponentAt(tabIndex, physicianScrollPane);
                asgui.getTabbedPane().setSelectedIndex(tabIndex);
            } else {
                System.err.println("Tab 'Physician' not found!");
            }
        }
        else if(e.getSource() == asgui.getpScheduleButton())
        {
            asgui.setButtonValue(11);
            asgui.setCreateButtonText("Add Schedule");
            asgui.showOnlyTabs("Physician Schedules");
            asgui.setTableLabel("Physician Schedule Records");
            asgui.setPathLabel("Home   /   Physician Schedule   /");
            System.out.println("Physician Schedule Button clicked!");
            List<PhysicianSchedule> schedules = physicianScheduleManagement.viewPhysicianSchedule();
            Object[][] data = new Object[schedules.size()][6];
            for(int i = 0; i < schedules.size(); i++)
            {
                PhysicianSchedule s = schedules.get(i);

                data[i][0] = false;
                data[i][1] = s.getPhysicianScheduleID();
                data[i][2] = s.getPhysicianID();
                data[i][3] = s.getDay();
                data[i][4] = s.getStartTime();
                data[i][5] = s.getEndTime();
            }

            String[] attributes = {" ", "PhysicianSchedule ID", "Physician ID", "Day", "Start time", "End time"};

            Map<Integer, Integer> colWidths = Map.of(
                    0, 106,  // checkbox
                    1, 150,  // ID
                    2, 242,  // Name
                    3, 242,
                    4, 242,
                    5, 242// Contact
            ); //1226 total = 106 checkbox, 150 ID, 970 left

            JTable physicianSchedulesTable = asgui.createTable(data, attributes, -1, 0, -1, colWidths);
            JScrollPane physicianSchedulesScrollPane = new JScrollPane(physicianSchedulesTable);
            asgui.setPhysicianSchedulesScrollPane(physicianSchedulesScrollPane);
            asgui.setCurrentPhysicianSchedulesTable(physicianSchedulesTable);
            int tabIndex = asgui.getTabIndex("Physician Schedules");
            if(tabIndex != -1) {
                asgui.getTabbedPane().setComponentAt(tabIndex, physicianSchedulesScrollPane);
                asgui.getTabbedPane().setSelectedIndex(tabIndex);
            } else {
                System.err.println("Tab 'Physician Schedules' not found!");
            }
        }
		else if(e.getSource() == asgui.getDeleteButton()) 
		{
            String currentLabel = asgui.getTableLabel().getText();
            if (!currentLabel.equals("Physician Records") && !currentLabel.equals("Physician Schedule Records")) return;

            JTable table;
            if (currentLabel.equals("Physician Records")) {
                table = asgui.getPhysicianTable();
            } else {
                table = asgui.getCurrentPhysicianSchedulesTable();
            }

            if (table == null) {
                JOptionPane.showMessageDialog(asgui, "No table data visible to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

			String entityType = (currentLabel.equals("Physician Records")) ? "Physician" : "Physician Schedule";

			List<Object> selectedIDs = asgui.getSelectedRowIDs(table);
            if (selectedIDs.isEmpty()) {
                JOptionPane.showMessageDialog(asgui, "No rows selected for deletion.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

			int confirm = JOptionPane.showConfirmDialog(asgui,
				"Are you sure you want to delete the selected " + selectedIDs.size() + " " + entityType + " record(s)?",
				"Confirm Deletion", JOptionPane.YES_NO_OPTION);

			if (confirm == JOptionPane.YES_OPTION) {
				int deletedCount = 0;
				for (Object id : selectedIDs) {
					int entityID = (int) id;
					boolean success = false;

					try {
						if (entityType.equals("Physician")) {
							success = physicianManagement.deletePhysicianRecord(entityID);
						} else if (entityType.equals("Physician Schedule")) {
							success = physicianScheduleManagement.deletePhysicianSchedule(entityID);
						}

						if (success) {
							deletedCount++;
						}
					} catch (Exception ex) {
						System.err.println("Error deleting " + entityType + " ID " + id + ": " + ex.getMessage());
					}
				}

				JOptionPane.showMessageDialog(asgui, deletedCount + " " + entityType + " record(s) deleted successfully.", "Deletion Complete", JOptionPane.INFORMATION_MESSAGE);

				// Refresh the current table display
				if (entityType.equals("Physician")) {
					asgui.getPhysicianButton().doClick();
				} else {
					asgui.getpScheduleButton().doClick();
				}
			}
		}
		else if(e.getSource() == asgui.getUpdateButton()) 
		{
            String currentLabel = asgui.getTableLabel().getText();

            JTable table;
            if (currentLabel.equals("Physician Records")) {
                table = asgui.getPhysicianTable();
            } else {
                table = asgui.getCurrentPhysicianSchedulesTable();
            }

            if (table == null) return;
            List<Object> selectedData = asgui.getSelectedRowData(table);
            if (selectedData == null) return;

            try {
                if (currentLabel.equals("Physician Records")) {
                    // [0:Chk] [1:ID] [2:Name Object] [3:Contact] [4:Specialization]
                    int physicianID = (int) selectedData.get(1);
                    Object[] nameObj = (Object[]) selectedData.get(2);
                    String fullName = (String) nameObj[1];
                    String[] names = fullName.split(", ");
                    String lastName = names[0];
                    String firstName = names[1];
                    String contact = (String) selectedData.get(3);
                    String specialization = (String) selectedData.get(4);

                    Physician selectedPhysician = new Physician(firstName, lastName, contact, specialization);
                    selectedPhysician.setPhysician_id(physicianID); 

                    UpdatePhysicianDialog updateDialog = new UpdatePhysicianDialog(asgui, selectedPhysician);
                    updateDialog.setVisible(true);
                    asgui.getPhysicianButton().doClick();

                } else if (currentLabel.equals("Physician Schedule Records")) {
                    // [0:Chk] [1:Schedule ID] [2:Physician ID] [3:Day] [4:Start Time] [5:End Time]
                    int scheduleID = (int) selectedData.get(1);
                    int physicianID = (int) selectedData.get(2);
                    String day = (String) selectedData.get(3);
                    String startTime = (String) selectedData.get(4);
                    String endTime = (String) selectedData.get(5);

                    PhysicianSchedule selectedSchedule = new PhysicianSchedule(physicianID, day, startTime, endTime);
                    selectedSchedule.setPhysicianScheduleID(scheduleID);
                    
                    UpdatePhysicianScheduleDialog updateDialog = new UpdatePhysicianScheduleDialog(asgui, selectedSchedule);
                    updateDialog.setVisible(true);
                    asgui.getpScheduleButton().doClick(); // Refresh
                } else {
                    JOptionPane.showMessageDialog(asgui, "Update not supported for the current table view.", "Update Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(asgui, "Error processing selected data for update: " + ex.getMessage(), "Data Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
		}
    }
}