package dev.fincke.hopper.platforms;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "platforms", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class Platform {
    
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String name = "";

    @Column(name = "platform_type", nullable = false)
    private String platformType = "";

    public Platform() {
    }

    public Platform(String name, String platformType) {
        this.name = name;
        this.platformType = platformType;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Platform platform = (Platform) o;
        return Objects.equals(id, platform.id) &&
               Objects.equals(name, platform.name) &&
               Objects.equals(platformType, platform.platformType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, platformType);
    }

    @Override
    public String toString() {
        return "Platform{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", platformType='" + platformType + '\'' +
                '}';
    }
}