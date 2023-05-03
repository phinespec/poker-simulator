package com.phinespec.pokersim.model


class Player(
    val id: Int,
    val name: String,
    val holeCards: Pair<Card, Card>
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