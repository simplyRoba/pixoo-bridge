package de.simplyroba.pixoobridge.bridge.draw.model

import io.swagger.v3.oas.annotations.media.Schema

data class Position(
  @Schema(minimum = "0", maximum = "64") val x: Int,
  @Schema(minimum = "0", maximum = "64") val y: Int,
)

fun Position.validate() = x in 0..64 && y in 0..64
