package view;

import model.Illness;
import model.IllnessManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateIllnessDialog extends JDialog {

    private JTextField illnessNameField, categoryField, illnessDescriptionField;
    private IllnessManagement illnessManagement;
    private Illness originalIllness;

    public UpdateIllnessDialog(JFrame parent, Illness illness) {
        super(parent, "Update Illness: " + illness.getIllness_id(), true);
        illnessManagement = new IllnessManagement();
        this.originalIllness = illness;

        // DIALOG BASE LAYOUT
        setSize(800, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // HEADER PANEL
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70)); // green
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Update Illness: " + illness.getIllness_id(), SwingConstants.CENTER);
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

        JLabel infoLabel = new JLabel("Illness Information");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(infoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // Name
        gbc.gridx = 0;
        formPanel.add(new JLabel("Illness Name:"), gbc);
        gbc.gridx = 1;
        illnessNameField = new JTextField(15);
        illnessNameField.setText(illness.getIllness_name());
        formPanel.add(illnessNameField, gbc);

        // Category
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        categoryField = new JTextField(15);
        categoryField.setText(illness.getCategory());
        formPanel.add(categoryField, gbc);

        // Description
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        illnessDescriptionField = new JTextField(25);
        illnessDescriptionField.setText(illness.getIllness_description());
        formPanel.add(illnessDescriptionField, gbc);


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
        String in = illnessNameField.getText().trim();
        String cy = categoryField.getText().trim();
        String idn = illnessDescriptionField.getText().trim();

        if (in.isEmpty() || cy.isEmpty() || idn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all required fields.", "Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create updated Illness object, preserving the ID
        Illness updatedIllness = new Illness(in, cy, idn);
        updatedIllness.setIllness_id(originalIllness.getIllness_id());

        boolean success = illnessManagement.updateIllness(updatedIllness);

        if (success) {
            JOptionPane.showMessageDialog(this, "Illness record updated successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update illness record. Check database constraints.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}