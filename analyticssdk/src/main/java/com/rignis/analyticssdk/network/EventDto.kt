package com.rignis.analyticssdk.network

data class EventDto(
    val name: String,
    val params: Map<String, String>,
    val clientTimeStamp: Long
)