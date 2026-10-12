package view;

import model.ReportManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreatePhysicianWorkloadDialog extends JDialog {

    private JTextField typeField, physicianIDField, dateOrMonthField, yearField;
    private ReportManagement reportMgmt;

    public CreatePhysicianWorkloadDialog(JFrame parent) {
        super(parent, "Create Report", true);
        reportMgmt = new ReportManagement();

        // --- DIALOG BASE LAYOUT ---
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- HEADER PANEL ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70)); // green
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Physician Workload Report", SwingConstants.CENTER);
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

        JLabel infoLabel = new JLabel("Physician Workload Filters");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(infoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // Type
        gbc.gridx = 0;
        formPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        typeField = new JTextField(15);
        formPanel.add(typeField, gbc);

        // Physician ID
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Physician ID:"), gbc);
        gbc.gridx = 1;
        physicianIDField = new JTextField(15);
        formPanel.add(physicianIDField, gbc);

        // Date or Month
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Date or Month: "), gbc);
        gbc.gridx = 1;
        dateOrMonthField = new JTextField(15);
        formPanel.add(dateOrMonthField, gbc);

        // Year
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Year: "), gbc);
        gbc.gridx = 1;
        yearField = new JTextField(15);
        formPanel.add(yearField, gbc);

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
        String type = typeField.getText().trim();
        String physicianIDText = physicianIDField.getText().trim();
        String dateOrMonth = dateOrMonthField.getText().trim();
        String yearText= yearField.getText().trim();

        if (type.isEmpty() || physicianIDText.isEmpty() || dateOrMonth.isEmpty() || yearText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all required fields.", "Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int physicianID;
        int year;

        try {
            physicianID = Integer.parseInt(physicianIDText);
            year = Integer.parseInt(yearText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Physician ID and Year must be valid whole numbers.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        boolean success = true;

        try{
            reportMgmt.getPhysiciansWorkloadReport(type, physicianID, dateOrMonth, year);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            success = false;
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Physician Workload Report created successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create Physician Workload Report.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- TEST LAUNCHER ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CreatePhysicianWorkloadDialog physicianWorkload = new CreatePhysicianWorkloadDialog(null);
            physicianWorkload.setVisible(true);
        });
    }
}