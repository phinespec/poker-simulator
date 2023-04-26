package com.phinespec.pokersim.data.remote


data class HandStrengthResponseDto(
    val players: List<Player>,
    val winners: List<Winner>
)