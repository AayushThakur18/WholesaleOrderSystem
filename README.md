# Wholesale Order Credit and Delivery Control System

A desktop-based application developed using **Java Swing, JDBC, Maven, and MySQL** to manage wholesale business operations, including customers, products, orders, payments, credit balances, and delivery tracking.

## About the Project

The Wholesale Order Credit and Delivery Control System helps wholesale businesses maintain their daily transactions in one place. It provides a graphical user interface to manage customer information, maintain product stock, create orders, record payments, track deliveries, and view business reports.

This project was developed as an academic project to demonstrate Java GUI development, database connectivity, SQL operations, and transaction management.

## Features

- **Dashboard:** View important business statistics, including orders, sales, payments received, and outstanding credit.
- **Customer Management:** Add and maintain customer details.
- **Product Management:** Add, update, and delete products, including prices and stock quantities.
- **Order Management:** Create orders, calculate total amounts, save order details, and update product stock.
- **Credit and Payment Management:** Record payments against orders and calculate outstanding balances.
- **Delivery Tracking:** Maintain delivery addresses and update delivery statuses, including pending, dispatched, and delivered.
- **Reports:** View order totals, sales, received payments, outstanding credit, and delivery status information.

## Technologies Used

| Technology | Purpose |
|---|---|
| Java | Application logic |
| Java Swing | Graphical user interface |
| JDBC | Database connectivity |
| MySQL | Data storage |
| Maven | Project and dependency management |
| MySQL Workbench | Database management |
| Visual Studio Code | Development environment |

## Project Structure

```text
WholesaleOrderSystem/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── wholesale/
│                   ├── App.java
│                   ├── DBConnection.java
│                   ├── CustomerForm.java
│                   ├── ProductForm.java
│                   ├── OrderForm.java
│                   ├── PaymentForm.java
│                   ├── DeliveryForm.java
│                   └── ReportForm.java
├── pom.xml
├── .gitignore
└── README.md
```

*The structure above represents the main application files; your actual project may contain additional files.*

## Requirements

Before running the project, install:

- JDK (Java Development Kit)
- Apache Maven
- MySQL Server
- MySQL Workbench (optional, for managing the database)

## Database Setup

1. Start your MySQL Server.
2. Open MySQL Workbench.
3. Create a database named `wholesale_system`:

```sql
CREATE DATABASE wholesale_system;
```

4. Create the required tables for customers, products, orders, order items, payments, and deliveries using your project's database schema.
5. Configure the database connection in `DBConnection.java` with your local MySQL settings.

**Note:** Keep your database username and password private. Do not commit real credentials to a public GitHub repository.

## How to Run

1. Clone the repository:

```bash
git clone https://github.com/AayushThakur18/WholesaleOrderSystem.git
```

2. Open the project folder:

```bash
cd WholesaleOrderSystem
```

3. Configure the MySQL connection and create the required database tables.

4. Compile the project:

```bash
mvn compile
```

5. Run the application:

```bash
mvn exec:java "-Dexec.mainClass=com.wholesale.App"
```

Make sure your `pom.xml` contains the required MySQL JDBC driver and Maven Exec Plugin configuration.

## Application Modules

| Module | Description |
|---|---|
| Dashboard | Displays key business statistics |
| Customers | Manages customer records |
| Products | Maintains products, prices, and stock |
| Orders | Creates wholesale orders and calculates totals |
| Credit & Payments | Records payments and outstanding balances |
| Delivery Tracking | Updates delivery information and status |
| Reports | Displays sales and payment summaries |

## Learning Outcomes

Through this project, I gained practical experience in:

- Java Swing GUI development
- Connecting Java applications to MySQL using JDBC
- Performing CRUD operations
- Using SQL joins and aggregate functions
- Managing database transactions
- Maintaining stock and payment records
- Organizing a Java application using Maven
- Using Git and GitHub for version control

## Future Enhancements

- User login and role-based access
- Invoice generation and PDF export
- Search and filter functionality
- Low-stock alerts
- Date-wise and customer-wise reports
- Improved input validation and error handling

## Author

**Aayush Thakur**

B.Tech Computer Science and Engineering

GitHub: [AayushThakur18](https://github.com/AayushThakur18)

---

*Developed as an academic project using Java Swing and MySQL.*
