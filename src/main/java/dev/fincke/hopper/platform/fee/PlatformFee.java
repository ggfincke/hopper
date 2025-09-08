package dev.fincke.hopper.platform.fee;

import dev.fincke.hopper.order.order.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
    name = "platform_fees",
    indexes = {
        @Index(name = "idx_platform_fees_order", columnList = "order_id"),
        @Index(name = "idx_platform_fees_type", columnList = "fee_type")
    }
)
public class PlatformFee 
{
    // * Attributes

    // UUID for platform fee
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // order this fee is associated with
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // type of fee ("transaction", "processing", "listing", "final_value")
    @NotBlank
    @Column(name = "fee_type", nullable = false)
    private String feeType = "";

    // amount of the fee
    @NotNull
    @DecimalMin("0.00")
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    // * Constructors

    protected PlatformFee() {}
    
    public PlatformFee(Order order, String feeType, BigDecimal amount) 
    {
        this.order = Objects.requireNonNull(order, "order");
        this.feeType = Objects.requireNonNull(feeType, "feeType").trim();
        this.amount = Objects.requireNonNull(amount, "amount");
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

    public Order getOrder() 
    {
        return order;
    }

    public void setOrder(Order order) 
    {
        this.order = order;
    }

    public String getFeeType() 
    {
        return feeType;
    }

    public void setFeeType(String feeType) 
    {
        this.feeType = feeType;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }

    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    // * Overrides

    @Override
    public boolean equals(Object o) 
    {
        if (this == o) return true;
        if (!(o instanceof PlatformFee other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() 
    {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() 
    {
        return "PlatformFee{" +
                "id=" + id +
                ", order=" + (order != null ? order.getId() : null) +
                ", feeType='" + feeType + '\'' +
                ", amount=" + amount +
                '}';
    }
}