package dev.fincke.hopper.platforms

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class PlatformDto(val id: String, val name: String, val platformType: String)

@RestController
@RequestMapping("/api/platforms")
class PlatformController(private val repo: PlatformRepository)
{
    // runs repo.findAll(), then maps each entity to PlatformDto (json)
    @GetMapping
    fun list(): List<PlatformDto> =
        repo.findAll().map { PlatformDto(it.id.toString(), it.name, it.platformType) }
}
