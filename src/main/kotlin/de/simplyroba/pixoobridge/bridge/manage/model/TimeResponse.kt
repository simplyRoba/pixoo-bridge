package de.simplyroba.pixoobridge.bridge.manage.model

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(name = "Time")
data class TimeResponse(val localTime: LocalDateTime, val utcTime: LocalDateTime)
