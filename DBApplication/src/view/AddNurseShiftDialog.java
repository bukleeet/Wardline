package view;

import model.NurseShift;
import model.NurseShiftManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddNurseShiftDialog extends JDialog {

    private JTextField nurseIDField, shiftDayField, shiftStartField, shiftEndField;
    private NurseShiftManagement nurseShiftMgmt;

    public AddNurseShiftDialog(JFrame parent) {
        super(parent, "Add Nurse Shift", true);
        nurseShiftMgmt = new NurseShiftManagement();

        // --- DIALOG BASE LAYOUT ---
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- HEADER PANEL ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70)); // green
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Add Nurse Shift", SwingConstants.CENTER);
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

        JLabel infoLabel = new JLabel("Nurse Shift Information");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(infoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // Nurse ID
        gbc.gridx = 0;
        formPanel.add(new JLabel("Nurse ID:"), gbc);
        gbc.gridx = 1;
        nurseIDField = new JTextField(15);
        formPanel.add(nurseIDField, gbc);

        // Shift Day ID
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Schedule Day:"), gbc);
        gbc.gridx = 1;
        shiftDayField = new JTextField(15);
        formPanel.add(shiftDayField, gbc);

        // Shift Start
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Start of Shift: "), gbc);
        gbc.gridx = 1;
        shiftStartField = new JTextField(15);
        formPanel.add(shiftStartField, gbc);

        // Shift End
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("End of Shift: "), gbc);
        gbc.gridx = 1;
        shiftEndField = new JTextField(15);
        formPanel.add(shiftEndField, gbc);


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
        String nurseIDText = nurseIDField.getText().trim();
        String shiftDay = shiftDayField.getText().trim();
        String shiftStart = shiftStartField.getText().trim();
        String shiftEnd = shiftEndField.getText().trim();

        if (nurseIDText.isEmpty() || shiftDay.isEmpty() || shiftStart.isEmpty() || shiftEnd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all required fields.", "Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int nurseID;

        try {
            nurseID = Integer.parseInt(nurseIDText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Nurse ID must be a valid whole number",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        NurseShift nurseShift = new NurseShift(nurseID, shiftDay, shiftStart, shiftEnd);
        boolean success = nurseShiftMgmt.createNurseShift(nurseShift);

        if (success) {
            JOptionPane.showMessageDialog(this, "Nurse Shift added successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add nurse shift.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- TEST LAUNCHER ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddNurseShiftDialog dialog = new AddNurseShiftDialog(null);
            dialog.setVisible(true);
        });
    }
}