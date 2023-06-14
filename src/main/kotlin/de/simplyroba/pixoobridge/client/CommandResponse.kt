package de.simplyroba.pixoobridge.client

import com.fasterxml.jackson.annotation.JsonProperty

data class CommandResponse(@JsonProperty("error_code") val errorCode: Int)
