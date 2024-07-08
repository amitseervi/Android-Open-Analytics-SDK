package com.rignis.analyticssdk.data.remote.dto

data class SyncRequestDto(
    val event: String,
    val params: Map<String, String>,
    val clientTimeStamp: Long,
)
