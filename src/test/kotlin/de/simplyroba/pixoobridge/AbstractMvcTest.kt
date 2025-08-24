package de.simplyroba.pixoobridge

import de.simplyroba.pixoobridge.client.PixooClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@AutoConfigureMockMvc
abstract class AbstractMvcTest {

  @Autowired lateinit var mockMvc: MockMvc

  @MockitoBean lateinit var pixooClient: PixooClient
}
