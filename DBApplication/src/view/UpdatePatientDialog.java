package view;

import model.Patient;
import model.PatientManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdatePatientDialog extends JDialog {

	private JTextField firstNameField, lastNameField, dobField, contactField;
	private JComboBox<String> genderBox, statusBox;
	private PatientManagement patientMgmt;
	private Patient originalPatient;

	public UpdatePatientDialog(JFrame parent, Patient patient) {
		super(parent, "Update Patient: " + patient.getPatientID(), true);
		patientMgmt = new PatientManagement();
		this.originalPatient = patient;

		// DIALOG BASE LAYOUT
		setSize(700, 440);
		setLocationRelativeTo(parent);
		setResizable(false);
		setLayout(new BorderLayout());

		// HEADER PANEL
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(new Color(76, 110, 70));
		headerPanel.setPreferredSize(new Dimension(600, 50));

		JLabel headerLabel = new JLabel("Update Patient: " + patient.getPatientID(), SwingConstants.CENTER);
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
		firstNameField.setText(patient.getFirstName());
		formPanel.add(firstNameField, gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		formPanel.add(new JLabel("Last Name:"), gbc);
		gbc.gridx = 1;
		lastNameField = new JTextField(15);
		lastNameField.setText(patient.getLastName());
		formPanel.add(lastNameField, gbc);

		// Date of Birth & Gender
		gbc.gridy++;
		gbc.gridx = 0;
		formPanel.add(new JLabel("Birthdate (YYYY-MM-DD):"), gbc);
		gbc.gridx = 1;
		dobField = new JTextField(15);
		dobField.setText(patient.getBirthDate());
		formPanel.add(dobField, gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		formPanel.add(new JLabel("Gender:"), gbc);
		gbc.gridx = 1;
		String[] genders = new String[]{"M", "F"};
		genderBox = new JComboBox<>(genders);
		genderBox.setSelectedItem(patient.getSex());
		formPanel.add(genderBox, gbc);

		// Contact Number
		gbc.gridy++;
		gbc.gridx = 0;
		formPanel.add(new JLabel("Contact Number:"), gbc);
		gbc.gridx = 1;
		contactField = new JTextField(15);
		contactField.setText(patient.getContact());
		formPanel.add(contactField, gbc);

		// Status
		gbc.gridy++;
		gbc.gridx = 0;
		formPanel.add(new JLabel("Status:"), gbc);
		gbc.gridx = 1;
		String[] statuses = new String[]{"Admitted", "Discharged"};
		statusBox = new JComboBox<>(statuses);
		statusBox.setSelectedItem(patient.getStatus());
		formPanel.add(statusBox, gbc);


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
		
		// Validate date format (simple check)
		try {
			java.sql.Date.valueOf(dob);
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, "Birthdate must be in YYYY-MM-DD format.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Create updated Patient object, preserving the ID
		Patient updatedPatient = new Patient(ln, fn, dob, contact, sex, status);
		updatedPatient.setPatientID(originalPatient.getPatientID());

		boolean success = patientMgmt.updatePatientRecord(updatedPatient);

		if (success) {
			JOptionPane.showMessageDialog(this, "Patient record updated successfully!");
			dispose();
		} else {
			JOptionPane.showMessageDialog(this, "Failed to update patient record.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}