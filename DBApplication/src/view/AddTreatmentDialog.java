package view;

import model.Patient;
import model.Treatment;
import model.TreatmentManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddTreatmentDialog extends JDialog {
    private JTextField nurseAssignIDField, diagnosisIDField, medIDField, treatmentDateField, treatmentProcedureField, remarksField, assignedPhysicianIDField;
    private JComboBox<String> performedBy_Box;
    private TreatmentManagement  treatmentManagement;

    public AddTreatmentDialog(JFrame parent) {
        super(parent, "Add Treatment", true);
        treatmentManagement = new TreatmentManagement();

        // --- DIALOG BASE LAYOUT ---
        setSize(800, 550);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- HEADER PANEL ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70)); // green
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Add Treatment", SwingConstants.CENTER);
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

        JLabel infoLabel = new JLabel("Treatment Information");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(infoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;


        //Nurse assignment ID
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Assigned Nurse ID: "), gbc);
        gbc.gridx = 1;
        nurseAssignIDField = new JTextField(15);
        formPanel.add(nurseAssignIDField, gbc);

        // assigned physician ID
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Assigned Physician ID: "), gbc);
        gbc.gridx = 1;
        assignedPhysicianIDField = new JTextField(15);
        formPanel.add(assignedPhysicianIDField, gbc);

        // diagnosis ID
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Diagnosis ID: "), gbc);
        gbc.gridx = 1;
        diagnosisIDField = new JTextField(15);
        formPanel.add(diagnosisIDField, gbc);

        // med ID
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Medicine ID: "), gbc);
        gbc.gridx = 1;
        medIDField = new JTextField(15);
        formPanel.add(medIDField, gbc);

        // treatment date
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Treatment Date: "), gbc);
        gbc.gridx = 1;
        treatmentDateField = new JTextField(15);
        formPanel.add(treatmentDateField, gbc);

        // treatment procedure
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Treatment Procedure: "), gbc);
        gbc.gridx = 1;
        treatmentProcedureField = new JTextField(15);
        formPanel.add(treatmentProcedureField, gbc);

        // remarks
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Remarks: "), gbc);
        gbc.gridx = 1;
        remarksField = new JTextField(25);
        formPanel.add(remarksField, gbc);

        // performed by
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Performed By:"), gbc);
        gbc.gridx = 1;
        performedBy_Box = new JComboBox<>(new String[]{"Nurse", "Diagnosing Physician", "Assigned Physician"});
        formPanel.add(performedBy_Box, gbc);

        // Right panel (placeholder image)
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setPreferredSize(new Dimension(180, 200));
        JLabel profilePic = new JLabel();
        profilePic.setPreferredSize(new Dimension(120, 120));
        profilePic.setOpaque(false);
        profilePic.setHorizontalAlignment(SwingConstants.CENTER);
        profilePic.setVerticalAlignment(SwingConstants.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(imagePanel, BorderLayout.EAST);

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
        String nID = nurseAssignIDField.getText().trim();
        String dID = diagnosisIDField.getText().trim();
        String mID = medIDField.getText().trim();
        String tDate = treatmentDateField.getText().trim();
        String tProc = treatmentProcedureField.getText().trim();
        String re = remarksField.getText().trim();
        String pID = assignedPhysicianIDField.getText().trim();
        String pBy = performedBy_Box.getSelectedItem().toString();

        if (nID.isEmpty() || dID.isEmpty() || mID.isEmpty() || tDate.isEmpty() || tProc.isEmpty() || re.isEmpty() || pID.isEmpty() || pBy.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all required fields.", "Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int nurseID = Integer.parseInt(nID);
        int diagnosisID = Integer.parseInt(dID);
        int medicalID = Integer.parseInt(mID);
        java.sql.Date treatmentDate = java.sql.Date.valueOf(tDate);
        int assignedPhysicianID = Integer.parseInt(pID);

        try {
            if (nurseID < 0 || diagnosisID < 0 || medicalID < 0)
                JOptionPane.showMessageDialog(this, "Cannot input a negative number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Physician ID must be a valid whole number",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Treatment treatment = new Treatment(nurseID, diagnosisID, medicalID, treatmentDate, tProc, re, assignedPhysicianID, pBy);
        boolean success = treatmentManagement.createTreatmentRecord(treatment);

        if (success) {
            JOptionPane.showMessageDialog(this, "Treatment added successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add Treatment.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- TEST LAUNCHER ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddTreatmentDialog dialog = new AddTreatmentDialog(null);
            dialog.setVisible(true);
        });
    }
}
