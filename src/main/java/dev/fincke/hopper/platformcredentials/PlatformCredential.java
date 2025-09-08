package dev.fincke.hopper.platformcredentials;

import dev.fincke.hopper.platforms.Platform;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
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
public class PlatformCredential 
{
    // * Attributes

    // UUID for platform credential
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // platform this credential belongs to
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    // key/name of the credential (e.g., "api_key", "secret", "token")
    @NotBlank
    @Column(name = "credential_key", nullable = false)
    private String credentialKey = "";

    // encrypted/encoded value of the credential
    @NotBlank
    @Column(name = "credential_value", nullable = false)
    private String credentialValue = "";

    // whether this credential is currently active
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

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
        this.isActive = isActive;
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
        this.credentialValue = credentialValue;
    }

    public Boolean getIsActive() 
    {
        return isActive;
    }

    public void setIsActive(Boolean isActive) 
    {
        this.isActive = isActive;
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