package de.simplyroba.pixoobridge.rest.manage

import com.github.tomakehurst.wiremock.client.WireMock.*
import de.simplyroba.pixoobridge.rest.AbstractRestIntegrationTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

// Test happy cases as complete e2e integration tests
class ManageControllerRestIntegrationTest: AbstractRestIntegrationTest() {

    @ParameterizedTest
    @CsvSource(value = ["on:1", "off:0"], delimiter = ':')
    fun `should manage display`(input: String, expected: String) {
        doPostCall("/manage/display/$input")
        verifyCommandSent("""{"Command":"Channel/OnOffScreen", "OnOff": $expected}""")
    }

    @Test
    fun `should set brightness`() {
        val brightnessValue = 65
        doPostCall("/manage/display/brightness/$brightnessValue")
        verifyCommandSent("""{"Command":"Channel/SetBrightness", "Brightness": $brightnessValue}""")
    }

    @ParameterizedTest
    @CsvSource(value = ["disabled:0", "enabled:1"], delimiter = ':')
    fun `should overclock display brightness`(input: String, expected: String) {
        doPostCall("/manage/display/brightness/overclock/$input")
        verifyCommandSent("""{"Command":"Device/SetHighLightMode", "Mode": $expected}""")
    }

    @ParameterizedTest
    @CsvSource(value = ["0:0", "90:1", "180:2", "270:3"], delimiter = ':')
    fun `should rotate display `(input: String, expected: String) {
        doPostCall("/manage/display/rotation/$input")
        verifyCommandSent("""{"Command":"Device/SetScreenRotationAngle", "Mode": $expected}""")
    }

    @ParameterizedTest
    @CsvSource(value = ["disabled:0", "enabled:1"], delimiter = ':')
    fun `should mirror display `(input: String, expected: String) {
        doPostCall("/manage/display/mirror/$input")
        verifyCommandSent("""{"Command":"Device/SetMirrorMode", "Mode": $expected}""")
    }

    @Test
    fun `should set display white balance`() {
        val redValue = 75
        val greenValue = 3
        val blueValue = 53
        doPostCallWithBody("/manage/display/white-balance", """
            {
                "red": $redValue,
                "green": $greenValue,
                "blue": $blueValue
            }
        """.trimIndent())
        verifyCommandSent("""
            {
                "Command":"Device/SetWhiteBalance", 
                "RValue": $redValue,
                "GValue": $greenValue,
                "BValue": $blueValue
            }
        """.trimIndent())
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
        val offsetValue = -1
        doPostCall("/manage/time/offset/$offsetValue")
        verifyCommandSent("""{"Command":"Sys/TimeZone", "TimeZoneValue": "GMT$offsetValue"}""")
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

    @Test
    fun `should set weather location`() {
        val longitude = "-56.34"
        val latitude = "23.89"
        doPostCallWithBody("/manage/weather/location", """
            {
                "longitude": "$longitude",
                "latitude": "$latitude"
            }
        """.trimIndent())
        verifyCommandSent("""
            {
                "Command":"Sys/LogAndLat", 
                "Longitude": "$longitude",
                "Latitude": "$latitude"
            }
        """.trimIndent())
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
}