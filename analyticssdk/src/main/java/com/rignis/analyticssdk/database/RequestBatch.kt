package com.rignis.analyticssdk.database

internal data class RequestBatch(val timeStamp: Long, val events: List<EventEntity>)