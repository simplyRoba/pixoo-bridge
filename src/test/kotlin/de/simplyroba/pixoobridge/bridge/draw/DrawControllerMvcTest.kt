package de.simplyroba.pixoobridge.bridge.draw

import de.simplyroba.pixoobridge.AbstractMvcTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
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
  fun `should return bad request on rgb value out of bound on fill`(
    red: Int,
    green: Int,
    blue: Int
  ) {
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

  @ParameterizedTest
  @ValueSource(ints = [-1, 21])
  fun `should return bad request on text id out of bound`(textId: Int) {
    mockMvc
      .perform(
        post("/draw/text")
          .contentType(MediaType.APPLICATION_JSON)
          .content(
            """
              {
                "id": $textId,
                "position": {
                  "x":0,
                  "y":40
                },
                "scrollDirection":"LEFT",
                "font":4,
                "textWidth":56,
                "text":"hello, world",
                "scrollSpeed": 10,
                "color": {
                  "red": 255,
                  "green": 255,
                  "blue": 255
                },
                "textAlignment":"LEFT"
              }
              """
              .trimIndent()
          )
      )
      .andExpect(MockMvcResultMatchers.status().isBadRequest)
    Mockito.verifyNoInteractions(pixooClient)
  }

  @ParameterizedTest
  @CsvSource(value = ["-1:0", "0:-1", "65:0", "0:65"], delimiter = ':')
  fun `should return bad request on position  out of bound`(x: Int, y: Int) {
    mockMvc
      .perform(
        post("/draw/text")
          .contentType(MediaType.APPLICATION_JSON)
          .content(
            """
              {
                "id": 4,
                "position": {
                  "x": $x,
                  "y": $y
                },
                "scrollDirection":"LEFT",
                "font":4,
                "textWidth":56,
                "text":"hello, world",
                "scrollSpeed": 10,
                "color": {
                  "red": 255,
                  "green": 255,
                  "blue": 255
                },
                "textAlignment":"MIDDLE"
              }
              """
              .trimIndent()
          )
      )
      .andExpect(MockMvcResultMatchers.status().isBadRequest)
    Mockito.verifyNoInteractions(pixooClient)
  }

  @ParameterizedTest
  @ValueSource(ints = [-1, 8])
  fun `should return bad request on font out of bound`(font: Int) {
    mockMvc
      .perform(
        post("/draw/text")
          .contentType(MediaType.APPLICATION_JSON)
          .content(
            """
              {
                "id": 4,
                "position": {
                  "x":0,
                  "y":40
                },
                "scrollDirection":"LEFT",
                "font": $font,
                "textWidth":56,
                "text":"hello, world",
                "scrollSpeed": 10,
                "color": {
                  "red": 255,
                  "green": 255,
                  "blue": 255
                },
                "textAlignment":"RIGHT"
              }
              """
              .trimIndent()
          )
      )
      .andExpect(MockMvcResultMatchers.status().isBadRequest)
    Mockito.verifyNoInteractions(pixooClient)
  }

  @ParameterizedTest
  @ValueSource(ints = [15, 65])
  fun `should return bad request on text width out of bound`(textWidth: Int) {
    mockMvc
      .perform(
        post("/draw/text")
          .contentType(MediaType.APPLICATION_JSON)
          .content(
            """
              {
                "id": 4,
                "position": {
                  "x":0,
                  "y":40
                },
                "scrollDirection":"RIGHT",
                "font": 4,
                "textWidth": $textWidth,
                "text":"hello, world",
                "scrollSpeed": 10,
                "color": {
                  "red": 255,
                  "green": 255,
                  "blue": 255
                },
                "textAlignment":"LEFT"
              }
              """
              .trimIndent()
          )
      )
      .andExpect(MockMvcResultMatchers.status().isBadRequest)
    Mockito.verifyNoInteractions(pixooClient)
  }

  @Test
  fun `should return bad request on to long text`() {
    mockMvc
      .perform(
        post("/draw/text")
          .contentType(MediaType.APPLICATION_JSON)
          .content(
            """
              {
                "id": 4,
                "position": {
                  "x":0,
                  "y":40
                },
                "scrollDirection":"LEFT",
                "font": 4,
                "textWidth":56,
                "text":"hello, world",
                "scrollSpeed": 10,
                "color": {
                  "red": 255,
                  "green": 255,
                  "blue": 255
                },
                "textAlignment":"LEFT"
              }
              """
              .trimIndent()
          )
      )
      .andExpect(MockMvcResultMatchers.status().isBadRequest)
    Mockito.verifyNoInteractions(pixooClient)
  }

  @ParameterizedTest
  @ValueSource(ints = [-1, 101])
  fun `should return bad request on speed out of bound`(speed: Int) {
    mockMvc
      .perform(
        post("/draw/text")
          .contentType(MediaType.APPLICATION_JSON)
          .content(
            """
              {
                "id": 4,
                "position": {
                  "x":0,
                  "y":40
                },
                "scrollDirection":"RIGHT",
                "font": 4,
                "textWidth":56,
                "text":"hello, world",
                "scrollSpeed": $speed,
                "color": {
                  "red": 255,
                  "green": 255,
                  "blue": 255
                },
                "textAlignment":"LEFT"
              }
              """
              .trimIndent()
          )
      )
      .andExpect(MockMvcResultMatchers.status().isBadRequest)
    Mockito.verifyNoInteractions(pixooClient)
  }

  @ParameterizedTest
  @CsvSource(
    value = ["-1:0:0", "0:-1:0", "0:0:-1", "256:0:0", "0:256:0", "0:0:256"],
    delimiter = ':'
  )
  fun `should return bad request on rgb value out of bound on text`(
    red: Int,
    green: Int,
    blue: Int
  ) {
    mockMvc
      .perform(
        post("/draw/text")
          .contentType(MediaType.APPLICATION_JSON)
          .content(
            """
              {
                "id":4,
                "position": {
                  "x":0,
                  "y":40
                },
                "scrollDirection":"LEFT",
                "font":4,
                "textWidth":48,
                "text":"hello, world",
                "scrollSpeed": 10,
                "color": {
                  "red": $red,
                  "green": $green,
                  "blue": $blue
                },
                "textAlignment":"MIDDLE"
              }
              """
              .trimIndent()
          )
      )
      .andExpect(MockMvcResultMatchers.status().isBadRequest)
    Mockito.verifyNoInteractions(pixooClient)
  }
}
