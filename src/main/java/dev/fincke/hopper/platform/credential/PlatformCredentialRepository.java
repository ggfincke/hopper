package dev.fincke.hopper.platform.credential;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlatformCredentialRepository extends JpaRepository<PlatformCredential, UUID>
{
    
    List<PlatformCredential> findByPlatformId(UUID platformId);
    
    List<PlatformCredential> findByPlatformIdAndIsActive(UUID platformId, Boolean isActive);
    
    Optional<PlatformCredential> findByPlatformIdAndCredentialKey(UUID platformId, String credentialKey);
    
    Optional<PlatformCredential> findByPlatformIdAndCredentialKeyAndIsActive(UUID platformId, String credentialKey, Boolean isActive);
    
    List<PlatformCredential> findByIsActive(Boolean isActive);
    
    @Query("SELECT pc FROM PlatformCredential pc WHERE pc.platform.name = :platformName")
    List<PlatformCredential> findByPlatformName(@Param("platformName") String platformName);
    
    @Query("SELECT pc FROM PlatformCredential pc WHERE pc.platform.name = :platformName AND pc.isActive = :isActive")
    List<PlatformCredential> findByPlatformNameAndIsActive(@Param("platformName") String platformName, @Param("isActive") Boolean isActive);
}