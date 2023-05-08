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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.phinespec.pokersim.model.Bet
import com.phinespec.pokersim.model.Player
import com.phinespec.pokersim.model.PlayingCard
import com.phinespec.pokersim.ui.GameUiState
import com.phinespec.pokersim.ui.MainViewModel
import com.phinespec.pokersim.ui.screens.main_game.FlipCard
import com.phinespec.pokersim.ui.theme.DarkFeltBlue
import com.phinespec.pokersim.ui.theme.DarkestFeltBlue
import com.phinespec.pokersim.ui.theme.LightFeltBlue
import com.phinespec.pokersim.ui.theme.PurpleGrey40
import com.phinespec.pokersim.utils.CardFace
import com.phinespec.pokersim.utils.Street
import timber.log.Timber


@Composable
fun MainGameScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {

    val gameUiState by viewModel.uiState.collectAsState()

    GameLayout(
        uiState = gameUiState,
        onClickReset = { viewModel.resetGame() },
        onClickDraw = {
            when (viewModel.uiState.value.drawCardButtonLabel) {
                "Flop" -> { viewModel.drawFlop() }
                "Turn" -> { viewModel.drawTurn() }
                "River" -> { viewModel.drawRiver() }
                else -> { viewModel.resetGame() }
            }
        },
        onClickPlayerLabel = {playerId ->
            viewModel.placeBet(playerId)
        }
    )

}

@Composable
fun GameLayout(
    uiState: GameUiState,
    onClickReset: () -> Unit,
    onClickDraw: () -> Unit,
    modifier: Modifier = Modifier,
    onClickPlayerLabel: (Int) -> Unit
) {
    TableTop(modifier, uiState, onClickReset = onClickReset, onClickDraw = onClickDraw, onClickPlayerLabel = { playerId -> onClickPlayerLabel(playerId) })
}

@Composable
fun TableTop(
    modifier: Modifier = Modifier,
    uiState: GameUiState,
    onClickReset: () -> Unit,
    onClickDraw: () -> Unit,
    onClickPlayerLabel: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkFeltBlue),
        contentAlignment = Alignment.Center
    ) {
        ButtonRow(onClickReset = { onClickReset() }, onClickDraw = { onClickDraw() }, drawButtonLabel = uiState.drawCardButtonLabel)
        CashDisplay(modifier = modifier.align(Alignment.BottomEnd), cash = uiState.cash)
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
    cash: Int,
    modifier: Modifier = Modifier
) {

    val cashCounter by animateIntAsState(
        targetValue = cash,
        animationSpec = tween(
            delayMillis = 500,
            durationMillis = 1000,
            easing = LinearEasing
        )
    )
    Text(
        modifier = modifier
            .padding(16.dp),
        text = "$$cashCounter",
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
            .offset(x =
            when (player.id) {
                0, 4 -> 220.dp
                1, 3 -> -(220).dp
                else -> 0.dp
            })
//            .background(Color.Red)
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
                player = player,
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
    player: Player,
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
                    else -> player.name
                }
            } else  player.name ,
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
    onClickReset: () -> Unit,
    drawButtonLabel: String
) {
    Row(
        modifier = modifier
            .offset(x = -(320).dp, y = 120.dp)
    ) {
        DrawCardButton(onClickDraw = { onClickDraw() }, buttonLabel = drawButtonLabel)
    }
}

@Composable
fun ResetButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    ElevatedButton(
        onClick = { onClick() },
        modifier = modifier
            .shadow(elevation = 5.dp, shape = CircleShape)
    ) {
        Text("Reset")
    }
}
@Composable
fun DrawCardButton(
    onClickDraw: () -> Unit,
    modifier: Modifier = Modifier,
    buttonLabel: String
) {
    ElevatedButton(
        onClick = { onClickDraw() },
        colors = ButtonDefaults.buttonColors(containerColor = DarkestFeltBlue),
        modifier = modifier
            .border(width = 2.dp, color = LightFeltBlue, shape = CircleShape)
            .height(80.dp)
            .width(100.dp)
            .shadow(elevation = 5.dp, shape = CircleShape)
    ) {
        Text(
            buttonLabel.uppercase(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape")
@Composable
fun TablePreview() {
    DrawCardButton(onClickDraw = { }, buttonLabel = "NEXT")
}