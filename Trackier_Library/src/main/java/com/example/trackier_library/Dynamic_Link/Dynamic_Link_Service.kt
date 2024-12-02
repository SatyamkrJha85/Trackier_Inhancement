package com.example.trackier_library.Dynamic_Link

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DynamicLinkService {
    @POST("dynamiclinks/create")
    suspend fun createDynamicLink(@Body config: DynamicLinkConfig): DynamicLinkResponse

    @GET("dynamiclinks/{linkId}")
    suspend fun getDynamicLinkAnalytics(@Path("linkId") linkId: String): DynamicLinkAnalytics
}