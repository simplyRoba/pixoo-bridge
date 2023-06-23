package de.simplyroba.pixoobridge.bridge.tool

import de.simplyroba.pixoobridge.bridge.tool.model.ScoreboardScores
import de.simplyroba.pixoobridge.bridge.tool.model.TimerSettings
import de.simplyroba.pixoobridge.client.PixooDeviceClient
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.badRequest
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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

  @PostMapping("/stopwatch/start")
  fun startStopwatch(): ResponseEntity<Unit> {
    pixooClient.setStopwatch(1)
    return ok().build()
  }

  @PostMapping("/stopwatch/stop")
  fun stopStopwatch(): ResponseEntity<Unit> {
    pixooClient.setStopwatch(0)
    return ok().build()
  }

  @PostMapping("/stopwatch/reset")
  fun resetStopwatch(): ResponseEntity<Unit> {
    pixooClient.setStopwatch(2)
    return ok().build()
  }

  @PostMapping("/scoreboard", consumes = [APPLICATION_JSON_VALUE])
  fun setScoreboard(@RequestBody body: ScoreboardScores): ResponseEntity<Unit> {
    if (body.redScore !in 0..999 || body.blueScore !in 0..999) return badRequest().build()
    pixooClient.setScoreBoard(body.redScore, body.blueScore)
    return ok().build()
  }

  @PostMapping("/soundmeter/start")
  fun startSoundMeter(): ResponseEntity<Unit> {
    pixooClient.setSoundMeter(true)
    return ok().build()
  }

  @PostMapping("/soundmeter/stop")
  fun stopSoundMeter(): ResponseEntity<Unit> {
    pixooClient.setSoundMeter(false)
    return ok().build()
  }
}
