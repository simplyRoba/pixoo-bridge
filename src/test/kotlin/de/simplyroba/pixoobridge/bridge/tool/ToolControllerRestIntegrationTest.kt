package de.simplyroba.pixoobridge.bridge.tool

import de.simplyroba.pixoobridge.AbstractRestIntegrationTest
import org.junit.jupiter.api.Test

// Test happy cases as complete e2e integration tests
class ToolControllerRestIntegrationTest : AbstractRestIntegrationTest() {

  @Test
  fun `should start timer with given time`() {
    val minutes = 5
    val seconds = 0
    doPostCallWithBodyExpectingSuccess(
      "/tool/timer/start",
      """
        {
          "minutes": $minutes,
          "seconds": $seconds
        }
        """
        .trimIndent()
    )
    verifyCommandSent(
      """{"Command":"Tools/SetTimer", "Minute":$minutes, "Second":$seconds, "Status":1 }"""
    )
  }

  @Test
  fun `should stop timer`() {
    doPostCallExpectingSuccess("/tool/timer/stop")
    verifyCommandSent("""{"Command":"Tools/SetTimer", "Minute":0, "Second":0, "Status":0 }""")
  }

  @Test
  fun `should stop stopwatch`() {
    doPostCallExpectingSuccess("/tool/stopwatch/stop")
    verifyCommandSent("""{"Command":"Tools/SetStopWatch", "Status":0 }""")
  }

  @Test
  fun `should start stopwatch`() {
    doPostCallExpectingSuccess("/tool/stopwatch/start")
    verifyCommandSent("""{"Command":"Tools/SetStopWatch", "Status":1 }""")
  }

  @Test
  fun `should reset stopwatch`() {
    doPostCallExpectingSuccess("/tool/stopwatch/reset")
    verifyCommandSent("""{"Command":"Tools/SetStopWatch", "Status":2 }""")
  }

  @Test
  fun `should set scoreboard to given values`() {
    val red = 5
    val blue = 0
    doPostCallWithBodyExpectingSuccess(
      "/tool/scoreboard",
      """
        {
          "redScore": $red,
          "blueScore": $blue
        }
        """
        .trimIndent()
    )
    verifyCommandSent("""{"Command":"Tools/SetScoreBoard", "BlueScore":$blue, "RedScore":$red }""")
  }

  @Test
  fun `should start sound meter`() {
    doPostCallExpectingSuccess("/tool/soundmeter/start")
    verifyCommandSent("""{"Command":"Tools/SetNoiseStatus", "NoiseStatus":1 }""")
  }

  @Test
  fun `should stop sound meter`() {
    doPostCallExpectingSuccess("/tool/soundmeter/stop")
    verifyCommandSent("""{"Command":"Tools/SetNoiseStatus", "NoiseStatus":0 }""")
  }
}
