package de.simplyroba.pixoobridge.bridge.tool.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Scoreboard scores")
data class ScoreboardScoresRequest(
  @Schema(minimum = "0", maximum = "999", defaultValue = "0") val redScore: Int,
  @Schema(minimum = "0", maximum = "999", defaultValue = "0") val blueScore: Int,
)
