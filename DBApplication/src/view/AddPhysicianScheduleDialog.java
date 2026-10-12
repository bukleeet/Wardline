package view;

import model.PhysicianSchedule;
import model.PhysicianScheduleManagement;

import javax.swing.*;
        import javax.swing.border.EmptyBorder;
import java.awt.*;
        import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddPhysicianScheduleDialog extends JDialog {

    private JTextField physicianIDField, scheduleDayField, scheduleStartField, scheduleEndField;
    private PhysicianScheduleManagement physicianScheduleMgmt;

    public AddPhysicianScheduleDialog(JFrame parent) {
        super(parent, "Add Physician Schedule", true);
        physicianScheduleMgmt = new PhysicianScheduleManagement();

        // --- DIALOG BASE LAYOUT ---
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- HEADER PANEL ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70)); // green
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Add Physician Schedule", SwingConstants.CENTER);
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

        JLabel infoLabel = new JLabel("Physician Schedule Information");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(infoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // Physician ID
        gbc.gridx = 0;
        formPanel.add(new JLabel("Physician ID:"), gbc);
        gbc.gridx = 1;
        physicianIDField = new JTextField(15);
        formPanel.add(physicianIDField, gbc);

        // Schedule Day ID
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Schedule Day:"), gbc);
        gbc.gridx = 1;
        scheduleDayField = new JTextField(15);
        formPanel.add(scheduleDayField, gbc);

        // Schedule Start
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Start of Schedule: "), gbc);
        gbc.gridx = 1;
        scheduleStartField = new JTextField(15);
        formPanel.add(scheduleStartField, gbc);

        // Schedule End
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("End of Schedule: "), gbc);
        gbc.gridx = 1;
        scheduleEndField = new JTextField(15);
        formPanel.add(scheduleEndField, gbc);


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
        String physicianIDText = physicianIDField.getText().trim();
        String scheduleDay = scheduleDayField.getText().trim();
        String scheduleStart = scheduleStartField.getText().trim();
        String scheduleEnd = scheduleEndField.getText().trim();

        if (physicianIDText.isEmpty() || scheduleDay.isEmpty() || scheduleStart.isEmpty() || scheduleEnd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all required fields.", "Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int physicianID;

        try {
            physicianID = Integer.parseInt(physicianIDText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Physician ID must be a valid whole number",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        PhysicianSchedule physicianSchedule = new PhysicianSchedule(physicianID, scheduleDay, scheduleStart, scheduleEnd);
        boolean success = physicianScheduleMgmt.createPhysicianSchedule(physicianSchedule);

        if (success) {
            JOptionPane.showMessageDialog(this, "Physician Schedule added successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add physician schedule.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- TEST LAUNCHER ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddPhysicianScheduleDialog dialog = new AddPhysicianScheduleDialog(null);
            dialog.setVisible(true);
        });
    }
}