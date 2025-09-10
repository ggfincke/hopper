package dev.fincke.hopper.platform.platform.dto;

// Request DTO for updating an existing platform (all fields optional for partial updates)
public record PlatformUpdateRequest(
    // name of platform
    String name,
    
    // type/category of platform
    String platformType
) {
    // * Compact Constructor
    
    // normalize data on construction
    public PlatformUpdateRequest
    {
        name = name != null ? name.trim() : null;
        platformType = platformType != null ? platformType.trim() : null;
    }
    
    // * Utility Methods
    
    // check if any field provided for update
    public boolean hasUpdates() 
    {
        return name != null || platformType != null;
    }
}