package de.simplyroba.pixoobridge.client.model

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonProperty
import de.simplyroba.pixoobridge.client.CommandType

data class Command(
  @get:JsonProperty("Command") val commandType: CommandType,
  @get:JsonAnyGetter val parameters: Map<String, Any>,
) {

  constructor(commandType: CommandType) : this(commandType, mapOf())

  constructor(
    commandType: CommandType,
    vararg parameters: Pair<String, Any>,
  ) : this(commandType, mapOf(*parameters))
}
