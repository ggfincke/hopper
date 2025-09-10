package dev.fincke.hopper.platform.platform.dto;

import dev.fincke.hopper.platform.platform.Platform;

import java.util.UUID;

// Response DTO for platform data (immutable for API responses)
public record PlatformResponse(
    // platform ID
    UUID id,
    
    // name of platform
    String name,
    
    // type/category of platform
    String platformType
) {
    // * Static Factory Methods
    
    // create response from entity
    public static PlatformResponse from(Platform platform) 
    {
        return new PlatformResponse(
            platform.getId(),
            platform.getName(),
            platform.getPlatformType()
        );
    }
}