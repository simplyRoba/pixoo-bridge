package de.simplyroba.pixoobridge.client

import com.fasterxml.jackson.databind.ObjectMapper
import de.simplyroba.pixoobridge.client.CommandType.*
import de.simplyroba.pixoobridge.client.model.Command
import de.simplyroba.pixoobridge.client.model.CommandResponse
import de.simplyroba.pixoobridge.config.PixooConfig
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@Component
class PixooClient(config: PixooConfig, private val mapper: ObjectMapper) {

  companion object {
    val DEFAULT_TIMEOUT = 10.seconds.toJavaDuration()
  }

  private val logger = LoggerFactory.getLogger(javaClass)

  private val webclient = WebClient.create("http://${config.host}")

  fun healthCheck() {
    logger.debug("Check connectivity")
    webclient
      .get()
      .uri("/get")
      .retrieve()
      .toBodilessEntity()
      .block(DEFAULT_TIMEOUT)
      ?.statusCode
      ?.takeIf { it.is2xxSuccessful } ?: throw IllegalStateException("Health check at pixoo failed")
  }

  fun reboot() {
    genericPostCommand(REBOOT)
  }

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
  fun setTwentyFourHourTimeMode(twentyFourModeEnabledBit: Boolean) =
    genericPostCommand(SET_SYSTEM_TIME_MODE, Pair("Mode", twentyFourModeEnabledBit.toBitNumber()))

  // TimeZoneValue, example=GMT-5, offset in GMT+/- or GMT0 format
  fun setSystemTimeOffset(offset: Int) =
    genericPostCommand(SET_SYSTEM_TIME_ZONE, Pair("TimeZoneValue", "GMT$offset"))

  /*
   * Returns:
   * UTCTime, example=1672416000, Unix epoch timestamps in seconds
   * LocalTime, example=2022-03-14 03:40:28, time in yyyy-MM-dd HH:mm:ss format
   */
  fun getSystemTime() = genericPostCommand(GET_SYSTEM_TIME)

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
  fun setWeatherTemperatureUnitFahrenheit(temperatureUnitBit: Boolean) =
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
  fun readWeatherInformation() = genericPostCommand(GET_WEATHER_INFO)

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
  fun readConfiguration() = genericPostCommand(GET_CONFIGURATION)

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

  // Status, 0|1|2, 0=stop; 1=start; 2=reset
  fun setStopwatch(status: Int) = genericPostCommand(TOOL_STOPWATCH, Pair("Status", status))

  /*
   * RedScore, 0-999, as number
   * BlueScore, 0-999, as number
   */
  fun setScoreBoard(redScore: Int, blueScore: Int) =
    genericPostCommand(TOOL_SCOREBOARD, Pair("RedScore", redScore), Pair("BlueScore", blueScore))

  // NoiseStatus, 0|1, 0=stop; 1=start
  fun setSoundMeter(onBit: Boolean) =
    genericPostCommand(TOOL_SOUND_METER, Pair("NoiseStatus", onBit.toBitNumber()))

  fun getNextPictureId() = genericPostCommand(GET_NEXT_PICTURE_ID)

  /*
   * PicNum, 1-59, number of frames (smaller than 60) in animation
   * PicWidth, 16|32|64, pixel per side
   * PicOffset, 0 - PicNum-1, index of the frame in the animation starting with 0
   * PicID, number, incrementing unique id for the animation starting with 1
   * PicSpeed, number, time each frame is shown in ms
   * PicData, base64 encoded, the picture Base64 encoded RGB data, The RGB data is left to right and up to down
   */
  fun sendAnimation(
    frameCount: Int,
    pixelPerSide: Int,
    frameIndex: Int,
    id: Int,
    animationSpeed: Int,
    data: String
  ) =
    genericPostCommand(
      DRAW_ANIMATION,
      Pair("PicNum", frameCount),
      Pair("PicWidth", pixelPerSide),
      Pair("PicOffset", frameIndex),
      Pair("PicID", id),
      Pair("PicSpeed", animationSpeed),
      Pair("PicData", data)
    )

  /*
   * TextId, 0-20, the text id is unique and will be replaced with the same id
   * x, 0-64, start x position
   * y, 0-64, start y position
   * dir, 0|1, scroll direction of the text 0=left; 1=right
   * font, 0-7, animation font
   * TextWidth, 16-64, the text width
   * TextString, 0-512, the text string in utf8
   * speed, 0,100, the scroll speed if it need scroll, the time (ms) the text move one step
   * color, example=#FFFF00, font color in hex notation
   * align, 1|2|3, horizontal text alignmen, 1=left; 2=middle; 3=right
   */
  fun writeText(
    id: Int,
    x: Int,
    y: Int,
    direction: Int,
    font: Int,
    textWidth: Int,
    text: String,
    speed: Int,
    color: String,
    align: Int
  ) =
    genericPostCommand(
      DRAW_TEXT,
      Pair("TextId", id),
      Pair("x", x),
      Pair("y", y),
      Pair("dir", direction),
      Pair("font", font),
      Pair("TextWidth", textWidth),
      Pair("TextString", text),
      Pair("speed", speed),
      Pair("color", color),
      Pair("align", align)
    )

  fun clearText() {
    genericPostCommand(CLEAR_TEXT)
  }

  private fun genericPostCommand(
    commandType: CommandType,
    vararg parameters: Pair<String, Any>
  ): CommandResponse {
    logger.debug("Sending command {} with {}", commandType, parameters)

    val rawResponse =
      webclient
        .post()
        .uri("/post")
        .contentType(APPLICATION_JSON)
        .body(BodyInserters.fromValue(Command(commandType, *parameters)))
        .retrieve()
        .toEntity(String::class.java)
        .block(DEFAULT_TIMEOUT)
        ?.body

    logger.debug("Response for {}: {}", commandType, rawResponse)

    // pixoo will always answer with text/html, although it's formatted like json.
    // Hence, we do mapping manually.
    val response = mapper.readValue(rawResponse, CommandResponse::class.java)

    if (response.errorCode != 0) throw PixooException("Error with code ${response.errorCode}")

    return response
  }

  private fun Boolean.toBitNumber() = if (this) 1 else 0
}
