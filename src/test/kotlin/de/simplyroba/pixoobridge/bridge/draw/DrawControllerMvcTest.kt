package de.simplyroba.pixoobridge.bridge.draw

import de.simplyroba.pixoobridge.AbstractMvcTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class DrawControllerMvcTest : AbstractMvcTest() {

  @ParameterizedTest
  @CsvSource(
    value = ["-1:0:0", "0:-1:0", "0:0:-1", "256:0:0", "0:256:0", "0:0:256"],
    delimiter = ':'
  )
  fun `should return bad request on rgb value out of bound`(red: Int, green: Int, blue: Int) {
    mockMvc
      .perform(
        post("/draw/fill")
          .contentType(MediaType.APPLICATION_JSON)
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
      .andExpect(MockMvcResultMatchers.status().isBadRequest)
    Mockito.verifyNoInteractions(pixooClient)
  }

  @Test
  fun `should return bad request on wrong file extension`() {
    mockMvc
      .perform(
        multipart("/draw/upload")
          .file("image", ClassPathResource("images/text.txt").contentAsByteArray)
          .contentType(MediaType.MULTIPART_FORM_DATA)
      )
      .andExpect(MockMvcResultMatchers.status().isBadRequest)
    Mockito.verifyNoInteractions(pixooClient)
  }

  @Test
  fun `should return bad request on wrong file format but with supported extension`() {
    mockMvc
      .perform(
        multipart("/draw/upload")
          .file("image", ClassPathResource("images/text.png").contentAsByteArray)
          .contentType(MediaType.MULTIPART_FORM_DATA)
      )
      .andExpect(MockMvcResultMatchers.status().isBadRequest)
    Mockito.verifyNoInteractions(pixooClient)
  }
}
