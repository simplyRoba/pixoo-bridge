package de.simplyroba.pixoobridge.client.model

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonProperty

data class CommandResponse(
  @JsonProperty("error_code") val errorCode: Int,
  //@field workaround for https://github.com/FasterXML/jackson-databind/issues/4508
  @field:JsonAnySetter val parameters: Map<String, Any> = mutableMapOf()
)
