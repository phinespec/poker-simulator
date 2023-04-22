package com.phinespec.pokersim.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phinespec.pokersim.model.Card
import com.phinespec.pokersim.model.Deck
import com.phinespec.pokersim.model.Player
import com.phinespec.pokersim.model.playerNames
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


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
                    holeCards = getRandomHoleCards()
                )
            )
        }
        if (playersToAdd.size < MAX_PLAYER_COUNT) {
            _uiState.value = _uiState.value.copy(
                players = playersToAdd
            )
        }
    }

    private fun getRandomName(): String = playerNames.random()

    private fun addNewPlayer() {

    }

    private fun drawHoleCards(playerCount: Int)  {

    }

    private fun getRandomHoleCards(): Pair<Card, Card> = Pair(mainDeck.removeFirst(), mainDeck.removeFirst())

    fun resetGame() {
        mainDeck.clear()
        _uiState.value = GameUiState()
        buildDeck()
        createStartingPlayers()
    }

    companion object {
        private val MAX_PLAYER_COUNT = 6
        private val STARTING_PLAYER_COUNT = 1
    }
}