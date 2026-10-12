package view;

import model.Discharge;
import model.DischargeManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddDischargeDialog extends JDialog {

    private JTextField admissionIDField, dischargeDateField;
    private DischargeManagement dischargeMgmt;

    public AddDischargeDialog(JFrame parent) {
        super(parent, "Add Discharge", true);
        dischargeMgmt = new DischargeManagement();

        // --- DIALOG BASE LAYOUT ---
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- HEADER PANEL ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70)); // green
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Add Discharge", SwingConstants.CENTER);
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

        JLabel infoLabel = new JLabel("Discharge Information");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(infoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // Admission ID
        gbc.gridx = 0;
        formPanel.add(new JLabel("Admission ID:"), gbc);
        gbc.gridx = 1;
        admissionIDField = new JTextField(15);
        formPanel.add(admissionIDField, gbc);

        // Date of Discharge
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Date of Discharge: "), gbc);
        gbc.gridx = 1;
        dischargeDateField = new JTextField(15);
        formPanel.add(dischargeDateField, gbc);

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
        String admissionIDText = admissionIDField.getText().trim();
        String dischargeDate = dischargeDateField.getText().trim();

        if (admissionIDText.isEmpty() || dischargeDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all required fields.", "Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int admissionID;

        try {
            admissionID = Integer.parseInt(admissionIDText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Patient ID and Ward ID must be valid whole numbers.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Discharge discharge = new Discharge(admissionID, dischargeDate);
        boolean success = dischargeMgmt.createDischargeRecord(discharge);

        if (success) {
            JOptionPane.showMessageDialog(this, "Discharge added successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add discharge.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- TEST LAUNCHER ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddDischargeDialog dialog = new AddDischargeDialog(null);
            dialog.setVisible(true);
        });
    }
}