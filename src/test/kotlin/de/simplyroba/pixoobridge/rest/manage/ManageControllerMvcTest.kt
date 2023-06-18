package de.simplyroba.pixoobridge.rest.manage

import de.simplyroba.pixoobridge.rest.AbstractMvcTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ManageControllerMvcTest: AbstractMvcTest() {

    @ParameterizedTest
    @ValueSource(ints = [101, 200, -1, -50])
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
    @ValueSource(ints = [101, 123, -1, -24])
    fun `should return bad request on red in white balance out of bound`(input: Int) {
        mockMvc.perform(post("/manage/display/white-balance")
            .contentType(APPLICATION_JSON)
            .content("""
                {
                    "red": $input,
                    "green": 50,
                    "blue": 50
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