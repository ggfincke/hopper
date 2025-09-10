package dev.fincke.hopper.platform.platform.dto;

import jakarta.validation.constraints.NotBlank;

// Request DTO for creating a new platform (with validation constraints)
public record PlatformCreateRequest(
    // name of platform (required and unique)
    @NotBlank(message = "Platform name is required")
    String name,
    
    // type/category of platform (required for classification)
    @NotBlank(message = "Platform type is required")
    String platformType
) {
    // * Compact Constructor
    
    // normalize data on construction
    public PlatformCreateRequest
    {
        name = name != null ? name.trim() : name;
        platformType = platformType != null ? platformType.trim() : platformType;
    }
}