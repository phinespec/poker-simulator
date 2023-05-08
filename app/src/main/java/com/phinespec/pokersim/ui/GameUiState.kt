package com.phinespec.pokersim.ui

import com.phinespec.pokersim.model.Bet
import com.phinespec.pokersim.model.Player
import com.phinespec.pokersim.model.PlayingCard
import com.phinespec.pokersim.utils.Street

data class GameUiState(
    val street: Street = Street.PREFLOP,
    val currentBet: Bet? = null,
    val cash: Int = 50,
    val communityCards: MutableList<PlayingCard> = mutableListOf(),
    val players: MutableList<Player> = mutableListOf(),
    val drawCardButtonLabel: String = "Flop",
    val handStrength: List<String>? = null,
    val winningHands: List<String>? = null,
    val winningPlayerIds: List<Int> = emptyList()
)
