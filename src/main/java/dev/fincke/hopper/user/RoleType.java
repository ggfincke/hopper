package dev.fincke.hopper.user;

// Enumeration of available user roles in the system
public enum RoleType
{
    // Administrator with full system access
    ADMIN("ADMIN"),
    
    // Regular user with standard permissions
    USER("USER"),
    
    // API client for programmatic access
    API_CLIENT("API_CLIENT");

    // * Attributes
    
    // String representation of the role for database storage
    private final String name;

    // * Constructor
    
    // Constructor with role name
    RoleType(String name)
    {
        this.name = name;
    }

    // * Methods
    
    // Get the string name of the role
    public String getName()
    {
        return name;
    }

    // Find role type by name (case-insensitive)
    public static RoleType fromName(String name)
    {
        if (name == null)
        {
            return null;
        }
        
        for (RoleType roleType : values())
        {
            if (roleType.name.equalsIgnoreCase(name.trim()))
            {
                return roleType;
            }
        }
        
        throw new IllegalArgumentException("Unknown role: " + name);
    }

    @Override
    public String toString()
    {
        return name;
    }
}