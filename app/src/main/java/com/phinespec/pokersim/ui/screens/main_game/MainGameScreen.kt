package com.phinespec.pokersim.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.phinespec.pokersim.data.remote.Winner
import com.phinespec.pokersim.model.Card
import com.phinespec.pokersim.model.Player
import com.phinespec.pokersim.ui.GameUiState
import com.phinespec.pokersim.ui.MainViewModel
import com.phinespec.pokersim.ui.screens.main_game.CardImage
import com.phinespec.pokersim.ui.theme.DarkFeltBlue
import com.phinespec.pokersim.ui.theme.LightFeltBlue
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
                "Draw Flop" -> { viewModel.drawFlop() }
                "Draw Turn" -> { viewModel.drawTurn() }
                "Draw River" -> { viewModel.drawRiver() }
                else -> { viewModel.resetGame() }
            }
        }
    )

}

@Composable
fun GameLayout(
    uiState: GameUiState,
    onClickReset: () -> Unit,
    onClickDraw: () -> Unit,
    modifier: Modifier = Modifier
) {
    TableTop(uiState, onClickReset = onClickReset, onClickDraw = onClickDraw)
}

@Composable
fun TableTop(
    uiState: GameUiState,
    onClickReset: () -> Unit,
    onClickDraw: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkFeltBlue),
        contentAlignment = Alignment.Center
    ) {
        ButtonRow(onClickReset = { onClickReset() }, onClickDraw = { onClickDraw() }, drawButtonLabel = uiState.drawCardButtonLabel)
        CommunityTemplate(uiState = uiState)
        HoleCards(modifier = Modifier.align(Alignment.BottomCenter), player = uiState.players[0], handStrength = uiState.handStrength?.get(0) ?: "", isWinner = uiState.winningPlayerIds.contains(0))
        HoleCards(modifier = Modifier.align(Alignment.CenterStart), player = uiState.players[1], handStrength = uiState.handStrength?.get(1) ?: "", isWinner = uiState.winningPlayerIds.contains(1))
        HoleCards(modifier = Modifier.align(Alignment.TopCenter), player = uiState.players[2], handStrength = uiState.handStrength?.get(2) ?: "", isWinner = uiState.winningPlayerIds.contains(2))
        HoleCards(modifier = Modifier.align(Alignment.CenterEnd), player = uiState.players[3], handStrength = uiState.handStrength?.get(3) ?: "", isWinner = uiState.winningPlayerIds.contains(3))
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
fun HoleCards(
    modifier: Modifier = Modifier,
    player: Player,
    handStrength: String,
    isWinner: Boolean = false
) {
    Column(
        modifier
            .padding(horizontal = 8.dp)
            .offset(y = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = modifier.offset(y = 8.dp)
        ) {
            CardImage(card = player.holeCards.first, isFaded = !isWinner && !handStrength.isNullOrBlank())
            CardImage(card = player.holeCards.second, isFaded = !isWinner && !handStrength.isNullOrBlank())
        }
        PlayerLabel(playerName = player.name, cash = player.cash, handStrength = handStrength, isWinner = isWinner)
    }
}

@Composable
fun PlayerLabel(
    modifier: Modifier = Modifier,
    playerName: String,
    cash: Double,
    handStrength: String,
    isWinner: Boolean
) {
    Button(
        modifier = modifier
            .width(160.dp)
            .height(40.dp)
            .offset(y = (-20).dp)
            .shadow(elevation = 4.dp, shape = CircleShape),
        onClick = {},

    ) {
        Text(
//            text = if (!handStrength.isNullOrBlank()) handStrength else playerName,
            text = when {
                !handStrength.isNullOrBlank() && !isWinner -> handStrength
                isWinner -> "$handStrength Wins!"
                else -> playerName
            },
            modifier = modifier,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White
        )
    }
}


@Composable
fun CommunityCards(
    communityCards: List<Card>,
    modifier: Modifier = Modifier,
    winningHands: List<String>
) {
    Row {
        if (communityCards.size == 3) {
            CardImage(card = communityCards[0])
            CardImage(card = communityCards[1])
            CardImage(card = communityCards[2])
        } else if (communityCards.size == 4) {
            CardImage(card = communityCards[0])
            CardImage(card = communityCards[1])
            CardImage(card = communityCards[2])
            CardImage(card = communityCards[3])
        } else if (communityCards.size == 5) {
            CardImage(card = communityCards[0], isWinner = getWinningCards(winningHands).contains(communityCards[0].cardString.uppercase()))
            CardImage(card = communityCards[1], isWinner = getWinningCards(winningHands).contains(communityCards[1].cardString.uppercase()))
            CardImage(card = communityCards[2], isWinner = getWinningCards(winningHands).contains(communityCards[2].cardString.uppercase()))
            CardImage(card = communityCards[3], isWinner = getWinningCards(winningHands).contains(communityCards[3].cardString.uppercase()))
            CardImage(card = communityCards[4], isWinner = getWinningCards(winningHands).contains(communityCards[4].cardString.uppercase()))
        }
    }
}

private fun getWinningCards(winningHands: List<String>): String {
    var winningCards = ""
    winningHands.forEach {
        winningCards += it.uppercase()
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
            .offset(x = -(320).dp, y = 140.dp)
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
        modifier = modifier
            .shadow(elevation = 5.dp, shape = CircleShape)
    ) {
        Text(buttonLabel)
    }
}


@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape")
@Composable
fun TablePreview() {
    val viewModel: MainViewModel = hiltViewModel()
    val gameUiState by viewModel.uiState.collectAsState()

    TableTop(uiState = gameUiState, onClickReset = { }, onClickDraw = {})
}