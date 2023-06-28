package de.simplyroba.pixoobridge.bridge.manage.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Weather")
data class WeatherResponse(
  val currentTemperature: Float,
  val minimalTemperature: Float,
  val maximalTemperature: Float,
  @Schema(allowableValues = ["Sunny", "Cloudy", "Rainy", "Rainy", "Frog"])
  val weatherString: String,
  @Schema(description = "in hPa") val pressure: Int,
  @Schema(description = "in %") val humidity: Int,
  @Schema(description = "in m/s") val windSpeed: Float
)
