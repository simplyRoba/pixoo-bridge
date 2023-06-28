package de.simplyroba.pixoobridge.bridge.manage.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Weather location")
data class WeatherLocationRequest(
        @Schema(minimum = "-180", maximum = "180", defaultValue = "0") val longitude: String,
        @Schema(minimum = "-90", maximum = "90", defaultValue = "0") val latitude: String
)
