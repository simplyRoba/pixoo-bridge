package de.simplyroba.pixoobridge.bridge.sound

import de.simplyroba.pixoobridge.AbstractMvcTest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class SoundControllerMvcTest : AbstractMvcTest() {

  @ParameterizedTest
  @CsvSource(
    // activeTimeInCycle out of bounds
    "99,0,1000",
    "10001,0,1000",
    // offTimeInCycle out of bounds
    "100, -1, 1000",
    "100, 10001, 1000",
    // totalPlayTime out of bounds
    "100,0,99",
    "100,0,100001",
    // totalPlayTime zero
    "100,0,0",
    // multiple invalid
    "-1,-1,-1",
    "10001,10001,100001",
  )
  fun `should return bad request on invalid values`(active: Int, off: Int, total: Int) {
    mockMvc
      .perform(
        post("/sound/play")
          .contentType(MediaType.APPLICATION_JSON)
          .content(
            """
            {
                "activeTimeInCycle": $active,
                "offTimeInCycle": $off,
                "totalPlayTime": $total
            }
            """
              .trimIndent()
          )
      )
      .andExpect(MockMvcResultMatchers.status().isBadRequest)
    Mockito.verifyNoInteractions(pixooClient)
  }
}
