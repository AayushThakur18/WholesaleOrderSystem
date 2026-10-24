
package com.wholesale;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;

public class PaymentForm extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField amountField;

    public PaymentForm() {
        setTitle("Credit and Payments Management");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel(
                "Credit and Payments Management",
                SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        add(heading, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{
                        "Order ID", "Customer", "Order Total",
                        "Paid Amount", "Outstanding"
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

        amountField = new JTextField(10);

        JButton paymentButton = new JButton("Record Payment");
        JButton refreshButton = new JButton("Refresh");

        JPanel bottom = new JPanel(new FlowLayout());
        bottom.add(new JLabel("Payment Amount:"));
        bottom.add(amountField);
        bottom.add(paymentButton);
        bottom.add(refreshButton);

        add(bottom, BorderLayout.SOUTH);

        paymentButton.addActionListener(e -> recordPayment());
        refreshButton.addActionListener(e -> loadOrders());

        loadOrders();
    }

    private void loadOrders() {
        model.setRowCount(0);

        String sql =
                "SELECT o.order_id, c.name, o.total_amount, " +
                "COALESCE(SUM(p.amount), 0) AS paid_amount " +
                "FROM orders o " +
                "JOIN customers c ON o.customer_id = c.customer_id " +
                "LEFT JOIN payments p ON o.order_id = p.order_id " +
                "GROUP BY o.order_id, c.name, o.total_amount " +
                "ORDER BY o.order_id DESC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total_amount");
                BigDecimal paid = rs.getBigDecimal("paid_amount");
                BigDecimal outstanding = total.subtract(paid);

                model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("name"),
                        total,
                        paid,
                        outstanding
                });
            }

        } catch (SQLException ex) {
            showError("Could not load orders: " + ex.getMessage());
        }
    }

    private void recordPayment() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow < 0) {
            showError("Select an order from the table first.");
            return;
        }

        int row = table.convertRowIndexToModel(selectedRow);

        int orderId = Integer.parseInt(
                model.getValueAt(row, 0).toString());

        BigDecimal outstanding =
                new BigDecimal(model.getValueAt(row, 4).toString());

        BigDecimal amount;

        try {
            amount = new BigDecimal(amountField.getText().trim());

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Payment must be greater than zero.");
                return;
            }

            if (amount.compareTo(outstanding) > 0) {
                showError("Payment cannot exceed the outstanding balance.");
                return;
            }

        } catch (NumberFormatException ex) {
            showError("Enter a valid payment amount.");
            return;
        }

        String sql =
                "INSERT INTO payments (order_id, amount) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                BigDecimal currentOutstanding;

                String balanceSql =
                        "SELECT o.total_amount - " +
                        "COALESCE(SUM(p.amount), 0) AS outstanding " +
                        "FROM orders o LEFT JOIN payments p " +
                        "ON o.order_id = p.order_id " +
                        "WHERE o.order_id = ? " +
                        "GROUP BY o.order_id, o.total_amount FOR UPDATE";

                try (PreparedStatement ps =
                             con.prepareStatement(balanceSql)) {
                    ps.setInt(1, orderId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Order not found.");
                        }
                        currentOutstanding =
                                rs.getBigDecimal("outstanding");
                    }
                }

                if (amount.compareTo(currentOutstanding) > 0) {
                    throw new SQLException(
                            "The outstanding balance changed. Refresh and try again.");
                }

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, orderId);
                    ps.setBigDecimal(2, amount);
                    ps.executeUpdate();
                }

                con.commit();

                JOptionPane.showMessageDialog(this,
                        "Payment recorded successfully!");

                amountField.setText("");
                loadOrders();

            } catch (Exception ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (Exception ex) {
            showError("Could not record payment: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this, message, "Payment Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
