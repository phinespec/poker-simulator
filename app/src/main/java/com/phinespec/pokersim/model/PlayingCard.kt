package com.phinespec.pokersim.model

import androidx.annotation.DrawableRes


data class PlayingCard(
    val cardString: String,
    @DrawableRes val image: Int
)


enum class Suit(val symbol: Char) {
    HEARTS('h'),
    DIAMONDS('d'),
    CLUBS('c'),
    SPADES('s');
}
