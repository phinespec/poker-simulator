package com.phinespec.pokersim.ui

import com.phinespec.pokersim.model.Card
import com.phinespec.pokersim.model.Player

data class GameUiState(
    val communityCards: MutableList<Card> = mutableListOf(),
    val players: MutableList<Player> = mutableListOf()
)
