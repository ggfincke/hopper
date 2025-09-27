package dev.fincke.hopper.testsupport;

import dev.fincke.hopper.order.buyer.Buyer;
import dev.fincke.hopper.order.order.Order;
import dev.fincke.hopper.platform.platform.Platform;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

// Builder that constructs Order aggregates with optional buyer relationships for tests
// Creates orders with realistic defaults for e-commerce workflow and relationship testing
public final class OrderTestBuilder
{
    // * Default Test Values
    
    // Auto-generated unique ID for each test order
    private UUID id = UUID.randomUUID();
    // Default platform entity for marketplace integration testing
    private Platform platform = PlatformTestBuilder.platform().build();
    // Buyer entity (null by default, set when testing buyer relationships)
    private Buyer buyer;
    // Standard external order ID format for uniqueness constraint testing
    private String externalOrderId = "ORD-1001";
    // Pending status (initial order state in business workflow)
    private String status = "pending";
    // Realistic order total with proper decimal precision
    private BigDecimal totalAmount = new BigDecimal("125.50");
    // Fixed test date for consistent temporal testing
    private Timestamp orderDate = Timestamp.from(Instant.parse("2024-01-01T00:00:00Z"));

    // * Factory Method
    
    // Private constructor enforces builder pattern usage
    private OrderTestBuilder()
    {
    }

    // Factory method to start builder chain for order entities
    public static OrderTestBuilder order()
    {
        return new OrderTestBuilder();
    }

    // * Builder Methods
    
    // Override default ID (useful for relationship and lookup testing)
    public OrderTestBuilder withId(UUID id)
    {
        this.id = id;
        return this;
    }

    // Override default platform (for testing marketplace-specific workflows)
    public OrderTestBuilder withPlatform(Platform platform)
    {
        this.platform = platform;
        return this;
    }

    // Set buyer (for testing buyer-order relationships and customer data)
    public OrderTestBuilder withBuyer(Buyer buyer)
    {
        this.buyer = buyer;
        return this;
    }

    // Override external order ID (for testing uniqueness constraints and duplicates)
    public OrderTestBuilder withExternalOrderId(String externalOrderId)
    {
        this.externalOrderId = externalOrderId;
        return this;
    }

    // Override status (for testing order state transitions and validation)
    public OrderTestBuilder withStatus(String status)
    {
        this.status = status;
        return this;
    }

    // Override total amount (for testing payment calculations and validation)
    public OrderTestBuilder withTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
        return this;
    }

    // Override order date (for testing temporal business logic and reporting)
    public OrderTestBuilder withOrderDate(Timestamp orderDate)
    {
        this.orderDate = orderDate;
        return this;
    }

    // * Entity Construction
    
    // Builds Order entity with configured relationships and optional buyer
    public Order build()
    {
        // Create order using main constructor (validates required platform relationship)
        Order order = new Order(platform, externalOrderId, status, totalAmount, orderDate);
        // Set ID manually (simulates database ID assignment)
        order.setId(id);
        // Set buyer if specified (buyer relationship is optional)
        if (buyer != null)
        {
            order.setBuyer(buyer);
        }
        return order;
    }
}
