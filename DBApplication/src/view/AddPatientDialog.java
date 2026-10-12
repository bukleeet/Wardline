package view;

import model.Patient;
import model.PatientManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddPatientDialog extends JDialog {

    private JTextField firstNameField, lastNameField, dobField, contactField;
    private JComboBox<String> genderBox, statusBox;
    private PatientManagement patientMgmt;

    public AddPatientDialog(JFrame parent) {
        super(parent, "Add Patient", true);
        patientMgmt = new PatientManagement();

        // --- DIALOG BASE LAYOUT ---
        setSize(700, 440);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- HEADER PANEL ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70)); // green
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Add Patient", SwingConstants.CENTER);
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

        JLabel infoLabel = new JLabel("Patient Information");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(infoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // First & Last Name
        gbc.gridx = 0;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(15);
        formPanel.add(firstNameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        lastNameField = new JTextField(15);
        formPanel.add(lastNameField, gbc);

        // Date of Birth & Gender
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Date of Birth:"), gbc);
        gbc.gridx = 1;
        dobField = new JTextField(15);
        formPanel.add(dobField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        genderBox = new JComboBox<>(new String[]{"M", "F"});
        formPanel.add(genderBox, gbc);

        // Contact Number
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Contact Number:"), gbc);
        gbc.gridx = 1;
        contactField = new JTextField(15);
        formPanel.add(contactField, gbc);

        // Status
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusBox = new JComboBox<>(new String[]{"Admitted", "Discharged" });
        formPanel.add(statusBox, gbc);

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
        String fn = firstNameField.getText().trim();
        String ln = lastNameField.getText().trim();
        String dob = dobField.getText().trim();
        String contact = contactField.getText().trim();
        String sex = genderBox.getSelectedItem().toString();
        String status = statusBox.getSelectedItem().toString();


        if (fn.isEmpty() || ln.isEmpty() || dob.isEmpty() || contact.isEmpty() || sex.isEmpty() || status.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all required fields.", "Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Patient patient = new Patient(ln, fn, dob, contact, sex, status);
        boolean success = patientMgmt.createPatientRecord(patient);

        if (success) {
            JOptionPane.showMessageDialog(this, "Patient added successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add patient.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- TEST LAUNCHER ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddPatientDialog dialog = new AddPatientDialog(null);
            dialog.setVisible(true);
        });
    }
}
