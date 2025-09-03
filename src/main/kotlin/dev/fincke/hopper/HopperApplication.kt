package dev.fincke.hopper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HopperApplication

fun main(args: Array<String>) {
	runApplication<HopperApplication>(*args)
}
