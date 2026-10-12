package view;

import model.Treatment;
import model.TreatmentManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;

public class UpdateTreatmentDialog extends JDialog {

    private JTextField nurseAssignIDField, diagnosisIDField, medIDField, treatmentDateField, 
                       treatmentProcedureField, remarksField, assignedPhysicianIDField;
    private JComboBox<String> performedBy_Box;
    private TreatmentManagement treatmentManagement;
    private Treatment originalTreatment;

    public UpdateTreatmentDialog(JFrame parent, Treatment treatment) {
        super(parent, "Update Treatment: " + treatment.getTreatmentID(), true);
        treatmentManagement = new TreatmentManagement();
        this.originalTreatment = treatment;

        // DIALOG BASE LAYOUT
        setSize(700, 550);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // HEADER PANEL
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70)); 
        headerPanel.setPreferredSize(new Dimension(700, 50));

        JLabel headerLabel = new JLabel("Update Treatment: " + treatment.getTreatmentID(), SwingConstants.CENTER);
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

        // Inputs for Update Dialog
        String[] performedByOptions = {"Nurse", "Diagnosing Physician", "Assigned Physician"};
        
        gbc.gridy = 0; gbc.gridx = 0; gbc.gridwidth = 2;
        formPanel.add(new JLabel("Treatment Details"), gbc);
        gbc.gridwidth = 1; gbc.gridy++;
        
        // Nurse assignment ID
        gbc.gridx = 0; formPanel.add(new JLabel("Nurse Assignment ID: "), gbc);
        gbc.gridx = 1;
        nurseAssignIDField = new JTextField(15);
        nurseAssignIDField.setText(String.valueOf(treatment.getNurseAssignment_id()));
        formPanel.add(nurseAssignIDField, gbc);

        // Assigned Physician ID
        gbc.gridy++; gbc.gridx = 0; formPanel.add(new JLabel("Assigned Physician ID (Optional): "), gbc);
        gbc.gridx = 1;
        assignedPhysicianIDField = new JTextField(15);
        assignedPhysicianIDField.setText(treatment.getAssignedPhysician() != null ? String.valueOf(treatment.getAssignedPhysician()) : "");
        formPanel.add(assignedPhysicianIDField, gbc);

        // Diagnosis ID
        gbc.gridy++; gbc.gridx = 0; formPanel.add(new JLabel("Diagnosis ID: "), gbc);
        gbc.gridx = 1;
        diagnosisIDField = new JTextField(15);
        diagnosisIDField.setText(String.valueOf(treatment.getDiagnosisID()));
        formPanel.add(diagnosisIDField, gbc);

        // Medicine ID
        gbc.gridy++; gbc.gridx = 0; formPanel.add(new JLabel("Medicine ID: "), gbc);
        gbc.gridx = 1;
        medIDField = new JTextField(15);
        medIDField.setText(String.valueOf(treatment.getMedicineID()));
        formPanel.add(medIDField, gbc);

        // Treatment Date
        gbc.gridy++; gbc.gridx = 0; formPanel.add(new JLabel("Treatment Date (YYYY-MM-DD): "), gbc);
        gbc.gridx = 1;
        treatmentDateField = new JTextField(15);
        treatmentDateField.setText(treatment.getTreatmentDate().toString());
        formPanel.add(treatmentDateField, gbc);

        // Treatment Procedure
        gbc.gridy++; gbc.gridx = 0; formPanel.add(new JLabel("Treatment Procedure: "), gbc);
        gbc.gridx = 1;
        treatmentProcedureField = new JTextField(15);
        treatmentProcedureField.setText(treatment.getProcedure());
        formPanel.add(treatmentProcedureField, gbc);

        // Remarks
        gbc.gridy++; gbc.gridx = 0; formPanel.add(new JLabel("Remarks: "), gbc);
        gbc.gridx = 1;
        remarksField = new JTextField(25);
        remarksField.setText(treatment.getRemarks());
        formPanel.add(remarksField, gbc);

        // Performed by
        gbc.gridy++; gbc.gridx = 0; formPanel.add(new JLabel("Performed By:"), gbc);
        gbc.gridx = 1;
        performedBy_Box = new JComboBox<>(performedByOptions);
        performedBy_Box.setSelectedItem(treatment.getPerformedBy());
        formPanel.add(performedBy_Box, gbc);

        // Layout setup (simplified)
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
        String nIDText = nurseAssignIDField.getText().trim();
        String dIDText = diagnosisIDField.getText().trim();
        String mIDText = medIDField.getText().trim();
        String tDateText = treatmentDateField.getText().trim();
        String tProc = treatmentProcedureField.getText().trim();
        String re = remarksField.getText().trim();
        String pIDText = assignedPhysicianIDField.getText().trim();
        String pBy = performedBy_Box.getSelectedItem().toString();

        if (nIDText.isEmpty() || dIDText.isEmpty() || mIDText.isEmpty() || tDateText.isEmpty() || tProc.isEmpty() || re.isEmpty() || pBy.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all required fields.", "Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer assignedPhysicianID = null;
        if (!pIDText.isEmpty()) {
             try {
                assignedPhysicianID = Integer.parseInt(pIDText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Assigned Physician ID must be a number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        int nurseID, diagnosisID, medicalID;
        Date treatmentDate;

        try {
            nurseID = Integer.parseInt(nIDText);
            diagnosisID = Integer.parseInt(dIDText);
            medicalID = Integer.parseInt(mIDText);
            treatmentDate = Date.valueOf(tDateText);

            if (nurseID < 0 || diagnosisID < 0 || medicalID < 0) {
                 JOptionPane.showMessageDialog(this, "IDs must be positive numbers.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                 return;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error in ID/Date format.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Treatment updatedTreatment = new Treatment(nurseID, diagnosisID, medicalID, treatmentDate, tProc, re, assignedPhysicianID, pBy);
        updatedTreatment.setTreatmentID(originalTreatment.getTreatmentID());

        boolean success = treatmentManagement.updateTreatmentRecord(updatedTreatment);

        if (success) {
            JOptionPane.showMessageDialog(this, "Treatment record updated successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update Treatment. Check date consistency or database error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}