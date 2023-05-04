package com.phinespec.pokersim.utils

import androidx.compose.ui.input.key.Key.Companion.Back

enum class Phase {
    PREFLOP,
    FLOP,
    TURN,
    RIVER
}

enum class HandValue(val multiplier: Int = 1) {
    HIGH_CARD,
    PAIR,
    TWO_PAIR,
    THREE_OF_KIND(2),
    STRAIGHT(2),
    FLUSH(2),
    FULL_HOUSE(3),
    FOUR_OF_KIND(4),
    STRAIGHT_FLUSH(5),
    ROYAL_FLUSH(10);

    override fun toString(): String {
        return this.name.toString().lowercase()
    }
}

enum class CardFace(val angle: Float) {
    Front(0f) {
        override val next: CardFace
            get() = Back
    },
    Back(180f) {
        override val next: CardFace
            get() = Front
    };

    abstract val next: CardFace
}
