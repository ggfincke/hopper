package dev.fincke.hopper.platform.platform;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
    name = "platforms",
    uniqueConstraints = {@UniqueConstraint(name = "uq_platforms_name", columnNames = {"name"})},
    indexes = {@Index(name = "idx_platforms_type", columnList = "platform_type")}
)
public class Platform 
{
    // * Attributes

    // UUID for platform
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // name of platform
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name = "";

    // type/category of platform
    @NotBlank
    @Column(name = "platform_type", nullable = false)
    private String platformType = "";

    // * Contructors

    protected Platform() {}
    public Platform(String name, String platformType) 
    {
        this.name = name;
        this.platformType = platformType;
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

    public String getName() 
    {
        return name;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getPlatformType() 
    {
        return platformType;
    }

    public void setPlatformType(String platformType) 
    {
        this.platformType = platformType;
    }

    // * Overrides
    @Override
    public boolean equals(Object o) 
    {
        if (this == o) return true;
        if (!(o instanceof Platform other)) return false;
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
        return "Platform{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", platformType='" + platformType + '\'' +
                '}';
    }
}
