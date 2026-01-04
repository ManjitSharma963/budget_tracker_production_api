# Authentication Guide

## Overview

The application now uses JWT (JSON Web Token) based authentication. All expenses, income, and credits are user-specific. Users can only see and manage their own data.

## Authentication Endpoints

### Register User

**POST** `/api/auth/register`

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe",
  "message": "User registered successfully"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Login

**POST** `/api/auth/login`

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe",
  "message": "Login successful"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

## Using JWT Token

After registering or logging in, you'll receive a JWT token. Include this token in the `Authorization` header for all protected endpoints:

```
Authorization: Bearer <your-token-here>
```

### Example: Get All Expenses (Authenticated)

```bash
curl -X GET http://localhost:8080/api/expenses \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Example: Create Expense (Authenticated)

```bash
curl -X POST http://localhost:8080/api/expenses \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.50,
    "description": "Grocery shopping",
    "category": "Food"
  }'
```

## Public Endpoints

These endpoints don't require authentication:

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `GET /api/health` - Health check

## Protected Endpoints

All other endpoints require authentication:

### Expenses
- `GET /api/expenses` - Get all expenses (user-specific)
- `GET /api/expenses/:id` - Get expense by ID (user-specific)
- `POST /api/expenses` - Create expense (automatically assigned to user)
- `PUT /api/expenses/:id` - Update expense (user-specific)
- `DELETE /api/expenses/:id` - Delete expense (user-specific)

### Income
- `GET /api/income` - Get all income (user-specific)
- `GET /api/income/:id` - Get income by ID (user-specific)
- `POST /api/income` - Create income (automatically assigned to user)
- `PUT /api/income/:id` - Update income (user-specific)
- `DELETE /api/income/:id` - Delete income (user-specific)

### Credits
- `GET /api/credits` - Get all credits (user-specific)
- `GET /api/credits/:id` - Get credit by ID (user-specific)
- `POST /api/credits` - Create credit (automatically assigned to user)
- `PUT /api/credits/:id` - Update credit (user-specific)
- `DELETE /api/credits/:id` - Delete credit (user-specific)

## Frontend Integration

### JavaScript/Fetch Example

```javascript
// Register
const register = async (username, email, password) => {
  const response = await fetch('http://localhost:8080/api/auth/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ username, email, password })
  });
  const data = await response.json();
  localStorage.setItem('token', data.token);
  return data;
};

// Login
const login = async (username, password) => {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ username, password })
  });
  const data = await response.json();
  localStorage.setItem('token', data.token);
  return data;
};

// Authenticated Request
const getExpenses = async () => {
  const token = localStorage.getItem('token');
  const response = await fetch('http://localhost:8080/api/expenses', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return await response.json();
};
```

### Axios Example

```javascript
import axios from 'axios';

// Set default authorization header
axios.defaults.headers.common['Authorization'] = `Bearer ${localStorage.getItem('token')}`;

// Register
const register = async (username, email, password) => {
  const response = await axios.post('http://localhost:8080/api/auth/register', {
    username, email, password
  });
  localStorage.setItem('token', response.data.token);
  return response.data;
};

// Login
const login = async (username, password) => {
  const response = await axios.post('http://localhost:8080/api/auth/login', {
    username, password
  });
  localStorage.setItem('token', response.data.token);
  axios.defaults.headers.common['Authorization'] = `Bearer ${response.data.token}`;
  return response.data;
};

// Get Expenses
const getExpenses = async () => {
  const response = await axios.get('http://localhost:8080/api/expenses');
  return response.data;
};
```

## User Data Isolation

- Each user can only see their own expenses, income, and credits
- When creating new records, they are automatically assigned to the authenticated user
- Users cannot access or modify other users' data
- All queries are filtered by the authenticated user

## Token Expiration

- Default token expiration: 24 hours (86400000 milliseconds)
- After expiration, users need to login again
- Token expiration can be configured in `application.properties`:
  ```
  jwt.expiration=86400000
  ```

## Security Notes

1. **Password Storage**: Passwords are hashed using BCrypt before storage
2. **JWT Secret**: Change the JWT secret in production (`application.properties`)
3. **HTTPS**: Use HTTPS in production for secure token transmission
4. **Token Storage**: Store tokens securely (e.g., httpOnly cookies in production)

## Error Responses

### Unauthorized (401)
```json
{
  "token": null,
  "username": null,
  "message": "Invalid username or password"
}
```

### Bad Request (400)
```json
{
  "token": null,
  "username": null,
  "message": "Username already exists"
}
```

### Forbidden (403)
When trying to access another user's data or with invalid token.

