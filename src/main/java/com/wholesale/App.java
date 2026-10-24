
package com.wholesale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class App {

    private static final Color NAVY = new Color(25, 42, 70);
    private static final Color BLUE = new Color(48, 105, 190);
    private static final Color BACKGROUND = new Color(244, 247, 252);
    private static final Color TEXT = new Color(43, 55, 75);
    private static final Color MUTED = new Color(110, 124, 145);
    private static final Color WHITE = Color.WHITE;

    private static JLabel ordersValue;
    private static JLabel salesValue;
    private static JLabel paymentsValue;
    private static JLabel outstandingValue;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::showDashboard);
    }

    private static void showDashboard() {

        JFrame frame = new JFrame(
                "Wholesale Order Credit and Delivery Control System");

        frame.setSize(1150, 720);
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BACKGROUND);
        frame.setLayout(new BorderLayout());

        // LEFT SIDEBAR
        JPanel sidebar = new JPanel();
        sidebar.setBackground(NAVY);
        sidebar.setPreferredSize(new Dimension(235, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(25, 15, 20, 15));

        JLabel brand = new JLabel("<html>WHOLESALE<br>MANAGEMENT</html>");
        brand.setForeground(WHITE);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 23));
        brand.setBorder(new EmptyBorder(0, 10, 5, 0));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Order Control System");
        subtitle.setForeground(new Color(180, 198, 222));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setBorder(new EmptyBorder(0, 10, 30, 0));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(brand);
        sidebar.add(subtitle);

        JLabel menuTitle = new JLabel("MAIN MENU");
        menuTitle.setForeground(new Color(160, 178, 205));
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        menuTitle.setBorder(new EmptyBorder(0, 10, 12, 0));
        menuTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(menuTitle);

        addMenuButton(sidebar, "Dashboard", null, frame);
        addMenuButton(sidebar, "Manage Customers", "customers", frame);
        addMenuButton(sidebar, "Manage Products", "products", frame);
        addMenuButton(sidebar, "Create Order", "order", frame);
        addMenuButton(sidebar, "Credit & Payments", "payments", frame);
        addMenuButton(sidebar, "Delivery Tracking", "delivery", frame);
        addMenuButton(sidebar, "View Reports", "reports", frame);

        sidebar.add(Box.createVerticalGlue());

        JLabel footer = new JLabel(
                "<html>Wholesale Order System<br>Java Swing + MySQL</html>");
        footer.setForeground(new Color(180, 198, 222));
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.setBorder(new EmptyBorder(10, 10, 5, 0));
        sidebar.add(footer);

        // MAIN CONTENT
        JPanel content = new JPanel(new BorderLayout(0, 22));
        content.setBackground(BACKGROUND);
        content.setBorder(new EmptyBorder(28, 30, 25, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel welcome = new JPanel();
        welcome.setOpaque(false);
        welcome.setLayout(new BoxLayout(welcome, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 29));
        title.setForeground(TEXT);

        JLabel description = new JLabel(
                "Welcome! Manage your wholesale business in one place.");
        description.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        description.setForeground(MUTED);
        description.setBorder(new EmptyBorder(6, 0, 0, 0));

        welcome.add(title);
        welcome.add(description);

        JLabel badge = new JLabel("  ADMIN PANEL  ");
        badge.setOpaque(true);
        badge.setBackground(new Color(225, 237, 255));
        badge.setForeground(BLUE);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setBorder(new EmptyBorder(10, 10, 10, 10));

        header.add(welcome, BorderLayout.WEST);
        header.add(badge, BorderLayout.EAST);

        // SUMMARY CARDS
        JPanel stats = new JPanel(new GridLayout(1, 4, 15, 15));
        stats.setOpaque(false);

        ordersValue = new JLabel("...");
        salesValue = new JLabel("...");
        paymentsValue = new JLabel("...");
        outstandingValue = new JLabel("...");

        stats.add(createStatCard(
                "TOTAL ORDERS", ordersValue, "Orders recorded"));
        stats.add(createStatCard(
                "TOTAL SALES", salesValue, "Value of all orders"));
        stats.add(createStatCard(
                "PAYMENTS RECEIVED", paymentsValue, "Money collected"));
        stats.add(createStatCard(
                "OUTSTANDING CREDIT", outstandingValue, "Remaining balance"));

        // QUICK ACCESS
        JPanel quickPanel = new JPanel(new BorderLayout(0, 15));
        quickPanel.setOpaque(false);

        JLabel quickTitle = new JLabel("Quick Access");
        quickTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        quickTitle.setForeground(TEXT);

        JLabel quickDescription = new JLabel(
                "Choose a module to continue working.");
        quickDescription.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        quickDescription.setForeground(MUTED);

        JPanel quickHeader = new JPanel();
        quickHeader.setOpaque(false);
        quickHeader.setLayout(new BoxLayout(quickHeader, BoxLayout.Y_AXIS));
        quickHeader.add(quickTitle);
        quickHeader.add(Box.createVerticalStrut(5));
        quickHeader.add(quickDescription);

        JPanel modules = new JPanel(new GridLayout(3, 2, 15, 15));
        modules.setOpaque(false);

        modules.add(createModuleCard(
                "Manage Customers",
                "Add and update customer details",
                "customers", frame));

        modules.add(createModuleCard(
                "Manage Products",
                "Maintain prices and stock",
                "products", frame));

        modules.add(createModuleCard(
                "Create Order",
                "Create a new wholesale order",
                "order", frame));

        modules.add(createModuleCard(
                "Credit & Payments",
                "Record payments and balances",
                "payments", frame));

        modules.add(createModuleCard(
                "Delivery Tracking",
                "Update delivery progress",
                "delivery", frame));

        modules.add(createModuleCard(
                "View Reports",
                "Review sales and business figures",
                "reports", frame));

        quickPanel.add(quickHeader, BorderLayout.NORTH);
        quickPanel.add(modules, BorderLayout.CENTER);

        JPanel dashboardBody = new JPanel(new BorderLayout(0, 24));
        dashboardBody.setOpaque(false);
        dashboardBody.add(stats, BorderLayout.NORTH);
        dashboardBody.add(quickPanel, BorderLayout.CENTER);

        content.add(header, BorderLayout.NORTH);
        content.add(dashboardBody, BorderLayout.CENTER);

        frame.add(sidebar, BorderLayout.WEST);
        frame.add(content, BorderLayout.CENTER);

        refreshStats();

        frame.setVisible(true);
    }

    private static JPanel createStatCard(
            String title, JLabel value, String note) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 231, 240)),
                new EmptyBorder(14, 14, 14, 14)));

        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 10));
        heading.setForeground(MUTED);

        value.setFont(new Font("Segoe UI", Font.BOLD, 21));
        value.setForeground(BLUE);

        JLabel description = new JLabel(note);
        description.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        description.setForeground(MUTED);

        card.add(heading);
        card.add(Box.createVerticalStrut(13));
        card.add(value);
        card.add(Box.createVerticalStrut(8));
        card.add(description);

        return card;
    }

    private static JButton createModuleCard(
            String title, String description,
            String module, JFrame frame) {

        JButton button = new JButton();
        button.setLayout(new BorderLayout(0, 8));
        button.setBackground(WHITE);
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR));

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 231, 240)),
                new EmptyBorder(16, 18, 16, 12)));

        JLabel name = new JLabel(title);
        name.setFont(new Font("Segoe UI", Font.BOLD, 15));
        name.setForeground(NAVY);

        JLabel detail = new JLabel(description);
        detail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detail.setForeground(MUTED);

        JLabel arrow = new JLabel("Open  >");
        arrow.setFont(new Font("Segoe UI", Font.BOLD, 12));
        arrow.setForeground(BLUE);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(name);
        textPanel.add(Box.createVerticalStrut(7));
        textPanel.add(detail);

        button.add(textPanel, BorderLayout.CENTER);
        button.add(arrow, BorderLayout.SOUTH);

        button.addActionListener(e -> openModule(module, frame));

        return button;
    }

    private static void addMenuButton(
            JPanel sidebar, String title,
            String module, JFrame frame) {

        JButton button = new JButton(title);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
        button.setPreferredSize(new Dimension(205, 43));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(WHITE);
        button.setBackground(module == null ? BLUE : NAVY);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(0, 12, 0, 8));
        button.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR));

        button.addActionListener(e -> openModule(module, frame));

        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(5));
    }

    private static void openModule(String module, JFrame frame) {

        if (module == null) {
            refreshStats();
            return;
        }

        switch (module) {
            case "customers":
                new CustomerForm().setVisible(true);
                break;
            case "products":
                new ProductForm().setVisible(true);
                break;
            case "order":
                new OrderForm().setVisible(true);
                break;
            case "payments":
                new PaymentForm().setVisible(true);
                break;
            case "delivery":
                new DeliveryForm().setVisible(true);
                break;
            case "reports":
                new ReportForm().setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(
                        frame, "Unknown module selected.");
        }
    }

    private static void refreshStats() {

        String sql =
                "SELECT " +
                "(SELECT COUNT(*) FROM orders) AS total_orders, " +
                "(SELECT COALESCE(SUM(total_amount), 0) FROM orders) AS sales, " +
                "(SELECT COALESCE(SUM(amount), 0) FROM payments) AS paid, " +
                "(SELECT COALESCE(SUM(o.total_amount - " +
                "COALESCE(p.paid, 0)), 0) FROM orders o " +
                "LEFT JOIN (SELECT order_id, SUM(amount) AS paid " +
                "FROM payments GROUP BY order_id) p " +
                "ON o.order_id = p.order_id) AS outstanding";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                ordersValue.setText(
                        String.valueOf(rs.getInt("total_orders")));

                salesValue.setText(
                        "₹" + rs.getBigDecimal("sales"));

                paymentsValue.setText(
                        "₹" + rs.getBigDecimal("paid"));

                outstandingValue.setText(
                        "₹" + rs.getBigDecimal("outstanding"));
            }

        } catch (SQLException ex) {
            ordersValue.setText("N/A");
            salesValue.setText("N/A");
            paymentsValue.setText("N/A");
            outstandingValue.setText("N/A");

            System.err.println(
                    "Could not load dashboard statistics: "
                    + ex.getMessage());
        }
    }
}
