package com.phinespec.pokersim.ui

import androidx.lifecycle.ViewModel
import com.phinespec.pokersim.model.Card
import com.phinespec.pokersim.model.Deck
import com.phinespec.pokersim.model.Player
import com.phinespec.pokersim.model.playerNames
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber


class MainViewModel : ViewModel() {

    private var mainDeck = mutableListOf<Card>()

    // Observables
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        resetGame()
    }

    // build deck and shuffle all cards before dealing
    private fun buildDeck() {
        val deck = Deck()
        deck.initialize()
        mainDeck = deck.cards
        mainDeck.shuffle()
    }

    private fun createStartingPlayers(count: Int = 1) {
        var playersToAdd = mutableListOf<Player>()
        for (i in 0..count) {
            playersToAdd.add(
                Player(
                    id = i,
                    name = getRandomName(),
                    holeCards = getHoleCards()
                )
            )
        }
        if (playersToAdd.size < MAX_PLAYER_COUNT) {
            _uiState.value = _uiState.value.copy(
                players = playersToAdd
            )
        }
    }

    fun drawCommunityCards() {
        var cardsToAdd = mutableListOf<Card>()

        repeat(5) {
            val topCard = mainDeck.removeFirst()
            cardsToAdd.add(topCard)
        }
        _uiState.value = _uiState.value.copy(communityCards = cardsToAdd)
    }

    private fun getRandomName(): String = playerNames.random()

    private fun getHoleCards(): Pair<Card, Card> = Pair(mainDeck.removeFirst(), mainDeck.removeFirst())

    fun resetGame() {
        mainDeck.clear()
        _uiState.value = GameUiState()
        buildDeck()
        createStartingPlayers()
        drawCommunityCards()
    }

    companion object {
        private val MAX_PLAYER_COUNT = 6
        private val STARTING_PLAYER_COUNT = 1
    }
}