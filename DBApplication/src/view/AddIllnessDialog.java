package view;

import model.Illness;
import model.IllnessManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddIllnessDialog extends JDialog{
    private JTextField illnessNameField, categoryField, illnessDescriptionField;
    private IllnessManagement illnessManagement;

    public AddIllnessDialog(JFrame parent) {
        super(parent, "Add Illness", true);
        illnessManagement = new IllnessManagement();

        // --- DIALOG BASE LAYOUT ---
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- HEADER PANEL ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70)); // green
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Add Illness", SwingConstants.CENTER);
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
        formPanel.add(illnessNameField, gbc);

        //category
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        categoryField = new JTextField(15);
        formPanel.add(categoryField, gbc);

        // description
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        illnessDescriptionField = new JTextField(25);
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
        String in = illnessNameField.getText().trim();
        String cy = categoryField.getText().trim();
        String idn = illnessDescriptionField.getText().trim();

        if (in.isEmpty() || cy.isEmpty() || idn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all required fields.", "Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }


        Illness illness = new Illness(in, cy, idn);
        boolean success = illnessManagement.createIllness(illness);

        if (success) {
            JOptionPane.showMessageDialog(this, "Illness added successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add illness.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- TEST LAUNCHER ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddIllnessDialog dialog = new AddIllnessDialog(null);
            dialog.setVisible(true);
        });
    }
}