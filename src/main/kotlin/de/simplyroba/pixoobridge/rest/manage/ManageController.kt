package de.simplyroba.pixoobridge.rest.manage

import de.simplyroba.pixoobridge.client.PixooDeviceClient
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

    @GetMapping("/settings/all", produces = [APPLICATION_JSON_VALUE])
    fun readConfiguration(): ResponseEntity<Map<String, Any>> {
        val config = pixooClient.readConfiguration()
        return ResponseEntity.ok(config)
    }
}