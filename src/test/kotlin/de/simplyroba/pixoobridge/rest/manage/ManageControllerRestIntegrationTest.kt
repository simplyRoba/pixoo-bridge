package de.simplyroba.pixoobridge.rest.manage

import com.github.tomakehurst.wiremock.client.WireMock.*
import de.simplyroba.pixoobridge.rest.AbstractRestIntegrationTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ManageControllerRestIntegrationTest: AbstractRestIntegrationTest() {

    @ParameterizedTest
    @CsvSource(value = ["on:1", "off:0"], delimiter = ':')
    fun `should manage display`(input: String, expected: String) {
        doPostCall("/manage/display/$input")
        verifyCommandSent("""{"Command":"Channel/OnOffScreen", "OnOff": $expected}""")
    }

    @Test
    fun `should set brightness`() {
        doPostCall("/manage/display/brightness/65")
        verifyCommandSent("""{"Command":"Channel/SetBrightness", "Brightness": 65}""")
    }

    @ParameterizedTest
    @CsvSource(value = ["0:0", "90:1", "180:2", "270:3"], delimiter = ':')
    fun `should rotate display `(input: String, expected: String) {
        doPostCall("/manage/display/rotation/$input")
        verifyCommandSent("""{"Command":"Device/SetScreenRotationAngle", "Mode": $expected}""")
    }

    @Test
    fun `should return all device settings`() {
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

        doGetCall("manage/settings").expectBody().json("""
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
        verifyCommandSent("""{"Command":"Channel/GetAllConf"}""")
    }

    @Test
    fun `should set system time`() {
        doPostCall("/manage/time")
        verifyCommandSent("""{"Command":"Device/SetUTC", "Utc": "${"$"}{json-unit.any-number}"}""")
    }

    @ParameterizedTest
    @CsvSource(value = ["12h:0", "24h:1"], delimiter = ':')
    fun `should set time mode`(input: String, expected: String) {
        doPostCall("/manage/time/mode/$input")
        verifyCommandSent("""{"Command":"Device/SetTime24Flag", "Mode": $expected}""")
    }

    @Test
    fun `should set system time zone`() {
        doPostCall("/manage/time/offset/-7")
        verifyCommandSent("""{"Command":"Sys/TimeZone", "TimeZoneValue": "GMT-7"}""")
    }

    @Test
    fun `should return device time`() {
        stubFor(
            post(urlEqualTo("/post")).willReturn(
                aResponse()
                    .withHeader("Content-Type", "text/html")
                    .withBody("""
                        {
                            "error_code":0,
                            "UTCTime":1647200428,
                            "LocalTime":"2022-03-14 03:40:28"
                        }
                    """.trimIndent())
            )
        )

        doGetCall("/manage/time").expectBody().json("""
            {
                "UTCTime":1647200428,
                "LocalTime":"2022-03-14 03:40:28"
            }
        """.trimIndent())
        verifyCommandSent("""{"Command":"Device/GetDeviceTime"}""")
    }
}