package de.simplyroba.pixoobridge.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("pixoo") data class PixooConfig(val host: String)
