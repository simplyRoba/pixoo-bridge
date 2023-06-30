package de.simplyroba.pixoobridge.bridge.draw.model

import io.swagger.v3.oas.annotations.media.Schema

data class RGB(
  @Schema(minimum = "0", maximum = "255") val red: Int,
  @Schema(minimum = "0", maximum = "255") val green: Int,
  @Schema(minimum = "0", maximum = "255") val blue: Int
)
