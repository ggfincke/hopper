package dev.fincke.hopper.platforms;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlatformRepository extends JpaRepository<Platform, UUID> 
{
    
    List<Platform> findByPlatformType(String platformType);
    
    Platform findByName(String name);
}