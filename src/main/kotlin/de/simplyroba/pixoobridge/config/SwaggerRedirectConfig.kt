package de.simplyroba.pixoobridge.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class SwaggerRedirectConfig : WebMvcConfigurer {

  override fun addViewControllers(registry: ViewControllerRegistry) {
    registry
      .addRedirectViewController("/", "/swagger-ui/index.html")
      .setStatusCode(HttpStatus.PERMANENT_REDIRECT)
  }
}
