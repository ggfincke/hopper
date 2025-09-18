# Hopper API Documentation

Comprehensive reference for all implemented endpoints. All responses are JSON unless noted. Base URL defaults to `http://localhost:8080`.

## Authentication

All API endpoints except `/healthz` and `/api/auth/login` require authentication via JWT Bearer token.

**Header**: `Authorization: Bearer <access_token>`

### Authentication Endpoints

#### POST `/api/auth/login`
Authenticate user and receive JWT tokens.

**Request Body:**
```json
{
  "username": "admin",
  "password": "password123",
  "rememberMe": false
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "user": {
    "id": "uuid",
    "username": "admin",
    "email": "admin@example.com",
    "enabled": true,
    "accountNonLocked": true,
    "roles": ["ADMIN"]
  }
}
```

**Note**: Refresh token is set as HTTP-only cookie

#### POST `/api/auth/refresh`
Refresh access token using refresh token cookie.

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

#### POST `/api/auth/logout`
Invalidate tokens and clear cookies.

**Response (200 OK):**
```json
{
  "message": "Logout successful"
}
```

#### GET `/api/auth/me`
Get current authenticated user information.

**Response (200 OK):**
```json
{
  "id": "uuid",
  "username": "admin",
  "email": "admin@example.com",
  "enabled": true,
  "accountNonLocked": true,
  "roles": ["ADMIN"]
}
```

#### POST `/api/auth/validate`
Validate current access token.

**Response (200 OK):**
```json
{
  "valid": true,
  "username": "admin",
  "roles": ["ADMIN"],
  "expiresAt": "2025-09-18T10:30:00Z"
}
```

## User Management

**Required Role**: ADMIN (except password change)

#### GET `/api/users`
List all users.

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "username": "admin",
    "email": "admin@example.com",
    "enabled": true,
    "accountNonLocked": true,
    "failedLoginAttempts": 0,
    "roles": ["ADMIN"]
  }
]
```

#### GET `/api/users/{id}`
Get user by ID.

#### POST `/api/users`
Create new user.

**Request Body:**
```json
{
  "username": "newuser",
  "email": "user@example.com",
  "password": "securePassword123",
  "enabled": true,
  "roles": ["USER"]
}
```

#### PUT `/api/users/{id}`
Update user details.

#### DELETE `/api/users/{id}`
Delete user.

#### PUT `/api/users/{id}/password`
Change user password (accessible by user themselves or ADMIN).

**Request Body:**
```json
{
  "currentPassword": "oldPassword123",
  "newPassword": "newPassword456"
}
```

#### PUT `/api/users/{id}/enable`
Enable/disable user account.

**Request Body:**
```json
{
  "enabled": true
}
```

#### PUT `/api/users/{id}/unlock`
Unlock user account.

## Product Management

#### GET `/api/products`
List all products.

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "sku": "PROD-001",
    "name": "Sample Product",
    "description": "Product description",
    "price": 29.99,
    "quantity": 100
  }
]
```

#### GET `/api/products/{id}`
Get product by ID.

#### POST `/api/products`
Create new product.

**Request Body:**
```json
{
  "sku": "PROD-002",
  "name": "New Product",
  "description": "Product description",
  "price": 39.99,
  "quantity": 50
}
```

#### PUT `/api/products/{id}`
Update product.

#### DELETE `/api/products/{id}`
Delete product.

## Platform Management

#### GET `/api/platforms`
List all platforms.

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "name": "eBay",
    "platformType": "MARKETPLACE"
  }
]
```

#### GET `/api/platforms/{id}`
Get platform by ID.

#### POST `/api/platforms`
Create new platform.

**Request Body:**
```json
{
  "name": "Amazon",
  "platformType": "MARKETPLACE"
}
```

#### PUT `/api/platforms/{id}`
Update platform.

#### DELETE `/api/platforms/{id}`
Delete platform.

## Platform Credentials

**Security Note**: All credential values are encrypted at rest and redacted in responses.

#### GET `/api/platform-credentials`
List platform credentials (values redacted).

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "platformId": "uuid",
    "credentialKey": "api_key",
    "credentialValue": "***REDACTED***",
    "active": true,
    "encryptionVersion": "AES-GCM-256-V1",
    "encryptedAt": "2025-09-18T10:00:00Z"
  }
]
```

#### POST `/api/platform-credentials`
Create encrypted credential.

**Request Body:**
```json
{
  "platformId": "uuid",
  "credentialKey": "api_secret",
  "credentialValue": "actual_secret_value",
  "active": true
}
```

#### GET `/api/platform-credentials/{id}/decrypt`
**⚠️ SENSITIVE**: Get decrypted credential value (audit logged).

**Required Role**: ADMIN

**Response (200 OK):**
```json
{
  "credentialValue": "actual_secret_value"
}
```

## Platform Fees

#### GET `/api/platform-fees`
List platform fees.

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "platformId": "uuid",
    "feeType": "COMMISSION",
    "feeAmount": 2.50,
    "orderId": "uuid"
  }
]
```

#### POST `/api/platform-fees`
Create platform fee.

**Request Body:**
```json
{
  "platformId": "uuid",
  "feeType": "COMMISSION",
  "feeAmount": 5.00,
  "orderId": "uuid"
}
```

## Listings Management

#### GET `/api/listings`
List all listings.

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "productId": "uuid",
    "platformId": "uuid",
    "externalListingId": "eBay123456",
    "status": "ACTIVE",
    "price": 29.99,
    "quantityListed": 10
  }
]
```

#### POST `/api/listings`
Create new listing.

**Request Body:**
```json
{
  "productId": "uuid",
  "platformId": "uuid",
  "externalListingId": "eBay789012",
  "status": "ACTIVE",
  "price": 34.99,
  "quantityListed": 5
}
```

## Order Management

#### GET `/api/orders`
List all orders.

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "platformId": "uuid",
    "buyerId": "uuid",
    "externalOrderId": "eBay-ORD-123",
    "status": "PENDING",
    "orderDate": "2025-09-18T09:00:00Z"
  }
]
```

#### POST `/api/orders`
Create new order.

**Request Body:**
```json
{
  "platformId": "uuid",
  "buyerId": "uuid",
  "externalOrderId": "eBay-ORD-456",
  "status": "PENDING"
}
```

#### GET `/api/orders/{id}/items`
Get order items for specific order.

#### POST `/api/orders/{id}/items`
Add item to order.

**Request Body:**
```json
{
  "listingId": "uuid",
  "quantity": 2,
  "unitPrice": 29.99
}
```

## Buyer Management

#### GET `/api/buyers`
List all buyers.

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "name": "John Doe",
    "email": "john.doe@example.com"
  }
]
```

#### POST `/api/buyers`
Create new buyer.

**Request Body:**
```json
{
  "name": "Jane Smith",
  "email": "jane.smith@example.com"
}
```

## Order Addresses

#### GET `/api/order-addresses`
List order addresses.

#### POST `/api/order-addresses`
Create order address.

**Request Body:**
```json
{
  "orderId": "uuid",
  "addressType": "SHIPPING",
  "street": "123 Main St",
  "city": "Springfield",
  "state": "IL",
  "zipCode": "62701",
  "country": "US"
}
```

## System Health

#### GET `/healthz`
**Public endpoint** - Application health check.

**Response (200 OK):**
```json
{
  "status": "UP",
  "service": "hopper",
  "version": "1.0.0"
}
```

## Error Responses

### Authentication Errors

**401 Unauthorized:**
```json
{
  "timestamp": "2025-09-18T10:00:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid credentials",
  "path": "/api/auth/login"
}
```

**403 Forbidden:**
```json
{
  "timestamp": "2025-09-18T10:00:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Insufficient privileges",
  "path": "/api/users"
}
```

### Validation Errors

**400 Bad Request:**
```json
{
  "timestamp": "2025-09-18T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "validationErrors": {
    "username": "Username is required",
    "email": "Invalid email format"
  },
  "path": "/api/users"
}
```

### Resource Errors

**404 Not Found:**
```json
{
  "timestamp": "2025-09-18T10:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: uuid",
  "path": "/api/users/uuid"
}
```

## Data Types

- **IDs**: UUID strings (e.g., "123e4567-e89b-12d3-a456-426614174000")
- **Money fields**: Decimal numbers (e.g., 29.99)
- **Timestamps**: ISO 8601 format (e.g., "2025-09-18T10:00:00Z")
- **Enum values**: String constants (e.g., "ACTIVE", "PENDING", "ADMIN")

## Role-Based Access

| Endpoint | ADMIN | USER | API_CLIENT |
|----------|-------|------|------------|
| `/api/auth/*` | ✅ | ✅ | ✅ |
| `/api/users` | ✅ | ❌ | ❌ |
| `/api/users/{id}/password` | ✅ | ✅ (own) | ❌ |
| `/api/products` | ✅ | ✅ | ✅ |
| `/api/platforms` | ✅ | ✅ | ✅ |
| `/api/platform-credentials` | ✅ | ❌ | ✅ |
| `/api/platform-credentials/{id}/decrypt` | ✅ | ❌ | ❌ |
| `/api/orders` | ✅ | ✅ | ✅ |
| `/api/listings` | ✅ | ✅ | ✅ |

## Security Headers

All authenticated requests require:
- `Authorization: Bearer <token>`
- `Content-Type: application/json` (for POST/PUT requests)

## Rate Limiting

*Not yet implemented* - Future enhancement for API protection.