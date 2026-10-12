package controller;

import model.*;
import view.ASGui;
import view.UpdateNurseDialog;
import view.UpdateNurseShiftDialog;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class NurseController implements ActionListener{
    private NurseManagement nurseManagement;
    private NurseShiftManagement nurseShiftManagement;
    private ASGui asgui; //view

    public NurseController(ASGui gui)
    {
        nurseManagement = new NurseManagement();
        nurseShiftManagement = new NurseShiftManagement();
        this.asgui = gui;
        gui.setActionListeners(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == asgui.getNurseButton())
        {
            asgui.setButtonValue(2);
            asgui.setCreateButtonText("Add Nurse");
            asgui.showOnlyTabs("Nurse", "Nurse Related Records");
            asgui.setTableLabel("Nurse Records");
            asgui.setPathLabel("Home   /   Nurse   /");
            System.out.println("Nurse Button clicked!");
            List<Nurse> nurses = nurseManagement.viewNurseRecords();
            Object[][] data = new Object[nurses.size()][4];
            for(int i = 0; i < nurses.size(); i++)
            {
                Nurse n = nurses.get(i);

                data[i][0] = false;
                data[i][1] = n.getNurseID();
                data[i][2] = new Object[]{ asgui.getProfileIcon(), n.getLastName() + ", " + n.getFirstName()};
                data[i][3] = n.getContactNo();
            }

            String[] attributes = {" ", "Nurse ID", "Nurse name", "Contact number"};

            Map<Integer, Integer> colWidths = Map.of(
                    0, 106,  // checkbox
                    1, 373,  // ID
                    2, 373,  // Name
                    3, 373  // Contact
            ); //1226 total: (1226-106) / 3 attributes =

            JTable nurseTable = asgui.createTable(data, attributes, 2, 0, -1, colWidths);
            JScrollPane nurseScrollPane = new JScrollPane(nurseTable);
            asgui.setNurseTable(nurseTable);
            asgui.setNurseScrollPane(nurseScrollPane);

            int tabIndex = asgui.getTabIndex("Nurse");
            if(tabIndex != -1) {
                asgui.getTabbedPane().setComponentAt(tabIndex, nurseScrollPane);
                asgui.getTabbedPane().setSelectedIndex(tabIndex);
            } else {
                System.err.println("Tab 'Physician' not found!");
            }
        }
        else if(e.getSource() == asgui.getnShiftButton())
        {
            asgui.setButtonValue(12);
            asgui.setCreateButtonText("Add Nurse Shift");
            asgui.showOnlyTabs("Nurse Shifts");
            asgui.setTableLabel("Nurse Shift Records");
            asgui.setPathLabel("Home   /   Nurse Shift   /");
            System.out.println("NurseShift Button clicked!");
            List<NurseShift> nurseShifts = nurseShiftManagement.viewNurseShifts();
            Object[][] data = new Object[nurseShifts.size()][6];
            for(int i = 0; i < nurseShifts.size(); i++)
            {
                NurseShift ns = nurseShifts.get(i);

                data[i][0] = false;
                data[i][1] = ns.getNurseShiftID();
                data[i][2] = ns.getNurseID();
                data[i][3] = ns.getShiftDay();
                data[i][4] = ns.getStartTime();
                data[i][5] = ns.getEndTime();
            }

            String[] attributes = {" ", "NurseShift ID", "Nurse ID", "Day", "Start time", "End time"};

            Map<Integer, Integer> colWidths = Map.of(
                    0, 106,  // checkbox
                    1, 150,  // ID
                    2, 242,
                    3, 242,
                    4, 242,
                    5, 242
            ); //1226 total: (1226-106) / 3 attributes =

            JTable nurseShiftTable = asgui.createTable(data, attributes, -1, 0, -1, colWidths);
            JScrollPane nurseShiftScrollPane = new JScrollPane(nurseShiftTable);

            asgui.setNurseShiftTable(nurseShiftTable);
            asgui.setNurseShiftScrollPane(nurseShiftScrollPane);

            int tabIndex = asgui.getTabIndex("Nurse Shifts");
            if(tabIndex != -1) {
                asgui.getTabbedPane().setComponentAt(tabIndex, nurseShiftScrollPane);
                asgui.getTabbedPane().setSelectedIndex(tabIndex);
            } else {
                System.err.println("Tab 'Nurse Shifts' not found!");
            }
        }
        else if(e.getSource() == asgui.getDeleteButton()) 
        {
            String currentLabel = asgui.getTableLabel().getText();
            if (!currentLabel.equals("Nurse Records") && !currentLabel.equals("Nurse Shift Records")) return;

            System.out.println("Delete Button clicked for " + currentLabel + "!");
            JTable table;
            if (currentLabel.equals("Nurse Records")) {
                table = asgui.getNurseTable();
            } else {
                table = asgui.getNurseShiftTable();
            }

            if (table == null) {
                JOptionPane.showMessageDialog(asgui, "No table data visible to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Object> selectedIDs = asgui.getSelectedRowIDs(table);

            if (selectedIDs.isEmpty()) {
                JOptionPane.showMessageDialog(asgui, "No rows selected for deletion.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(asgui, 
                "Are you sure you want to delete the selected " + selectedIDs.size() + " " + currentLabel + " record(s)?", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                int deletedCount = 0;
                boolean isShift = currentLabel.equals("Nurse Shift Records");

                for (Object id : selectedIDs) {
                    try {
                        boolean success = isShift ? 
                            nurseShiftManagement.deleteNurseShift((int) id) : 
                            nurseManagement.deleteNurseRecord((int) id);
                        
                        if (success) {
                            deletedCount++;
                        }
                    } catch (Exception ex) {
                        System.err.println("Error deleting record ID " + id + ": " + ex.getMessage());
                    }
                }

                JOptionPane.showMessageDialog(asgui, deletedCount + " " + currentLabel + " record(s) deleted successfully.", "Deletion Complete", JOptionPane.INFORMATION_MESSAGE);
                
                if (isShift) {
                    asgui.getnShiftButton().doClick();
                } else {
                    asgui.getNurseButton().doClick();
                }
            }
        }
        else if(e.getSource() == asgui.getUpdateButton())
        {
            String currentLabel = asgui.getTableLabel().getText();
            JTable table;
            if (currentLabel.equals("Nurse Records")) {
                table = asgui.getNurseTable();
            } else {
                table = asgui.getNurseShiftTable();
            }

            if (table == null) {
                JOptionPane.showMessageDialog(asgui, "No table data visible to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Object> selectedData = asgui.getSelectedRowData(table);
            if (selectedData == null) return;
            
            try {
                if (currentLabel.equals("Nurse Records")) {
                    // [0:Chk] [1:ID] [2:Name Object] [3:Contact] 
                    int nurseID = (int) selectedData.get(1);
                    Object[] nameObj = (Object[]) selectedData.get(2);
                    String fullName = (String) nameObj[1];
                    String[] names = fullName.split(", ");
                    String lastName = names[0];
                    String firstName = names.length > 1 ? names[1] : "";
                    String contact = (String) selectedData.get(3);
                    
                    Nurse selectedNurse = new Nurse(lastName, firstName, contact);
                    selectedNurse.setNurse_id(nurseID); 

                    UpdateNurseDialog updateDialog = new UpdateNurseDialog(asgui, selectedNurse); // Correct constructor call
                    updateDialog.setVisible(true);
                    asgui.getNurseButton().doClick();

                } else if (currentLabel.equals("Nurse Shift Records")) {
                    // [0:Chk] [1:Shift ID] [2:Nurse ID] [3:Day] [4:Start Time] [5:End Time]
                    int shiftID = (int) selectedData.get(1);
                    int nurseID = Integer.parseInt(selectedData.get(2).toString());
                    String day = (String) selectedData.get(3);
                    String startTime = (String) selectedData.get(4);
                    String endTime = (String) selectedData.get(5);

                    NurseShift selectedShift = new NurseShift(nurseID, day, startTime, endTime);
                    selectedShift.setNurseShiftID(shiftID);
                    
                    UpdateNurseShiftDialog updateDialog = new UpdateNurseShiftDialog(asgui, selectedShift);
                    updateDialog.setVisible(true);
                    asgui.getnShiftButton().doClick();
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