package dev.fincke.hopper.testsupport;

import dev.fincke.hopper.platform.platform.Platform;

import java.util.UUID;

// Test builder for Platform entities to keep unit tests focused on behavior rather than setup
// Creates platforms with realistic defaults for multi-platform integration testing
public final class PlatformTestBuilder
{
    // * Default Test Values
    
    // Auto-generated unique ID for each test platform
    private UUID id = UUID.randomUUID();
    // Generic platform name for identification testing
    private String name = "Test Platform";
    // Standard platform type for marketplace integration
    private String platformType = "MARKETPLACE";

    // * Factory Method
    
    // Private constructor enforces builder pattern usage
    private PlatformTestBuilder()
    {
    }

    // Factory method to start builder chain
    public static PlatformTestBuilder platform()
    {
        return new PlatformTestBuilder();
    }

    // * Builder Methods
    
    // Override default ID (useful for relationship and reference testing)
    public PlatformTestBuilder withId(UUID id)
    {
        this.id = id;
        return this;
    }

    // Override default name (for specific platform identification testing)
    public PlatformTestBuilder withName(String name)
    {
        this.name = name;
        return this;
    }

    // Override default platform type (for multi-platform integration testing)
    public PlatformTestBuilder withPlatformType(String platformType)
    {
        this.platformType = platformType;
        return this;
    }

    // * Entity Construction
    
    // Builds Platform entity with configured values
    public Platform build()
    {
        // Create platform using main constructor (validates required fields)
        Platform platform = new Platform(name, platformType);
        // Set ID manually (simulates database ID assignment)
        platform.setId(id);
        return platform;
    }
}
