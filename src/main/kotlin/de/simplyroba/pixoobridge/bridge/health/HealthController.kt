package de.simplyroba.pixoobridge.bridge.health

import de.simplyroba.pixoobridge.client.PixooDeviceClient
import de.simplyroba.pixoobridge.config.PixooConfig
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/health")
class HealthController(val pixooClient: PixooDeviceClient, val config: PixooConfig) {

  @GetMapping("/check")
  fun healthCheck(): ResponseEntity<Unit> {
    if (config.health.forward) pixooClient.healthCheck()
    return ok().build()
  }
}
