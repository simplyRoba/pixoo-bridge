package de.simplyroba.pixoobridge.rest.manage

import de.simplyroba.pixoobridge.rest.AbstractMvcTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ManageControllerMvcTest: AbstractMvcTest() {

    @ParameterizedTest
    @ValueSource(ints = [101, -1])
    fun `should return bad request on brightness out of bound`(input: Int) {
        mockMvc.perform(post("/manage/display/brightness/$input"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return bad request on false rotation degree`() {
        mockMvc.perform(post("/manage/display/rotation/91"))
            .andExpect(status().isBadRequest)
    }

    @ParameterizedTest
    @CsvSource(value = ["101:50:50", "-1:50:50", "50:101:50", "50:-1:50", "50:50:101", "50:50:-1"], delimiter = ':')
    fun `should return bad request on white balance out of bound`(red: Int, green: Int, blue: Int) {
        mockMvc.perform(post("/manage/display/white-balance")
            .contentType(APPLICATION_JSON)
            .content("""
                {
                    "red": $red,
                    "green": $green,
                    "blue": $blue
                }
            """.trimIndent()))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return bad request on to low time offset`() {
        mockMvc.perform(post("/manage/time/offset/-13"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return bad request on to high time offset`() {
        mockMvc.perform(post("/manage/time/offset/15"))
            .andExpect(status().isBadRequest)
    }
}