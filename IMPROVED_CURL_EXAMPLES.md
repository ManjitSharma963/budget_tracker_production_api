# Improved cURL Examples for Ledger Entries API

## Prerequisites
- Replace `YOUR_TOKEN_HERE` with your actual JWT token
- Ensure the server is running on `http://localhost:8080`
- Party with ID 1 must exist in the system

---

## 1. Create Purchase Entry (Credit Transaction)

**Purpose:** Record a purchase transaction that increases the outstanding balance.

```bash
curl -X POST http://localhost:8080/api/ledger/entries \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "party": {
      "id": 1
    },
    "transactionType": "PURCHASE",
    "amount": 30000.00,
    "transactionDate": "2026-01-15",
    "description": "Purchase of granite slabs"
  }'
```

**Alternative format using direct partyId:**
```bash
curl -X POST http://localhost:8080/api/ledger/entries \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "partyId": 1,
    "transactionType": "PURCHASE",
    "amount": 30000.00,
    "transactionDate": "2026-01-15",
    "description": "Purchase of granite slabs"
  }'
```

**Key Fields:**
- `party.id` or `partyId`: Required - ID of the party
- `transactionType`: Required - Must be "PURCHASE"
- `amount`: Required - Must be positive (e.g., 30000.00)
- `transactionDate`: Required - Format: YYYY-MM-DD
- `description`: Optional - Description of the transaction

---

## 2. Create Payment Entry (Debit Transaction)

**Purpose:** Record a payment that decreases the outstanding balance.

```bash
curl -X POST http://localhost:8080/api/ledger/entries \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "party": {
      "id": 1
    },
    "transactionType": "PAYMENT",
    "amount": 20000.00,
    "transactionDate": "2026-01-15",
    "description": "Payment made for granite purchase",
    "paymentMode": "Cash"
  }'
```

**Alternative format using direct partyId:**
```bash
curl -X POST http://localhost:8080/api/ledger/entries \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "partyId": 1,
    "transactionType": "PAYMENT",
    "amount": 20000.00,
    "transactionDate": "2026-01-15",
    "description": "Payment made for granite purchase",
    "paymentMode": "Cash"
  }'
```

**Key Fields:**
- `party.id` or `partyId`: Required - ID of the party
- `transactionType`: Required - Must be "PAYMENT"
- `amount`: Required - Must be positive (e.g., 20000.00)
- `transactionDate`: Required - Format: YYYY-MM-DD
- `description`: Optional - Description of the payment
- `paymentMode`: Optional - Payment method (e.g., "Cash", "Cheque", "Bank Transfer", "UPI", "Card")

---

## 3. Create Adjustment Entry

**Purpose:** Record an adjustment to correct balances or handle special cases.

```bash
curl -X POST http://localhost:8080/api/ledger/entries \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "party": {
      "id": 1
    },
    "transactionType": "ADJUSTMENT",
    "amount": 5000.00,
    "transactionDate": "2026-01-15",
    "description": "Discount adjustment"
  }'
```

---

## Improvements Made

1. **Enhanced descriptions**: More descriptive text for better record-keeping
2. **Alternative format support**: Both nested `party.id` and direct `partyId` formats are supported
3. **Better documentation**: Clear field descriptions and purposes
4. **Payment mode examples**: Common payment modes for better clarity
5. **Additional transaction type**: Included ADJUSTMENT example for completeness

---

## Response Example

**Success Response (201 Created):**
```json
{
  "id": 1,
  "partyId": 1,
  "partyName": "Mohit Granite",
  "transactionType": "PURCHASE",
  "amount": 30000.00,
  "transactionDate": "2026-01-15",
  "description": "Purchase of granite slabs",
  "paymentMode": null,
  "runningBalance": 30000.00,
  "createdAt": "2026-01-15T10:30:00",
  "updatedAt": "2026-01-15T10:30:00"
}
```

**Error Response (400 Bad Request):**
```json
{
  "message": "Party not found or access denied"
}
```

---

## Validation Rules

- `party.id` or `partyId`: Required, must exist and belong to the authenticated user
- `transactionType`: Required, must be one of: "PURCHASE", "PAYMENT", "ADJUSTMENT"
- `amount`: Required, must be a positive number
- `transactionDate`: Required, format: YYYY-MM-DD (defaults to today if not provided)
- `description`: Optional, max 500 characters
- `paymentMode`: Optional, max 50 characters

---

## Testing Tips

1. **Get your JWT token** by logging in first:
   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username": "your_username", "password": "your_password"}'
   ```

2. **Verify party exists** before creating entries:
   ```bash
   curl -X GET http://localhost:8080/api/parties/1 \
     -H "Authorization: Bearer YOUR_TOKEN_HERE"
   ```

3. **Check running balance** after creating entries:
   ```bash
   curl -X GET http://localhost:8080/api/ledger/parties/1/outstanding \
     -H "Authorization: Bearer YOUR_TOKEN_HERE"
   ```

