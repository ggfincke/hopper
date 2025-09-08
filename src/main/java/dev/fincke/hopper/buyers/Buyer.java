package dev.fincke.hopper.buyers;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
    name = "buyers",
    indexes = {
        @Index(name = "idx_buyers_email", columnList = "email")
    }
)
public class Buyer
{
    // * Attributes

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // email address of the buyer
    @Email
    @Column(name = "email", nullable = true)
    private String email;

    // display name of the buyer
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name = "";

    // * Constructors
    protected Buyer()
    {}

    public Buyer(String email, String name)
    {
        this.email = email == null ? null : email.trim().toLowerCase();
        this.name = Objects.requireNonNull(name, "name").trim();
    }

    // * Getters and Setters
    
    // buyer ID
    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    // email
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email == null ? null : email.trim().toLowerCase();
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

    // * Overrides
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Buyer other)) return false;
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
        return "Buyer{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}