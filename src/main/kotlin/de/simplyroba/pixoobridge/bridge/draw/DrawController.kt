package de.simplyroba.pixoobridge.bridge.draw

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Draw")
@RestController
@RequestMapping("/draw")
class DrawController {

  @PostMapping("/fill")
  fun fill() {
    throw RuntimeException("Not yet implemented!")
  }
}
