package dev.fincke.hopper.catalog.listing;

import dev.fincke.hopper.platform.platform.Platform;
import dev.fincke.hopper.catalog.product.Product;
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
    name = "listings", 
    uniqueConstraints = {@UniqueConstraint(name = "uq_listings_external_per_platform", columnNames = {"platform_id", "external_listing_id"})},
        indexes = {
                @Index(name = "idx_listings_product", columnList = "product_id"),
                @Index(name = "idx_listings_platform", columnList = "platform_id"),
                @Index(name = "idx_listings_status", columnList = "status")
        }
)

public class Listing
{
    // * Attributes

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // many listings belong to one product
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // many listings belong to one platform
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    @NotBlank
    @Column(name = "external_listing_id", nullable = false)
    private String externalListingId = "";

    @NotBlank
    @Column(name = "status", nullable = false)
    private String status = "";

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "quantity_listed", nullable = false)
    private int quantityListed = 0;

    // * Constructors
    protected Listing() 
    {}

    public Listing(Product product,
                   Platform platform,
                   String externalListingId,
                   String status,
                   BigDecimal price,
                   int quantityListed)
    {
        this.product = Objects.requireNonNull(product, "product");
        this.platform = Objects.requireNonNull(platform, "platform");
        this.externalListingId = Objects.requireNonNull(externalListingId, "externalListingId").trim();
        this.status = Objects.requireNonNull(status, "status").trim();
        this.price = Objects.requireNonNull(price, "price").setScale(2, RoundingMode.HALF_UP);
        this.quantityListed = Math.max(0, quantityListed);
    }

    // * Getters and Setters
    public UUID getId() 
    {
        return id;
    }

    public void setId(UUID id) 
    {
        this.id = id;
    }

    public Product getProduct() 
    {
        return product;
    }

    public void setProduct(Product product) 
    {
        this.product = product;
    }

    public Platform getPlatform() 
    {
        return platform;
    }

    public void setPlatform(Platform platform) 
    {
        this.platform = platform;
    }

    public String getExternalListingId() 
    {
        return externalListingId;
    }

    public void setExternalListingId(String externalListingId) 
    {
        this.externalListingId = externalListingId == null ? null : externalListingId.trim();
    }

    public String getStatus() 
    {
        return status;
    }

    public void setStatus(String status) 
    {
        this.status = status == null ? null : status.trim();
    }

    public BigDecimal getPrice() 
    {
        return price;
    }

    public void setPrice(BigDecimal price) 
    {
        this.price = price == null ? null : price.setScale(2, RoundingMode.HALF_UP);
    }

    public int getQuantityListed() 
    {
        return quantityListed;
    }

    public void setQuantityListed(int quantityListed) 
    {
        this.quantityListed = Math.max(0, quantityListed);
    }

    // * Overrides
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Listing other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }
}
