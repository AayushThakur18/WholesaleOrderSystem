
package com.wholesale;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CustomerForm extends JFrame {

    private JTextField nameField, phoneField, addressField, creditField;
    private JTable table;
    private DefaultTableModel model;

    public CustomerForm() {
        setTitle("Customer Management");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        nameField = new JTextField();
        phoneField = new JTextField();
        addressField = new JTextField();
        creditField = new JTextField();

        form.add(new JLabel("Customer Name:"));
        form.add(nameField);
        form.add(new JLabel("Phone:"));
        form.add(phoneField);
        form.add(new JLabel("Address:"));
        form.add(addressField);
        form.add(new JLabel("Credit Limit:"));
        form.add(creditField);

        JButton addButton = new JButton("Add Customer");
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");

        form.add(addButton);
        form.add(updateButton);

        JPanel actions = new JPanel();
        actions.add(deleteButton);
        actions.add(refreshButton);

        model = new DefaultTableModel(
                new String[]{"ID", "Name", "Phone", "Address", "Credit Limit"}, 0);
        table = new JTable(model);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addCustomer());
        updateButton.addActionListener(e -> updateCustomer());
        deleteButton.addActionListener(e -> deleteCustomer());
        refreshButton.addActionListener(e -> loadCustomers());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                nameField.setText(model.getValueAt(row, 1).toString());
                phoneField.setText(model.getValueAt(row, 2).toString());
                addressField.setText(model.getValueAt(row, 3).toString());
                creditField.setText(model.getValueAt(row, 4).toString());
            }
        });

        loadCustomers();
    }

    private void addCustomer() {
        String sql = "INSERT INTO customers(name, phone, address, credit_limit) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nameField.getText().trim());
            ps.setString(2, phoneField.getText().trim());
            ps.setString(3, addressField.getText().trim());
            ps.setBigDecimal(4, new java.math.BigDecimal(creditField.getText().trim()));

            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Customer name is required.");
                return;
            }

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Customer added successfully!");
            clearFields();
            loadCustomers();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not add customer: " + ex.getMessage());
        }
    }

    private void loadCustomers() {
        model.setRowCount(0);
        String sql = "SELECT customer_id, name, phone, address, credit_limit FROM customers";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getBigDecimal("credit_limit")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load customers: " + ex.getMessage());
        }
    }

    private void updateCustomer() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a customer first.");
            return;
        }

        String sql = "UPDATE customers SET name=?, phone=?, address=?, credit_limit=? WHERE customer_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nameField.getText().trim());
            ps.setString(2, phoneField.getText().trim());
            ps.setString(3, addressField.getText().trim());
            ps.setBigDecimal(4, new java.math.BigDecimal(creditField.getText().trim()));
            ps.setInt(5, Integer.parseInt(model.getValueAt(row, 0).toString()));

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Customer updated successfully!");
            clearFields();
            loadCustomers();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not update customer: " + ex.getMessage());
        }
    }

    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a customer first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this, "Delete this customer?", "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM customers WHERE customer_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(model.getValueAt(row, 0).toString()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Customer deleted successfully!");
            clearFields();
            loadCustomers();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not delete customer: " + ex.getMessage());
        }
    }

    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        addressField.setText("");
        creditField.setText("");
        table.clearSelection();
    }
}
