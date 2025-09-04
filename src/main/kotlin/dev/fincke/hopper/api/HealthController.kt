package dev.fincke.hopper.api

import org.springframework.boot.info.BuildProperties
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

data class Health(val status: String = "ok", val service: String = "hopper", val version: String? = null)

@RestController
class HealthController(private val build: BuildProperties)
{
    @GetMapping("/healthz")
    fun health(): Health = Health(version = build.version)
}