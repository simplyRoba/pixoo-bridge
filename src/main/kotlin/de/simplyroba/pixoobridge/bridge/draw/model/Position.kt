package de.simplyroba.pixoobridge.bridge.draw.model

import io.swagger.v3.oas.annotations.media.Schema

data class Position(
  @param:Schema(minimum = "0", maximum = "64") val x: Int,
  @param:Schema(minimum = "0", maximum = "64") val y: Int,
)

fun Position.valid() = x in 0..64 && y in 0..64
