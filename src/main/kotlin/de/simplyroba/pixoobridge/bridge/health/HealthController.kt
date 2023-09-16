package de.simplyroba.pixoobridge.bridge.health

import de.simplyroba.pixoobridge.client.PixooClient
import de.simplyroba.pixoobridge.config.PixooConfig
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Health")
@RestController
@RequestMapping("/health")
class HealthController(val pixooClient: PixooClient, val config: PixooConfig) {

  @Operation(description = "Check health of the service")
  @GetMapping("/check")
  fun healthCheck(): ResponseEntity<Unit> {
    if (config.health.forward) pixooClient.healthCheck()
    return ok().build()
  }
}
