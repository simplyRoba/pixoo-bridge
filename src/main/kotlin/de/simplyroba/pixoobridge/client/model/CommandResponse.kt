package de.simplyroba.pixoobridge.client.model

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonProperty

data class CommandResponse(
  @param:JsonProperty("error_code") val errorCode: Int,
  @param:JsonAnySetter val parameters: Map<String, Any> = mutableMapOf(),
)
