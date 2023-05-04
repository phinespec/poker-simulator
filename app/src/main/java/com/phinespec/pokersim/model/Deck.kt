package com.phinespec.pokersim.model

import com.phinespec.pokersim.utils.mapNameToImage

class Deck(val cards: MutableList<PlayingCard> = mutableListOf()) {

    fun initialize() {
        for (suit in (Suit.values())) {
            for (rank in 2..14) {
                val cardString = "${getRankAsString(rank)}${suit.symbol}"
                cards.add(PlayingCard(cardString, mapNameToImage(cardString)))
            }
        }
    }

    private fun getRankAsString(rank: Int): String {
        return when (rank) {
            11 -> "j"
            12 -> "q"
            13 -> "k"
            14 -> "a"
            else -> rank.toString()
        }
    }

}