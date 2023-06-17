package de.simplyroba.pixoobridge.client

import de.simplyroba.pixoobridge.client.CommandType.ON_OFF_SCREEN
import de.simplyroba.pixoobridge.config.PixooConfig
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@Component
class PixooDeviceClient(private val config: PixooConfig) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val webclient = WebClient.create("http://${config.host}")

    fun switchDisplay(on: Boolean) {
        genericPostCommand(ON_OFF_SCREEN, Pair("OnOff", on.toInt()))
    }

    private fun genericPostCommand(
        commandType: CommandType,
        parameters: Pair<String, Int>
    ): CommandResponse? {
        logger.info("Sending command $commandType with $parameters")

        return webclient.post().uri("/post")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .body(BodyInserters.fromValue(Command(commandType, parameters)))
            .retrieve()
            .toEntity(CommandResponse::class.java)
            .block()?.body
    }

    private fun Boolean.toInt() = if (this) 1 else 0
}

