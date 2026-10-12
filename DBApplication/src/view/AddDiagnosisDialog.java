package view;

import model.Diagnosis;
import model.DiagnosisManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddDiagnosisDialog extends JDialog {

    private JTextField patientIDField, illnessIDField, phySchedIDField, diagnosisDateField, notesField;
    private DiagnosisManagement diagnosisMgmt;

    public AddDiagnosisDialog(JFrame parent) {
        super(parent, "Add Diagnosis", true);
        diagnosisMgmt = new DiagnosisManagement();

        // --- DIALOG BASE LAYOUT ---
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- HEADER PANEL ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70)); // green
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Add Diagnosis", SwingConstants.CENTER);
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

        JLabel infoLabel = new JLabel("Diagnosis Information");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(infoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // Patient ID
        gbc.gridx = 0;
        formPanel.add(new JLabel("Patient ID:"), gbc);
        gbc.gridx = 1;
        patientIDField = new JTextField(15);
        formPanel.add(patientIDField, gbc);

        // Illness ID
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Illness ID:"), gbc);
        gbc.gridx = 1;
        illnessIDField = new JTextField(15);
        formPanel.add(illnessIDField, gbc);

        // Physician Schedule ID
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Physician Schedule ID:"), gbc);
        gbc.gridx = 1;
        phySchedIDField = new JTextField(15);
        formPanel.add(phySchedIDField, gbc);

        // Date of Diagnosis
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Date of Diagnosis:"), gbc);
        gbc.gridx = 1;
        diagnosisDateField = new JTextField(15);
        formPanel.add(diagnosisDateField, gbc);

        // Notes
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        notesField = new JTextField(15);
        formPanel.add(notesField, gbc);

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
        String patientIDText = patientIDField.getText().trim();
        String illnessIDText = illnessIDField.getText().trim();
        String phySchedIDText = phySchedIDField.getText().trim();
        String diagnosisDateText = diagnosisDateField.getText().trim();
        String notes = notesField.getText().trim();

        if (patientIDText.isEmpty() || illnessIDText.isEmpty() || phySchedIDText.isEmpty() || diagnosisDateText.isEmpty() || notes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all required fields.", "Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int patientID;
        int illnessID;
        int phySchedID;
        java.sql.Date diagnosisDate = java.sql.Date.valueOf(diagnosisDateText);

        try {
            patientID = Integer.parseInt(patientIDText);
            illnessID = Integer.parseInt(illnessIDText);
            phySchedID = Integer.parseInt(phySchedIDText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Patient ID and Ward ID must be valid whole numbers.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Diagnosis diagnosis = new Diagnosis(phySchedID, patientID, illnessID, diagnosisDate, notes);
        boolean success = diagnosisMgmt.createDiagnosisRecord(diagnosis);

        if (success) {
            JOptionPane.showMessageDialog(this, "Diagnosis added successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add diagnosis.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- TEST LAUNCHER ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddDiagnosisDialog dialog = new AddDiagnosisDialog(null);
            dialog.setVisible(true);
        });
    }
}