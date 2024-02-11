package de.simplyroba.pixoobridge.bridge.system

import de.simplyroba.pixoobridge.client.PixooClient
import de.simplyroba.pixoobridge.config.PixooConfig
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "System")
@RestController
class SystemController(val pixooClient: PixooClient, val config: PixooConfig) {

  @Operation(
    summary = "Check health of the service",
    description = "If configured this will also ping the pixoo."
  )
  @GetMapping("/health/check")
  fun healthCheck(): ResponseEntity<Unit> {
    if (config.health.forward) pixooClient.healthCheck()
    return ok().build()
  }

  @Operation(
    summary = "Reboot the pixoo",
    description =
      "This might respond with a 500 after a timeout as it might happen that the pixoo reboots before responding."
  )
  @PostMapping("/reboot")
  fun reboot() {
    pixooClient.reboot()
  }
}
