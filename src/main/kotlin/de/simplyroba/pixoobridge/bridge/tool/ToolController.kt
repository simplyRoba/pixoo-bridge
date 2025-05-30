package de.simplyroba.pixoobridge.bridge.tool

import de.simplyroba.pixoobridge.bridge.tool.model.ScoreboardScoresRequest
import de.simplyroba.pixoobridge.bridge.tool.model.TimerSettingsRequest
import de.simplyroba.pixoobridge.client.PixooClient
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn.PATH
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.*

@Tag(name = "Tool")
@RestController
@RequestMapping("/tool")
class ToolController(private val pixooClient: PixooClient) {

  @Operation(summary = "Start the timer tool.")
  @ApiResponses(value = [ApiResponse(responseCode = "400", description = "Invalid path variable.")])
  @PostMapping("/timer/start", consumes = [APPLICATION_JSON_VALUE])
  fun startTimer(@RequestBody body: TimerSettingsRequest): ResponseEntity<Unit> {
    if (body.minutes !in 0..99 || body.seconds !in 0..59) return badRequest().build()
    pixooClient.setTimer(body.minutes, body.seconds, true)
    return ok().build()
  }

  @Operation(summary = "Stop the timer tool.")
  @PostMapping("/timer/stop")
  fun stopTimer(): ResponseEntity<Unit> {
    pixooClient.setTimer(0, 0, false)
    return ok().build()
  }

  @Operation(summary = "Control stopwatch tool.")
  @Parameter(
    name = "action",
    `in` = PATH,
    description = "Action to execute.",
    schema = Schema(allowableValues = ["start", "stop", "reset"]),
  )
  @ApiResponses(value = [ApiResponse(responseCode = "400", description = "Invalid path variable.")])
  @PostMapping("/stopwatch/{action}")
  fun controlStopwatch(@PathVariable("action") action: String): ResponseEntity<Unit> {
    when (action) {
      "start" -> pixooClient.setStopwatch(1)
      "stop" -> pixooClient.setStopwatch(0)
      "reset" -> pixooClient.setStopwatch(2)
      else -> return badRequest().build()
    }
    return ok().build()
  }

  @Operation(summary = "Control scoreboard tool.")
  @ApiResponses(value = [ApiResponse(responseCode = "400", description = "Invalid request body.")])
  @PostMapping("/scoreboard", consumes = [APPLICATION_JSON_VALUE])
  fun setScoreboard(@RequestBody body: ScoreboardScoresRequest): ResponseEntity<Unit> {
    if (body.redScore !in 0..999 || body.blueScore !in 0..999) return badRequest().build()
    pixooClient.setScoreBoard(body.redScore, body.blueScore)
    return ok().build()
  }

  @Operation(summary = "Control sound meter tool.")
  @Parameter(
    name = "action",
    `in` = PATH,
    description = "Action to execute.",
    schema = Schema(allowableValues = ["start", "stop"]),
  )
  @ApiResponses(value = [ApiResponse(responseCode = "400", description = "Invalid path variable.")])
  @PostMapping("/soundmeter/{action}")
  fun controlSoundMeter(@PathVariable("action") action: String): ResponseEntity<Unit> {
    when (action) {
      "start" -> pixooClient.setSoundMeter(true)
      "stop" -> pixooClient.setSoundMeter(false)
      else -> return badRequest().build()
    }
    return ok().build()
  }
}
