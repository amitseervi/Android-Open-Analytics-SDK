package com.rignis.analyticssdk.network

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

internal interface ApiService {
    @POST("/event")
    fun postEvent(@Body data: SyncRequestPayloadDto): Call<Response<JsonObject>>
}