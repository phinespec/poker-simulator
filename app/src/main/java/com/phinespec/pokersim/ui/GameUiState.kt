package com.phinespec.pokersim.ui

import com.phinespec.pokersim.model.Bet
import com.phinespec.pokersim.model.Player
import com.phinespec.pokersim.model.PlayingCard
import com.phinespec.pokersim.utils.AlertType
import com.phinespec.pokersim.utils.Bonus
import com.phinespec.pokersim.utils.Street

data class GameUiState(
    val street: Street = Street.PREFLOP,
    val currentBet: Bet? = null,
    val cash: Int = 100,
    val communityCards: MutableList<PlayingCard> = mutableListOf(),
    val players: MutableList<Player> = mutableListOf(),
    val drawCardButtonLabel: String = "Flop",
    val winningHands: List<String>? = null,
    val winningPlayerIds: List<Int> = emptyList(),
    val alert: AlertWrapper? = null,
    val bonus: Bonus? = null,
)


data class AlertWrapper(
    val shouldShow: Boolean,
    val alertType: AlertType,
)