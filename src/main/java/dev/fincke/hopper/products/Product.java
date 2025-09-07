package dev.fincke.hopper.products;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
    name = "products", 
    uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})}
)

public class Product 
{
    // * Attributes

    // UUID for product
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // name of product
    @NotBlank
    @Column(nullable = false)
    private String name = "";

    // SKU of product; optional, unique when present
    @Column(name = "sku", unique = true, nullable = true)
    private String sku;

    // description (should be present but can be NULL)
    @Column(name = "description")
    private String description = "";

    // price of product
    @NotNull
    @DecimalMin("0.00")
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    // quantity of product in stock
    @Column(name = "quantity", nullable = false)
    private int quantity = 0;

    // * Contructor
    protected Product() {}

    public Product(String sku, String name, BigDecimal price) 
    {
        this.sku = Objects.requireNonNull(sku, "sku").trim();
        this.name = Objects.requireNonNull(name, "name").trim();
        this.price = Objects.requireNonNull(price, "price").setScale(2, RoundingMode.HALF_UP);
        this.quantity = 0;
    }

    // * Getters and Setters

    // product ID
    public UUID getId() 
    {
        return id;
    }

    public void setId(UUID id) 
    {
        this.id = id;
    }

    // name
    public String getName() 
    {
        return name;
    }

    public void setName(String name) 
    {
        this.name = name == null ? null : name.trim();
    }

    // SKU
    public String getSku() 
    {
        return sku;
    }   
    public void setSku(String sku) 
    {
        this.sku = sku;
    }

    // description
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    // price
    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price == null ? null : price.setScale(2, RoundingMode.HALF_UP);
    }

    // quantity
    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    // * Overrides
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Product other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() 
    {
        return id != null ? id.hashCode() : 0;
    }
}
