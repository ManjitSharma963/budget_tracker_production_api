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

**Note:** This application connects to an **external MySQL database** (not in Docker container).

#### Option 1: Manual MySQL Setup (Recommended)

1. Install MySQL 8.0 on your system or use an existing MySQL server
2. Create database:
   ```sql
   CREATE DATABASE expenses_tracker CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Create user:
   ```sql
   CREATE USER 'appuser'@'localhost' IDENTIFIED BY 'apppass';
   ```
4. Grant privileges:
   ```sql
   GRANT ALL PRIVILEGES ON expenses_tracker.* TO 'appuser'@'localhost';
   FLUSH PRIVILEGES;
   ```

#### Option 2: Let Application Create Database

The application will automatically create the database if it doesn't exist (requires CREATE DATABASE privilege).

#### Configuration via Environment Variables

You can override database connection using environment variables:

```bash
export DB_HOST=localhost          # MySQL host (default: localhost)
export DB_PORT=3306              # MySQL port (default: 3306)
export DB_NAME=expenses_tracker   # Database name (default: expenses_tracker)
export DB_USER=appuser            # MySQL username (default: appuser)
export DB_PASSWORD=apppass        # MySQL password (default: apppass)
```

See `EXTERNAL_MYSQL_SETUP.md` for detailed setup instructions.

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
- **Database:** `expenses_tracker`
- **Username:** `appuser`
- **Password:** `apppass`
- **Root Password:** `root123`

### Database Connection Details

- JDBC URL: `jdbc:mysql://localhost:3306/expenses_tracker`
- The database will be created automatically if it doesn't exist (via `createDatabaseIfNotExist=true`)

### Connect to MySQL

```bash
# Connect to external MySQL
mysql -u appuser -papppass expenses_tracker

# Or using root
mysql -u root -p
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

