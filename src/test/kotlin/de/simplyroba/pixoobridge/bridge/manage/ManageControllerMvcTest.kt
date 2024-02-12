package de.simplyroba.pixoobridge.bridge.manage

import de.simplyroba.pixoobridge.AbstractMvcTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.verifyNoInteractions
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

// Test error and validation cases as mock mvc tests for performance
class ManageControllerMvcTest : AbstractMvcTest() {

  @ParameterizedTest
  @ValueSource(strings = ["wrong", "path", "variable", "99"])
  fun `should return bad request on wrong display control action`(path: String) {
    mockMvc.perform(post("/manage/display/$path")).andExpect(status().isBadRequest)
    verifyNoInteractions(pixooClient)
  }

  @ParameterizedTest
  @ValueSource(ints = [101, -1])
  fun `should return bad request on brightness out of bound`(input: Int) {
    mockMvc.perform(post("/manage/display/brightness/$input")).andExpect(status().isBadRequest)
    verifyNoInteractions(pixooClient)
  }

  @ParameterizedTest
  @ValueSource(strings = ["wrong", "path", "variable", "99"])
  fun `should return bad request on wrong brightness overclock action`(path: String) {
    mockMvc.perform(post("/manage/display/brightness/overclock/$path")).andExpect(status().isBadRequest)
    verifyNoInteractions(pixooClient)
  }

  @Test
  fun `should return bad request on false rotation degree`() {
    mockMvc.perform(post("/manage/display/rotation/91")).andExpect(status().isBadRequest)
    verifyNoInteractions(pixooClient)
  }

  @ParameterizedTest
  @ValueSource(strings = ["wrong", "path", "variable", "99"])
  fun `should return bad request on wrong display mirror action`(path: String) {
    mockMvc.perform(post("/manage/display/mirror/$path")).andExpect(status().isBadRequest)
    verifyNoInteractions(pixooClient)
  }

  @ParameterizedTest
  @CsvSource(
    value = ["101:50:50", "-1:50:50", "50:101:50", "50:-1:50", "50:50:101", "50:50:-1"],
    delimiter = ':'
  )
  fun `should return bad request on white balance out of bound`(red: Int, green: Int, blue: Int) {
    mockMvc
      .perform(
        post("/manage/display/white-balance")
          .contentType(APPLICATION_JSON)
          .content(
            """
              {
                "red": $red,
                "green": $green,
                "blue": $blue
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
  fun `should return bad request on wrong time mode`(path: String) {
    mockMvc.perform(post("/manage/time/mode/$path")).andExpect(status().isBadRequest)
    verifyNoInteractions(pixooClient)
  }

  @ParameterizedTest
  @ValueSource(strings = ["-13", "15"])
  fun `should return bad request on time offset out of bound`(path: String) {
    mockMvc.perform(post("/manage/time/offset/$path")).andExpect(status().isBadRequest)
    verifyNoInteractions(pixooClient)
  }

  @ParameterizedTest
  @CsvSource(value = ["-181:0", "181:0", "0:-91", "0:91"], delimiter = ':')
  fun `should return bad request on weather location out of bound`(
    longitude: String,
    latitude: String
  ) {
    mockMvc
      .perform(
        post("/manage/weather/location")
          .contentType(APPLICATION_JSON)
          .content(
            """
              {
                "longitude": "$longitude",
                "latitude": "$latitude"
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
  fun `should return bad request on wrong temperature unit`(path: String) {
    mockMvc.perform(post("/manage/weather/temperature-unit/$path")).andExpect(status().isBadRequest)
    verifyNoInteractions(pixooClient)
  }
}
