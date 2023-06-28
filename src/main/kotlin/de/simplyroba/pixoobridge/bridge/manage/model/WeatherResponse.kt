package de.simplyroba.pixoobridge.bridge.manage.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Weather")
data class WeatherResponse(
  @Schema(defaultValue = "33.68") val currentTemperature: Float,
  @Schema(defaultValue = "31.68")val minimalTemperature: Float,
  @Schema(defaultValue = "34.68")val maximalTemperature: Float,
  @Schema(allowableValues = ["Sunny", "Cloudy", "Rainy", "Rainy", "Frog"])
  val weatherString: String,
  @Schema(description = "in hPa", defaultValue = "1006") val pressure: Int,
  @Schema(description = "in %", defaultValue = "47") val humidity: Int,
  @Schema(description = "in m/s", defaultValue = "2.54") val windSpeed: Float
)
