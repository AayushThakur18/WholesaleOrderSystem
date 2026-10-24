
package com.wholesale;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ReportForm extends JFrame {

    private JLabel ordersLabel;
    private JLabel salesLabel;
    private JLabel paymentsLabel;
    private JLabel outstandingLabel;
    private JTable deliveryTable;
    private DefaultTableModel deliveryModel;

    public ReportForm() {
        setTitle("Wholesale Business Reports");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel(
                "Wholesale Business Reports",
                SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        add(heading, BorderLayout.NORTH);

        JPanel summaryPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        summaryPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20));

        ordersLabel = new JLabel("Total Orders: 0");
        salesLabel = new JLabel("Total Sales: ₹0.00");
        paymentsLabel = new JLabel("Payments Received: ₹0.00");
        outstandingLabel = new JLabel("Outstanding Credit: ₹0.00");

        Font summaryFont = new Font("Arial", Font.BOLD, 16);
        ordersLabel.setFont(summaryFont);
        salesLabel.setFont(summaryFont);
        paymentsLabel.setFont(summaryFont);
        outstandingLabel.setFont(summaryFont);

        summaryPanel.add(ordersLabel);
        summaryPanel.add(salesLabel);
        summaryPanel.add(paymentsLabel);
        summaryPanel.add(outstandingLabel);

        deliveryModel = new DefaultTableModel(
                new String[]{"Delivery Status", "Number of Deliveries"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        deliveryTable = new JTable(deliveryModel);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(summaryPanel, BorderLayout.NORTH);
        centerPanel.add(new JLabel("Delivery Summary", SwingConstants.CENTER),
                BorderLayout.CENTER);
        centerPanel.add(new JScrollPane(deliveryTable), BorderLayout.SOUTH);

        JButton refreshButton = new JButton("Refresh Reports");
        refreshButton.addActionListener(e -> loadReports());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(refreshButton);

        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadReports();
    }

    private void loadReports() {
        try (Connection con = DBConnection.getConnection()) {

            String summarySql =
                    "SELECT " +
                    "(SELECT COUNT(*) FROM orders) AS total_orders, " +
                    "(SELECT COALESCE(SUM(total_amount), 0) FROM orders) AS total_sales, " +
                    "(SELECT COALESCE(SUM(amount), 0) FROM payments) AS payments_received, " +
                    "(SELECT COALESCE(SUM(o.total_amount - " +
                    "COALESCE(p.paid, 0)), 0) " +
                    "FROM orders o LEFT JOIN " +
                    "(SELECT order_id, SUM(amount) AS paid " +
                    "FROM payments GROUP BY order_id) p " +
                    "ON o.order_id = p.order_id) AS outstanding_credit";

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(summarySql)) {

                if (rs.next()) {
                    ordersLabel.setText(
                            "Total Orders: " + rs.getInt("total_orders"));

                    salesLabel.setText(
                            "Total Sales: ₹" + rs.getBigDecimal("total_sales"));

                    paymentsLabel.setText(
                            "Payments Received: ₹" +
                            rs.getBigDecimal("payments_received"));

                    outstandingLabel.setText(
                            "Outstanding Credit: ₹" +
                            rs.getBigDecimal("outstanding_credit"));
                }
            }

            deliveryModel.setRowCount(0);

            String deliverySql =
                    "SELECT delivery_status, COUNT(*) AS total " +
                    "FROM deliveries GROUP BY delivery_status";

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(deliverySql)) {

                while (rs.next()) {
                    deliveryModel.addRow(new Object[]{
                            rs.getString("delivery_status"),
                            rs.getInt("total")
                    });
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load reports: " + ex.getMessage(),
                    "Report Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
