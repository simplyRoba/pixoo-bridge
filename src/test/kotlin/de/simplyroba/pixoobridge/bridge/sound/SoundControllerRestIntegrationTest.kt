package de.simplyroba.pixoobridge.bridge.sound

import de.simplyroba.pixoobridge.AbstractRestIntegrationTest
import org.junit.jupiter.api.Test

class SoundControllerRestIntegrationTest : AbstractRestIntegrationTest() {

  @Test
  fun `should play sound with valid request`() {
    val active = 500
    val off = 200
    val total = 2000
    doPostCallWithBodyExpectingSuccess(
      "/sound/play",
      """
            {
                "activeTimeInCycle": $active,
                "offTimeInCycle": $off,
                "totalPlayTime": $total
            }
            """
        .trimIndent(),
    )
    verifyCommandSent(
      """
            {
                "Command": "Device/PlayBuzzer",
                "ActiveTimeInCycle": $active,
                "OffTimeInCycle": $off,
                "PlayTotalTime": $total
            }
            """
        .trimIndent()
    )
  }
}
