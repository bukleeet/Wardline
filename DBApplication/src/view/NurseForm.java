package view;

import model.Nurse;
import model.NurseManagement;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class NurseForm extends JFrame {

    // DECLARE Management, Table, and Buttons
    private NurseManagement nurseMgmt; 
    private JTable nurseTable;
    private DefaultTableModel tableModel;
    
    private JButton addBtn;
    private JButton editBtn;
    private JButton deleteBtn;

    public NurseForm() {
        super("Nurse Management");
        // INSTANTIATE Management Class
        nurseMgmt = new NurseManagement(); 
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());
        
        // TABLE SETUP (READ Operation)
        tableModel = new DefaultTableModel(new Object[]{"ID", "First Name", "Last Name", "Contact No"}, 0);
        nurseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(nurseTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // BUTTON PANEL (CUD Operations)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // INSTANTIATE Buttons
        addBtn = new JButton("Add Nurse");
        editBtn = new JButton("Edit Selected");
        deleteBtn = new JButton("Delete Selected");
        
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // LINK BUTTONS TO HANDLERS
        linkCrudButtons();
        
        // Initial load of data (READ operation)
        loadNurseRecords();
        
        setLocationRelativeTo(null);
    }
    
    // READ Operation Helper (Call this after C, U, or D)
    private void loadNurseRecords() {
        // In a real application, you call nurseMgmt.viewNurseRecords() 
        // and populate the table model here.
        System.out.println("Nurse Records Loaded. (R - READ) is ready.");
        // Example: tableModel.addRow(new Object[]{1001, "Jane", "Doe", "123456789"});
        // You would dynamically pull this from the database using nurseMgmt.viewNurseRecords()
    }

    private void linkCrudButtons() {
        // CREATE Operation Link (C)
        addBtn.addActionListener(e -> {
            // Opens a dialog similar to AddPatientDialog
            JOptionPane.showMessageDialog(this, "Opening Add Nurse Dialog (CREATE operation)");
            // After successful creation in the dialog, call loadNurseRecords(); 
        });

        // UPDATE Operation Link (U)
        editBtn.addActionListener(e -> handleEdit());
        
        // DELETE Operation Link (D)
        deleteBtn.addActionListener(e -> handleDelete());
    }

    // UPDATE Handler: Retrieves ID and calls Management
    private void handleEdit() {
        int selectedRow = nurseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a nurse to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Must cast the table value to the correct type (assuming ID is int in column 0)
        int nurseID = (int) tableModel.getValueAt(selectedRow, 0); 
        
        // Simulate getting updated data from an Edit Dialog
        Nurse updatedNurse = new Nurse("NEW_LAST_NAME", "NEW_FIRST_NAME", "NEW_CONTACT");
        updatedNurse.setNurseID(nurseID); // CRITICAL: Sets the ID for the SQL WHERE clause
        
        // CALL: UPDATE (U) Operation
        boolean success = nurseMgmt.updateNurseRecord(updatedNurse);

        if (success) {
            JOptionPane.showMessageDialog(this, "Nurse ID " + nurseID + " updated successfully!");
            loadNurseRecords(); // Refresh table (R)
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update nurse record.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // DELETE Handler: Retrieves ID and calls Management
    private void handleDelete() {
        int selectedRow = nurseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a nurse to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int nurseID = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete Nurse ID: " + nurseID + "?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            
            // CALL: DELETE (D) Operation
            boolean success = nurseMgmt.deleteNurseRecord(nurseID);

            if (success) {
                JOptionPane.showMessageDialog(this, "Nurse ID " + nurseID + " deleted successfully!");
                loadNurseRecords(); // Refresh table (R)
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete nurse. Check constraints.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new NurseForm().setVisible(true);
        });
    }
}