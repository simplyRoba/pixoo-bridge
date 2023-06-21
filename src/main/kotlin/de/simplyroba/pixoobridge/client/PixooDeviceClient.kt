package de.simplyroba.pixoobridge.client

import com.fasterxml.jackson.databind.ObjectMapper
import de.simplyroba.pixoobridge.client.CommandType.*
import de.simplyroba.pixoobridge.client.model.Command
import de.simplyroba.pixoobridge.client.model.CommandResponse
import de.simplyroba.pixoobridge.config.PixooConfig
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@Component
class PixooDeviceClient(config: PixooConfig, private val mapper: ObjectMapper) {

  private val logger = LoggerFactory.getLogger(javaClass)

  private val webclient = WebClient.create("http://${config.host}")

  // OnOff, 0|1, 1=on; 0=off
  fun switchDisplay(onBit: Boolean) =
    genericPostCommand(SET_DISPLAY_ON_OFF, Pair("OnOff", onBit.toBitNumber()))

  // Brightness, 0-100, percentage of brightness
  fun setDisplayBrightness(percentageValue: Int) =
    genericPostCommand(SET_DISPLAY_BRIGHTNESS, Pair("Brightness", percentageValue))

  // Mode, 0|1, 1=brightness overclock (it won’t be saved and reset when the device power off)
  fun setDisplayBrightnessOverclock(brightnessOverclockEnabledBit: Boolean) =
    genericPostCommand(
      SET_DISPLAY_BRIGHTNESS_OVERCLOCK,
      Pair("Mode", brightnessOverclockEnabledBit.toBitNumber())
    )

  // Mode, 0-3, the rotation angle 0=normal; 1=90; 2=180; 3=270
  fun setDisplayRotation(rotationFlag: Int) =
    genericPostCommand(SET_DISPLAY_ROTATION, Pair("Mode", rotationFlag))

  // Mode, 0|1, 1=screen is mirrored (it won’t be saved and reset when the device power off)
  fun setDisplayMirrored(mirrorEnabledBit: Boolean) =
    genericPostCommand(SET_DISPLAY_MIRRORED, Pair("Mode", mirrorEnabledBit.toBitNumber()))

  /*
   * RValue, 0-100, red (it won’t be saved and reset when the device power off)
   * GValue, 0-100, green (it won’t be saved and reset when the device power off)
   * BValue, 0-100, blue (it won’t be saved and reset when the device power off)
   */
  fun setDisplayWhiteBalance(redPercentage: Int, greenPercentage: Int, bluePercentage: Int) =
    genericPostCommand(
      SET_DISPLAY_WHITE_BALANCE,
      Pair("RValue", redPercentage),
      Pair("GValue", greenPercentage),
      Pair("BValue", bluePercentage)
    )

  // Utc, example=1672416000, Unix epoch timestamps in seconds
  fun setSystemTimeInUtc(unixTimeInSeconds: Long) =
    genericPostCommand(SET_SYSTEM_TIME, Pair("Utc", unixTimeInSeconds))

  // Mode, 0|1, 1=24 hour mode; 0=12 hour mode
  fun setSystemTimeMode(twentyFourModeEnabledBit: Boolean) =
    genericPostCommand(SET_SYSTEM_TIME_MODE, Pair("Mode", twentyFourModeEnabledBit.toBitNumber()))

  // TimeZoneValue, example=GMT-5, offset in GMT+/- or GMT0 format
  fun setSystemTimeOffset(offset: Int) =
    genericPostCommand(SET_SYSTEM_TIME_ZONE, Pair("TimeZoneValue", "GMT$offset"))

  /*
   * Returns:
   * UTCTime, example=1672416000, Unix epoch timestamps in seconds
   * LocalTime, example=2022-03-14 03:40:28, time in yyyy-MM-dd HH:mm:ss format
   */
  fun getSystemTime(): Map<String, Any> = genericPostCommand(GET_SYSTEM_TIME)?.parameters ?: mapOf()

  /*
   * Longitude, -180-180, as String
   * Latitude, -90-90, as String
   */
  fun setWeatherLocation(longitude: String, latitude: String) =
    genericPostCommand(
      SET_WEATHER_LOCATION,
      Pair("Longitude", longitude),
      Pair("Latitude", latitude)
    )

  // Mode, 0|1, 0=Celsius; 1=Fahrenheit (it won’t be saved and reset when the device power off)
  fun setWeatherTemperatureUnit(temperatureUnitBit: Boolean) =
    genericPostCommand(SET_WEATHER_TEMP_UNIT, Pair("Mode", temperatureUnitBit.toBitNumber()))

  /*
   * Returns:
   * Weather, “Sunny”|”Cloudy”|”Rainy”|”Rainy”|”Frog”, as String
   * CurTemp, example=33.680000, the current temperature as number
   * MinTemp, example=31.580000, the minimum temperature as number
   * MaxTemp, example=34.670000, the maximum temperature as number
   * Pressure, example=1006, current pressure as number
   * Humidity, example=50, current humidity as number
   * Visibility, example=10000, current visibility as number
   * WindSpeed, example=2.54, current wind speed as number in m/s
   */
  fun readWeatherInformation(): Map<String, Any> =
    genericPostCommand(GET_WEATHER_INFO)?.parameters ?: mapOf()

  /*
   * Returns:
   * Brightness, 0-100, the system brightness
   * RotationFlag, 0|1, 1=it will switch to display faces and gifs
   * ClockTime, ??, the time of displaying faces (will be active with RotationFlag = 1)
   * GalleryTime, ??, the time of displaying gifs (will be active with RotationFlag = 1)
   * SingleGalleyTime, ??, the time of displaying each gif
   * PowerOnChannelId, Channel id, device will display the channel when it powers on
   * CurClockId, Face id, the running’s face id
   * GalleryShowTimeFlag, 0|1, 1=it will display time at right-top
   * Time24Flag, 0|1, 1=24 hour mode; 0=12 hour mode
   * TemperatureMode, 0|1, 0=Celsius; 1=Fahrenheit
   * GyrateAngle, 0-3, the rotation angle 0=normal; 1=90; 2=180; 3=270
   * MirrorFlag, 0|1, 1=screen is mirrored
   * LightSwitch, 0|1, 1=screen on: 0=screen off
   */
  fun readConfiguration(): Map<String, Any> =
    genericPostCommand(GET_CONFIGURATION)?.parameters ?: mapOf()

  /*
   * Minute: 0-99, as number
   * Second: 0-59, as number
   * Status: 0|1, 1=start; 0=stop
   */
  fun setTimer(minutes: Int, seconds: Int, startBit: Boolean) =
    genericPostCommand(
      TOOL_TIMER,
      Pair("Minute", minutes),
      Pair("Second", seconds),
      Pair("Status", startBit.toBitNumber())
    )

  // Status, 0-1, 0=stop; 1=start; 2=reset
  fun setStopwatch(status: Int) =
    genericPostCommand(TOOL_STOPWATCH, Pair("Status", status))

  private fun genericPostCommand(
    commandType: CommandType,
    vararg parameters: Pair<String, Any>
  ): CommandResponse? {
    logger.debug("Sending command {} with {}", commandType, parameters)

    val rawResponse =
      webclient
        .post()
        .uri("/post")
        .contentType(APPLICATION_JSON)
        .body(BodyInserters.fromValue(Command(commandType, *parameters)))
        .retrieve()
        .toEntity(String::class.java)
        .block()
        ?.body

    logger.debug("Response for {}: {}", commandType, rawResponse)

    // pixoo will always answer with text/html, although it's formatted like json.
    // Hence, we do mapping manually.
    return mapper.readValue(rawResponse, CommandResponse::class.java)
  }

  private fun Boolean.toBitNumber() = if (this) 1 else 0
}
