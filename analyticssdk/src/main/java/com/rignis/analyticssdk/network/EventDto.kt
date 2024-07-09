package com.rignis.analyticssdk.network

internal data class EventDto(
    val name: String,
    val params: Map<String, String>,
    val clientTimeStamp: Long
)