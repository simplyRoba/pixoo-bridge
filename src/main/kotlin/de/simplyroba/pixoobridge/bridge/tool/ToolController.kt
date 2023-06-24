package de.simplyroba.pixoobridge.bridge.tool

import de.simplyroba.pixoobridge.bridge.tool.model.ScoreboardScores
import de.simplyroba.pixoobridge.bridge.tool.model.TimerSettings
import de.simplyroba.pixoobridge.client.PixooDeviceClient
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.*

@Tag(name = "Tool")
@RestController
@RequestMapping("/tool")
class ToolController(private val pixooClient: PixooDeviceClient) {

  @PostMapping("/timer/start", consumes = [APPLICATION_JSON_VALUE])
  fun startTimer(@RequestBody body: TimerSettings): ResponseEntity<Unit> {
    if (body.minutes !in 0..99 || body.seconds !in 0..59) return badRequest().build()
    pixooClient.setTimer(body.minutes, body.seconds, true)
    return ok().build()
  }

  @PostMapping("/timer/stop")
  fun stopTimer(): ResponseEntity<Unit> {
    pixooClient.setTimer(0, 0, false)
    return ok().build()
  }

  @PostMapping("/stopwatch/{command}")
  fun controlStopwatch(@PathVariable("command") command: String): ResponseEntity<Unit> {
    when (command.lowercase()) {
      "start" -> pixooClient.setStopwatch(1)
      "stop" -> pixooClient.setStopwatch(0)
      "reset" -> pixooClient.setStopwatch(2)
      else -> return notFound().build()
    }
    return ok().build()
  }

  @PostMapping("/scoreboard", consumes = [APPLICATION_JSON_VALUE])
  fun setScoreboard(@RequestBody body: ScoreboardScores): ResponseEntity<Unit> {
    if (body.redScore !in 0..999 || body.blueScore !in 0..999) return badRequest().build()
    pixooClient.setScoreBoard(body.redScore, body.blueScore)
    return ok().build()
  }

  @PostMapping("/soundmeter/{command}")
  fun controlSoundMeter(@PathVariable("command") command: String): ResponseEntity<Unit> {
    when (command.lowercase()) {
      "start" -> pixooClient.setSoundMeter(true)
      "stop" -> pixooClient.setSoundMeter(false)
      else -> return notFound().build()
    }
    return ok().build()
  }
}
