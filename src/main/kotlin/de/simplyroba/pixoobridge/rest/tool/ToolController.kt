package de.simplyroba.pixoobridge.rest.tool

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
  fun startTimer(@RequestBody body: TimerRequestBody): ResponseEntity<Unit> {
    if (body.minutes !in 0..99 || body.seconds !in 0..59) return badRequest().build()
    pixooClient.setTimer(body.minutes, body.seconds, true)
    return ok().build()
  }

  @PostMapping("/timer/stop")
  fun stopTimer(): ResponseEntity<Unit> {
    pixooClient.setTimer(0, 0, false)
    return ok().build()
  }
}
