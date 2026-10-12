package view;

import model.Diagnosis;
import model.DiagnosisManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date; // Need java.sql.Date for the constructor

public class UpdateDiagnosisDialog extends JDialog {

    private JTextField patientIDField, illnessIDField, phySchedIDField, diagnosisDateField, notesField;
    private DiagnosisManagement diagnosisMgmt;
    private Diagnosis originalDiagnosis;

    public UpdateDiagnosisDialog(JFrame parent, Diagnosis diagnosis) {
        super(parent, "Update Diagnosis: " + diagnosis.getDiagnosis_id(), true);
        diagnosisMgmt = new DiagnosisManagement();
        this.originalDiagnosis = diagnosis;

        // DIALOG BASE LAYOUT
        setSize(600, 480);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // HEADER PANEL
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70));
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Update Diagnosis: " + diagnosis.getDiagnosis_id(), SwingConstants.CENTER);
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
        patientIDField.setText(String.valueOf(diagnosis.getPatient_id()));
        formPanel.add(patientIDField, gbc);

        // Illness ID
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Illness ID:"), gbc);
        gbc.gridx = 1;
        illnessIDField = new JTextField(15);
        illnessIDField.setText(String.valueOf(diagnosis.getIllness_id()));
        formPanel.add(illnessIDField, gbc);

        // Physician Schedule ID
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Physician Schedule ID:"), gbc);
        gbc.gridx = 1;
        phySchedIDField = new JTextField(15);
        phySchedIDField.setText(String.valueOf(diagnosis.getPhysicianSchedule_id()));
        formPanel.add(phySchedIDField, gbc);

        // Date of Diagnosis
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Date of Diagnosis (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        diagnosisDateField = new JTextField(15);
        diagnosisDateField.setText(diagnosis.getDiagnosis_date().toString()); // Use toString() on sql.Date
        formPanel.add(diagnosisDateField, gbc);

        // Notes
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        notesField = new JTextField(15);
        notesField.setText(diagnosis.getNotes());
        formPanel.add(notesField, gbc);


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
        Date diagnosisDate;

        try {
            patientID = Integer.parseInt(patientIDText);
            illnessID = Integer.parseInt(illnessIDText);
            phySchedID = Integer.parseInt(phySchedIDText);
            diagnosisDate = Date.valueOf(diagnosisDateText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "All ID fields must be valid whole numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Diagnosis Date must be in YYYY-MM-DD format.", "Invalid Date Format", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Diagnosis updatedDiagnosis = new Diagnosis(phySchedID, patientID, illnessID, diagnosisDate, notes);
        updatedDiagnosis.setDiagnosis_id(originalDiagnosis.getDiagnosis_id());

        boolean success = diagnosisMgmt.updateDiagnosisRecord(updatedDiagnosis);

        if (success) {
            JOptionPane.showMessageDialog(this, "Diagnosis record updated successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update diagnosis record. Check constraints.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}