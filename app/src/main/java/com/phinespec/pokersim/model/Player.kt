package com.phinespec.pokersim.model


data class Player(
    val id: Int,
    val name: String,
    val holeCards: Pair<PlayingCard, PlayingCard>,
    val handStrength: String = ""
) {


}

val playerNames = listOf(
    "George",
    "Kosmo",
    "Jerry",
    "Elaine",
    "Bob",
    "Neil",
    "Darren",
    "Mulva"
)