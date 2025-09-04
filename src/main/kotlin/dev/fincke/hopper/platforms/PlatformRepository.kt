package dev.fincke.hopper.platforms

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

// data access layer for Platform (auto implements CRUD & query methods)
interface PlatformRepository : JpaRepository<Platform, UUID>
{
    // finds platform by type
    fun findByPlatformType(platformType: String): List<Platform>
    // finds platform by name
    fun findByName(name: String): Platform?
}
