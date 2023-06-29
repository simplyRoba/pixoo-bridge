package de.simplyroba.pixoobridge.config

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("pixoo")
data class PixooConfig(val host: String, val health: PixooHealthConfig) {

  private val logger = LoggerFactory.getLogger(javaClass)

  init {
    logger.info("Configuration: $this")
  }
}

data class PixooHealthConfig(val forward: Boolean)
