package dev.fincke.hopper.order.order;

import dev.fincke.hopper.order.buyer.Buyer;
import dev.fincke.hopper.platform.platform.Platform;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
    name = "orders",
    uniqueConstraints = {@UniqueConstraint(name = "uq_orders_external_per_platform", columnNames = {"platform_id", "external_order_id"})},
    indexes = {
        @Index(name = "idx_orders_platform", columnList = "platform_id"),
        @Index(name = "idx_orders_buyer", columnList = "buyer_id"),
        @Index(name = "idx_orders_status", columnList = "status"),
        @Index(name = "idx_orders_date", columnList = "order_date")
    }
)
public class Order
{
    // * Attributes

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // many orders belong to one platform
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    // many orders may belong to one buyer (optional)
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = true)
    private Buyer buyer;

    // identifier on the external platform
    @NotBlank
    @Column(name = "external_order_id", nullable = false)
    private String externalOrderId = "";

    // status of the order (e.g., pending, paid, shipped, cancelled)
    @NotBlank
    @Column(name = "status", nullable = false)
    private String status = "";

    // total amount for this order
    @NotNull
    @DecimalMin("0.00")
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // when the order was placed
    @NotNull
    @Column(name = "order_date", nullable = false)
    private Timestamp orderDate;

    // * Constructors
    protected Order()
    {}

    public Order(Platform platform,
                 String externalOrderId,
                 String status,
                 BigDecimal totalAmount,
                 Timestamp orderDate)
    {
        this.platform = Objects.requireNonNull(platform, "platform");
        this.externalOrderId = Objects.requireNonNull(externalOrderId, "externalOrderId").trim();
        this.status = Objects.requireNonNull(status, "status").trim();
        this.totalAmount = Objects.requireNonNull(totalAmount, "totalAmount").setScale(2, RoundingMode.HALF_UP);
        this.orderDate = Objects.requireNonNull(orderDate, "orderDate");
        this.buyer = null;
    }

    public Order(Platform platform,
                 String externalOrderId,
                 String status,
                 BigDecimal totalAmount,
                 Timestamp orderDate,
                 Buyer buyer)
    {
        this.platform = Objects.requireNonNull(platform, "platform");
        this.externalOrderId = Objects.requireNonNull(externalOrderId, "externalOrderId").trim();
        this.status = Objects.requireNonNull(status, "status").trim();
        this.totalAmount = Objects.requireNonNull(totalAmount, "totalAmount").setScale(2, RoundingMode.HALF_UP);
        this.orderDate = Objects.requireNonNull(orderDate, "orderDate");
        this.buyer = buyer;
    }

    // * Getters and Setters
    
    // order ID
    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    // platform
    public Platform getPlatform()
    {
        return platform;
    }

    public void setPlatform(Platform platform)
    {
        this.platform = platform;
    }

    // buyer
    public Buyer getBuyer()
    {
        return buyer;
    }

    public void setBuyer(Buyer buyer)
    {
        this.buyer = buyer;
    }

    // external order ID
    public String getExternalOrderId()
    {
        return externalOrderId;
    }

    public void setExternalOrderId(String externalOrderId)
    {
        this.externalOrderId = externalOrderId == null ? null : externalOrderId.trim();
    }

    // status
    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status == null ? null : status.trim();
    }

    // total amount
    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount == null ? null : totalAmount.setScale(2, RoundingMode.HALF_UP);
    }

    // order date
    public Timestamp getOrderDate()
    {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate)
    {
        this.orderDate = orderDate;
    }

    // * Overrides
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Order other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }
}