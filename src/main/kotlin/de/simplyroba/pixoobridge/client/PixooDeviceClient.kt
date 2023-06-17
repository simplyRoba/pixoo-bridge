package de.simplyroba.pixoobridge.client

import com.fasterxml.jackson.databind.ObjectMapper
import de.simplyroba.pixoobridge.client.CommandType.*
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

    /**
     * OnOff, 0|1, 1=on; 0=off
     */
    fun switchDisplay(onOffFlag: Int) =
        genericPostCommand(SET_DISPLAY_ON_OFF, Pair("OnOff", onOffFlag))

    /**
     * Brightness, 0-100, percentage of brightness.
     */
    fun setDisplayBrightness(percentageValue: Int) =
        genericPostCommand(SET_DISPLAY_BRIGHTNESS, Pair("Brightness", percentageValue))

    /**
     * Mode, 0-3, the rotation angle 0=normal; 1=90; 2=180; 3=270
     */
    fun setDisplayRotation(rotationFlag: Int) =
        genericPostCommand(SET_DISPLAY_ROTATION, Pair("Mode", rotationFlag))


    /**
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

    /**
     * Utc, example=1672416000, Unix epoch timestamps in seconds
     */
    fun setSystemTimeInUtc(unixTimeInSeconds: Long) =
        genericPostCommand(SET_SYSTEM_TIME, Pair("Utc", unixTimeInSeconds))

    /**
     * Mode, 0|1, 1=24 hour mode; 0=12 hour mode
     */
    fun setSystemTimeMode(twentyFourModeFlag: Int) =
        genericPostCommand(SET_SYSTEM_TIME_MODE, Pair("Mode", twentyFourModeFlag))

    /**
     * TimeZoneValue, example=GMT-5, offset in GMT+/- or GMT0 format
     */
    fun setSystemTimeOffset(timeZone: String) =
        genericPostCommand(SET_SYSTEM_TIME_ZONE, Pair("TimeZoneValue", timeZone))

    /**
     * Returns:
     * UTCTime, example=1672416000, Unix epoch timestamps in seconds
     * LocalTime, example=2022-03-14 03:40:28, time in yyyy-MM-dd HH:mm:ss format
     */
    fun getSystemTime(): Map<String, Any> =
        genericPostCommand(GET_SYSTEM_TIME)?.parameters ?: mapOf()


    private fun genericPostCommand(
        commandType: CommandType,
        vararg parameters: Pair<String, Any>
    ): CommandResponse? {
        logger.debug("Sending command {} with {}", commandType, parameters)

        val rawResponse = webclient.post().uri("/post")
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(Command(commandType, *parameters)))
                .retrieve()
                .toEntity(String::class.java)
                .block()?.body

        logger.debug("Response for {}: {}", commandType, rawResponse)

        // pixoo will always answer with text/html, although it's formatted like json.
        // Hence, we do mapping manually.
        return mapper.readValue(rawResponse, CommandResponse::class.java)
    }
}

