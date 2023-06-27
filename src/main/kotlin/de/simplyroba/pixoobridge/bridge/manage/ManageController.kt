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

  @Operation(description = "Control the display rotation")
  @Parameter(
          name = "degree",
          `in` = ParameterIn.PATH,
          description = "Rotation degree.",
          schema = Schema(allowableValues = ["0", "90", "180", "270"])
  )
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

  @Operation(description = "Control if the display is mirrored")
  @Parameter(
          name = "action",
          `in` = ParameterIn.PATH,
          description = "Action to execute.",
          schema = Schema(allowableValues = ["on", "off"])
  )
  @PostMapping("/display/mirror/{action}")
  fun manageDisplayMirrorMode(@PathVariable action: String): ResponseEntity<Void> {
    when (action) {
      "on" -> pixooClient.setDisplayMirrored(true)
      "off" -> pixooClient.setDisplayMirrored(false)
      else -> return notFound().build()
    }
    return ok().build()
  }

  @Operation(description = "Control the white balance")
  @PostMapping("/display/white-balance", consumes = [APPLICATION_JSON_VALUE])
  fun manageDisplayWhiteBalance(@RequestBody body: WhiteBalance): ResponseEntity<Void> {
    if (body.red !in 0..100 || body.green !in 0..100 || body.blue !in 0..100)
      return badRequest().build()
    pixooClient.setDisplayWhiteBalance(body.red, body.green, body.blue)
    return ok().build()
  }

  @Operation(description = "Set the time of the pixoo to the correct time of the bridge")
  @PostMapping("/time")
  fun refreshSystemTime(): ResponseEntity<Void> {
    pixooClient.setSystemTimeInUtc(OffsetDateTime.now(UTC).toEpochSecond())
    return ok().build()
  }

  @Operation(description = "Configure if the mode time is display")
  @Parameter(
          name = "mode",
          `in` = ParameterIn.PATH,
          description = "Time display mode",
          schema = Schema(allowableValues = ["12h", "24h"])
  )
  @PostMapping("/time/mode/{mode}")
  fun setSystemTimeMode(@PathVariable mode: String): ResponseEntity<Void> {
    when (mode) {
      "24h" -> pixooClient.setTwentyFourHourTimeMode(true)
      "12h" -> pixooClient.setTwentyFourHourTimeMode(false)
      else -> return notFound().build()
    }
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
