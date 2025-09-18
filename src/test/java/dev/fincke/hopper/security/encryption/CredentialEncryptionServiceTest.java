package dev.fincke.hopper.security.encryption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class CredentialEncryptionServiceTest 
{
    @Mock
    private EncryptionProperties encryptionProperties;
    
    private CredentialEncryptionServiceImpl encryptionService;
    private String testMasterKey = "testMasterKeyForEncryptionMinimum32Characters";
    private String testPlaintext = "supersecretapikey123";
    
    @BeforeEach
    void setUp() 
    {
        // Configure mock properties with test values using lenient to avoid unnecessary stubbing warnings
        lenient().when(encryptionProperties.getEffectiveMasterKey()).thenReturn(testMasterKey);
        lenient().when(encryptionProperties.getEncryptionVersion()).thenReturn("AES-GCM-256-V1");
        lenient().when(encryptionProperties.getKeyDerivationIterations()).thenReturn(10000); // Lower for tests
        lenient().when(encryptionProperties.getSaltLength()).thenReturn(32);
        lenient().when(encryptionProperties.getGcmTagLength()).thenReturn(128);
        lenient().when(encryptionProperties.getMaxCredentialAge()).thenReturn(365);
        lenient().when(encryptionProperties.isAuditLogging()).thenReturn(true);
        
        encryptionService = new CredentialEncryptionServiceImpl(encryptionProperties);
    }
    
    @Test
    void shouldEncryptAndDecryptSuccessfully() 
    {
        // Given a plaintext credential
        String plaintext = testPlaintext;
        
        // When encrypting and then decrypting
        EncryptedCredential encrypted = encryptionService.encrypt(plaintext);
        String decrypted = encryptionService.decrypt(encrypted);
        
        // Then the decrypted value should match the original
        assertEquals(plaintext, decrypted);
        assertNotNull(encrypted.encryptedValue());
        assertNotNull(encrypted.salt());
        assertNotNull(encrypted.keyId());
        assertNotNull(encrypted.encryptedAt());
        assertEquals("AES-GCM-256-V1", encrypted.encryptionVersion());
    }
    
    @Test
    void shouldProduceDifferentEncryptionForSamePlaintext() 
    {
        // Given the same plaintext encrypted twice
        EncryptedCredential encrypted1 = encryptionService.encrypt(testPlaintext);
        EncryptedCredential encrypted2 = encryptionService.encrypt(testPlaintext);
        
        // Then the encrypted values should be different (due to different salts and IVs)
        assertNotEquals(encrypted1.encryptedValue(), encrypted2.encryptedValue());
        assertNotEquals(encrypted1.salt(), encrypted2.salt());
        assertNotEquals(encrypted1.keyId(), encrypted2.keyId());
        
        // But both should decrypt to the same plaintext
        String decrypted1 = encryptionService.decrypt(encrypted1);
        String decrypted2 = encryptionService.decrypt(encrypted2);
        assertEquals(testPlaintext, decrypted1);
        assertEquals(testPlaintext, decrypted2);
    }
    
    @Test
    void shouldValidateEncryptionSuccessfully() 
    {
        // Given an encrypted credential
        EncryptedCredential encrypted = encryptionService.encrypt(testPlaintext);
        
        // When validating encryption
        boolean isValid = encryptionService.validateEncryption(encrypted);
        
        // Then validation should succeed
        assertTrue(isValid);
    }
    
    @Test
    void shouldFailValidationForCorruptedData() 
    {
        // Given an encrypted credential with corrupted data
        EncryptedCredential encrypted = encryptionService.encrypt(testPlaintext);
        EncryptedCredential corrupted = EncryptedCredential.fromDatabase(
            "corrupteddata", 
            encrypted.salt(), 
            encrypted.encryptionVersion(), 
            encrypted.encryptedAt(), 
            encrypted.keyId()
        );
        
        // When validating encryption
        boolean isValid = encryptionService.validateEncryption(corrupted);
        
        // Then validation should fail
        assertFalse(isValid);
    }
    
    @Test
    void shouldReEncryptWithCurrentVersion() 
    {
        // Given an encrypted credential
        EncryptedCredential original = encryptionService.encrypt(testPlaintext);
        
        // When re-encrypting
        EncryptedCredential reEncrypted = encryptionService.reEncrypt(original);
        
        // Then the re-encrypted credential should be different but decrypt to same value
        assertNotEquals(original.encryptedValue(), reEncrypted.encryptedValue());
        assertNotEquals(original.salt(), reEncrypted.salt());
        assertNotEquals(original.keyId(), reEncrypted.keyId());
        assertEquals(original.encryptionVersion(), reEncrypted.encryptionVersion());
        
        String decrypted = encryptionService.decrypt(reEncrypted);
        assertEquals(testPlaintext, decrypted);
    }
    
    @Test
    void shouldIdentifyCredentialsNeedingReEncryption() 
    {
        // Given an old encryption version
        when(encryptionProperties.getEncryptionVersion()).thenReturn("AES-GCM-256-V2");
        
        EncryptedCredential oldVersionCredential = EncryptedCredential.fromDatabase(
            "someencryptedvalue",
            "somesalt",
            "AES-GCM-256-V1", // Old version
            java.time.LocalDateTime.now().minusDays(400), // Old date
            "oldkeyid"
        );
        
        // When checking if re-encryption is needed
        boolean needsReEncryption = encryptionService.needsReEncryption(oldVersionCredential);
        
        // Then it should return true
        assertTrue(needsReEncryption);
    }
    
    @Test
    void shouldNotNeedReEncryptionForCurrentCredential() 
    {
        // Given a current encrypted credential
        EncryptedCredential current = encryptionService.encrypt(testPlaintext);
        
        // When checking if re-encryption is needed
        boolean needsReEncryption = encryptionService.needsReEncryption(current);
        
        // Then it should return false
        assertFalse(needsReEncryption);
    }
    
    @Test
    void shouldThrowExceptionForNullPlaintext() 
    {
        // When encrypting null plaintext
        // Then it should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> encryptionService.encrypt(null));
    }
    
    @Test
    void shouldThrowExceptionForEmptyPlaintext() 
    {
        // When encrypting empty plaintext
        // Then it should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> encryptionService.encrypt(""));
    }
    
    @Test
    void shouldThrowExceptionWhenMasterKeyNotConfigured() 
    {
        // Given no master key configured
        when(encryptionProperties.getEffectiveMasterKey()).thenReturn("");
        
        // When trying to encrypt
        // Then it should throw KeyManagementException
        assertThrows(KeyManagementException.class, () -> encryptionService.encrypt(testPlaintext));
    }
    
    @Test
    void shouldReturnCurrentEncryptionVersion() 
    {
        // When getting current encryption version
        String version = encryptionService.getCurrentEncryptionVersion();
        
        // Then it should return the configured version
        assertEquals("AES-GCM-256-V1", version);
    }
    
    @Test
    void shouldCorrectlyIdentifyMasterKeyConfiguration() 
    {
        // When master key is properly configured
        boolean isConfigured = encryptionService.isMasterKeyConfigured();
        
        // Then it should return true
        assertTrue(isConfigured);
        
        // When master key is not configured
        when(encryptionProperties.getEffectiveMasterKey()).thenReturn("short");
        boolean isNotConfigured = encryptionService.isMasterKeyConfigured();
        
        // Then it should return false
        assertFalse(isNotConfigured);
    }
    
    @Test
    void shouldHandleUnicodeCharacters() 
    {
        // Given plaintext with unicode characters
        String unicodePlaintext = "🔑 Secret API Key with émojis and spëcial chars 中文";
        
        // When encrypting and decrypting
        EncryptedCredential encrypted = encryptionService.encrypt(unicodePlaintext);
        String decrypted = encryptionService.decrypt(encrypted);
        
        // Then unicode should be preserved
        assertEquals(unicodePlaintext, decrypted);
    }
}