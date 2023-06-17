package de.simplyroba.pixoobridge.rest.manage

import de.simplyroba.pixoobridge.rest.AbstractMvcTest
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ManageControllerMvcTest: AbstractMvcTest() {

    @Test
    fun `should return bad request on more than 100 brightness`() {
        mockMvc.perform(post("/manage/display/brightness/101"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return bad request on negative brightness`() {
        mockMvc.perform(post("/manage/display/brightness/-1"))
            .andExpect(status().isBadRequest)
    }
}