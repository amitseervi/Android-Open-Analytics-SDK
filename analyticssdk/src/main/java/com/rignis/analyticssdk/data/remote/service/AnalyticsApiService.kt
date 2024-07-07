package com.rignis.analyticssdk.data.remote.service

import com.rignis.analyticssdk.data.remote.dto.SyncRequestPayloadDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AnalyticsApiService {
    @POST("/event")
    suspend fun postEvent(@Body data: SyncRequestPayloadDto): Response<Void>
}
