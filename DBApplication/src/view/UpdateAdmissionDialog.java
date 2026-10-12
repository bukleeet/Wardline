package view;

import model.Admission;
import model.AdmissionManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateAdmissionDialog extends JDialog {

    private JTextField patientIDField, wardIDField, admissionDateField;
    private AdmissionManagement admissionMgmt;
    private Admission originalAdmission;

    public UpdateAdmissionDialog(JFrame parent, Admission admission) {
        super(parent, "Update Admission: " + admission.getAdmissionID(), true);
        admissionMgmt = new AdmissionManagement();
        this.originalAdmission = admission;

        // --- DIALOG BASE LAYOUT ---
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- HEADER PANEL ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70));
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Update Admission: " + admission.getAdmissionID(), SwingConstants.CENTER);
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

        JLabel infoLabel = new JLabel("Admission Information");
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
        patientIDField.setText(String.valueOf(admission.getPatientID()));
        formPanel.add(patientIDField, gbc);

        // Ward ID
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Ward ID:"), gbc);
        gbc.gridx = 1;
        wardIDField = new JTextField(15);
        wardIDField.setText(String.valueOf(admission.getWardID()));
        formPanel.add(wardIDField, gbc);

        // Date of Admission
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Date of Admission (YYYY-MM-DD): "), gbc);
        gbc.gridx = 1;
        admissionDateField = new JTextField(15);
        admissionDateField.setText(admission.getAdmissionDate());
        formPanel.add(admissionDateField, gbc);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(formPanel, BorderLayout.CENTER);

        contentPanel.add(centerPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // --- SUBMIT BUTTON ---
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

        // --- ACTION HANDLER ---
        updateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmit();
            }
        });
    }

    private void handleSubmit() {
        String patientIDText = patientIDField.getText().trim();
        String wardIDText = wardIDField.getText().trim();
        String admissionDate = admissionDateField.getText().trim();

        if (patientIDText.isEmpty() || wardIDText.isEmpty() || admissionDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all required fields.", "Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int patientID;
        int wardID;

        try {
            patientID = Integer.parseInt(patientIDText);
            wardID = Integer.parseInt(wardIDText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Patient ID and Ward ID must be valid whole numbers.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Admission updatedAdmission = new Admission(patientID, wardID, admissionDate);
        updatedAdmission.setAdmissionID(originalAdmission.getAdmissionID());

        boolean success = admissionMgmt.updateAdmissionRecord(updatedAdmission);

        if (success) {
            JOptionPane.showMessageDialog(this, "Admission record updated successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update admission record. Check constraints or if the target ward is occupied.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}