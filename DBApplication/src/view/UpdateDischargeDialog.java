package view;

import model.Discharge;
import model.DischargeManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateDischargeDialog extends JDialog {

    private JTextField admissionIDField, dischargeDateField;
    private DischargeManagement dischargeMgmt;
    private Discharge originalDischarge;

    public UpdateDischargeDialog(JFrame parent, Discharge discharge) {
        super(parent, "Update Discharge: " + discharge.getDischargeID(), true);
        dischargeMgmt = new DischargeManagement();
        this.originalDischarge = discharge;

        // DIALOG BASE LAYOUT
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // HEADER PANEL
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70));
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Update Discharge: " + discharge.getDischargeID(), SwingConstants.CENTER);
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

        JLabel infoLabel = new JLabel("Discharge Information");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(infoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // Admission ID
        gbc.gridx = 0;
        formPanel.add(new JLabel("Admission ID:"), gbc);
        gbc.gridx = 1;
        // The Admission ID should generally not be editable, but is required for the object model
        admissionIDField = new JTextField(15);
        admissionIDField.setText(String.valueOf(discharge.getAdmissionID()));
        admissionIDField.setEditable(false); // Should prevent changing the associated Admission ID
        formPanel.add(admissionIDField, gbc);

        // Date of Discharge
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Date of Discharge (YYYY-MM-DD): "), gbc);
        gbc.gridx = 1;
        dischargeDateField = new JTextField(15);
        dischargeDateField.setText(discharge.getDischargeDate());
        formPanel.add(dischargeDateField, gbc);

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
        String admissionIDText = admissionIDField.getText().trim();
        String dischargeDate = dischargeDateField.getText().trim();

        if (admissionIDText.isEmpty() || dischargeDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all required fields.", "Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int admissionID;
        try {
            admissionID = Integer.parseInt(admissionIDText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Admission ID must be a valid whole number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Discharge updatedDischarge = new Discharge(admissionID, dischargeDate);
        updatedDischarge.setDischargeID(originalDischarge.getDischargeID());

        boolean success = dischargeMgmt.updateDischargeRecord(updatedDischarge);

        if (success) {
            JOptionPane.showMessageDialog(this, "Discharge record updated successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update discharge record. Check constraints.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}