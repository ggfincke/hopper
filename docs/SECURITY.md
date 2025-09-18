# Security & Secrets

## Authentication & Authorization

### JWT Authentication Implementation
- **Token Types**: Access tokens (15 min) and refresh tokens (7 days, 30 days with remember-me)
- **Token Storage**: Bearer tokens for access, secure HTTP-only cookies for refresh
- **Security Integration**: Spring Security filters with custom authentication providers
- **Token Validation**: Real-time user account status validation (enabled, locked)
- **Endpoints**: Login, logout, refresh, validate, and user profile management

### Role-Based Access Control (RBAC)
- **System Roles**: ADMIN, USER, API_CLIENT with hierarchical permissions
- **Method Security**: `@PreAuthorize` annotations for fine-grained endpoint control
- **Role Assignment**: Dynamic role management through user administration
- **Access Patterns**: Role-specific endpoint access and data visibility

### Account Management
- **User Registration**: Username, email, and password with validation
- **Account Security**: BCrypt password encoding with salt
- **Failed Login Tracking**: Automatic account locking after failed attempts
- **Account Status**: Enable/disable and lock/unlock functionality
- **Password Management**: Secure password change with current password validation

## Credential Encryption at Rest

### Overview
Platform credentials are now encrypted at rest using AES-256-GCM encryption with the following features:
- **Algorithm**: AES-256-GCM (provides both confidentiality and authenticity)
- **Key Management**: PBKDF2-based key derivation with per-credential salts
- **Automatic Encryption**: Credentials are encrypted before database storage via JPA entity listeners
- **Audit Logging**: All encryption/decryption operations are logged for compliance

### Encryption Architecture
- **Master Key**: Derived from `CREDENTIAL_MASTER_KEY` environment variable (32+ characters required)
- **Salt Generation**: Cryptographically secure random salt per credential (32 bytes)
- **Key Derivation**: PBKDF2WithHmacSHA256 with 100,000 iterations
- **IV Generation**: Random 96-bit IV per encryption operation
- **Authentication**: GCM mode provides built-in authentication with 128-bit tag

### Database Schema
The `platform_credentials` table includes encryption metadata:
- `encryption_version`: Algorithm version for rotation support (e.g., "AES-GCM-256-V1")
- `salt`: Base64-encoded salt for key derivation
- `encrypted_at`: Timestamp of encryption for age tracking
- `key_id`: Unique identifier for key rotation tracking

### Configuration
Encryption settings are configured in `application.properties`:
```properties
app.encryption.encryption-version=AES-GCM-256-V1
app.encryption.key-derivation-iterations=100000
app.encryption.salt-length=32
app.encryption.gcm-tag-length=128
app.encryption.key-rotation-days=90
app.encryption.audit-logging=true
app.encryption.max-credential-age=365
```

### Environment Setup
**Development**: Master key configured in `application-dev.properties` (for testing only)
**Production**: Set `CREDENTIAL_MASTER_KEY` environment variable with a secure 32+ character key

### Security Features
- **Automatic Encryption**: All credential values are encrypted before database storage
- **Decryption on Demand**: Credentials are only decrypted when explicitly requested
- **Response Security**: All API responses redact credential values (show "***REDACTED***")
- **Key Rotation**: Support for re-encrypting credentials with new keys/algorithms
- **Validation**: Built-in validation to ensure credentials can be decrypted
- **Audit Trail**: Comprehensive logging of all encryption operations

### API Operations
- `getDecryptedCredentialValue(id)`: Securely retrieve plaintext credential (audit logged)
- `validateCredentialEncryption(id)`: Verify credential can be decrypted
- `reEncryptCredential(id)`: Update credential with current encryption version
- `findCredentialsNeedingReEncryption()`: Identify old credentials for rotation

## Data Protection Best Practices
- Credentials never stored in plaintext in database
- Master key never logged or exposed in error messages
- Encryption keys derived uniquely per credential using secure salts
- Database access restricted by role; separate app and admin users
- All sensitive operations include comprehensive audit logging

## Operational Security
- **HTTPS Required**: Enable HTTPS/TLS termination proxy in production
- **Environment Variables**: All secrets externalized via environment variables
- **Key Rotation**: Regular rotation of encryption keys supported
- **Monitoring**: Encryption operation metrics and alerts
- **Backup Security**: Database backups contain encrypted credentials only

## Development Guidelines
- **Local Development**: Use provided development master key (never for production)
- **Testing**: Unit tests validate encryption/decryption functionality
- **Secret Management**: Never commit real API keys or secrets to repository
- **Code Review**: All credential-related code requires security review

## Compliance & Auditing
- All credential access operations are logged with context
- Encryption metadata tracked for compliance reporting
- Failed decryption attempts logged and monitored
- Key rotation events audited with timestamps
- Database schema includes encryption version tracking for algorithm evolution
