# JWT Authentication Guide

This document explains how to use the JWT authentication system in the Hopper API and provides secure token storage recommendations.

## Authentication Endpoints

### Login
**POST `/api/auth/login`**

Authenticate with username/email and password to receive JWT tokens.

**Request Body:**
```json
{
  "usernameOrEmail": "user@example.com",
  "password": "password123",
  "rememberMe": false
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "refreshExpiresIn": 604800,
  "user": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "username": "johndoe",
    "email": "user@example.com",
    "roles": ["USER"],
    "enabled": true,
    "accountLocked": false,
    "lastLogin": "2023-12-01T10:30:00"
  }
}
```

### Refresh Token
**POST `/api/auth/refresh`**

Get a new access token using a refresh token.

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Logout
**POST `/api/auth/logout`**

Logout and clear tokens (client-side operation).

### Current User
**GET `/api/auth/me`**

Get current authenticated user information.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token Validation
**POST `/api/auth/validate`**

Validate a JWT token and get user information.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Using JWT Tokens

### In HTTP Headers
Include the access token in the Authorization header:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token Expiration
- **Access Token**: 15 minutes (standard), 30 days (remember me)
- **Refresh Token**: 7 days
- **Remember Me Token**: 30 days

## Secure Token Storage Recommendations

### Web Applications (JavaScript/React/Vue/Angular)

#### ✅ Recommended: Memory + HTTP-Only Cookies
```javascript
// Store access token in memory (not localStorage!)
class TokenManager {
  constructor() {
    this.accessToken = null;
  }

  setAccessToken(token) {
    this.accessToken = token;
  }

  getAccessToken() {
    return this.accessToken;
  }

  clearTokens() {
    this.accessToken = null;
    // Refresh token is cleared via logout API call
  }
}

// Use with HTTP-only cookies for refresh tokens
fetch('/api/auth/login', {
  method: 'POST',
  credentials: 'include', // Include HTTP-only cookies
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({ username, password })
});
```

#### ❌ Never Do This
```javascript
// DON'T store tokens in localStorage or sessionStorage
localStorage.setItem('token', accessToken); // Vulnerable to XSS
sessionStorage.setItem('token', accessToken); // Vulnerable to XSS
```

### Mobile Applications (iOS/Android)

#### ✅ iOS: Keychain Services
```swift
import Security

class TokenStorage {
    func saveToken(_ token: String, key: String) {
        let data = token.data(using: .utf8)!
        
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: key,
            kSecValueData as String: data,
            kSecAttrAccessible as String: kSecAttrAccessibleWhenUnlockedThisDeviceOnly
        ]
        
        SecItemDelete(query as CFDictionary)
        SecItemAdd(query as CFDictionary, nil)
    }
    
    func loadToken(key: String) -> String? {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: key,
            kSecReturnData as String: kCFBooleanTrue!,
            kSecMatchLimit as String: kSecMatchLimitOne
        ]
        
        var dataTypeRef: AnyObject?
        let status = SecItemCopyMatching(query as CFDictionary, &dataTypeRef)
        
        if status == errSecSuccess {
            if let data = dataTypeRef as? Data {
                return String(data: data, encoding: .utf8)
            }
        }
        return nil
    }
}
```

#### ✅ Android: Encrypted SharedPreferences
```kotlin
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class TokenStorage(context: Context) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    
    private val sharedPreferences = EncryptedSharedPreferences.create(
        "secure_tokens",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    fun saveToken(key: String, token: String) {
        sharedPreferences.edit().putString(key, token).apply()
    }
    
    fun getToken(key: String): String? {
        return sharedPreferences.getString(key, null)
    }
}
```

### Server-to-Server (API Clients)

#### ✅ Environment Variables
```bash
# Set environment variables
export JWT_ACCESS_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
export JWT_REFRESH_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

```java
// Java example
String accessToken = System.getenv("JWT_ACCESS_TOKEN");
```

#### ✅ Secure Configuration Management
- **Kubernetes**: Use Secrets
- **Docker**: Use Docker Secrets
- **AWS**: Use AWS Secrets Manager
- **Azure**: Use Azure Key Vault
- **GCP**: Use Google Secret Manager

## Security Best Practices

### 1. Token Rotation
Always generate new refresh tokens when refreshing access tokens:

```javascript
async function refreshAccessToken() {
  const response = await fetch('/api/auth/refresh', {
    method: 'POST',
    credentials: 'include'
  });
  
  if (response.ok) {
    const { accessToken, refreshToken } = await response.json();
    tokenManager.setAccessToken(accessToken);
    // New refresh token is set in HTTP-only cookie automatically
    return accessToken;
  }
  
  // Refresh failed, redirect to login
  window.location.href = '/login';
}
```

### 2. CSRF Protection
When using cookies, implement CSRF protection:

```javascript
// Include CSRF token in requests when using cookies
fetch('/api/protected', {
  method: 'POST',
  credentials: 'include',
  headers: {
    'X-CSRF-Token': getCsrfToken(),
    'Content-Type': 'application/json'
  }
});
```

### 3. Automatic Token Refresh
Implement automatic token refresh before expiration:

```javascript
class AuthInterceptor {
  async handleRequest(request) {
    const token = tokenManager.getAccessToken();
    
    if (this.isTokenNearExpiry(token)) {
      await this.refreshAccessToken();
    }
    
    request.headers.Authorization = `Bearer ${tokenManager.getAccessToken()}`;
    return request;
  }
  
  isTokenNearExpiry(token) {
    if (!token) return true;
    
    const payload = JSON.parse(atob(token.split('.')[1]));
    const expiryTime = payload.exp * 1000;
    const currentTime = Date.now();
    
    // Refresh if less than 5 minutes remaining
    return (expiryTime - currentTime) < 5 * 60 * 1000;
  }
}
```

### 4. Secure Production Configuration

#### Environment Variables
```bash
# Production environment variables
export JWT_SECRET="your-super-secure-256-bit-secret-key-here-32-chars-minimum"
export JWT_ISSUER="your-app-name"
export JWT_AUDIENCE="your-app-users"
```

#### Application Properties
```properties
# application-prod.properties
app.jwt.secret=${JWT_SECRET}
app.jwt.access-token-expiration=15m
app.jwt.refresh-token-expiration=7d
app.jwt.remember-me-expiration=30d
```

### 5. Cookie Security Settings
For HTTP-only refresh token cookies:

```java
// In AuthController
Cookie cookie = new Cookie("refresh-token", refreshToken);
cookie.setHttpOnly(true);
cookie.setSecure(true);        // HTTPS only
cookie.setPath("/");
cookie.setMaxAge(7 * 24 * 3600); // 7 days
cookie.setAttribute("SameSite", "Strict");
```

### 6. Content Security Policy
Add CSP headers to prevent XSS:

```http
Content-Security-Policy: default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'
```

## Common Security Pitfalls to Avoid

### ❌ Never Store Tokens in Local Storage
```javascript
// WRONG - Vulnerable to XSS attacks
localStorage.setItem('accessToken', token);
```

### ❌ Never Include Tokens in URLs
```javascript
// WRONG - Tokens visible in browser history/logs
window.location.href = `/dashboard?token=${accessToken}`;
```

### ❌ Never Log Tokens
```javascript
// WRONG - Tokens in application logs
console.log('User token:', accessToken);
```

### ❌ Never Send Tokens Over HTTP
Ensure all token exchanges happen over HTTPS in production.

### ❌ Never Use Weak Secrets
```properties
# WRONG - Weak secret
app.jwt.secret=secret123
```

## Troubleshooting

### 401 Unauthorized
- Check if token is expired
- Verify token format in Authorization header
- Ensure user account is enabled and not locked

### 403 Forbidden
- Check user roles and permissions
- Verify endpoint access requirements

### Invalid Token Errors
- Validate token signature
- Check token expiration
- Ensure secret key consistency

## Testing Authentication

### Using cURL
```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"test@example.com","password":"password123"}'

# Use token
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Using HTTPie
```bash
# Login
http POST localhost:8080/api/auth/login usernameOrEmail=test@example.com password=password123

# Use token
http GET localhost:8080/api/auth/me Authorization:"Bearer YOUR_TOKEN_HERE"
```

This JWT authentication system provides robust security while maintaining ease of use for client applications.