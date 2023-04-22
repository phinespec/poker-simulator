package com.phinespec.pokersim.model

import com.phinespec.pokersim.utils.mapNameToImage
import timber.log.Timber

class Deck(val cards: MutableList<Card> = mutableListOf()) {

    fun initialize() {
        for (suit in (Suit.values())) {
            for (rank in 2..14) {
                val cardString = "${getRankAsString(rank)}${suit.symbol}"
                cards.add(Card(cardString, mapNameToImage(cardString)))
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