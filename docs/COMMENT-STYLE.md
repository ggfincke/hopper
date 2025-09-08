# Comment Style Guide

This document describes the comment conventions used in the Hopper codebase, designed to help Java beginners understand what's happening and why.

## Comment Philosophy

Comments should explain **why** something is done, not just what is done. The code itself shows what; comments explain the reasoning, purpose, and important context.

## Section Organization

Use `// *` to mark major sections within classes:

```java
public class Product {
    // * Attributes
    
    // * Constructors
    
    // * Getters and Setters
    
    // * Overrides
}
```

## Field Documentation

Document fields with their purpose and any important constraints:

```java
// UUID for product (auto-generated primary key)
@Id
@GeneratedValue
@UuidGenerator
private UUID id;

// many orders belong to one platform (lazy loading for performance)
@ManyToOne(optional = false, fetch = FetchType.LAZY)
@JoinColumn(name = "platform_id", nullable = false)
private Platform platform;

// price of product (stored with 2 decimal precision)
@NotNull
@DecimalMin("0.00")
@Column(name = "price", nullable = false, precision = 12, scale = 2)
private BigDecimal price = BigDecimal.ZERO;
```

## Method Documentation

### Getters and Setters
Simple getters/setters only need the field name:

```java
// product ID
public UUID getId() {
    return id;
}

// name (trims whitespace on set)
public void setName(String name) {
    this.name = name == null ? null : name.trim();
}
```

### API Endpoints
Document the HTTP method, path, and purpose:

```java
// GET /api/products - list all products
@GetMapping
public List<ProductDto> list() {
    // Convert entities to DTOs for API response
    return repo.findAll().stream()
            .map(p -> new ProductDto(...))
            .collect(Collectors.toList());
}
```

### Constructors
Document the purpose, especially for protected constructors:

```java
// Default constructor required by JPA
protected Product() {}

// Main constructor for creating new products
public Product(String sku, String name, BigDecimal price) {
    // Use Objects.requireNonNull to validate required parameters
    this.sku = Objects.requireNonNull(sku, "sku").trim();
    this.name = Objects.requireNonNull(name, "name").trim();
    // Always round prices to 2 decimal places
    this.price = Objects.requireNonNull(price, "price").setScale(2, RoundingMode.HALF_UP);
    this.quantity = 0;
}
```

## Annotation Documentation

Explain Spring/JPA annotations that affect behavior:

```java
// Entity mapped to "products" table with unique name constraint
@Entity
@Table(
    name = "products", 
    uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})}
)

// REST controller handling product API endpoints
@RestController
@RequestMapping("/api/products")
public class ProductController {
    // Spring will inject repository dependency
    private final ProductRepository repo;
}
```

## Database Relationships

Always explain relationship mappings and their implications:

```java
// One platform can have many credentials (one-to-many)
@OneToMany(mappedBy = "platform", cascade = CascadeType.ALL, orphanRemoval = true)
private List<PlatformCredential> credentials = new ArrayList<>();

// Each listing belongs to exactly one product (many-to-one)
@ManyToOne(optional = false, fetch = FetchType.LAZY)
@JoinColumn(name = "product_id", nullable = false)
private Product product;
```

## What NOT to Comment

Avoid comments that merely repeat the code:

```java
// BAD: Obvious what the code does
// Set the name to name
public void setName(String name) {
    this.name = name;
}

// GOOD: Explains the why or adds important context  
// name (trims whitespace on set)
public void setName(String name) {
    this.name = name == null ? null : name.trim();
}
```

## DTO Classes

Document the purpose and structure:

```java
// * DTO
// Represents a product in API responses (excludes internal fields)
static class ProductDto {
    // product ID (as string for JSON serialization)
    private final String id;
    
    // * Getters (for serialization)
    // Spring Boot uses these getters to convert to JSON
    public String getId() {
        return id;
    }
}
```

## Complex Logic

When business logic is involved, explain the reasoning:

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Product other)) return false;
    // Two products are equal if they have the same ID
    // (ID-based equality for JPA entities)
    return id != null && id.equals(other.id);
}

@Override
public int hashCode() {
    // Use ID for hash code, or 0 if not persisted yet
    return id != null ? id.hashCode() : 0;
}
```

## Validation Annotations

Explain business rules behind constraints:

```java
// SKU is optional but must be unique when present
@Column(name = "sku", unique = true, nullable = true)
private String sku;

// Price must be non-negative (business rule)
@NotNull
@DecimalMin("0.00")
private BigDecimal price;
```

## Key Principles

1. **Context over Code**: Explain why decisions were made
2. **Business Rules**: Document constraints and their business reasons  
3. **Framework Behavior**: Explain how Spring/JPA annotations affect runtime behavior
4. **Relationships**: Make database relationships and their implications clear
5. **Be Concise**: One line per comment when possible
6. **Update Comments**: Keep them current when code changes

This style helps Java beginners understand not just the syntax, but the architectural decisions and business logic that drive the implementation.