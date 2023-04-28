package com.phinespec.pokersim.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HandStrengthApi {

    @GET("winner/texas_holdem")
    suspend fun getHandResults(
        @Query("cc") cc: String,
        @Query("pc[]") pc: List<String>,
    ): Response<HandStrengthResponseDto>
}