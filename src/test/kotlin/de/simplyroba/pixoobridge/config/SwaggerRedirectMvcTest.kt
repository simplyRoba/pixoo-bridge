package de.simplyroba.pixoobridge.config

import de.simplyroba.pixoobridge.AbstractMvcTest
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get

class SwaggerRedirectMvcTest : AbstractMvcTest() {

  @Test
  fun `root path should permanently redirect to swagger-ui`() {
    mockMvc.get("/").andExpect {
      status { isPermanentRedirect() }
      redirectedUrl("/swagger-ui/index.html")
    }
  }
}
