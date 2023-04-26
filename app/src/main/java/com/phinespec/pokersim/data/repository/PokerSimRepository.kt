package com.phinespec.pokersim.data.repository

import com.phinespec.pokersim.data.remote.HandStrengthApi
import com.phinespec.pokersim.data.remote.HandStrengthResponseDto
import retrofit2.Response
import javax.inject.Inject

class PokerSimRepository @Inject constructor(
    private val api: HandStrengthApi
) {
    suspend fun getHandResults(cc: String, pc: String): Response<HandStrengthResponseDto> {
        return api.getHandResults(cc, pc)
    }
}