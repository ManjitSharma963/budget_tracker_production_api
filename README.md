# Expenses Tracker Backend

A Spring Boot REST API for managing expenses, income, and credits.

## Features

- **Expenses Management**: Create, read, update, and delete expenses
- **Income Management**: Create, read, update, and delete income records
- **Credits Management**: Create, read, update, and delete credit records
- **Health Check**: API status endpoint

## Technology Stack

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- MySQL 8.0
- Maven

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 (or Docker for running MySQL)

## Getting Started

### Database Setup

#### Option 1: Using Docker Compose (Recommended)

```bash
docker-compose up -d
```

This will start MySQL 8.0 with the following configuration:
- Database: `expenes_tracker`
- Username: `appuser`
- Password: `apppass`
- Root Password: `root123`
- Port: `3306`

#### Option 2: Manual MySQL Setup

1. Install MySQL 8.0
2. Create database: `CREATE DATABASE expenes_tracker;`
3. Create user: `CREATE USER 'appuser'@'localhost' IDENTIFIED BY 'apppass';`
4. Grant privileges: `GRANT ALL PRIVILEGES ON expenes_tracker.* TO 'appuser'@'localhost';`
5. Flush privileges: `FLUSH PRIVILEGES;`

### Build the Project

```bash
mvn clean install
```

### Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

**Note:** Make sure MySQL is running before starting the application.

## API Endpoints

### Expenses

- `GET /api/expenses` - Fetch all expenses
- `GET /api/expenses/:id` - Fetch single expense
- `POST /api/expenses` - Create expense
- `PUT /api/expenses/:id` - Update expense
- `DELETE /api/expenses/:id` - Delete expense

### Income

- `GET /api/income` - Fetch all income
- `GET /api/income/:id` - Fetch single income
- `POST /api/income` - Create income
- `PUT /api/income/:id` - Update income
- `DELETE /api/income/:id` - Delete income

### Credits

- `GET /api/credits` - Fetch all credits
- `GET /api/credits/:id` - Fetch single credit
- `POST /api/credits` - Create credit
- `PUT /api/credits/:id` - Update credit
- `DELETE /api/credits/:id` - Delete credit

### Health Check

- `GET /api/health` - Check API status

## Example Requests

### Create an Expense

```bash
curl -X POST http://localhost:8080/api/expenses \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.50,
    "description": "Grocery shopping",
    "category": "Food"
  }'
```

### Create an Income

```bash
curl -X POST http://localhost:8080/api/income \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 5000.00,
    "description": "Monthly salary",
    "source": "Employer"
  }'
```

### Create a Credit

```bash
curl -X POST http://localhost:8080/api/credits \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 200.00,
    "description": "Loan from friend",
    "creditor": "John Doe"
  }'
```

## Database

The application uses MySQL 8.0 database. The database configuration is:

- **Host:** `localhost`
- **Port:** `3306`
- **Database:** `expenes_tracker`
- **Username:** `appuser`
- **Password:** `apppass`
- **Root Password:** `root123`

### Database Connection Details

- JDBC URL: `jdbc:mysql://localhost:3306/expenes_tracker`
- The database will be created automatically if it doesn't exist (via `createDatabaseIfNotExist=true`)

### Connect to MySQL

```bash
# Using Docker
docker exec -it expenses-tracker-mysql mysql -u appuser -papppass expenes_tracker

# Or using root
docker exec -it expenses-tracker-mysql mysql -u root -proot123
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/expensetracker/
│   │       ├── ExpensesTrackerApplication.java
│   │       ├── controller/
│   │       │   ├── ExpenseController.java
│   │       │   ├── IncomeController.java
│   │       │   ├── CreditController.java
│   │       │   └── HealthController.java
│   │       ├── entity/
│   │       │   ├── Expense.java
│   │       │   ├── Income.java
│   │       │   └── Credit.java
│   │       ├── repository/
│   │       │   ├── ExpenseRepository.java
│   │       │   ├── IncomeRepository.java
│   │       │   └── CreditRepository.java
│   │       └── service/
│   │           ├── ExpenseService.java
│   │           ├── IncomeService.java
│   │           └── CreditService.java
│   └── resources/
│       └── application.properties
└── pom.xml
```

