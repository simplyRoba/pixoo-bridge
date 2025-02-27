package de.simplyroba.pixoobridge.bridge.manage

import de.simplyroba.pixoobridge.bridge.manage.model.*
import de.simplyroba.pixoobridge.bridge.manage.model.SettingsResponse.TemperatureUnit.CELSIUS
import de.simplyroba.pixoobridge.bridge.manage.model.SettingsResponse.TemperatureUnit.FAHRENHEIT
import de.simplyroba.pixoobridge.bridge.manage.model.SettingsResponse.TimeMode.TWELVE
import de.simplyroba.pixoobridge.bridge.manage.model.SettingsResponse.TimeMode.TWENTY_FOUR
import de.simplyroba.pixoobridge.client.PixooClient
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter
import org.apache.commons.lang3.math.NumberUtils.toLong
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.*

@Tag(name = "Manage")
@RestController
@RequestMapping("/manage")
class ManageController(private val pixooClient: PixooClient) {

  @Operation(summary = "Turn display on or off.")
  @Parameter(
    name = "action",
    `in` = ParameterIn.PATH,
    description = "Action to execute.",
    schema = Schema(allowableValues = ["on", "off"]),
  )
  @ApiResponses(value = [ApiResponse(responseCode = "400", description = "Invalid path variable.")])
  @PostMapping("/display/{action}")
  fun manageDisplay(@PathVariable("action") action: String): ResponseEntity<Unit> {
    when (action) {
      "on" -> pixooClient.switchDisplay(true)
      "off" -> pixooClient.switchDisplay(false)
      else -> return badRequest().build()
    }
    return ok().build()
  }

  @Operation(summary = "Control display brightness.")
  @Parameter(
    name = "value",
    `in` = ParameterIn.PATH,
    description = "Brightness value in percentage from 0-100.",
    schema = Schema(type = "integer", minimum = "0", maximum = "100", defaultValue = "50"),
  )
  @ApiResponses(value = [ApiResponse(responseCode = "400", description = "Invalid path variable.")])
  @PostMapping("/display/brightness/{value}")
  fun manageDisplayBrightness(@PathVariable value: Int): ResponseEntity<Unit> {
    if (value !in 0..100) return badRequest().build()
    pixooClient.setDisplayBrightness(value)
    return ok().build()
  }

  @Operation(
    summary = "Turn brightness overclock on or off.",
    description = "This won’t be saved on the pixoo and resets when the device powers off.",
  )
  @Parameter(
    name = "action",
    `in` = ParameterIn.PATH,
    description = "Action to execute.",
    schema = Schema(allowableValues = ["on", "off"]),
  )
  @ApiResponses(value = [ApiResponse(responseCode = "400", description = "Invalid path variable.")])
  @PostMapping("/display/brightness/overclock/{action}")
  fun manageDisplayBrightnessOverclockMode(@PathVariable action: String): ResponseEntity<Unit> {
    when (action) {
      "on" -> pixooClient.setDisplayBrightnessOverclock(true)
      "off" -> pixooClient.setDisplayBrightnessOverclock(false)
      else -> return badRequest().build()
    }
    return ok().build()
  }

  @Operation(
    summary = "Control the display rotation",
    description = "This won’t be saved on the pixoo and resets when the device powers off.",
  )
  @Parameter(
    name = "angle",
    `in` = ParameterIn.PATH,
    description = "Rotation angle.",
    schema = Schema(allowableValues = ["0", "90", "180", "270"]),
  )
  @ApiResponses(value = [ApiResponse(responseCode = "400", description = "Invalid path variable.")])
  @PostMapping("/display/rotation/{angle}")
  fun manageDisplayRotation(@PathVariable angle: Int): ResponseEntity<Unit> {
    when (angle) {
      0 -> pixooClient.setDisplayRotation(0)
      90,
      180,
      270 -> pixooClient.setDisplayRotation(angle / 90)
      else -> return badRequest().build()
    }
    return ok().build()
  }

  @Operation(
    summary = "Control if the display is mirrored",
    description = "This won’t be saved on the pixoo and resets when the device powers off.",
  )
  @Parameter(
    name = "action",
    `in` = ParameterIn.PATH,
    description = "Action to execute.",
    schema = Schema(allowableValues = ["on", "off"]),
  )
  @ApiResponses(value = [ApiResponse(responseCode = "400", description = "Invalid path variable.")])
  @PostMapping("/display/mirror/{action}")
  fun manageDisplayMirrorMode(@PathVariable action: String): ResponseEntity<Unit> {
    when (action) {
      "on" -> pixooClient.setDisplayMirrored(true)
      "off" -> pixooClient.setDisplayMirrored(false)
      else -> return badRequest().build()
    }
    return ok().build()
  }

  @Operation(
    summary = "Control the white balance",
    description = "This won’t be saved on the pixoo and resets when the device powers off.",
  )
  @ApiResponses(value = [ApiResponse(responseCode = "400", description = "Invalid request body.")])
  @PostMapping("/display/white-balance", consumes = [APPLICATION_JSON_VALUE])
  fun manageDisplayWhiteBalance(@RequestBody body: WhiteBalanceRequest): ResponseEntity<Unit> {
    if (body.red !in 0..100 || body.green !in 0..100 || body.blue !in 0..100)
      return badRequest().build()
    pixooClient.setDisplayWhiteBalance(body.red, body.green, body.blue)
    return ok().build()
  }

  @Operation(summary = "Set the time of the pixoo to the correct time of the bridge")
  @PostMapping("/time")
  fun refreshSystemTime(): ResponseEntity<Unit> {
    pixooClient.setSystemTimeInUtc(OffsetDateTime.now(UTC).toEpochSecond())
    return ok().build()
  }

  @Operation(
    summary = "Configure if the mode time is display",
    description = "This won’t be saved on the pixoo and resets when the device powers off.",
  )
  @Parameter(
    name = "mode",
    `in` = ParameterIn.PATH,
    description = "Time display mode.",
    schema = Schema(allowableValues = ["12h", "24h"]),
  )
  @ApiResponses(value = [ApiResponse(responseCode = "400", description = "Invalid path variable.")])
  @PostMapping("/time/mode/{mode}")
  fun setSystemTimeMode(@PathVariable mode: String): ResponseEntity<Unit> {
    when (mode) {
      "24h" -> pixooClient.setTwentyFourHourTimeMode(true)
      "12h" -> pixooClient.setTwentyFourHourTimeMode(false)
      else -> return badRequest().build()
    }
    return ok().build()
  }

  @Operation(summary = "Configure if the time offset of the current time zone")
  @Parameter(
    name = "offset",
    `in` = ParameterIn.PATH,
    description = "The time offset of the timezone. Between -12 and 14.",
    schema = Schema(type = "integer", minimum = "-12", maximum = "14"),
  )
  @ApiResponses(value = [ApiResponse(responseCode = "400", description = "Invalid path variable.")])
  @PostMapping("/time/offset/{offset}")
  fun setSystemTimeOffset(@PathVariable offset: Int): ResponseEntity<Unit> {
    if (offset >= 14 || offset <= -12) return badRequest().build()
    pixooClient.setSystemTimeOffset(offset)
    return ok().build()
  }

  @Operation(summary = "Get time on the pixoo")
  @GetMapping("/time", produces = [APPLICATION_JSON_VALUE])
  fun readDeviceTime(): ResponseEntity<TimeResponse> {
    val clientResponse = pixooClient.getSystemTime().parameters
    val response =
      TimeResponse(
        utcTime = LocalDateTime.ofEpochSecond(toLong(clientResponse["UTCTime"].toString()), 0, UTC),
        localTime =
          LocalDateTime.parse(
            clientResponse["LocalTime"].toString(),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
          ),
      )
    return ok(response)
  }

  @Operation(
    summary = "Configure the location for the weather forecast",
    description = "All data comes from https://openweathermap.org/.",
  )
  @ApiResponses(value = [ApiResponse(responseCode = "400", description = "Invalid request body.")])
  @PostMapping("/weather/location", consumes = [APPLICATION_JSON_VALUE])
  fun manageWeatherLocation(@RequestBody body: WeatherLocationRequest): ResponseEntity<Unit> {
    if (body.longitude.toFloat() !in -180f..180f || body.latitude.toFloat() !in -90f..90f)
      return badRequest().build()
    pixooClient.setWeatherLocation(body.longitude, body.latitude)
    return ok().build()
  }

  @Operation(
    summary = "Configure if the weather temperature unit",
    description = "This won’t be saved on the pixoo and resets when the device powers off.",
  )
  @Parameter(
    name = "unit",
    `in` = ParameterIn.PATH,
    description = "Temperature unit.",
    schema = Schema(allowableValues = ["celsius", "fahrenheit"]),
  )
  @ApiResponses(value = [ApiResponse(responseCode = "400", description = "Invalid path variable.")])
  @PostMapping("/weather/temperature-unit/{unit}")
  fun manageTemperatureUnit(@PathVariable unit: String): ResponseEntity<Unit> {
    when (unit) {
      "celsius" -> pixooClient.setWeatherTemperatureUnitFahrenheit(false)
      "fahrenheit" -> pixooClient.setWeatherTemperatureUnitFahrenheit(true)
      else -> return badRequest().build()
    }
    return ok().build()
  }

  @Operation(summary = "Get weather information")
  @GetMapping("/weather", produces = [APPLICATION_JSON_VALUE])
  fun readWeatherInformation(): ResponseEntity<WeatherResponse> {
    val clientResponse = pixooClient.readWeatherInformation().parameters
    val response =
      WeatherResponse(
        currentTemperature = clientResponse["CurTemp"].toString().toFloat(),
        minimalTemperature = clientResponse["MinTemp"].toString().toFloat(),
        maximalTemperature = clientResponse["MaxTemp"].toString().toFloat(),
        weatherString = clientResponse["Weather"].toString(),
        pressure = clientResponse["Pressure"].toString().toInt(),
        humidity = clientResponse["Humidity"].toString().toInt(),
        windSpeed = clientResponse["WindSpeed"].toString().toFloat(),
      )
    return ok(response)
  }

  @Operation(summary = "Get all settings")
  @GetMapping("/settings", produces = [APPLICATION_JSON_VALUE])
  fun readDeviceConfiguration(): ResponseEntity<SettingsResponse> {
    val clientResponse = pixooClient.readConfiguration().parameters
    val response =
      SettingsResponse(
        displayOn = "1" == clientResponse["LightSwitch"].toString(),
        brightness = clientResponse["Brightness"].toString().toInt(),
        timeMode = if ("1" == clientResponse["Time24Flag"].toString()) TWENTY_FOUR else TWELVE,
        rotationAngle =
          when (val it = clientResponse["RotationFlag"].toString()) {
            "0" -> 0
            else -> it.toInt() * 90
          },
        mirrored = "1" == clientResponse["MirrorFlag"].toString(),
        temperatureUnit =
          if ("1" == clientResponse["TemperatureMode"].toString()) FAHRENHEIT else CELSIUS,
        currentClockId = clientResponse["CurClockId"].toString().toInt(),
      )
    return ok(response)
  }
}
