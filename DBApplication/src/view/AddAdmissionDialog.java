package view;

import model.Admission;
import model.AdmissionManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddAdmissionDialog extends JDialog {

    private JTextField patientIDField, wardIDField, admissionDateField;
    private AdmissionManagement admissionMgmt;

    public AddAdmissionDialog(JFrame parent) {
        super(parent, "Add Admission", true);
        admissionMgmt = new AdmissionManagement();

        // --- DIALOG BASE LAYOUT ---
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- HEADER PANEL ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70)); // green
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Add Admission", SwingConstants.CENTER);
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
        formPanel.add(patientIDField, gbc);

        // Ward ID
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Ward ID:"), gbc);
        gbc.gridx = 1;
        wardIDField = new JTextField(15);
        formPanel.add(wardIDField, gbc);

        // Date of Admission
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Date of Admission: "), gbc);
        gbc.gridx = 1;
        admissionDateField = new JTextField(15);
        formPanel.add(admissionDateField, gbc);


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

        Admission admission = new Admission(patientID, wardID, admissionDate);
        boolean success = admissionMgmt.createAdmissionRecord(admission);

        if (success) {
            JOptionPane.showMessageDialog(this, "Admission added successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add admission.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- TEST LAUNCHER ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddAdmissionDialog dialog = new AddAdmissionDialog(null);
            dialog.setVisible(true);
        });
    }
}