
package com.wholesale;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DeliveryForm extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField addressField;
    private JComboBox<String> statusBox;

    public DeliveryForm() {
        setTitle("Delivery Tracking");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel(
                "Delivery Tracking Management",
                SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        add(heading, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{
                        "Delivery ID", "Order ID", "Customer",
                        "Address", "Status", "Dispatch Date", "Delivery Date"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        addressField = new JTextField(18);
        statusBox = new JComboBox<>(
                new String[]{"Pending", "Dispatched", "Delivered"});

        JButton saveButton = new JButton("Save / Update Delivery");
        JButton refreshButton = new JButton("Refresh");

        JPanel bottom = new JPanel(new FlowLayout());
        bottom.add(new JLabel("Delivery Address:"));
        bottom.add(addressField);
        bottom.add(new JLabel("Status:"));
        bottom.add(statusBox);
        bottom.add(saveButton);
        bottom.add(refreshButton);

        add(bottom, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.convertRowIndexToModel(
                        table.getSelectedRow());

                Object address = model.getValueAt(row, 3);
                addressField.setText(
                        address == null ? "" : address.toString());

                statusBox.setSelectedItem(
                        model.getValueAt(row, 4).toString());
            }
        });

        saveButton.addActionListener(e -> saveDelivery());
        refreshButton.addActionListener(e -> loadDeliveries());

        loadDeliveries();
    }

    private void loadDeliveries() {
        model.setRowCount(0);

        String sql =
                "SELECT d.delivery_id, d.order_id, c.name, " +
                "d.delivery_address, d.delivery_status, " +
                "d.dispatch_date, d.delivery_date " +
                "FROM deliveries d " +
                "JOIN orders o ON d.order_id = o.order_id " +
                "JOIN customers c ON o.customer_id = c.customer_id " +
                "ORDER BY d.delivery_id DESC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("delivery_id"),
                        rs.getInt("order_id"),
                        rs.getString("name"),
                        rs.getString("delivery_address"),
                        rs.getString("delivery_status"),
                        rs.getDate("dispatch_date"),
                        rs.getDate("delivery_date")
                });
            }

        } catch (SQLException ex) {
            showError("Could not load deliveries: " + ex.getMessage());
        }
    }

    private void saveDelivery() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow < 0) {
            showError("Select a delivery row first.");
            return;
        }

        int row = table.convertRowIndexToModel(selectedRow);
        int deliveryId = Integer.parseInt(
                model.getValueAt(row, 0).toString());

        String address = addressField.getText().trim();
        String status = (String) statusBox.getSelectedItem();

        if (address.isEmpty()) {
            showError("Enter the delivery address.");
            return;
        }

        String sql =
                "UPDATE deliveries SET delivery_address=?, " +
                "delivery_status=?, " +
                "dispatch_date = CASE " +
                "WHEN ? = 'Dispatched' AND dispatch_date IS NULL " +
                "THEN CURRENT_DATE ELSE dispatch_date END, " +
                "delivery_date = CASE " +
                "WHEN ? = 'Delivered' AND delivery_date IS NULL " +
                "THEN CURRENT_DATE ELSE delivery_date END " +
                "WHERE delivery_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, address);
            ps.setString(2, status);
            ps.setString(3, status);
            ps.setString(4, status);
            ps.setInt(5, deliveryId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Delivery updated successfully!");

            loadDeliveries();

        } catch (SQLException ex) {
            showError("Could not update delivery: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message,
                "Delivery Error", JOptionPane.ERROR_MESSAGE);
    }
}
