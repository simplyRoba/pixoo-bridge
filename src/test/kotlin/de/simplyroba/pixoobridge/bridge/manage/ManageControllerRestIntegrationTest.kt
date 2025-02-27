package de.simplyroba.pixoobridge.bridge.manage

import com.github.tomakehurst.wiremock.client.WireMock.*
import de.simplyroba.pixoobridge.AbstractRestIntegrationTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

// Test happy cases as complete e2e integration tests
class ManageControllerRestIntegrationTest : AbstractRestIntegrationTest() {

  @ParameterizedTest
  @CsvSource(value = ["on:1", "off:0"], delimiter = ':')
  fun `should manage display`(input: String, expected: String) {
    doPostCallExpectingSuccess("/manage/display/$input")
    verifyCommandSent("""{"Command":"Channel/OnOffScreen", "OnOff": $expected}""")
  }

  @Test
  fun `should set brightness`() {
    val brightnessValue = 65
    doPostCallExpectingSuccess("/manage/display/brightness/$brightnessValue")
    verifyCommandSent("""{"Command":"Channel/SetBrightness", "Brightness": $brightnessValue}""")
  }

  @ParameterizedTest
  @CsvSource(value = ["on:1", "off:0"], delimiter = ':')
  fun `should overclock display brightness`(input: String, expected: String) {
    doPostCallExpectingSuccess("/manage/display/brightness/overclock/$input")
    verifyCommandSent("""{"Command":"Device/SetHighLightMode", "Mode": $expected}""")
  }

  @ParameterizedTest
  @CsvSource(value = ["0:0", "90:1", "180:2", "270:3"], delimiter = ':')
  fun `should rotate display `(input: String, expected: String) {
    doPostCallExpectingSuccess("/manage/display/rotation/$input")
    verifyCommandSent("""{"Command":"Device/SetScreenRotationAngle", "Mode": $expected}""")
  }

  @ParameterizedTest
  @CsvSource(value = ["off:0", "on:1"], delimiter = ':')
  fun `should mirror display `(input: String, expected: String) {
    doPostCallExpectingSuccess("/manage/display/mirror/$input")
    verifyCommandSent("""{"Command":"Device/SetMirrorMode", "Mode": $expected}""")
  }

  @Test
  fun `should set display white balance`() {
    val redValue = 75
    val greenValue = 3
    val blueValue = 53
    doPostCallWithBodyExpectingSuccess(
      "/manage/display/white-balance",
      """
        {
          "red": $redValue,
          "green": $greenValue,
          "blue": $blueValue
        }
        """
        .trimIndent(),
    )
    verifyCommandSent(
      """
        {
          "Command":"Device/SetWhiteBalance", 
          "RValue": $redValue,
          "GValue": $greenValue,
          "BValue": $blueValue
        }
        """
        .trimIndent()
    )
  }

  @Test
  fun `should set system time`() {
    doPostCallExpectingSuccess("/manage/time")
    verifyCommandSent("""{"Command":"Device/SetUTC", "Utc": "#{json-unit.any-number}"}""")
  }

  @ParameterizedTest
  @CsvSource(value = ["12h:0", "24h:1"], delimiter = ':')
  fun `should set time mode`(input: String, expected: String) {
    doPostCallExpectingSuccess("/manage/time/mode/$input")
    verifyCommandSent("""{"Command":"Device/SetTime24Flag", "Mode": $expected}""")
  }

  @Test
  fun `should set system time offset`() {
    val offsetValue = -1
    doPostCallExpectingSuccess("/manage/time/offset/$offsetValue")
    verifyCommandSent("""{"Command":"Sys/TimeZone", "TimeZoneValue": "GMT$offsetValue"}""")
  }

  @Test
  fun `should return device time`() {
    stubFor(
      post(urlEqualTo("/post"))
        .willReturn(
          aResponse()
            .withHeader("Content-Type", "text/html")
            .withBody(
              """
                {
                  "error_code":0,
                  "UTCTime":1647200428,
                  "LocalTime":"2022-03-14 03:40:28"
                }
                    """
                .trimIndent()
            )
        )
    )

    doGetCallExpectingSuccess("/manage/time")
      .expectBody()
      .json(
        """
          {
            "utcTime":"2022-03-13T19:40:28",
            "localTime":"2022-03-14T03:40:28"
          }
        """
          .trimIndent()
      )
    verifyCommandSent("""{"Command":"Device/GetDeviceTime"}""")
  }

  @Test
  fun `should set weather location`() {
    val longitude = "-56.34"
    val latitude = "23.89"
    doPostCallWithBodyExpectingSuccess(
      "/manage/weather/location",
      """
        {
          "longitude": "$longitude",
          "latitude": "$latitude"
        }
        """
        .trimIndent(),
    )
    verifyCommandSent(
      """
        {
          "Command":"Sys/LogAndLat", 
          "Longitude": "$longitude",
          "Latitude": "$latitude"
        }
        """
        .trimIndent()
    )
  }

  @ParameterizedTest
  @CsvSource(value = ["fahrenheit:1", "celsius:0"], delimiter = ':')
  fun `should set temperature unit`(input: String, expected: String) {
    doPostCallExpectingSuccess("/manage/weather/temperature-unit/$input")
    verifyCommandSent("""{"Command":"Device/SetDisTempMode", "Mode": $expected}""")
  }

  @Test
  fun `should return weather information`() {
    stubFor(
      post(urlEqualTo("/post"))
        .willReturn(
          aResponse()
            .withHeader("Content-Type", "text/html")
            .withBody(
              """
                {
                  "error_code":0,
                  "Weather": "Cloudy",
                  "CurTemp": 33.68,
                  "MinTemp": 31.85,
                  "MaxTemp": 33.68,
                  "Pressure": 1006,
                  "Humidity": 50,
                  "Visibility": 10000,
                  "WindSpeed": 2.54
                }
                    """
                .trimIndent()
            )
        )
    )

    doGetCallExpectingSuccess("manage/weather")
      .expectBody()
      .json(
        """
          {
            "weatherString": "Cloudy",
            "currentTemperature": 33.68,
            "minimalTemperature": 31.85,
            "maximalTemperature": 33.68,
            "pressure": 1006,
            "humidity": 50,
            "windSpeed": 2.54
          }
        """
          .trimIndent()
      )
    verifyCommandSent("""{"Command":"Device/GetWeatherInfo"}""")
  }

  @Test
  fun `should return all device settings`() {
    stubFor(
      post(urlEqualTo("/post"))
        .willReturn(
          aResponse()
            .withHeader("Content-Type", "text/html")
            .withBody(
              """
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
                    """
                .trimIndent()
            )
        )
    )

    doGetCallExpectingSuccess("manage/settings")
      .expectBody()
      .json(
        """
          {
            "displayOn":true,
            "brightness":100,
            "timeMode":"TWENTY_FOUR",
            "rotationAngle":90,
            "mirrored":true,
            "temperatureUnit":"FAHRENHEIT",
            "currentClockId":1
          }
        """
          .trimIndent()
      )
    verifyCommandSent("""{"Command":"Channel/GetAllConf"}""")
  }
}
