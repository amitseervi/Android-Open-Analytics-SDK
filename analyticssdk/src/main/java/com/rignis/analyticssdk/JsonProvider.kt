package com.rignis.analyticssdk

import kotlinx.serialization.json.Json

object JsonProvider {
    val json = Json {
        prettyPrint = true
        isLenient = true
    }
}