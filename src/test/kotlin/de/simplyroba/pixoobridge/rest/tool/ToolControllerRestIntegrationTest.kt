package de.simplyroba.pixoobridge.rest.tool

import de.simplyroba.pixoobridge.rest.AbstractRestIntegrationTest
import org.junit.jupiter.api.Test

// Test happy cases as complete e2e integration tests
class ToolControllerRestIntegrationTest : AbstractRestIntegrationTest() {

  @Test
  fun `should start timer with given time`() {
    val minutes = 5
    val seconds = 0
    doPostCallWithBody(
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
    doPostCall("/tool/timer/stop")
    verifyCommandSent("""{"Command":"Tools/SetTimer", "Minute":0, "Second":0, "Status":0 }""")
  }

  @Test
  fun `should stop stopwatch`() {
    doPostCall("/tool/stopwatch/stop")
    verifyCommandSent("""{"Command":"Tools/SetStopWatch", "Status":0 }""")
  }

  @Test
  fun `should start stopwatch`() {
    doPostCall("/tool/stopwatch/start")
    verifyCommandSent("""{"Command":"Tools/SetStopWatch", "Status":1 }""")
  }

  @Test
  fun `should reset stopwatch`() {
    doPostCall("/tool/stopwatch/reset")
    verifyCommandSent("""{"Command":"Tools/SetStopWatch", "Status":2 }""")
  }

  @Test
  fun `should set scoreboard to given values`() {
    val red = 5
    val blue = 0
    doPostCallWithBody(
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
    doPostCall("/tool/soundmeter/start")
    verifyCommandSent("""{"Command":"Tools/SetNoiseStatus", "NoiseStatus":1 }""")
  }

  @Test
  fun `should stop sound meter`() {
    doPostCall("/tool/soundmeter/stop")
    verifyCommandSent("""{"Command":"Tools/SetNoiseStatus", "NoiseStatus":0 }""")
  }
}
