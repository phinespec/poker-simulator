package com.phinespec.pokersim.model

import com.phinespec.pokersim.utils.Phase

data class Bet private constructor(
    val playerId: Int,
    val amount: Int,
    val isLocked: Boolean
) {

    companion object {
        fun create(playerId: Int, gamePhase: Phase, isLocked: Boolean): Bet {
            return when (gamePhase) {
                Phase.PREFLOP -> { Bet(playerId = playerId, amount = 50, isLocked = true) }
                Phase.FLOP -> { Bet(playerId = playerId, amount = 20, isLocked = true) }
                else -> { Bet(playerId = playerId, amount = 10, isLocked = true) }
            }
        }
    }
}
