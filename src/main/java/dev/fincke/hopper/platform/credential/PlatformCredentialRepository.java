package dev.fincke.hopper.platform.credential;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// * Repository
// Data-access layer for PlatformCredential entities with encrypted credential management
public interface PlatformCredentialRepository extends JpaRepository<PlatformCredential, UUID>
{
    
    // find all credentials for a specific platform
    List<PlatformCredential> findByPlatformId(UUID platformId);
    
    // find credentials for platform filtered by active status
    List<PlatformCredential> findByPlatformIdAndIsActive(UUID platformId, Boolean isActive);
    
    // find specific credential by platform and key name
    Optional<PlatformCredential> findByPlatformIdAndCredentialKey(UUID platformId, String credentialKey);
    
    // find active/inactive credential by platform and key name
    Optional<PlatformCredential> findByPlatformIdAndCredentialKeyAndIsActive(UUID platformId, String credentialKey, Boolean isActive);
    
    // find all credentials filtered by active status
    List<PlatformCredential> findByIsActive(Boolean isActive);
    
    // check if any credentials exist for given platform ID
    boolean existsByPlatformId(UUID platformId);
    
    // find credentials by platform name (custom JPQL query)
    @Query("SELECT pc FROM PlatformCredential pc WHERE pc.platform.name = :platformName")
    List<PlatformCredential> findByPlatformName(@Param("platformName") String platformName);
    
    // find active/inactive credentials by platform name (custom JPQL query)
    @Query("SELECT pc FROM PlatformCredential pc WHERE pc.platform.name = :platformName AND pc.isActive = :isActive")
    List<PlatformCredential> findByPlatformNameAndIsActive(@Param("platformName") String platformName, @Param("isActive") Boolean isActive);
}