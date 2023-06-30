package de.simplyroba.pixoobridge.bridge.draw

import de.simplyroba.pixoobridge.bridge.draw.model.RGB
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Draw")
@RestController
@RequestMapping("/draw")
class DrawController {

  @Operation(description = "Fill complete screen with rgb color")
  @PostMapping("/fill")
  fun fill(@RequestBody rgb: RGB) {
    throw RuntimeException("Not yet implemented!")
  }
}
