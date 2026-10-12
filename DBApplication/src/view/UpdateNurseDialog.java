package view;

import model.Nurse;
import model.NurseManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateNurseDialog extends JDialog {
    private JTextField firstNameField, lastNameField, contactField;
    private NurseManagement nurseManagement;
    private Nurse originalNurse;

    public UpdateNurseDialog(JFrame parent, Nurse nurse) {
        super(parent, "Update Nurse: " + nurse.getNurseID(), true);
        nurseManagement = new NurseManagement();
        this.originalNurse = nurse;

        // DIALOG BASE LAYOUT
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // HEADER PANEL
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70)); // green
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Update Nurse: " + nurse.getNurseID(), SwingConstants.CENTER);
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

        JLabel infoLabel = new JLabel("Nurse Information");
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
        firstNameField.setText(nurse.getFirstName());
        formPanel.add(firstNameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        lastNameField = new JTextField(15);
        lastNameField.setText(nurse.getLastName());
        formPanel.add(lastNameField, gbc);

        // Contact Number
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Contact Number:"), gbc);
        gbc.gridx = 1;
        contactField = new JTextField(15);
        contactField.setText(nurse.getContactNo());
        formPanel.add(contactField, gbc);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(formPanel, BorderLayout.CENTER);

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

        if (fn.isEmpty() || ln.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all required fields.", "Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Nurse updatedNurse = new Nurse(ln, fn, contact);
        updatedNurse.setNurse_id(originalNurse.getNurseID());

        boolean success = nurseManagement.updateNurseRecord(updatedNurse);

        if (success) {
            JOptionPane.showMessageDialog(this, "Nurse record updated successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update nurse record.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}