package de.simplyroba.pixoobridge.config

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("pixoo")
data class PixooConfig(val host: String, val size: Int, val health: PixooHealthConfig) {

  companion object {
    val ACCEPTABLE_SIZES = intArrayOf(16, 32, 64)
  }

  private val logger = LoggerFactory.getLogger(javaClass)

  init {
    logger.info("Configuration: $this")
    if (ACCEPTABLE_SIZES.contains(size).not())
      throw IllegalArgumentException(
        "Size: $size is not supported. Possible values: ${ACCEPTABLE_SIZES.contentToString()}"
      )
  }
}

data class PixooHealthConfig(val forward: Boolean)
