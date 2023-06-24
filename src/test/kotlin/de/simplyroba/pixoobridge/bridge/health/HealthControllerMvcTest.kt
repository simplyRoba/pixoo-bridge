package de.simplyroba.pixoobridge.bridge.health

import de.simplyroba.pixoobridge.AbstractMvcTest
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verifyNoInteractions
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@TestPropertySource(properties = ["pixoo.health-forward=false"])
class HealthControllerMvcTest : AbstractMvcTest() {

  @Test
  fun `should skip call to device when property set`() {
    mockMvc.perform(get("/health/check"))
    verifyNoInteractions(pixooClient)
  }
}
