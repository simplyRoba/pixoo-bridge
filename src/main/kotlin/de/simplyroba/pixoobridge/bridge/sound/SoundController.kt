package de.simplyroba.pixoobridge.bridge.sound

import de.simplyroba.pixoobridge.bridge.sound.model.SoundRequest
import de.simplyroba.pixoobridge.client.PixooClient
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Sound")
@RestController
@RequestMapping("/sound")
class SoundController(private val pixooClient: PixooClient) {

  @Operation(
    summary = "Play a sound",
    description =
      "This will play a sound on the pixoo. " +
        "The Pixoo will just beep for the specified active time in cycle and then be silent for the off time in cycle. " +
        "This will repeat until the total play time is reached. Length or tone of the beep cannot be configured.",
  )
  @ApiResponses(value = [ApiResponse(responseCode = "400", description = "Invalid request body.")])
  @PostMapping("/play")
  fun play(@RequestBody body: SoundRequest): ResponseEntity<Unit> {
    if (!body.valid()) return ResponseEntity.badRequest().build()

    pixooClient.playSound(body.activeTimeInCycle, body.offTimeInCycle, body.totalPlayTime)
    return ResponseEntity.ok().build()
  }
}
