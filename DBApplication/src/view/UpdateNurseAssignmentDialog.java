package view;

import model.NurseAssignment;
import model.NurseAssignmentManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;

public class UpdateNurseAssignmentDialog extends JDialog {

    private JTextField nurseShiftIDField, patientIDField, assignedDateField, assignedUntilField;
    private NurseAssignmentManagement nurseAssignmentMgmt;
    private NurseAssignment originalAssignment;

    public UpdateNurseAssignmentDialog(JFrame parent, NurseAssignment assignment) {
        super(parent, "Update Nurse Assignment: " + assignment.getNurseAssignmentID(), true);
        nurseAssignmentMgmt = new NurseAssignmentManagement();
        this.originalAssignment = assignment;

        // DIALOG BASE LAYOUT
        setSize(600, 480);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // HEADER PANEL
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70));
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Update Nurse Assignment: " + assignment.getNurseAssignmentID(), SwingConstants.CENTER);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // MAIN CONTENT PANEL
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Left panel (form fields)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel infoLabel = new JLabel("Assignment Details");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(infoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // Nurse Shift ID
        gbc.gridx = 0;
        formPanel.add(new JLabel("Nurse Shift ID:"), gbc);
        gbc.gridx = 1;
        nurseShiftIDField = new JTextField(15);
        nurseShiftIDField.setText(String.valueOf(assignment.getNurseShiftID()));
        formPanel.add(nurseShiftIDField, gbc);

        // Patient ID
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Patient ID:"), gbc);
        gbc.gridx = 1;
        patientIDField = new JTextField(15);
        patientIDField.setText(String.valueOf(assignment.getPatientID()));
        formPanel.add(patientIDField, gbc);

        // Date Assigned
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Date Assigned (YYYY-MM-DD): "), gbc);
        gbc.gridx = 1;
        assignedDateField = new JTextField(15);
        assignedDateField.setText(assignment.getDateAssigned().toString());
        formPanel.add(assignedDateField, gbc);

        // Date Until
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Assigned Until (YYYY-MM-DD): "), gbc);
        gbc.gridx = 1;
        assignedUntilField = new JTextField(15);
        
        // Handle potential null AssignedUntil date
        String assignedUntilStr = (assignment.getAssignedUntil() != null) ? assignment.getAssignedUntil().toString() : "";
        assignedUntilField.setText(assignedUntilStr);
        formPanel.add(assignedUntilField, gbc);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(formPanel, BorderLayout.CENTER);

        contentPanel.add(centerPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // SUBMIT BUTTON
        JButton updateBtn = new JButton("UPDATE");
        updateBtn.setBackground(new Color(76, 110, 70));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateBtn.setFocusPainted(false);
        updateBtn.setPreferredSize(new Dimension(100, 35));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(updateBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // ACTION HANDLER
        updateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmit();
            }
        });
    }

    private void handleSubmit() {
        String nurseShiftIDText = nurseShiftIDField.getText().trim();
        String patientIDText = patientIDField.getText().trim();
        String assignedDateText = assignedDateField.getText().trim();
        String assignedUntilText = assignedUntilField.getText().trim();

        if (nurseShiftIDText.isEmpty() || patientIDText.isEmpty() || assignedDateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all required fields (except Assigned Until).", "Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int nurseShiftID;
        int patientID;
        Date assignedDate;
        Date assignedUntil = null;

        try {
            nurseShiftID = Integer.parseInt(nurseShiftIDText);
            patientID = Integer.parseInt(patientIDText);
            assignedDate = Date.valueOf(assignedDateText);
            
            if (!assignedUntilText.isEmpty()) {
                assignedUntil = Date.valueOf(assignedUntilText);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "IDs must be valid whole numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Date fields must be in YYYY-MM-DD format.", "Invalid Date Format", JOptionPane.ERROR_MESSAGE);
            return;
        }

        NurseAssignment updatedAssignment = new NurseAssignment(nurseShiftID, patientID, assignedDate, assignedUntil);
        updatedAssignment.setAssignmentID(originalAssignment.getNurseAssignmentID());

        boolean success = nurseAssignmentMgmt.updateNurseAssignment(updatedAssignment);

        if (success) {
            JOptionPane.showMessageDialog(this, "Nurse Assignment record updated successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update assignment record. Check date constraints.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}