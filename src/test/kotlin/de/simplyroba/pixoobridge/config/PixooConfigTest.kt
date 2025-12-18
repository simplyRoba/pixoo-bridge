package de.simplyroba.pixoobridge.config

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.util.unit.DataSize

class PixooConfigTest {

  private fun bridge() =
    PixooBridgeConfig(
      maxImageSize = DataSize.ofKilobytes(10),
      health = PixooHealthConfig(forward = true),
      docs = PixooDocumentationConfig(enabled = true),
      log = PixooLogLevelConfig(level = "INFO"),
    )

  @Test
  fun `valid config passes`() {
    val cfg =
      PixooConfig(
        baseUrl = "http://localhost",
        size = 32,
        animationSpeedFactor = 1.0f,
        bridge = bridge(),
      )

    assertThat(cfg.size).isEqualTo(32)
  }

  @Test
  fun `unsupported size throws`() {
    val ex = assertThatThrownBy {
      PixooConfig(
        baseUrl = "http://localhost",
        size = 99,
        animationSpeedFactor = 1.0f,
        bridge = bridge(),
      )
    }

    ex.isInstanceOf(UnsupportedConfigurationException::class.java).hasMessageContaining("Size: 99")
  }

  @Test
  fun `negative animation speed throws`() {
    val ex = assertThatThrownBy {
      PixooConfig(
        baseUrl = "http://localhost",
        size = 32,
        animationSpeedFactor = -0.1f,
        bridge = bridge(),
      )
    }

    ex
      .isInstanceOf(UnsupportedConfigurationException::class.java)
      .hasMessageContaining("Animation speed factor")
  }

  @Test
  fun `acceptable sizes allowed`() {
    PixooConfig.ACCEPTABLE_SIZES.forEach { s ->
      assertThatCode {
          PixooConfig(
            baseUrl = "http://x",
            size = s,
            animationSpeedFactor = 0.5f,
            bridge = bridge(),
          )
        }
        .doesNotThrowAnyException()
    }
  }
}
