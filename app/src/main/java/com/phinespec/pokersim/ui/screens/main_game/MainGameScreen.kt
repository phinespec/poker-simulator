package com.phinespec.pokersim.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.phinespec.pokersim.model.Bet
import com.phinespec.pokersim.model.Player
import com.phinespec.pokersim.model.PlayingCard
import com.phinespec.pokersim.ui.GameUiState
import com.phinespec.pokersim.ui.MainViewModel
import com.phinespec.pokersim.ui.screens.main_game.BonusText
import com.phinespec.pokersim.ui.screens.main_game.CountdownBar
import com.phinespec.pokersim.ui.screens.main_game.FlipCard
import com.phinespec.pokersim.ui.theme.DarkFeltBlue
import com.phinespec.pokersim.ui.theme.DarkestFeltBlue
import com.phinespec.pokersim.ui.theme.DialogBackground
import com.phinespec.pokersim.ui.theme.LightFeltBlue
import com.phinespec.pokersim.ui.theme.PurpleGrey40
import com.phinespec.pokersim.utils.AlertType
import com.phinespec.pokersim.utils.Bonus
import com.phinespec.pokersim.utils.CardFace
import com.phinespec.pokersim.utils.Street
import com.phinespec.pokersim.utils.UIEvent
import timber.log.Timber


@Composable
fun MainGameScreen(
    viewModel: MainViewModel = hiltViewModel()
) {

    val gameUiState by viewModel.uiState.collectAsState()
    val seconds = viewModel.seconds.collectAsState()

    Box {
        GameLayout(
            uiState = gameUiState,
            onClickDraw = {
                when (viewModel.uiState.value.drawCardButtonLabel) {
                    "Flop" -> { viewModel.onEvent(UIEvent.Draw(Street.PREFLOP)) }
                    "Turn" -> { viewModel.onEvent(UIEvent.Draw(Street.FLOP)) }
                    "River" -> { viewModel.onEvent(UIEvent.Draw(Street.TURN)) }
                    else -> { viewModel.onEvent(UIEvent.ResetGame()) }
                }
            },
            onClickPlayerLabel = { playerId ->
                viewModel.onEvent(UIEvent.PlaceBet(playerId))
            },
            seconds = seconds.value
        )

        // Show alerts
        if (gameUiState.alert != null) {
            gameUiState.alert?.let { alertWrapper ->
                AlertDialog(
                    shape = MaterialTheme.shapes.large,
                    containerColor = DialogBackground,
                    text = { Text(
                        text = getAlertMessage(alertWrapper.alertType),
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(.75f)
                    ) },
                    onDismissRequest = {  },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.onEvent(UIEvent.ResetGame(true)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .align(Alignment.Center)
                        ) {
                            Text("Reset")
                        }
                    }
                )
            }
        }
    }

}

private fun getAlertMessage(alertType: AlertType): String {
    return when (alertType) {
        is AlertType.Basic -> alertType.message
        is AlertType.GameOver -> alertType.message
        is AlertType.Timeout -> alertType.message
    }
}

@Composable
fun GameLayout(
    uiState: GameUiState,
    onClickDraw: () -> Unit,
    modifier: Modifier = Modifier,
    onClickPlayerLabel: (Int) -> Unit,
    seconds: Int
) {
    TableTop(modifier, uiState, onClickDraw = onClickDraw, onClickPlayerLabel = { playerId -> onClickPlayerLabel(playerId) }, seconds = seconds)
}

@Composable
fun TableTop(
    modifier: Modifier = Modifier,
    uiState: GameUiState,
    onClickDraw: () -> Unit,
    onClickPlayerLabel: (Int) -> Unit,
    seconds: Int
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkFeltBlue),
        contentAlignment = Alignment.Center
    ) {
        ButtonRow(onClickDraw = { onClickDraw() }, drawButtonLabel = uiState.drawCardButtonLabel, seconds = seconds)
        CashDisplay(modifier = modifier.align(Alignment.BottomEnd), uiState = uiState)
        if (uiState.bonus != null) {
            BonusText(
                text = when (uiState.bonus) {
                    is Bonus.Time -> {
                        "+${uiState.bonus.seconds}s"
                    }
                    else -> ""
                },
                modifier = modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 76.dp, y = -(40.dp)),
            )
        }
        CommunityTemplate(uiState = uiState)

        // Player indexes start at top left
        HoleCards(modifier = modifier.align(Alignment.TopStart), player = uiState.players[0], street = uiState.street, handStrength = uiState.players[0].handStrength,
            isWinner = uiState.winningPlayerIds.contains(0), onClickPlayerLabel = { playerId -> onClickPlayerLabel(playerId) }, betPlaced = uiState.currentBet
        )
        HoleCards(modifier = modifier.align(Alignment.TopEnd), player = uiState.players[1], street = uiState.street, handStrength = uiState.players[1].handStrength,
            isWinner = uiState.winningPlayerIds.contains(1), onClickPlayerLabel = { playerId -> onClickPlayerLabel(playerId) }, betPlaced = uiState.currentBet
        )
        HoleCards(modifier = modifier.align(Alignment.CenterEnd), player = uiState.players[2], street = uiState.street, handStrength = uiState.players[2].handStrength,
            isWinner = uiState.winningPlayerIds.contains(2), onClickPlayerLabel = { playerId -> onClickPlayerLabel(playerId) }, betPlaced = uiState.currentBet
        )
        HoleCards(modifier = modifier.align(Alignment.BottomEnd), player = uiState.players[3], street = uiState.street, handStrength = uiState.players[3].handStrength,
            isWinner = uiState.winningPlayerIds.contains(3), onClickPlayerLabel = { playerId -> onClickPlayerLabel(playerId) }, betPlaced = uiState.currentBet
        )
        HoleCards(modifier = modifier.align(Alignment.BottomStart), player = uiState.players[4], street = uiState.street, handStrength = uiState.players[4].handStrength,
            isWinner = uiState.winningPlayerIds.contains(4), onClickPlayerLabel = { playerId -> onClickPlayerLabel(playerId) }, betPlaced = uiState.currentBet
        )
        HoleCards(modifier = modifier.align(Alignment.CenterStart), player = uiState.players[5], street = uiState.street, handStrength = uiState.players[5].handStrength,
            isWinner = uiState.winningPlayerIds.contains(5), onClickPlayerLabel = { playerId -> onClickPlayerLabel(playerId) }, betPlaced = uiState.currentBet
        )
    }
}

@Composable
fun CommunityTemplate(
    uiState: GameUiState,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    Box(
        modifier = modifier
            .width(screenWidth / 1.5f)
            .height(screenHeight / 2)
            .border(
                width = 2.dp, color = LightFeltBlue, shape = RoundedCornerShape(100.dp)
            )
            .clip(RoundedCornerShape(50)),
        contentAlignment = Alignment.Center
    ) {
        CommunityCards(communityCards = uiState.communityCards, winningHands = uiState.winningHands ?: listOf())
    }
}

@Composable
fun CashDisplay(
    uiState: GameUiState,
    modifier: Modifier = Modifier
) {

    val cashCounter by animateIntAsState(
        targetValue = uiState.cash,
        animationSpec = tween(
            delayMillis = 500,
            durationMillis = 1000,
            easing = LinearEasing
        )
    )
    Text(
        modifier = modifier
            .padding(16.dp),
        text = "$${if (uiState.street == Street.PREFLOP) uiState.cash else cashCounter}",
        style = MaterialTheme.typography.displaySmall,
        color = Color.White
    )
}

@Composable
fun HoleCards(
    modifier: Modifier = Modifier,
    player: Player,
    street: Street,
    handStrength: String,
    isWinner: Boolean = false,
    betPlaced: Bet? = null,
    onClickPlayerLabel: (Int) -> Unit
) {
    Box(
        modifier
            .padding(horizontal = 8.dp)
            .offset(
                x =
                when (player.id) {
                    0, 4 -> 220.dp
                    1, 3 -> -(220).dp
                    else -> 0.dp
                }
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var isVisible by remember { mutableStateOf(false) }
            LaunchedEffect(key1 = true) {
                isVisible = true
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    ),
                    initialOffsetY = { 80 }
                )
            ) {
                Row(
                    modifier = modifier.offset(y = 8.dp)
                ) {

                    FlipCard(isFaded = !isWinner && !handStrength.isNullOrBlank() && street == Street.RIVER, front = {
                        Image(
                            painter = painterResource(player.holeCards.first.image),
                            contentDescription = "",
                            contentScale = ContentScale.FillBounds
                        )
                    }) {
                        CardBack()
                    }
                    FlipCard(isFaded = !isWinner && !handStrength.isNullOrBlank() && street == Street.RIVER, front = {
                        Image(
                            painter = painterResource(player.holeCards.second.image),
                            contentDescription = "",
                            contentScale = ContentScale.FillBounds
                        )
                    }) {
                        CardBack()
                    }
                }
            }
            PlayerLabel(
                street = street,
                handStrength = handStrength,
                isWinner = isWinner,
                onClick = {
                    if (betPlaced == null) {
                        onClickPlayerLabel(player.id)
                    }
                }
            )
        }
        if (betPlaced?.playerId == player.id) {
            Icon(
                imageVector = Icons.Default.Lock, contentDescription = "",
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.Center)
                    .offset(y = (-24).dp)
                    .alpha(.5f)
            )
        }
    }
}

@Composable
fun PlayerLabel(
    modifier: Modifier = Modifier,
    street: Street,
    handStrength: String,
    isWinner: Boolean,
    onClick: () -> Unit
) {
    ElevatedButton(
        modifier = Modifier
            .width(160.dp)
            .height(40.dp)
            .offset(y = -(16).dp)
            .shadow(elevation = 4.dp, shape = CircleShape),
        colors = ButtonDefaults.buttonColors(containerColor = DarkestFeltBlue),
        onClick = { onClick() },

    ) {
        Text(
            text =
            if (street == Street.RIVER) {
                when {
                    !handStrength.isNullOrBlank() && !isWinner -> handStrength
                    isWinner -> "$handStrength Wins!"
                    else -> "Lock in Bet"
                }
            } else  "Lock in Bet",
            modifier = modifier,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White
        )
    }
}


@Composable
fun CommunityCards(
    communityCards: List<PlayingCard>,
    modifier: Modifier = Modifier,
    winningHands: List<String>
) {

    Row {
        for (i in communityCards.indices) {
            var cardFace by remember { mutableStateOf(CardFace.Back) }
            FlipCard(
                face = cardFace,
                isFaded = if (communityCards.size == 5)
                    !getWinningCards(winningHands).contains(
                        communityCards[i]
                            .cardString.uppercase()
                    )
                else false,
                front = {
                    Image(
                        painter = painterResource(id = communityCards[i].image),
                        modifier = Modifier.fillMaxSize(),
                        contentDescription = "",
                        contentScale = ContentScale.FillBounds
                    )
                },
                back = {
                    CardBack()
                }
            )
            LaunchedEffect(key1 = true) {
                cardFace = cardFace.next
            }
        }
    }
}

@Composable
fun CardBack(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PurpleGrey40)
    )
}

private fun getWinningCards(winningHands: List<String>): String {
    var winningCards = ""
    winningHands.forEach {
        winningCards += it.uppercase()
        winningCards += ","
    }
    Timber.d("Winning cards => $winningCards")
    return winningCards
}

@Composable
fun ButtonRow(
    modifier: Modifier = Modifier,
    onClickDraw: () -> Unit,
    drawButtonLabel: String,
    seconds: Int
) {
    Row(
        modifier = modifier
            .offset(x = -(320).dp, y = 120.dp)
    ) {
        DrawCardButton(onClickDraw = { onClickDraw() }, buttonLabel = drawButtonLabel, seconds = seconds)
    }
}

@Composable
fun DrawCardButton(
    onClickDraw: () -> Unit,
    modifier: Modifier = Modifier,
    buttonLabel: String,
    seconds: Int
) {
    Box {
        ElevatedButton(
            onClick = { onClickDraw() },
            colors = ButtonDefaults.buttonColors(containerColor = DarkestFeltBlue),
            modifier = modifier
                .border(width = 8.dp, color = LightFeltBlue, shape = CircleShape)
                .height(100.dp)
                .width(100.dp)
                .shadow(elevation = 5.dp, shape = CircleShape)
        ) {
            Text(
                buttonLabel.uppercase(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        CountdownBar(seconds = seconds)
    }
}

private const val PLAYER_LABEL_DEFAULT = "Place Bet"


@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape")
@Composable
fun TablePreview() {
//    DrawCardButton(onClickDraw = { }, buttonLabel = "NEXT", onTimeout = {})
}