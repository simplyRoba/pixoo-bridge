package de.simplyroba.pixoobridge.bridge.manage

import de.simplyroba.pixoobridge.bridge.manage.model.WeatherLocation
import de.simplyroba.pixoobridge.bridge.manage.model.WhiteBalance
import de.simplyroba.pixoobridge.client.PixooDeviceClient
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.*

@Tag(name = "Manage")
@RestController
@RequestMapping("/manage")
class ManageController(private val pixooClient: PixooDeviceClient) {

  @Operation(description = "Turn display on or off.")
  @Parameter(
          name = "action",
          `in` = ParameterIn.PATH,
          description = "Action to execute.",
          schema = Schema(allowableValues = ["on", "off"])
  )
  @PostMapping("/display/{action}")
  fun manageDisplay(@PathVariable("action") action: String): ResponseEntity<Void> {
    when (action) {
      "on" -> pixooClient.switchDisplay(true)
      "off" -> pixooClient.switchDisplay(false)
      else -> return notFound().build()
    }
    return ok().build()
  }

  @Operation(description = "Control display brightness.")
  @Parameter(
          name = "value",
          `in` = ParameterIn.PATH,
          description = "Brightness value in percentage from 0-100.",
          schema = Schema(type = "integer", minimum = "0", maximum = "100", defaultValue = "50")
  )
  @PostMapping("/display/brightness/{value}")
  fun manageDisplayBrightness(@PathVariable value: Int): ResponseEntity<Void> {
    if (value !in 0..100) return badRequest().build()
    pixooClient.setDisplayBrightness(value)
    return ok().build()
  }

  @Operation(description = "Turn brightness overclock on or off.")
  @Parameter(
          name = "action",
          `in` = ParameterIn.PATH,
          description = "Action to execute.",
          schema = Schema(allowableValues = ["on", "off"])
  )
  @PostMapping("/display/brightness/overclock/{action}")
  fun manageDisplayBrightnessOverclockMode(@PathVariable action: String): ResponseEntity<Void> {
    when (action) {
      "on" -> pixooClient.setDisplayBrightnessOverclock(true)
      "off" -> pixooClient.setDisplayBrightnessOverclock(false)
      else -> return notFound().build()
    }
    return ok().build()
  }

  //TODO hier weiter!!
  @PostMapping("/display/rotation/{degree}")
  fun manageDisplayRotation(@PathVariable degree: Int): ResponseEntity<Void> {
    when (degree) {
      0 -> pixooClient.setDisplayRotation(0)
      90,
      180,
      270 -> pixooClient.setDisplayRotation(degree / 90)
      else -> return badRequest().build()
    }
    return ok().build()
  }

  @PostMapping(path = ["/display/mirror/enabled", "/display/mirror/disabled"])
  fun manageDisplayMirrorMode(request: HttpServletRequest): ResponseEntity<Void> {
    val mirrorEnabledBit = request.servletPath.contains("/enabled")
    pixooClient.setDisplayMirrored(mirrorEnabledBit)
    return ok().build()
  }

  @PostMapping("/display/white-balance", consumes = [APPLICATION_JSON_VALUE])
  fun manageDisplayWhiteBalance(@RequestBody body: WhiteBalance): ResponseEntity<Void> {
    if (body.red !in 0..100 || body.green !in 0..100 || body.blue !in 0..100)
      return badRequest().build()
    pixooClient.setDisplayWhiteBalance(body.red, body.green, body.blue)
    return ok().build()
  }

  @PostMapping("/time")
  fun refreshSystemTime(): ResponseEntity<Void> {
    pixooClient.setSystemTimeInUtc(OffsetDateTime.now(UTC).toEpochSecond())
    return ok().build()
  }

  @PostMapping(path = ["/time/mode/12h", "/time/mode/24h"])
  fun setSystemTimeMode(request: HttpServletRequest): ResponseEntity<Void> {
    val twentyFourModeEnabledBit = request.servletPath.contains("/24h")
    pixooClient.setSystemTimeMode(twentyFourModeEnabledBit)
    return ok().build()
  }

  @PostMapping("/time/offset/{offset}")
  fun setSystemTimeOffset(@PathVariable offset: Int): ResponseEntity<Void> {
    if (offset >= 14 || offset <= -12) return badRequest().build()
    pixooClient.setSystemTimeOffset(offset)
    return ok().build()
  }

  @GetMapping("/time", produces = [APPLICATION_JSON_VALUE])
  fun readDeviceTime(): ResponseEntity<Map<String, Any>> {
    val time = pixooClient.getSystemTime()
    return ok(time)
  }

  @PostMapping("/weather/location", consumes = [APPLICATION_JSON_VALUE])
  fun manageWeatherLocation(@RequestBody body: WeatherLocation): ResponseEntity<Void> {
    if (body.longitude.toFloat() !in -180f..180f || body.latitude.toFloat() !in -90f..90f)
      return badRequest().build()
    pixooClient.setWeatherLocation(body.longitude, body.latitude)
    return ok().build()
  }

  @PostMapping(path = ["/weather/temperature-unit/celsius", "/weather/temperature-unit/fahrenheit"])
  fun manageTemperatureUnit(request: HttpServletRequest): ResponseEntity<Void> {
    val temperatureUnitBit = request.servletPath.contains("/fahrenheit")
    pixooClient.setWeatherTemperatureUnit(temperatureUnitBit)
    return ok().build()
  }

  @GetMapping("/weather", produces = [APPLICATION_JSON_VALUE])
  fun readWeatherInformation(): ResponseEntity<Map<String, Any>> {
    val weather = pixooClient.readWeatherInformation()
    return ok(weather)
  }

  @GetMapping("/settings", produces = [APPLICATION_JSON_VALUE])
  fun readDeviceConfiguration(): ResponseEntity<Map<String, Any>> {
    val config = pixooClient.readConfiguration()
    return ok(config)
  }
}
