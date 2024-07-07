package com.rignis.analyticssdk.utils

import kotlinx.serialization.json.Json

object JsonProvider {
    val json by lazy {
        Json {
            prettyPrint = true
            allowStructuredMapKeys = true
        }
    }
}
