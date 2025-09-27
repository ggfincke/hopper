package dev.fincke.hopper.testsupport;

import dev.fincke.hopper.order.buyer.Buyer;

import java.util.UUID;

// Test builder for Buyer entities to simplify service unit test setup
// Uses builder pattern to create test buyers with realistic default values
public final class BuyerTestBuilder
{
    // * Default Test Values
    
    // Auto-generated unique ID for each test buyer
    private UUID id = UUID.randomUUID();
    // Realistic email address for buyer contact testing
    private String email = "buyer@example.com";
    // Human-readable name for buyer identification in tests
    private String name = "Sample Buyer";

    // * Factory Method
    
    // Private constructor enforces builder pattern usage
    private BuyerTestBuilder()
    {
    }

    // Factory method to start builder chain
    public static BuyerTestBuilder buyer()
    {
        return new BuyerTestBuilder();
    }

    // * Builder Methods
    
    // Override default ID (useful for specific relationship testing)
    public BuyerTestBuilder withId(UUID id)
    {
        this.id = id;
        return this;
    }

    // Override default email (for email validation and uniqueness testing)
    public BuyerTestBuilder withEmail(String email)
    {
        this.email = email;
        return this;
    }

    // Override default name (for display and search functionality testing)
    public BuyerTestBuilder withName(String name)
    {
        this.name = name;
        return this;
    }

    // * Entity Construction
    
    // Builds Buyer entity with configured values
    public Buyer build()
    {
        // Create buyer using main constructor (validates required fields)
        Buyer buyer = new Buyer(email, name);
        // Set ID manually (simulates database ID assignment)
        buyer.setId(id);
        return buyer;
    }
}
