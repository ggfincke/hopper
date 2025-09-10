package dev.fincke.hopper.platform.platform.exception;

// Business rule exception for duplicate platform names (carries name for context)
public class DuplicatePlatformNameException extends RuntimeException
{
    
    // The duplicate name that caused the conflict
    private final String name;
    
    // Constructor with the conflicting platform name
    public DuplicatePlatformNameException(String name)
    {
        super("Platform with name '" + name + "' already exists");
        this.name = name;
    }
    
    // Name that caused the conflict
    public String getName()
    {
        return name;
    }
}