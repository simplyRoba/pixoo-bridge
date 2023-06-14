package de.simplyroba.pixoobridge.client

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonProperty

data class Command(
    @JsonProperty("command") val commandType: CommandType,
    @JsonAnyGetter val parameters: Map<String, Any>) {

    constructor(commandType: CommandType) : this(commandType, mapOf())
    constructor(commandType: CommandType, vararg parameters: Pair<String, Any>) : this(commandType, mapOf(*parameters))
}