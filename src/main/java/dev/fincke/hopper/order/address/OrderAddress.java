package dev.fincke.hopper.order.address;

import dev.fincke.hopper.order.order.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
    name = "order_addresses",
    uniqueConstraints = {@UniqueConstraint(name = "uq_order_addresses_order", columnNames = {"order_id"})},
    indexes = {
        @Index(name = "idx_order_addresses_order", columnList = "order_id"),
        @Index(name = "idx_order_addresses_country", columnList = "country"),
        @Index(name = "idx_order_addresses_city", columnList = "city")
    }
)
public class OrderAddress
{
    // * Attributes

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // one-to-one relationship with order
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // street address
    @NotBlank
    @Column(name = "street", nullable = false)
    private String street = "";

    // city name
    @NotBlank
    @Column(name = "city", nullable = false)
    private String city = "";

    // state/region/province
    @NotBlank
    @Column(name = "state", nullable = false)
    private String state = "";

    // postal/zip code
    @NotBlank
    @Column(name = "postal_code", nullable = false)
    private String postalCode = "";

    // country (preferably ISO code)
    @NotBlank
    @Column(name = "country", nullable = false)
    private String country = "";

    // * Constructors
    protected OrderAddress()
    {}

    public OrderAddress(Order order,
                       String street,
                       String city,
                       String state,
                       String postalCode,
                       String country)
    {
        this.order = Objects.requireNonNull(order, "order");
        this.street = Objects.requireNonNull(street, "street").trim();
        this.city = Objects.requireNonNull(city, "city").trim();
        this.state = Objects.requireNonNull(state, "state").trim();
        this.postalCode = Objects.requireNonNull(postalCode, "postalCode").trim();
        this.country = Objects.requireNonNull(country, "country").trim().toUpperCase();
    }

    // * Getters and Setters
    
    // order address ID
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

    // street
    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street == null ? null : street.trim();
    }

    // city
    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city == null ? null : city.trim();
    }

    // state
    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state == null ? null : state.trim();
    }

    // postal code
    public String getPostalCode()
    {
        return postalCode;
    }

    public void setPostalCode(String postalCode)
    {
        this.postalCode = postalCode == null ? null : postalCode.trim();
    }

    // country
    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country == null ? null : country.trim().toUpperCase();
    }

    // * Overrides
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof OrderAddress other)) return false;
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
        return "OrderAddress{" +
                "id=" + id +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}