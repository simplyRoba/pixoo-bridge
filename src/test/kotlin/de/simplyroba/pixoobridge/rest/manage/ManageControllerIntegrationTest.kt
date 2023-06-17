package de.simplyroba.pixoobridge.rest.manage

import com.github.tomakehurst.wiremock.client.WireMock.*
import de.simplyroba.pixoobridge.rest.AbstractRestIntegrationTest
import org.junit.jupiter.api.Test

class ManageControllerIntegrationTest: AbstractRestIntegrationTest() {

    @Test
    fun `should turn display on`() {
        doPostCall("/manage/display/on")
        verifyCommandSent("""{"Command":"Channel/OnOffScreen", "OnOff": 1}""")
    }

    @Test
    fun `should turn display off`() {
        doPostCall("/manage/display/off")
        verifyCommandSent("""{"Command":"Channel/OnOffScreen", "OnOff": 0}""")
    }

    @Test
    fun `should set brightness`() {
        doPostCall("/manage/display/brightness/65")
        verifyCommandSent("""{"Command":"Channel/SetBrightness", "Brightness": 65}""")
    }

    @Test
    fun `should return all settings`() {
        stubFor(
            post(urlEqualTo("/post")).willReturn(
                aResponse()
                    .withHeader("Content-Type", "text/html")
                    .withBody("""
                        {
                            "error_code":0,
                            "Brightness":100,
                            "RotationFlag":1,
                            "ClockTime":60,
                            "GalleryTime":60,
                            "SingleGalleyTime":5,
                            "PowerOnChannelId":1,
                            "GalleryShowTimeFlag":1,
                            "CurClockId":1,
                            "Time24Flag":1,
                            "TemperatureMode":1,
                            "GyrateAngle":1,
                            "MirrorFlag":1,
                            "LightSwitch":1
                        }
                    """.trimIndent())
            )
        )

        doGetCall("manage/settings/all").expectBody().json("""
            {
                "LightSwitch":1,
                "MirrorFlag":1,
                "GyrateAngle":1,
                "TemperatureMode":1,
                "Time24Flag":1,
                "CurClockId":1,
                "GalleryShowTimeFlag":1,
                "PowerOnChannelId":1,
                "SingleGalleyTime":5,
                "GalleryTime":60,
                "ClockTime":60,
                "RotationFlag":1,
                "Brightness":100
            }
        """.trimIndent())
        verifyCommandSent("{\"Command\":\"Channel/GetAllConf\"}")
    }
}