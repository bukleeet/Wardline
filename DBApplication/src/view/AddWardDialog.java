package view;

import model.Ward;
import model.WardManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddWardDialog extends JDialog {

    private JTextField wardNumberField, floorField;
    private JComboBox<String> statusBox;
    private WardManagement  wardManagement;

    public AddWardDialog(JFrame parent) {
        super(parent, "Add Ward", true);
        wardManagement = new WardManagement();

        // --- DIALOG BASE LAYOUT ---
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- HEADER PANEL ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70)); // green
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Add Ward", SwingConstants.CENTER);
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

        JLabel infoLabel = new JLabel("Ward Information");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(infoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // Ward number and floor
        gbc.gridx = 0;
        formPanel.add(new JLabel("Ward Number:"), gbc);
        gbc.gridx = 1;
        wardNumberField = new JTextField(15);
        formPanel.add(wardNumberField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Floor:"), gbc);
        gbc.gridx = 1;
        floorField = new JTextField(15);
        formPanel.add(floorField, gbc);

        // status
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusBox = new JComboBox<>(new String[]{"Available", "Occupied"});
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
        String wardn = wardNumberField.getText().trim();
        String fr = floorField.getText().trim();
        String st = statusBox.getSelectedItem().toString();

        if (wardn.isEmpty() || fr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all required fields.", "Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int wn = Integer.parseInt(wardn);

        // check if stock is negative number
        try {
            if (wn < 0)
                JOptionPane.showMessageDialog(this, "Ward number cannot be a negative number", "Invalid Input", JOptionPane.WARNING_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ward number must be a valid number", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Ward ward = new Ward(fr, wn, st);
        boolean success = wardManagement.createWard(ward);

        if (success) {
            JOptionPane.showMessageDialog(this, "Ward added successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add ward.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- TEST LAUNCHER ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddWardDialog dialog = new AddWardDialog(null);
            dialog.setVisible(true);
        });
    }
}