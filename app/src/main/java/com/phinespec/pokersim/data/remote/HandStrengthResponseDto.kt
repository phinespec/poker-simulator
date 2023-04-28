package com.phinespec.pokersim.data.remote


data class HandStrengthResponseDto(
    val players: List<PlayerDto>,
    val winners: List<Winner>
)