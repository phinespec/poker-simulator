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
import com.phinespec.pokersim.utils.AlertType
import com.phinespec.pokersim.utils.HandValue
import com.phinespec.pokersim.utils.Street
import com.phinespec.pokersim.utils.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
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

    fun onEvent(event: UIEvent) {
        when (event) {
            is UIEvent.Draw -> {
                when (event.street) {
                    Street.PREFLOP -> drawFlop()
                    Street.FLOP -> drawTurn()
                    else -> drawRiver()
                }
            }
            is UIEvent.ResetGame -> resetGame(event.isHard)
            is UIEvent.PlaceBet -> placeBet(event.playerId)
        }
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

    private fun createStartingPlayers(count: Int = MAX_PLAYER_COUNT) {
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
        var pc = mutableListOf<String>()
        var cc = ""

        // Player cards
        _uiState.value.players.forEach { player ->
            val playerCards = player.holeCards
            var cs = ""
            cs += playerCards.first.cardString.uppercase()
            cs += ","
            cs += playerCards.second.cardString.uppercase()
            pc.add(cs)
        }

        // Community cards
        mainDeck.subList(0,5).forEach { card ->
            val cardString = card.cardString
            cc += "$cardString,".uppercase()
        }

        viewModelScope.launch(Dispatchers.IO) {
            val response = getHandResults(cc, pc)
            val handStrengths = mutableListOf<String>()
            val winningHands = mutableListOf<String>()

            withContext(Dispatchers.Main) {
                val players = _uiState.value.players
                response?.players?.forEachIndexed { i, player ->
                    val handStrength = handStrengthMapToString.getOrDefault(player.result, "Other")
                    players[i] = players[i].copy(
                        handStrength = handStrength
                    )
//                    handStrengths.add(handStrength)
                }

                response?.winners?.forEach { winner ->
                    winningHands.add(winner.hand)
                }

                val winningIds = getWinningPlayerIds(winningHands)

                repeat(3) { count ->
                    val topCard = mainDeck.removeFirst()
                    cardsToAdd.add(topCard)
                }

                _uiState.value = _uiState.value.copy(
                    street = Street.FLOP,
                    communityCards = cardsToAdd,
                    drawCardButtonLabel = "Turn",
                    players = players,
                    winningHands = winningHands,
                    winningPlayerIds = winningIds
                )
            }
        }
    }

    fun drawTurn() {
        var cardsToAdd = uiState.value.communityCards

        if (cardsToAdd.size < MAX_COMMUNITY_COUNT) {
            cardsToAdd.add(mainDeck.removeFirst())

            _uiState.value = _uiState.value.copy(
                street = Street.TURN,
                communityCards = cardsToAdd,
                drawCardButtonLabel = "River"
            )
        }
    }

        fun drawRiver() {
            var cardsToAdd = uiState.value.communityCards

            if (cardsToAdd.size < MAX_COMMUNITY_COUNT) {
                cardsToAdd.add(mainDeck.removeFirst())

                _uiState.value = _uiState.value.copy(
                    street = Street.RIVER,
                    communityCards = cardsToAdd,
                    drawCardButtonLabel = "Next",
                )
                checkIfDidWin(_uiState.value.players[_uiState.value.winningPlayerIds.first()].handStrength)
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

    private fun resetGame(hard: Boolean = false) {
        Timber.d("resetGame called")
        mainDeck.clear()
        _uiState.value = GameUiState(cash = if (hard) STARTING_CASH else _uiState.value.cash)
        buildDeck()
        createStartingPlayers()
    }

    private fun placeBet(playerId: Int) {
        val bet = Bet.create(
            playerId = playerId,
            street = _uiState.value.street,
            isLocked = true
        )

        _uiState.value = _uiState.value.copy(
            currentBet = bet
        )
    }

    private fun checkIfDidWin(winningHand: String?) {
        Timber.d("winningHand = $winningHand")
        _uiState.value.currentBet?.let { bet ->
            if (_uiState.value.winningPlayerIds.contains(bet.playerId)) {
                addCash(getPayoutAmount(bet.amount, winningHand))
            } else {
                subCash(getPayoutAmount(bet.amount, winningHand))
            }
        }
    }

    // Determine payout base on hand value
    private fun getPayoutAmount(betAmount: Int, handStrength: String? = null): Int {
        Timber.d("handStrength => $handStrength")
        handStrength?.let {
            val multiplier = handStrengthMapToHandValue[it]?.multiplier
            Timber.d("multiplier => $multiplier")
            multiplier?.let { mult ->
                Timber.d("returning betAmount * multiplier")
                return mult * betAmount
            }
        }
        Timber.d("returning betAmount only")
        return betAmount
    }

    private fun addCash(amount: Int) {
        Timber.d("addCash => $amount")
        _uiState.value = _uiState.value.copy(cash = _uiState.value.cash + amount)
    }

    private fun subCash(amount: Int) {
        if ((_uiState.value.cash - amount) <= 0) {
            gameOver()
        }
        Timber.d("subCash => $amount")
        _uiState.value = _uiState.value.copy(cash = _uiState.value.cash - amount)
    }

    private fun gameOver() {
        showDialog(
            AlertWrapper(
                true,
                AlertType.GameOver(),
            )
        )
    }

    fun timeout() {
        showDialog(
            AlertWrapper(
                true,
                AlertType.Timeout()
            )
        )
    }

    fun showDialog(alert: AlertWrapper) {
        _uiState.value = _uiState.value.copy(alert = alert)
    }

    companion object {
        private const val MAX_PLAYER_COUNT = 6
        private const val MAX_COMMUNITY_COUNT = 5
        private const val STARTING_CASH = 50

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
            "High Card" to HandValue.HIGH_CARD,
            "Pair" to HandValue.PAIR,
            "2 Pair" to HandValue.TWO_PAIR,
            "3 of a Kind" to HandValue.THREE_OF_KIND,
            "Straight" to HandValue.STRAIGHT,
            "Flush" to HandValue.FLUSH,
            "Full House" to HandValue.FULL_HOUSE,
            "4 of a Kind" to HandValue.FOUR_OF_KIND,
            "Straight Flush" to HandValue.STRAIGHT_FLUSH,
            "Royal Flush" to HandValue.ROYAL_FLUSH
        )
    }
}