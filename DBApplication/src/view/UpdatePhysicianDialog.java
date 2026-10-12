package view;

import model.Physician;
import model.PhysicianManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdatePhysicianDialog extends JDialog {

	private JTextField firstNameField, lastNameField, contactField, specializationField;
	private PhysicianManagement physicianManagement;
	private Physician originalPhysician;

	public UpdatePhysicianDialog(JFrame parent, Physician physician) {
		super(parent, "Update Physician: " + physician.getPhysicianID(), true);
		physicianManagement = new PhysicianManagement();
		this.originalPhysician = physician;

		// DIALOG BASE LAYOUT
		setSize(600, 400);
		setLocationRelativeTo(parent);
		setResizable(false);
		setLayout(new BorderLayout());

		// HEADER PANEL
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(new Color(76, 110, 70)); // green
		headerPanel.setPreferredSize(new Dimension(600, 50));

		JLabel headerLabel = new JLabel("Update Physician: " + physician.getPhysicianID(), SwingConstants.CENTER);
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

		JLabel infoLabel = new JLabel("Physician Information");
		infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		formPanel.add(infoLabel, gbc);

		gbc.gridwidth = 1;
		gbc.gridy++;

		// First & Last Name
		gbc.gridx = 0;
		formPanel.add(new JLabel("First Name:"), gbc);
		gbc.gridx = 1;
		firstNameField = new JTextField(15);
		firstNameField.setText(physician.getFirstName());
		formPanel.add(firstNameField, gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		formPanel.add(new JLabel("Last Name:"), gbc);
		gbc.gridx = 1;
		lastNameField = new JTextField(15);
		lastNameField.setText(physician.getLastName());
		formPanel.add(lastNameField, gbc);

		// Contact Number
		gbc.gridy++;
		gbc.gridx = 0;
		formPanel.add(new JLabel("Contact Number:"), gbc);
		gbc.gridx = 1;
		contactField = new JTextField(15);
		contactField.setText(physician.getContact());
		formPanel.add(contactField, gbc);

		// Specialization
		gbc.gridy++;
		gbc.gridx = 0;
		formPanel.add(new JLabel("Specialization:"), gbc);
		gbc.gridx = 1;
		specializationField = new JTextField(15);
		specializationField.setText(physician.getSpecialization());
		formPanel.add(specializationField, gbc);

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

		// UPDATE BUTTON
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
		String contact = contactField.getText().trim();
		String specialization = specializationField.getText().trim();

		if (fn.isEmpty() || ln.isEmpty() || specialization.isEmpty() || contact.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please fill out all required fields.", "Incomplete", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		// Create updated Physician object, preserving the ID
		Physician updatedPhysician = new Physician(fn, ln, contact, specialization);
		updatedPhysician.setPhysician_id(originalPhysician.getPhysicianID());

		boolean success = physicianManagement.updatePhysicianRecord(updatedPhysician);

		if (success) {
			JOptionPane.showMessageDialog(this, "Physician record updated successfully!");
			dispose();
		} else {
			JOptionPane.showMessageDialog(this, "Failed to update physician record.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// TEST LAUNCHER (Optional)
	/*
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			Physician dummyPhysician = new Physician("Alex", "Karev", "1234567890", "Pediatrics");
			dummyPhysician.setPhysician_id(900);
			
			UpdatePhysicianDialog dialog = new UpdatePhysicianDialog(null, dummyPhysician);
			dialog.setVisible(true);
		});
	}
	*/
}
