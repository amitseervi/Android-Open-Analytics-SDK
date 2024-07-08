package com.rignis.analyticssdk.database

data class RequestBatch(val timeStamp: Long, val events: List<EventEntity>)