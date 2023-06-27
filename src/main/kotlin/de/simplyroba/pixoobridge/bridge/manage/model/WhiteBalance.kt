package de.simplyroba.pixoobridge.bridge.manage.model

import io.swagger.v3.oas.annotations.media.Schema

data class WhiteBalance(
        @Schema(minimum = "0", maximum = "100", defaultValue = "0") val red: Int,
        @Schema(minimum = "0", maximum = "100", defaultValue = "0") val green: Int,
        @Schema(minimum = "0", maximum = "100", defaultValue = "0") val blue: Int
)
