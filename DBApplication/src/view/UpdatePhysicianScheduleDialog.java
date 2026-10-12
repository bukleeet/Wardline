package view;

import model.PhysicianSchedule;
import model.PhysicianScheduleManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdatePhysicianScheduleDialog extends JDialog {

    private JTextField physicianIDField, scheduleDayField, scheduleStartField, scheduleEndField;
    private PhysicianScheduleManagement physicianScheduleMgmt;
    private PhysicianSchedule originalSchedule;

    public UpdatePhysicianScheduleDialog(JFrame parent, PhysicianSchedule schedule) {
        super(parent, "Update Physician Schedule: " + schedule.getPhysicianScheduleID(), true);
        physicianScheduleMgmt = new PhysicianScheduleManagement();
        this.originalSchedule = schedule;

        // DIALOG BASE LAYOUT
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // HEADER PANEL
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70));
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Update Physician Schedule: " + schedule.getPhysicianScheduleID(), SwingConstants.CENTER);
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

        JLabel infoLabel = new JLabel("Schedule Details");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(infoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // Physician ID (Non-editable FK)
        gbc.gridx = 0;
        formPanel.add(new JLabel("Physician ID:"), gbc);
        gbc.gridx = 1;
        physicianIDField = new JTextField(15);
        physicianIDField.setText(String.valueOf(schedule.getPhysicianID()));
        physicianIDField.setEditable(false); 
        formPanel.add(physicianIDField, gbc);

        // Schedule Day
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Schedule Day:"), gbc);
        gbc.gridx = 1;
        scheduleDayField = new JTextField(15);
        scheduleDayField.setText(schedule.getDay());
        formPanel.add(scheduleDayField, gbc);

        // Start Time
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Start Time (HH:MM:SS): "), gbc);
        gbc.gridx = 1;
        scheduleStartField = new JTextField(15);
        scheduleStartField.setText(schedule.getStartTime());
        formPanel.add(scheduleStartField, gbc);

        // End Time
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("End Time (HH:MM:SS): "), gbc);
        gbc.gridx = 1;
        scheduleEndField = new JTextField(15);
        scheduleEndField.setText(schedule.getEndTime());
        formPanel.add(scheduleEndField, gbc);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(formPanel, BorderLayout.CENTER);

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
            JOptionPane.showMessageDialog(this, "Physician ID must be a valid whole number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PhysicianSchedule updatedSchedule = new PhysicianSchedule(physicianID, scheduleDay, scheduleStart, scheduleEnd);
        updatedSchedule.setPhysicianScheduleID(originalSchedule.getPhysicianScheduleID()); 

        boolean success = physicianScheduleMgmt.updatePhysicianSchedule(updatedSchedule);

        if (success) {
            JOptionPane.showMessageDialog(this, "Physician Schedule updated successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update schedule. Check constraints.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}