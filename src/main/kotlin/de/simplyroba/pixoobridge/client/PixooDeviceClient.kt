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
class PixooDeviceClient(private val config: PixooConfig, private val mapper: ObjectMapper) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val webclient = WebClient.create("http://${config.host}")

    /**
     * OnOff, 0|1, 1=on; 0=off
     */
    fun switchDisplay(on: Boolean) =
        genericPostCommand(ON_OFF_SCREEN, Pair("OnOff", on.toInt()))

    /**
     * Brightness, 0-100, percentage of brightness.
     */
    fun setBrightness(percentageValue: Int) =
        genericPostCommand(SET_BRIGHTNESS, Pair("Brightness", percentageValue))

    /**
     * Returns:
     * Brightness, 0-100, the system brightness
     * RotationFlag, 0|1, 1=it will switch to display faces and gifs
     * ClockTime, ??, the time of displaying faces (will be active with RotationFlag = 1)
     * GalleryTime, ??, the time of displaying gifs (will be active with RotationFlag = 1)
     * SingleGalleyTime, ??, the time of displaying each gif
     * PowerOnChannelId, Channel Id, device will display the channel when it powers on
     * CurClockId, Face Id, the running’s face id
     * GalleryShowTimeFlag, 0|1, 1=it will display time at right-top
     * Time24Flag, 0|1, 1=24 hour mode; 0=12 hour mode
     * TemperatureMode, 0|1, 0=Celsius; 1=Fahrenheit
     * GyrateAngle, 0-3, the rotation angle 0=normal; 1=90; 2=180; 3=270
     * MirrorFlag, 0|1, 1=screen is mirrored
     * LightSwitch, 0|1, 1=screen on: 0=screen off
     */
    fun readConfiguration(): Map<String, Any> =
        genericPostCommand(GET_CONFIGURATION)?.parameters ?: mapOf()


    private fun genericPostCommand(
        commandType: CommandType,
        vararg parameters: Pair<String, Int>
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

    private fun Boolean.toInt() = if (this) 1 else 0
}

