package dev.fincke.hopper.order.item;

import dev.fincke.hopper.catalog.listing.Listing;
import dev.fincke.hopper.order.order.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
    name = "order_items",
    indexes = {
        @Index(name = "idx_order_items_order", columnList = "order_id"),
        @Index(name = "idx_order_items_listing", columnList = "listing_id")
    }
)
public class OrderItem
{
    // * Attributes

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // many order items belong to one order
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // many order items refer to one listing
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    // quantity purchased
    @Min(1)
    @Column(name = "quantity", nullable = false)
    private int quantity = 1;

    // unit price at time of sale
    @NotNull
    @DecimalMin("0.00")
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    // * Constructors
    protected OrderItem()
    {}

    public OrderItem(Order order,
                     Listing listing,
                     int quantity,
                     BigDecimal price)
    {
        this.order = Objects.requireNonNull(order, "order");
        this.listing = Objects.requireNonNull(listing, "listing");
        this.quantity = Math.max(1, quantity);
        this.price = Objects.requireNonNull(price, "price").setScale(2, RoundingMode.HALF_UP);
    }

    // * Getters and Setters
    
    // order item ID
    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    // order
    public Order getOrder()
    {
        return order;
    }

    public void setOrder(Order order)
    {
        this.order = order;
    }

    // listing
    public Listing getListing()
    {
        return listing;
    }

    public void setListing(Listing listing)
    {
        this.listing = listing;
    }

    // quantity
    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = Math.max(1, quantity);
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

    // * Overrides
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof OrderItem other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }
}