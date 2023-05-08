package com.phinespec.pokersim.model

import com.phinespec.pokersim.utils.Street

data class Bet private constructor(
    val playerId: Int,
    val amount: Int,
    val isLocked: Boolean
) {

    companion object {
        fun create(playerId: Int, street: Street, isLocked: Boolean): Bet {
            return when (street) {
                Street.PREFLOP -> { Bet(playerId = playerId, amount = 50, isLocked = true) }
                Street.FLOP -> { Bet(playerId = playerId, amount = 20, isLocked = true) }
                else -> { Bet(playerId = playerId, amount = 10, isLocked = true) }
            }
        }
    }
}
