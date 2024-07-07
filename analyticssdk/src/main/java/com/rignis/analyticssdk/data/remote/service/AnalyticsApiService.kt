package com.rignis.analyticssdk.data.remote.service

import com.rignis.analyticssdk.data.remote.dto.SyncRequestPayloadDto

interface AnalyticsApiService {
    fun postEvent(data: SyncRequestPayloadDto)
}
