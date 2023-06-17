package de.simplyroba.pixoobridge.rest.manage

import de.simplyroba.pixoobridge.client.PixooDeviceClient
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC

@RestController
@RequestMapping("/manage")
class ManageController(private val pixooClient: PixooDeviceClient) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping(path = ["/display/on", "display/off"])
    fun manageDisplay(request: HttpServletRequest): ResponseEntity<Unit> {
        pixooClient.switchDisplay(request.servletPath.contains("/on"))
        return ResponseEntity.ok().build()
    }

    @PostMapping("/display/brightness/{value}")
    fun manageBrightness(@PathVariable value: Int): ResponseEntity<Unit> {
        pixooClient.setBrightness(value)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/settings", produces = [APPLICATION_JSON_VALUE])
    fun readDeviceConfiguration(): ResponseEntity<Map<String, Any>> {
        val config = pixooClient.readConfiguration()
        return ResponseEntity.ok(config)
    }

    @PostMapping("/time")
    fun refreshSystemTime(): ResponseEntity<Unit> {
        pixooClient.setSystemTimeInUtc(OffsetDateTime.now(UTC).toEpochSecond())
        return ResponseEntity.ok().build()
    }

    @PostMapping("/time/offset/{offset}")
    fun setSystemTimeOffset(@PathVariable offset: Int): ResponseEntity<Unit> {
        pixooClient.setSystemTimeOffset(offset)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/time")
    fun readDeviceTime(): ResponseEntity<Map<String, Any>> {
        val time = pixooClient.getSystemTime()
        return ResponseEntity.ok(time)
    }
}