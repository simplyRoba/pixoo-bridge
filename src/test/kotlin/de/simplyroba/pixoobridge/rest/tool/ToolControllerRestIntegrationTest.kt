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
}
