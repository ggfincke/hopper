package dev.fincke.hopper.platforms

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

@Entity
@Table(name = "platforms", uniqueConstraints = [UniqueConstraint(columnNames = ["name"])])
class Platform(
    // platform ID
    @Id @GeneratedValue @UuidGenerator
    var id: UUID? = null,

    // platform name
    @Column(nullable = false)
    var name: String = "",

    // platform type
    @Column(name = "platform_type", nullable = false)
    // String for now, will eventually use enum
    var platformType: String = ""
)
