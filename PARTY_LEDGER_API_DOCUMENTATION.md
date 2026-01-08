# Party-wise Ledger / Account Management API Documentation

## Overview

This feature provides complete party-wise ledger management for trading businesses (granite/marble/supplier-based). Each party has an isolated account with transaction history, running balance, and outstanding amount tracking.

## Database Schema

### Tables

#### 1. `parties` Table
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `name` (VARCHAR(200), NOT NULL) - Party name (e.g., "Mohit Granite", "Sri Ram Marble")
- `address` (VARCHAR(500)) - Party address
- `phone` (VARCHAR(50)) - Contact phone
- `email` (VARCHAR(100)) - Contact email
- `gst_number` (VARCHAR(100)) - GST number
- `notes` (VARCHAR(1000)) - Additional notes
- `opening_balance` (DECIMAL(19,2)) - Opening balance (default: 0)
- `user_id` (BIGINT, FOREIGN KEY → users.id) - Owner user
- `created_at` (DATETIME)
- `updated_at` (DATETIME)

#### 2. `ledger_entries` Table
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `party_id` (BIGINT, FOREIGN KEY → parties.id, NOT NULL)
- `transaction_type` (ENUM: PURCHASE, PAYMENT, ADJUSTMENT, NOT NULL)
- `amount` (DECIMAL(19,2), NOT NULL, POSITIVE)
- `transaction_date` (DATE, NOT NULL)
- `description` (VARCHAR(500)) - Transaction description
- `reference_number` (VARCHAR(100)) - Invoice/Receipt number
- `running_balance` (DECIMAL(19,2)) - Calculated running balance
- `user_id` (BIGINT, FOREIGN KEY → users.id, NOT NULL)
- `created_at` (DATETIME)
- `updated_at` (DATETIME)

### Relationships
- One `User` can have many `Party`
- One `Party` can have many `LedgerEntry`
- All data is isolated per user (user-specific)

### Balance Calculation
```
Outstanding Balance = Opening Balance + Total Purchases - Total Payments
Running Balance = Opening Balance + Sum of all previous transactions (in chronological order)
```

---

## API Endpoints

### Authentication
All endpoints require JWT authentication. Include token in header:
```
Authorization: Bearer <your_jwt_token>
```

---

## Party Management APIs

### 1. Get All Parties
**GET** `/api/parties`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "Mohit Granite",
    "address": "123 Main St, City",
    "phone": "9876543210",
    "email": "mohit@granite.com",
    "gstNumber": "GST123456",
    "notes": "Regular supplier",
    "openingBalance": 0.00,
    "createdAt": "2026-01-08T10:00:00",
    "updatedAt": "2026-01-08T10:00:00"
  },
  {
    "id": 2,
    "name": "Sri Ram Marble",
    "address": "456 Park Ave",
    "phone": "9876543211",
    "email": null,
    "gstNumber": null,
    "notes": null,
    "openingBalance": 5000.00,
    "createdAt": "2026-01-08T11:00:00",
    "updatedAt": "2026-01-08T11:00:00"
  }
]
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/parties \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 2. Get Party by ID
**GET** `/api/parties/{id}`

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "Mohit Granite",
  "address": "123 Main St, City",
  "phone": "9876543210",
  "email": "mohit@granite.com",
  "gstNumber": "GST123456",
  "notes": "Regular supplier",
  "openingBalance": 0.00,
  "createdAt": "2026-01-08T10:00:00",
  "updatedAt": "2026-01-08T10:00:00"
}
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/parties/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 3. Search Parties
**GET** `/api/parties/search?q={searchTerm}`

**Response:** `200 OK` (List of parties matching search term)

**cURL:**
```bash
curl -X GET "http://localhost:8080/api/parties/search?q=Mohit" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 4. Create Party
**POST** `/api/parties`

**Request Body:**
```json
{
  "name": "Mohit Granite",
  "address": "123 Main St, City",
  "phone": "9876543210",
  "email": "mohit@granite.com",
  "gstNumber": "GST123456",
  "notes": "Regular supplier",
  "openingBalance": 0.00
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "name": "Mohit Granite",
  "address": "123 Main St, City",
  "phone": "9876543210",
  "email": "mohit@granite.com",
  "gstNumber": "GST123456",
  "notes": "Regular supplier",
  "openingBalance": 0.00,
  "createdAt": "2026-01-08T10:00:00",
  "updatedAt": "2026-01-08T10:00:00"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/parties \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "name": "Mohit Granite",
    "address": "123 Main St, City",
    "phone": "9876543210",
    "email": "mohit@granite.com",
    "gstNumber": "GST123456",
    "notes": "Regular supplier",
    "openingBalance": 0.00
  }'
```

**Validation:**
- `name` is required (1-200 characters)
- All other fields are optional

---

### 5. Update Party
**PUT** `/api/parties/{id}`

**Request Body:** (Same as Create, all fields optional except name)

**Response:** `200 OK` (Updated party object)

**cURL:**
```bash
curl -X PUT http://localhost:8080/api/parties/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "name": "Mohit Granite Updated",
    "phone": "9876543219"
  }'
```

**Note:** Opening balance cannot be updated directly. Use an ADJUSTMENT ledger entry instead.

---

### 6. Delete Party
**DELETE** `/api/parties/{id}`

**Response:** `204 No Content`

**cURL:**
```bash
curl -X DELETE http://localhost:8080/api/parties/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Note:** Deleting a party will also delete all associated ledger entries (if cascade is configured).

---

## Ledger Entry APIs

### 7. Create Ledger Entry
**POST** `/api/ledger/entries`

**Request Body:**
```json
{
  "party": {
    "id": 1
  },
  "transactionType": "PURCHASE",
  "amount": 50000.00,
  "transactionDate": "2026-01-08",
  "description": "Purchase of granite slabs",
  "referenceNumber": "INV-001"
}
```

**Transaction Types:**
- `PURCHASE` - Credit entry (money to be paid, increases outstanding)
- `PAYMENT` - Debit entry (installment paid, decreases outstanding)
- `ADJUSTMENT` - Adjustment entry (can be positive or negative)

**Response:** `201 Created`
```json
{
  "id": 1,
  "partyId": 1,
  "partyName": "Mohit Granite",
  "transactionType": "PURCHASE",
  "amount": 50000.00,
  "transactionDate": "2026-01-08",
  "description": "Purchase of granite slabs",
  "referenceNumber": "INV-001",
  "runningBalance": 50000.00,
  "createdAt": "2026-01-08T10:00:00",
  "updatedAt": "2026-01-08T10:00:00"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/ledger/entries \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "party": {"id": 1},
    "transactionType": "PURCHASE",
    "amount": 50000.00,
    "transactionDate": "2026-01-08",
    "description": "Purchase of granite slabs",
    "referenceNumber": "INV-001"
  }'
```

**Validation:**
- `party.id` is required
- `transactionType` is required (PURCHASE, PAYMENT, or ADJUSTMENT)
- `amount` is required and must be positive
- `transactionDate` is required (defaults to today if not provided)

**Note:** Running balance is automatically calculated and updated for all entries of the party.

---

### 8. Get Ledger Entry by ID
**GET** `/api/ledger/entries/{id}`

**Response:** `200 OK` (LedgerEntryDto object)

**cURL:**
```bash
curl -X GET http://localhost:8080/api/ledger/entries/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 9. Update Ledger Entry
**PUT** `/api/ledger/entries/{id}`

**Request Body:** (Same as Create, all fields optional)

**Response:** `200 OK` (Updated LedgerEntryDto)

**cURL:**
```bash
curl -X PUT http://localhost:8080/api/ledger/entries/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "amount": 55000.00,
    "description": "Updated purchase amount"
  }'
```

**Note:** Updating an entry will recalculate running balances for all entries of that party.

---

### 10. Delete Ledger Entry
**DELETE** `/api/ledger/entries/{id}`

**Response:** `204 No Content`

**cURL:**
```bash
curl -X DELETE http://localhost:8080/api/ledger/entries/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Note:** Deleting an entry will recalculate running balances for all remaining entries of that party.

---

### 11. Get All Ledger Entries for a Party
**GET** `/api/ledger/parties/{partyId}/entries`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "partyId": 1,
    "partyName": "Mohit Granite",
    "transactionType": "PURCHASE",
    "amount": 50000.00,
    "transactionDate": "2026-01-08",
    "description": "Purchase of granite slabs",
    "referenceNumber": "INV-001",
    "runningBalance": 50000.00,
    "createdAt": "2026-01-08T10:00:00",
    "updatedAt": "2026-01-08T10:00:00"
  },
  {
    "id": 2,
    "partyId": 1,
    "partyName": "Mohit Granite",
    "transactionType": "PAYMENT",
    "amount": 10000.00,
    "transactionDate": "2026-01-09",
    "description": "First installment payment",
    "referenceNumber": "PAY-001",
    "runningBalance": 40000.00,
    "createdAt": "2026-01-09T10:00:00",
    "updatedAt": "2026-01-09T10:00:00"
  }
]
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/ledger/parties/1/entries \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Note:** Entries are returned in chronological order (by transaction date, then by ID).

---

### 12. Get Ledger Entries by Date Range
**GET** `/api/ledger/parties/{partyId}/entries/date-range?startDate={date}&endDate={date}`

**Response:** `200 OK` (List of LedgerEntryDto)

**cURL:**
```bash
curl -X GET "http://localhost:8080/api/ledger/parties/1/entries/date-range?startDate=2026-01-01&endDate=2026-01-31" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 13. Get Party Ledger Summary
**GET** `/api/ledger/parties/{partyId}/summary`

**Response:** `200 OK`
```json
{
  "partyId": 1,
  "partyName": "Mohit Granite",
  "openingBalance": 0.00,
  "totalPurchases": 50000.00,
  "totalPayments": 10000.00,
  "outstandingBalance": 40000.00,
  "transactions": [
    {
      "id": 1,
      "partyId": 1,
      "partyName": "Mohit Granite",
      "transactionType": "PURCHASE",
      "amount": 50000.00,
      "transactionDate": "2026-01-08",
      "description": "Purchase of granite slabs",
      "referenceNumber": "INV-001",
      "runningBalance": 50000.00,
      "createdAt": "2026-01-08T10:00:00",
      "updatedAt": "2026-01-08T10:00:00"
    },
    {
      "id": 2,
      "partyId": 1,
      "partyName": "Mohit Granite",
      "transactionType": "PAYMENT",
      "amount": 10000.00,
      "transactionDate": "2026-01-09",
      "description": "First installment payment",
      "referenceNumber": "PAY-001",
      "runningBalance": 40000.00,
      "createdAt": "2026-01-09T10:00:00",
      "updatedAt": "2026-01-09T10:00:00"
    }
  ]
}
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/ledger/parties/1/summary \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Fields:**
- `openingBalance` - Opening balance of the party
- `totalPurchases` - Sum of all PURCHASE entries
- `totalPayments` - Sum of all PAYMENT entries
- `outstandingBalance` - Opening Balance + Total Purchases - Total Payments
- `transactions` - Complete transaction history in chronological order

---

### 14. Get Party Outstanding Balance
**GET** `/api/ledger/parties/{partyId}/outstanding`

**Response:** `200 OK`
```json
40000.00
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/ledger/parties/1/outstanding \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Complete Workflow Example

### Scenario: Track transactions for "Mohit Granite"

**Step 1: Create Party**
```bash
POST /api/parties
{
  "name": "Mohit Granite",
  "address": "123 Main St",
  "phone": "9876543210",
  "openingBalance": 0.00
}
```

**Step 2: Add Purchase (Credit)**
```bash
POST /api/ledger/entries
{
  "party": {"id": 1},
  "transactionType": "PURCHASE",
  "amount": 200000.00,
  "transactionDate": "2026-01-08",
  "description": "Purchase of granite slabs",
  "referenceNumber": "INV-001"
}
```
**Result:** Outstanding = 200,000.00

**Step 3: Add Payment (Debit)**
```bash
POST /api/ledger/entries
{
  "party": {"id": 1},
  "transactionType": "PAYMENT",
  "amount": 5000.00,
  "transactionDate": "2026-01-09",
  "description": "First installment",
  "referenceNumber": "PAY-001"
}
```
**Result:** Outstanding = 195,000.00

**Step 4: Add Another Payment**
```bash
POST /api/ledger/entries
{
  "party": {"id": 1},
  "transactionType": "PAYMENT",
  "amount": 50000.00,
  "transactionDate": "2026-01-10",
  "description": "Second installment",
  "referenceNumber": "PAY-002"
}
```
**Result:** Outstanding = 145,000.00

**Step 5: Get Summary**
```bash
GET /api/ledger/parties/1/summary
```
**Response:**
- Opening Balance: 0.00
- Total Purchases: 200,000.00
- Total Payments: 55,000.00
- **Outstanding Balance: 145,000.00**

---

## Error Responses

### 400 Bad Request
```json
{
  "message": "Validation error"
}
```

### 401 Unauthorized
```json
{
  "message": "Invalid or missing JWT token"
}
```

### 404 Not Found
```json
{
  "message": "Party not found or access denied"
}
```

---

## Key Features

1. **User Isolation**: All parties and ledger entries are isolated per user
2. **Automatic Balance Calculation**: Running balances are automatically recalculated when entries are added/updated/deleted
3. **Chronological Ordering**: Transactions are always returned in date order
4. **Transaction Types**: Support for Purchase (credit), Payment (debit), and Adjustment
5. **Complete History**: Full transaction history with running balance for each entry
6. **Outstanding Balance**: Real-time calculation of outstanding amount per party
7. **Date Range Filtering**: Filter transactions by date range
8. **Search**: Search parties by name

---

## Integration Notes

- All endpoints follow existing authentication pattern (JWT)
- All data is user-specific (no cross-user data access)
- Follows existing coding standards and folder structure
- Uses existing SecurityUtil for user context
- Compatible with existing database configuration
- Tables will be created automatically on first startup (Hibernate DDL auto-update)

---

## Database Migration

The tables will be created automatically when the application starts (due to `spring.jpa.hibernate.ddl-auto=update`). No manual migration needed.

If you need to create tables manually, use the SQL schema provided in the "Database Schema" section above.

