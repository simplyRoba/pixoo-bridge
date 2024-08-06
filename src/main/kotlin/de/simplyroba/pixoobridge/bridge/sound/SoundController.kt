package de.simplyroba.pixoobridge.bridge.sound

import de.simplyroba.pixoobridge.client.PixooClient
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Sound")
@RestController
@RequestMapping("/sound")
class SoundController(private val pixooClient: PixooClient) {

  @Operation(summary = "Play a sound", description = "This will play a sound on the pixoo.")
  @PostMapping("/play")
  fun play() {
    pixooClient.playSound()
  }
}
