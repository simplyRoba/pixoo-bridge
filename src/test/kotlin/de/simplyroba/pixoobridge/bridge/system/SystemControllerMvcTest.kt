package de.simplyroba.pixoobridge.bridge.system

import de.simplyroba.pixoobridge.AbstractMvcTest
import de.simplyroba.pixoobridge.client.PixooClient
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.verifyNoInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@TestPropertySource(properties = ["pixoo.health.forward=false"])
class SystemControllerMvcTest(
    @param:Autowired val mockMvc: MockMvc,
    @param:Mock val pixooClient: PixooClient,
) : AbstractMvcTest() {

  @Test
  fun `should skip call to device when property set`() {
    mockMvc.perform(get("/health/check"))
    verifyNoInteractions(pixooClient)
  }
}
