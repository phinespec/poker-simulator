package com.phinespec.pokersim.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phinespec.pokersim.data.remote.HandStrengthResponseDto
import com.phinespec.pokersim.data.repository.PokerSimRepository
import com.phinespec.pokersim.model.Card
import com.phinespec.pokersim.model.Deck
import com.phinespec.pokersim.model.Player
import com.phinespec.pokersim.model.playerNames
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: PokerSimRepository
) : ViewModel() {

    private var mainDeck = mutableListOf<Card>()

    // Observables
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        resetGame()
    }

    suspend fun getHandResults(cc: String, pc: List<String>): HandStrengthResponseDto? {
        val result = repository.getHandResults(cc, pc)
        return if (result.isSuccessful) {
            result.body()!!
        } else null
    }

    // build deck and shuffle all cards before dealing
    private fun buildDeck() {
        val deck = Deck()
        deck.initialize()
        mainDeck = deck.cards
        mainDeck.shuffle()
    }

    private fun createStartingPlayers(count: Int = STARTING_PLAYER_COUNT) {
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

    fun drawFlop() {
        var cardsToAdd = mutableListOf<Card>()

        repeat(3) { count ->
            val topCard = mainDeck.removeFirst()
            cardsToAdd.add(topCard)
        }

        _uiState.value = _uiState.value.copy(
            communityCards = cardsToAdd,
            drawCardButtonLabel = "Draw Turn"
        )
    }

    fun drawTurn() {

        var cardsToAdd = uiState.value.communityCards

        if (cardsToAdd.size < MAX_COMMUNITY_COUNT) {
            cardsToAdd.add(mainDeck.removeFirst())

            _uiState.value = _uiState.value.copy(
                communityCards = cardsToAdd,
                drawCardButtonLabel = "Draw River"
            )
        }
    }

    fun drawRiver() {
        var cc = ""
        var pc = mutableListOf<String>()

        _uiState.value.players.forEach { player ->
            val playerCards = player.holeCards
            var cs = ""
            cs += playerCards.first.cardString.uppercase()
            cs += ","
            cs += playerCards.second.cardString.uppercase()
            pc.add(cs)
        }

        var cardsToAdd = uiState.value.communityCards

        if (cardsToAdd.size < MAX_COMMUNITY_COUNT) {
            cardsToAdd.add(mainDeck.removeFirst())

            cardsToAdd.forEach { card ->
                val cardString = card.cardString
                cc += "$cardString,".uppercase()
            }

            viewModelScope.launch(Dispatchers.IO) {
                val response = getHandResults(cc, pc)
                val handStrengths = mutableListOf<String>()
                val winningHands = mutableListOf<String>()
                val winningHoles = mutableListOf<String>()

                withContext(Dispatchers.Main) {
                    response?.players?.forEach { player ->
                        val handStrength = handStrengthMap.getOrDefault(player.result, "Other")
                        handStrengths.add(handStrength)
                    }

                    response?.winners?.forEach { winner ->
                        winningHands.add(winner.hand)
                        winningHoles.add(winner.cards)
                    }
                    _uiState.value = _uiState.value.copy(
                        communityCards = cardsToAdd,
                        drawCardButtonLabel = "New Hand",
                        handStrength = handStrengths,
                        winningHands = winningHands,
                        winningHoles = winningHoles
                    )
                }
            }
        }
    }

    private fun getRandomName(): String = playerNames.random()

    private fun getHoleCards(): Pair<Card, Card> = Pair(mainDeck.removeFirst(), mainDeck.removeFirst())

    fun resetGame() {
        mainDeck.clear()
        _uiState.value = GameUiState()
        buildDeck()
        createStartingPlayers()
    }

    companion object {
        private val MAX_PLAYER_COUNT = 6
        private val STARTING_PLAYER_COUNT = 4
        private val MAX_COMMUNITY_COUNT = 5

        private val handStrengthMap = mapOf(
            "high_card" to "High Card",
            "pair" to "Pair",
            "two_pair" to "2 Pair",
            "three_of_kind" to "3 of a Kind",
            "straight" to "Straight",
            "flush" to "Flush",
            "full_house" to "Full House",
            "four_of_kind" to "Quads",
            "straight_flush" to "Straight Flush",
            "royal_flush" to "Royal Flush"
        )
    }
}