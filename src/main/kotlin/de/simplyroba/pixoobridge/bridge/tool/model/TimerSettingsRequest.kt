package de.simplyroba.pixoobridge.bridge.tool.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Timer settings")
data class TimerSettingsRequest(
  @Schema(minimum = "0", maximum = "99", defaultValue = "1") val minutes: Int,
  @Schema(minimum = "0", maximum = "59", defaultValue = "0") val seconds: Int,
)
