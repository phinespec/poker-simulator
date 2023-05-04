package com.phinespec.pokersim.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phinespec.pokersim.data.remote.HandStrengthResponseDto
import com.phinespec.pokersim.data.repository.PokerSimRepository
import com.phinespec.pokersim.model.Bet
import com.phinespec.pokersim.model.Deck
import com.phinespec.pokersim.model.Player
import com.phinespec.pokersim.model.PlayingCard
import com.phinespec.pokersim.model.playerNames
import com.phinespec.pokersim.utils.HandValue
import com.phinespec.pokersim.utils.Phase
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

    private var mainDeck = mutableListOf<PlayingCard>()

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
        repeat(3) { mainDeck.shuffle() }
    }

    private fun createStartingPlayers(count: Int = STARTING_PLAYER_COUNT) {
        var playersToAdd = mutableListOf<Player>()
        for (i in 0 until count) {
            playersToAdd.add(
                Player(
                    id = i,
                    name = getRandomName(),
                    holeCards = getHoleCards()
                )
            )
        }
        if (playersToAdd.size <= MAX_PLAYER_COUNT) {
            _uiState.value = _uiState.value.copy(
                players = playersToAdd
            )
        }
    }

    fun drawFlop() {
        var cardsToAdd = mutableListOf<PlayingCard>()

        repeat(3) { count ->
            val topCard = mainDeck.removeFirst()
            cardsToAdd.add(topCard)
        }

        _uiState.value = _uiState.value.copy(
            gamePhase = Phase.FLOP,
            communityCards = cardsToAdd,
            drawCardButtonLabel = "Turn"
        )
    }

    fun drawTurn() {
        var cardsToAdd = uiState.value.communityCards

        if (cardsToAdd.size < MAX_COMMUNITY_COUNT) {
            cardsToAdd.add(mainDeck.removeFirst())

            _uiState.value = _uiState.value.copy(
                gamePhase = Phase.TURN,
                communityCards = cardsToAdd,
                drawCardButtonLabel = "River"
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

                withContext(Dispatchers.Main) {
                    response?.players?.forEach { player ->
                        val handStrength = handStrengthMapToString.getOrDefault(player.result, "Other")
                        handStrengths.add(handStrength)
                    }

                    response?.winners?.forEach { winner ->
                        winningHands.add(winner.hand)
                    }

                    val winningIds = getWinningPlayerIds(winningHands)

                    _uiState.value = _uiState.value.copy(
                        gamePhase = Phase.RIVER,
                        communityCards = cardsToAdd,
                        drawCardButtonLabel = "Next",
                        handStrength = handStrengths,
                        winningHands = winningHands,
                        winningPlayerIds = winningIds
                    )
                    checkIfDidWin(response?.winners?.first()?.result)
                }
            }
        }
    }

    /**
     * This function returns a list of player ids for winners
     *
     * @param winningHands is a list of strings which represent winning hands
     */
    private fun getWinningPlayerIds(winningHands: List<String>): List<Int> {
        var winningIds = mutableListOf<Int>()

        _uiState.value.players.forEach { player ->
            var holeString = ""
            holeString += player.holeCards.first.cardString.uppercase()
            holeString += ","
            holeString += player.holeCards.second.cardString.uppercase()

            for (hand in winningHands) {
                if (hand.contains(holeString.takeWhile { it.isLetterOrDigit() }) ||
                    hand.contains(holeString.takeLastWhile { it.isLetterOrDigit() })
                ) {
                    winningIds.add(player.id)
                }
            }
        }
        return winningIds
    }

    private fun getRandomName(): String = playerNames.random()

    private fun getHoleCards(): Pair<PlayingCard, PlayingCard> = Pair(mainDeck.removeFirst(), mainDeck.removeFirst())

    fun resetGame() {
        mainDeck.clear()
        _uiState.value = GameUiState(cash = _uiState.value.cash)
        buildDeck()
        createStartingPlayers()
    }

    fun placeBet(playerId: Int) {
        val bet = Bet.create(
            playerId = playerId,
            gamePhase = _uiState.value.gamePhase,
            isLocked = true
        )

        _uiState.value = _uiState.value.copy(
            currentBet = bet
        )
    }

    private fun checkIfDidWin(winningHand: String?) {
        _uiState.value.currentBet?.let { bet ->
            if (_uiState.value.winningPlayerIds.contains(bet.playerId)) {
                addCash(getPayoutAmount(bet.amount, winningHand))
            } else {
                subCash(getPayoutAmount(bet.amount))
            }
        }
    }

    // Determine payout base on hand value
    private fun getPayoutAmount(betAmount: Int, handStrength: String? = null): Int {
        handStrength?.let {
            val multiplier = handStrengthMapToHandValue[it]?.multiplier
            multiplier?.let { mult ->
                return mult * betAmount
            }
        }
        return betAmount
    }

    private fun addCash(amount: Int) {
        _uiState.value = _uiState.value.copy(cash = _uiState.value.cash + amount)
    }

    private fun subCash(amount: Int) {
        _uiState.value = _uiState.value.copy(cash = _uiState.value.cash - amount)
    }

    companion object {
        private const val MAX_PLAYER_COUNT = 4
        private const val STARTING_PLAYER_COUNT = 4
        private const val MAX_COMMUNITY_COUNT = 5

        private val handStrengthMapToString = mapOf(
            "high_card" to "High Card",
            "pair" to "Pair",
            "two_pair" to "2 Pair",
            "three_of_kind" to "3 of a Kind",
            "straight" to "Straight",
            "flush" to "Flush",
            "full_house" to "Full House",
            "four_of_kind" to "4 of a Kind",
            "straight_flush" to "Straight Flush",
            "royal_flush" to "Royal Flush"
        )

        private val handStrengthMapToHandValue = mapOf<String, HandValue>(
            "high_card" to HandValue.HIGH_CARD,
            "pair" to HandValue.PAIR,
            "two_pair" to HandValue.TWO_PAIR,
            "three_of_kind" to HandValue.THREE_OF_KIND,
            "straight" to HandValue.STRAIGHT,
            "flush" to HandValue.FLUSH,
            "full_house" to HandValue.FULL_HOUSE,
            "four_of_kind" to HandValue.FOUR_OF_KIND,
            "straight_flush" to HandValue.STRAIGHT_FLUSH,
            "royal_flush" to HandValue.ROYAL_FLUSH
        )
    }
}