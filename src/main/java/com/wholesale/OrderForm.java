
package com.wholesale;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderForm extends JFrame {

    private JComboBox<Customer> customerBox;
    private JComboBox<Product> productBox;
    private JTextField quantityField;
    private JLabel priceLabel, totalLabel;

    public OrderForm() {
        setTitle("Create Wholesale Order");
        setSize(550, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        customerBox = new JComboBox<>();
        productBox = new JComboBox<>();
        quantityField = new JTextField("1", 10);
        priceLabel = new JLabel("₹0.00");
        totalLabel = new JLabel("₹0.00");

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Select Customer:"));
        panel.add(customerBox);

        panel.add(new JLabel("Select Product:"));
        panel.add(productBox);

        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);

        panel.add(new JLabel("Unit Price:"));
        panel.add(priceLabel);

        panel.add(new JLabel("Order Total:"));
        panel.add(totalLabel);

        JButton saveButton = new JButton("Save Order");
        panel.add(new JLabel(""));
        panel.add(saveButton);

        add(panel);

        productBox.addActionListener(e -> updateTotal());
        quantityField.addActionListener(e -> updateTotal());

        saveButton.addActionListener(e -> saveOrder());

        loadCustomers();
        loadProducts();
        updateTotal();
    }

    private void loadCustomers() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT customer_id, name FROM customers ORDER BY name")) {

            while (rs.next()) {
                customerBox.addItem(
                        new Customer(rs.getInt("customer_id"),
                                rs.getString("name")));
            }

        } catch (SQLException ex) {
            showError("Could not load customers: " + ex.getMessage());
        }
    }

    private void loadProducts() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT product_id, product_name, price, stock_quantity " +
                     "FROM products ORDER BY product_name")) {

            while (rs.next()) {
                productBox.addItem(
                        new Product(
                                rs.getInt("product_id"),
                                rs.getString("product_name"),
                                rs.getBigDecimal("price"),
                                rs.getInt("stock_quantity")));
            }

        } catch (SQLException ex) {
            showError("Could not load products: " + ex.getMessage());
        }
    }

    private void updateTotal() {
        Product product = (Product) productBox.getSelectedItem();

        if (product == null) {
            priceLabel.setText("₹0.00");
            totalLabel.setText("₹0.00");
            return;
        }

        priceLabel.setText("₹" + product.price);

        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());

            if (quantity < 1) {
                totalLabel.setText("Enter quantity above zero");
                return;
            }

            totalLabel.setText(
                    "₹" + product.price.multiply(BigDecimal.valueOf(quantity)));

        } catch (NumberFormatException ex) {
            totalLabel.setText("Enter a valid quantity");
        }
    }

    private void saveOrder() {
        Customer customer = (Customer) customerBox.getSelectedItem();
        Product product = (Product) productBox.getSelectedItem();

        if (customer == null || product == null) {
            showError("Add at least one customer and one product first.");
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(quantityField.getText().trim());

            if (quantity < 1) {
                showError("Quantity must be at least 1.");
                return;
            }

        } catch (NumberFormatException ex) {
            showError("Enter a valid whole-number quantity.");
            return;
        }

        String orderSql =
                "INSERT INTO orders (customer_id, total_amount, status) " +
                "VALUES (?, ?, 'Pending')";

        String itemSql =
                "INSERT INTO order_items " +
                "(order_id, product_id, quantity, unit_price) " +
                "VALUES (?, ?, ?, ?)";

        String stockSql =
                "UPDATE products SET stock_quantity = stock_quantity - ? " +
                "WHERE product_id = ? AND stock_quantity >= ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                BigDecimal total =
                        product.price.multiply(BigDecimal.valueOf(quantity));

                // Check and reduce stock safely.
                try (PreparedStatement ps = con.prepareStatement(stockSql)) {
                    ps.setInt(1, quantity);
                    ps.setInt(2, product.id);
                    ps.setInt(3, quantity);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "Insufficient stock. Refresh the product list.");
                    }
                }

                int orderId;

                try (PreparedStatement ps = con.prepareStatement(
                        orderSql, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, customer.id);
                    ps.setBigDecimal(2, total);
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Could not obtain order ID.");
                        }
                        orderId = keys.getInt(1);
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(itemSql)) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, product.id);
                    ps.setInt(3, quantity);
                    ps.setBigDecimal(4, product.price);
                    ps.executeUpdate();
                }

                con.commit();

                JOptionPane.showMessageDialog(this,
                        "Order saved successfully!\nOrder ID: " + orderId +
                        "\nTotal: ₹" + total);

                productBox.removeAllItems();
                loadProducts();
                quantityField.setText("1");
                updateTotal();

            } catch (Exception ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (Exception ex) {
            showError("Could not save order: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message,
                "Order Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class Customer {
        int id;
        String name;

        Customer(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class Product {
        int id;
        String name;
        BigDecimal price;
        int stock;

        Product(int id, String name, BigDecimal price, int stock) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        @Override
        public String toString() {
            return name + " (Stock: " + stock + ")";
        }
    }
}
