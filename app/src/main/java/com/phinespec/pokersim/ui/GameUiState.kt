package com.phinespec.pokersim.ui

import com.phinespec.pokersim.data.remote.Winner
import com.phinespec.pokersim.model.Card
import com.phinespec.pokersim.model.Player

data class GameUiState(
    val communityCards: MutableList<Card> = mutableListOf(),
    val players: MutableList<Player> = mutableListOf(),
    val drawCardButtonLabel: String = "Draw Flop",
    val handStrength: String = ""
)
