package de.simplyroba.pixoobridge.rest.manage

import de.simplyroba.pixoobridge.client.PixooDeviceClient
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.badRequest
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC

@RestController
@RequestMapping("/manage")
class ManageController(private val pixooClient: PixooDeviceClient) {

    @PostMapping(path = ["/display/on", "display/off"])
    fun manageDisplay(request: HttpServletRequest): ResponseEntity<Unit> {
        val onOffFlag = request.servletPath.contains("/on").toInt()
        pixooClient.switchDisplay(onOffFlag)
        return ok().build()
    }

    @PostMapping("/display/brightness/{value}")
    fun manageDisplayBrightness(@PathVariable value: Int): ResponseEntity<Unit> {
        if (value !in 0..100)
            return badRequest().build()
        pixooClient.setDisplayBrightness(value)
        return ok().build()
    }

    @PostMapping("/display/rotation/{degree}")
    fun manageDisplayRotation(@PathVariable degree: Int):ResponseEntity<Unit> {
        when (degree) {
            0 -> pixooClient.setDisplayRotation(0)
            90, 180, 270 -> pixooClient.setDisplayRotation(degree / 90)
            else -> return badRequest().build()
        }
        return ok().build()
    }

    @GetMapping("/settings", produces = [APPLICATION_JSON_VALUE])
    fun readDeviceConfiguration(): ResponseEntity<Map<String, Any>> {
        val config = pixooClient.readConfiguration()
        return ok(config)
    }

    @PostMapping("/time")
    fun refreshSystemTime(): ResponseEntity<Unit> {
        pixooClient.setSystemTimeInUtc(OffsetDateTime.now(UTC).toEpochSecond())
        return ok().build()
    }

    @PostMapping(path = ["/time/mode/12h", "/time/mode/24h"])
    fun setSystemTimeMode(request: HttpServletRequest): ResponseEntity<Unit> {
        val twentyFourModeFlag = request.servletPath.contains("/24h").toInt()
        pixooClient.setSystemTimeMode(twentyFourModeFlag)
        return ok().build()
    }

    @PostMapping("/time/offset/{offset}")
    fun setSystemTimeOffset(@PathVariable offset: Int): ResponseEntity<Unit> {
        if (offset >= 14 || offset <= -12)
            return badRequest().build()
        pixooClient.setSystemTimeOffset("GMT$offset")
        return ok().build()
    }

    @GetMapping("/time")
    fun readDeviceTime(): ResponseEntity<Map<String, Any>> {
        val time = pixooClient.getSystemTime()
        return ok(time)
    }



    private fun Boolean.toInt() = if (this) 1 else 0
}