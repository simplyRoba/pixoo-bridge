package de.simplyroba.pixoobridge

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication @ConfigurationPropertiesScan class PixooBridgeApplication

fun main(args: Array<String>) {
  runApplication<PixooBridgeApplication>(*args)
}
