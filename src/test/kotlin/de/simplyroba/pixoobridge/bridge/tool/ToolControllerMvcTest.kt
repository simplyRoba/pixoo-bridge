package de.simplyroba.pixoobridge.bridge.tool

import de.simplyroba.pixoobridge.AbstractMvcTest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.verifyNoInteractions
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

// Test error and validation cases as mock mvc tests for performance
class ToolControllerMvcTest : AbstractMvcTest() {

  @ParameterizedTest
  @CsvSource(value = ["-1:0", "0:-1", "0:61", "100:0"], delimiter = ':')
  fun `should return bad request timer minutes or seconds out of bound`(
    minutes: Int,
    seconds: Int
  ) {
    mockMvc
      .perform(
        post("/tool/timer/start")
          .contentType(MediaType.APPLICATION_JSON)
          .content(
            """
              {
                "minutes": $minutes,
                "seconds": $seconds
              }
            """
              .trimIndent()
          )
      )
      .andExpect(status().isBadRequest)

    verifyNoInteractions(pixooClient)
  }

  @ParameterizedTest
  @ValueSource(strings = ["wrong", "path", "variable", "99"])
  fun `should return not found on wrong stopwatch action`(path: String) {
    mockMvc.perform(post("/tool/stopwatch/$path")).andExpect(status().isNotFound)
  }

  @ParameterizedTest
  @CsvSource(value = ["-1:0", "0:-1", "0:1000", "1000:0"], delimiter = ':')
  fun `should return bad request when scores out of bound`(redScore: Int, blueScore: Int) {
    mockMvc
      .perform(
        post("/tool/scoreboard")
          .contentType(MediaType.APPLICATION_JSON)
          .content(
            """
              {
                "redScore": $redScore,
                "blueScore": $blueScore
              }
            """
              .trimIndent()
          )
      )
      .andExpect(status().isBadRequest)

    verifyNoInteractions(pixooClient)
  }

  @ParameterizedTest
  @ValueSource(strings = ["wrong", "path", "variable", "99"])
  fun `should return not found on wrong sound meter action`(path: String) {
    mockMvc.perform(post("/tool/soundmeter/$path")).andExpect(status().isNotFound)
  }
}
