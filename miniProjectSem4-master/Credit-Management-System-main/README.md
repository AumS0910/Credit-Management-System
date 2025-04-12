# Restaurant Credit Management System

A Spring Boot application for managing restaurant customer credits and orders.

## Features

- Customer Management
  - Add, edit, and delete customers
  - Track customer credit limits and balances
  - Handle full and partial credit settlements
- Order Management
  - Create and manage orders
  - Automatic credit balance updates
- Transaction History
  - Track all credit, debit, and settlement transactions
  - Detailed transaction history per customer
- Dashboard
  - Overview of credit statistics
  - Quick access to customer information
- Reporting
  - Generate credit and transaction reports

## Technologies Used

- Java 11
- Spring Boot
- Spring Data JPA
- Thymeleaf
- Bootstrap 5
- MySQL Database

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven
- MySQL

### Installation

1. Clone the repository:
   ```bash
   git clone [your-repository-url]
   ```

2. Navigate to the project directory:
   ```bash
   cd restaurant-credit-system
   ```

3. Create a MySQL database:
   ```sql
   CREATE DATABASE restaurant_credit;
   ```

4. Configure the application.properties file with your database credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/restaurant_credit
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

5. Build and run the application:
   ```bash
   mvn spring-boot:run
   ```

6. Access the application at: http://localhost:8080

## Usage

1. Register an admin account
2. Log in to access the dashboard
3. Add customers and set their credit limits
4. Create orders and manage credit settlements
5. View transaction history and generate reports

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.
