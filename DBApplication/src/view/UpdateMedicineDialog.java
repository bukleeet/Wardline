package view;

import model.Medicine;
import model.MedicineManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateMedicineDialog extends JDialog {

    private JTextField medicineNameField, stockQtyField;
    private MedicineManagement medicineManagement;
    private Medicine originalMedicine;

    public UpdateMedicineDialog(JFrame parent, Medicine medicine) {
        super(parent, "Update Medicine: " + medicine.getMedicineID(), true);
        medicineManagement = new MedicineManagement();
        this.originalMedicine = medicine;

        // DIALOG BASE LAYOUT
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // HEADER PANEL
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 110, 70)); // green
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Update Medicine: " + medicine.getMedicineID(), SwingConstants.CENTER);
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

        JLabel infoLabel = new JLabel("Medicine Information");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(infoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // Medicine Name
        gbc.gridx = 0;
        formPanel.add(new JLabel("Medicine Name:"), gbc);
        gbc.gridx = 1;
        medicineNameField = new JTextField(15);
        medicineNameField.setText(medicine.getMedicineName());
        formPanel.add(medicineNameField, gbc);

        // Stock Quantity
        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Stock Quantity:"), gbc);
        gbc.gridx = 1;
        stockQtyField = new JTextField(15);
        stockQtyField.setText(String.valueOf(medicine.getStockQty()));
        formPanel.add(stockQtyField, gbc);

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
        String mn = medicineNameField.getText().trim();
        String stockQtyText = stockQtyField.getText().trim();

        if (mn.isEmpty() || stockQtyText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all required fields.", "Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int stockQty;
        try {
            stockQty = Integer.parseInt(stockQtyText);
            if (stockQty < 0) {
                JOptionPane.showMessageDialog(this, "Stock quantity cannot be a negative number", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Stock quantity must be a valid number", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create updated Medicine object, preserving the ID
        Medicine updatedMedicine = new Medicine(mn, stockQty);
        updatedMedicine.setMedicineID(originalMedicine.getMedicineID());

        boolean success = medicineManagement.updateMedicineRecord(updatedMedicine);

        if (success) {
            JOptionPane.showMessageDialog(this, "Medicine record updated successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update medicine record. Check database constraints.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}