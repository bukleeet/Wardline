package view;

import model.Ward;
import model.WardManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateWardDialog extends JDialog {

    private JTextField wardNumberField, floorField;
    private JComboBox<String> statusBox;
    private WardManagement wardManagement;
    private Ward originalWard;

    public UpdateWardDialog(JFrame parent, Ward ward) {
        super(parent, "Update Ward: " + ward.getWard_id(), true);
        wardManagement = new WardManagement();
        this.originalWard = ward;

        // --- DIALOG BASE LAYOUT ---
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- HEADER PANEL ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70)); // green
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Update Ward: " + ward.getWard_id(), SwingConstants.CENTER);
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

        // Ward number
        gbc.gridx = 0;
        formPanel.add(new JLabel("Ward Number:"), gbc);
        gbc.gridx = 1;
        wardNumberField = new JTextField(15);
        wardNumberField.setText(String.valueOf(ward.getWardNo()));
        formPanel.add(wardNumberField, gbc);

        // Floor
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Floor:"), gbc);
        gbc.gridx = 1;
        floorField = new JTextField(15);
        floorField.setText(ward.getFloor());
        formPanel.add(floorField, gbc);

        // Status
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        String[] statuses = new String[]{"Available", "Occupied"};
        statusBox = new JComboBox<>(statuses);
        statusBox.setSelectedItem(ward.getStatus());
        formPanel.add(statusBox, gbc);

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
        String wardnText = wardNumberField.getText().trim();
        String fr = floorField.getText().trim();
        String st = statusBox.getSelectedItem().toString();

        if (wardnText.isEmpty() || fr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all required fields.", "Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int wn;
        try {
            wn = Integer.parseInt(wardnText);
            if (wn < 0) {
                JOptionPane.showMessageDialog(this, "Ward number cannot be a negative number", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ward number must be a valid number", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create updated Ward object, preserving the ID
        Ward updatedWard = new Ward(fr, wn, st);
        updatedWard.setWard_id(originalWard.getWard_id());

        boolean success = wardManagement.updateWard(updatedWard);

        if (success) {
            JOptionPane.showMessageDialog(this, "Ward record updated successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update ward record. Check constraints.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}