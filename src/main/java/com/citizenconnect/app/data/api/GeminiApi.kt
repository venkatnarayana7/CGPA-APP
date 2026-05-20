package com.citizenconnect.app.data.api

import com.citizenconnect.app.data.model.GeminiRequest
import com.citizenconnect.app.data.model.GeminiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GeminiApi {
    // Switched to v1beta as it's often required for Flash 1.5 on free keys
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}
