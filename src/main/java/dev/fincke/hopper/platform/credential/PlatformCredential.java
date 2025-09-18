package dev.fincke.hopper.platform.credential;

import dev.fincke.hopper.platform.platform.Platform;
import dev.fincke.hopper.security.encryption.EncryptedCredential;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "platform_credentials",
    uniqueConstraints = {@UniqueConstraint(name = "uq_platform_credentials_key", columnNames = {"platform_id", "credential_key"})},
    indexes = {
        @Index(name = "idx_platform_credentials_platform", columnList = "platform_id"),
        @Index(name = "idx_platform_credentials_active", columnList = "is_active")
    }
)
@EntityListeners(PlatformCredentialEncryptionListener.class)
public class PlatformCredential 
{
    // * Attributes

    // Use UUID so credentials stay unique across distributed systems
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // Owning platform relationship tells us which integration consumes the secret
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    // Differentiates multiple secrets per platform (e.g., "api_key", "secret", "token")
    @NotBlank
    @Column(name = "credential_key", nullable = false)
    private String credentialKey = "";

    // Secret value persisted as ciphertext once the listener seals it
    @NotBlank
    @Column(name = "credential_value", nullable = false)
    private String credentialValue = "";

    // Toggle to disable credentials without deleting their history
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // track which encryption scheme produced the stored ciphertext
    @Column(name = "encryption_version")
    private String encryptionVersion;

    // persist salt so key derivation can be repeated during decryption
    @Column(name = "salt")
    private String salt;

    // drive re-encryption policies by knowing when encryption last ran
    @Column(name = "encrypted_at")
    private LocalDateTime encryptedAt;

    // allow auditing which derived key protected this credential
    @Column(name = "key_id")
    private String keyId;

    // * Constructors

    protected PlatformCredential() {}
    
    public PlatformCredential(Platform platform, String credentialKey, String credentialValue) 
    {
        this.platform = platform;
        this.credentialKey = credentialKey;
        this.credentialValue = credentialValue;
        this.isActive = true;
    }

    public PlatformCredential(Platform platform, String credentialKey, String credentialValue, Boolean isActive) 
    {
        this.platform = platform;
        this.credentialKey = credentialKey;
        this.credentialValue = credentialValue;
        this.isActive = isActive == null || isActive;
    }

    // * Getters and Setters

    public UUID getId() 
    {
        return id;
    }

    public void setId(UUID id) 
    {
        this.id = id;
    }

    public Platform getPlatform() 
    {
        return platform;
    }

    public void setPlatform(Platform platform) 
    {
        this.platform = platform;
    }

    public String getCredentialKey() 
    {
        return credentialKey;
    }

    public void setCredentialKey(String credentialKey) 
    {
        this.credentialKey = credentialKey;
    }

    public String getCredentialValue() 
    {
        return credentialValue;
    }

    public void setCredentialValue(String credentialValue) 
    {
        if (credentialValue == null)
        {
            this.credentialValue = null;
            clearEncryptionMetadata();
            return;
        }

        if (!credentialValue.equals(this.credentialValue))
        {
            this.credentialValue = credentialValue;
            // forcing metadata reset ensures listener re-encrypts the new secret
            clearEncryptionMetadata();
        }
    }

    public boolean isActive()
    {
        return isActive;
    }

    public boolean getIsActive()
    {
        return isActive;
    }

    public void setActive(boolean active)
    {
        this.isActive = active;
    }

    public void setIsActive(boolean isActive)
    {
        this.isActive = isActive;
    }

    public String getEncryptionVersion()
    {
        return encryptionVersion;
    }

    public void setEncryptionVersion(String encryptionVersion)
    {
        this.encryptionVersion = encryptionVersion;
    }

    public String getSalt()
    {
        return salt;
    }

    public void setSalt(String salt)
    {
        this.salt = salt;
    }

    public LocalDateTime getEncryptedAt()
    {
        return encryptedAt;
    }

    public void setEncryptedAt(LocalDateTime encryptedAt)
    {
        this.encryptedAt = encryptedAt;
    }

    public String getKeyId()
    {
        return keyId;
    }

    public void setKeyId(String keyId)
    {
        this.keyId = keyId;
    }

    // * Encryption Helper Methods

    // expose encryption metadata to the service when loading from JPA
    public EncryptedCredential toEncryptedCredential()
    {
        if (encryptionVersion == null || salt == null || encryptedAt == null || keyId == null)
        {
            return null;
        }
        return EncryptedCredential.fromDatabase(credentialValue, salt, encryptionVersion, encryptedAt, keyId);
    }

    // copy freshly generated encryption metadata onto the entity after sealing the value
    public void updateFromEncryptedCredential(EncryptedCredential encrypted)
    {
        this.credentialValue = encrypted.encryptedValue();
        this.salt = encrypted.salt();
        this.encryptionVersion = encrypted.encryptionVersion();
        this.encryptedAt = encrypted.encryptedAt();
        this.keyId = encrypted.keyId();
    }

    // detect ciphertext vs plaintext by verifying required metadata is present
    public boolean isEncrypted()
    {
        return encryptionVersion != null && salt != null && keyId != null;
    }

    // remove stale metadata so plaintext credentials are not treated as encrypted
    private void clearEncryptionMetadata()
    {
        this.encryptionVersion = null;
        this.salt = null;
        this.encryptedAt = null;
        this.keyId = null;
    }

    // * Overrides

    @Override
    public boolean equals(Object o) 
    {
        if (this == o) return true;
        if (!(o instanceof PlatformCredential other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() 
    {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() 
    {
        return "PlatformCredential{" +
                "id=" + id +
                ", platform=" + (platform != null ? platform.getName() : null) +
                ", credentialKey='" + credentialKey + '\'' +
                ", credentialValue='***REDACTED***'" +
                ", isActive=" + isActive +
                '}';
    }
}
