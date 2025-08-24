package de.simplyroba.pixoobridge.config

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.util.unit.DataSize

// TODO test this config
@ConfigurationProperties("pixoo")
data class PixooConfig(
  val baseUrl: String,
  val size: Int,
  val animationSpeedFactor: Float,
  val maxImageSize: DataSize,
  val health: PixooHealthConfig,
  val docs: PixooDocumentationConfig,
  val log: PixooLogLevelConfig,
) {

  companion object {
    val ACCEPTABLE_SIZES = intArrayOf(16, 32, 64)
  }

  private val logger = LoggerFactory.getLogger(javaClass)

  init {
    logger.info("Configuration: $this")
    if (ACCEPTABLE_SIZES.contains(size).not())
      throw UnsupportedConfigurationException(
        "Size: $size is not supported. Possible values: ${ACCEPTABLE_SIZES.contentToString()}."
      )
    if (animationSpeedFactor < 0)
      throw UnsupportedConfigurationException(
        "Animation speed factor needs to be positive. Current value $animationSpeedFactor"
      )
  }
}

data class PixooHealthConfig(val forward: Boolean)

data class PixooDocumentationConfig(val enabled: Boolean)

data class PixooLogLevelConfig(val level: String)
