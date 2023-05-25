package com.phinespec.pokersim.model


data class Player(
    val id: Int,
    val holeCards: Pair<PlayingCard, PlayingCard>,
    val handStrength: String = ""
)