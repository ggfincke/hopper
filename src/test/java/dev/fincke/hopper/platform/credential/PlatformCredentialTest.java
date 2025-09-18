package dev.fincke.hopper.platform.credential;

import dev.fincke.hopper.platform.platform.Platform;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PlatformCredentialTest
{
    @Test
    void setCredentialValueShouldClearEncryptionMetadataWhenValueChanges()
    {
        Platform platform = new Platform("Test Platform", "TEST");
        PlatformCredential credential = new PlatformCredential(platform, "api_key", "old-value");

        credential.setEncryptionVersion("version-1");
        credential.setSalt("salt-value");
        credential.setEncryptedAt(LocalDateTime.now());
        credential.setKeyId("key-id-1");
        assertTrue(credential.isEncrypted());

        credential.setCredentialValue("new-value");

        assertEquals("new-value", credential.getCredentialValue());
        assertFalse(credential.isEncrypted());
        assertNull(credential.getEncryptionVersion());
        assertNull(credential.getSalt());
        assertNull(credential.getEncryptedAt());
        assertNull(credential.getKeyId());
    }

    @Test
    void setCredentialValueShouldPreserveEncryptionMetadataWhenValueUnchanged()
    {
        Platform platform = new Platform("Test Platform", "TEST");
        PlatformCredential credential = new PlatformCredential(platform, "api_key", "same-value");

        credential.setEncryptionVersion("version-1");
        credential.setSalt("salt-value");
        credential.setEncryptedAt(LocalDateTime.now());
        credential.setKeyId("key-id-1");

        credential.setCredentialValue("same-value");

        assertTrue(credential.isEncrypted());
        assertEquals("version-1", credential.getEncryptionVersion());
        assertEquals("salt-value", credential.getSalt());
    }
}
