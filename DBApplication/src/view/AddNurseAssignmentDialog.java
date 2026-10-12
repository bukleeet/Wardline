package view;

import model.NurseAssignment;
import model.NurseAssignmentManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddNurseAssignmentDialog extends JDialog {

    private JTextField nurseShiftIDField, patientIDField, assignedDateField, assignedUntilField;
    private NurseAssignmentManagement nurseAssignmentMgmt;

    public AddNurseAssignmentDialog(JFrame parent) {
        super(parent, "Add Nurse Assignment", true);
        nurseAssignmentMgmt = new NurseAssignmentManagement();

        // --- DIALOG BASE LAYOUT ---
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- HEADER PANEL ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70)); // green
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Add Nurse Assignment", SwingConstants.CENTER);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // --- MAIN CONTENT PANEL ---
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

        JLabel infoLabel = new JLabel("Nurse Assignment Information");
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
        formPanel.add(nurseShiftIDField, gbc);

        // Patient ID
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Patient ID:"), gbc);
        gbc.gridx = 1;
        patientIDField = new JTextField(15);
        formPanel.add(patientIDField, gbc);

        // Date Assigned
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Date Assigned: "), gbc);
        gbc.gridx = 1;
        assignedDateField = new JTextField(15);
        formPanel.add(assignedDateField, gbc);

        // Date Until
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Date Until: "), gbc);
        gbc.gridx = 1;
        assignedUntilField = new JTextField(15);
        formPanel.add(assignedUntilField, gbc);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(formPanel, BorderLayout.CENTER);

        contentPanel.add(centerPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // --- SUBMIT BUTTON ---
        JButton submitBtn = new JButton("SUBMIT");
        submitBtn.setBackground(new Color(76, 110, 70));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitBtn.setFocusPainted(false);
        submitBtn.setPreferredSize(new Dimension(100, 35));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(submitBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- ACTION HANDLER ---
        submitBtn.addActionListener(new ActionListener() {
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

        if (nurseShiftIDText.isEmpty() || patientIDText.isEmpty() || assignedDateText.isEmpty() || assignedUntilText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all required fields.", "Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int nurseShiftID = Integer.parseInt(nurseShiftIDText);
        int patientID = Integer.parseInt(patientIDText);;
        java.sql.Date assignedDate = java.sql.Date.valueOf(assignedDateText);
        java.sql.Date assignedUntil = java.sql.Date.valueOf(assignedUntilText);


        try {
            if (nurseShiftID < 0 || patientID < 0)
            JOptionPane.showMessageDialog(this, "Ward number cannot be a negative number", "Invalid Input", JOptionPane.WARNING_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Nurse Shift ID and Patient ID must be valid whole numbers.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        NurseAssignment nurseAssignment = new NurseAssignment(nurseShiftID, patientID, assignedDate, assignedUntil);
        boolean success = nurseAssignmentMgmt.assignNurseToPatient(nurseAssignment);

        if (success) {
            JOptionPane.showMessageDialog(this, "Nurse Assignment added successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add nurse assignment.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- TEST LAUNCHER ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddNurseAssignmentDialog dialog = new AddNurseAssignmentDialog(null);
            dialog.setVisible(true);
        });
    }
}