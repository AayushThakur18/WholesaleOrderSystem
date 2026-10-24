
package com.wholesale;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;

public class ProductForm extends JFrame {

    private JTextField nameField, priceField, stockField;
    private JTable table;
    private DefaultTableModel model;

    public ProductForm() {
        setTitle("Product Management");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel("Product Management", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 22));

        nameField = new JTextField();
        priceField = new JTextField();
        stockField = new JTextField();

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        form.add(new JLabel("Product Name:"));
        form.add(nameField);
        form.add(new JLabel("Price:"));
        form.add(priceField);
        form.add(new JLabel("Stock Quantity:"));
        form.add(stockField);

        JButton addButton = new JButton("Add Product");
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");
        JButton clearButton = new JButton("Clear Fields");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(clearButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(heading, BorderLayout.NORTH);
        topPanel.add(form, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        model = new DefaultTableModel(
                new String[]{"ID", "Product Name", "Price", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        refreshButton.addActionListener(e -> loadProducts());
        clearButton.addActionListener(e -> clearFields());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.convertRowIndexToModel(
                        table.getSelectedRow());

                nameField.setText(model.getValueAt(row, 1).toString());
                priceField.setText(model.getValueAt(row, 2).toString());
                stockField.setText(model.getValueAt(row, 3).toString());
            }
        });

        loadProducts();
    }

    private boolean validateFields() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Product name is required.");
            return false;
        }

        try {
            BigDecimal price = new BigDecimal(
                    priceField.getText().trim());

            int stock = Integer.parseInt(stockField.getText().trim());

            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Price must be greater than zero.");
                return false;
            }

            if (stock < 0) {
                JOptionPane.showMessageDialog(this,
                        "Stock cannot be negative.");
                return false;
            }

            return true;

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Enter a valid price and whole-number stock quantity.");
            return false;
        }
    }

    private void addProduct() {
        if (!validateFields()) return;

        String sql = "INSERT INTO products " +
                "(product_name, price, stock_quantity) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nameField.getText().trim());
            ps.setBigDecimal(2,
                    new BigDecimal(priceField.getText().trim()));
            ps.setInt(3,
                    Integer.parseInt(stockField.getText().trim()));

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Product added successfully!");

            clearFields();
            loadProducts();

        } catch (SQLException ex) {
            showDatabaseError("add product", ex);
        }
    }

    private void loadProducts() {
        model.setRowCount(0);

        String sql = "SELECT product_id, product_name, price, " +
                "stock_quantity FROM products ORDER BY product_id";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getBigDecimal("price"),
                        rs.getInt("stock_quantity")
                });
            }

        } catch (SQLException ex) {
            showDatabaseError("load products", ex);
        }
    }

    private void updateProduct() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a product from the table first.");
            return;
        }

        if (!validateFields()) return;

        int row = table.convertRowIndexToModel(selectedRow);
        int productId = Integer.parseInt(
                model.getValueAt(row, 0).toString());

        String sql = "UPDATE products SET product_name=?, price=?, " +
                "stock_quantity=? WHERE product_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nameField.getText().trim());
            ps.setBigDecimal(2,
                    new BigDecimal(priceField.getText().trim()));
            ps.setInt(3,
                    Integer.parseInt(stockField.getText().trim()));
            ps.setInt(4, productId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Product updated successfully!");

            clearFields();
            loadProducts();

        } catch (SQLException ex) {
            showDatabaseError("update product", ex);
        }
    }

    private void deleteProduct() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a product from the table first.");
            return;
        }

        int row = table.convertRowIndexToModel(selectedRow);
        int productId = Integer.parseInt(
                model.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this product?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM products WHERE product_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Product deleted successfully.");

            clearFields();
            loadProducts();

        } catch (SQLException ex) {
            showDatabaseError(
                    "delete product (it may already be used in an order)", ex);
        }
    }

    private void clearFields() {
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
        table.clearSelection();
    }

    private void showDatabaseError(String operation, SQLException ex) {
        JOptionPane.showMessageDialog(this,
                "Could not " + operation + ".\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
