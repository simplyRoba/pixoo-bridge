package de.simplyroba.pixoobridge.bridge.manage.model

import io.swagger.v3.oas.annotations.media.Schema

data class SettingsResponse(
  val displayOn: Boolean,
  @Schema(minimum = "0", maximum = "100", defaultValue = "50") val brightness: Int,
  val timeMode: TimeMode,
  @Schema(allowableValues = ["0", "90", "180", "270"], defaultValue = "0") val rotationAngle: Int,
  val mirrored: Boolean,
  val temperatureUnit: TemperatureUnit,
  val currentClockId: Int
) {
  enum class TimeMode() {
    TWELVE,
    TWENTY_FOUR
  }

  enum class TemperatureUnit() {
    CELSIUS,
    FAHRENHEIT
  }
}
