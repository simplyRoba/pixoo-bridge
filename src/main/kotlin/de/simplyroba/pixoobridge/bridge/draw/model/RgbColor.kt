package de.simplyroba.pixoobridge.bridge.draw.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Color")
data class RgbColor(
  @Schema(minimum = "0", maximum = "255") val red: Int,
  @Schema(minimum = "0", maximum = "255") val green: Int,
  @Schema(minimum = "0", maximum = "255") val blue: Int,
)

fun RgbColor.valid(): Boolean = red in 0..255 && green in 0..255 && blue in 0..255

fun RgbColor.toHexString() = String.format("#%02X%02X%02X", red, green, blue)
