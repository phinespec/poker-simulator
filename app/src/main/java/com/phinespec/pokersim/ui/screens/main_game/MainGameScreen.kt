package com.phinespec.pokersim.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import com.phinespec.pokersim.model.Card
import com.phinespec.pokersim.model.Player
import com.phinespec.pokersim.ui.GameUiState
import com.phinespec.pokersim.ui.MainViewModel
import com.phinespec.pokersim.ui.screens.main_game.CardImage
import com.phinespec.pokersim.ui.theme.DarkFeltBlue
import com.phinespec.pokersim.ui.theme.LightFeltBlue


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
        CommunityTemplate(communityCards = uiState.communityCards)
        HoleCards(player = uiState.players.first(), handStrength = uiState.handStrength)
    }
}

@Composable
fun CommunityTemplate(
    communityCards: List<Card>,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    Box(
        modifier = modifier
            .width(screenWidth / 1.5f)
            .height(screenHeight / 2)
            .offset(y = (-20).dp)
            .border(
                width = 2.dp,
                color = LightFeltBlue,
                shape = RoundedCornerShape(100.dp)
            )
            .clip(RoundedCornerShape(50)),
        contentAlignment = Alignment.Center
    ) {
        CommunityCards(communityCards = communityCards)
    }
}

@Composable
fun HoleCards(
    modifier: Modifier = Modifier,
    player: Player,
    handStrength: String
) {
    Column(
        modifier
            .offset(y = 115.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = modifier.offset(y = 8.dp)
        ) {
            CardImage(imageResource = player.holeCards.first.image)
            Spacer(modifier.width(4.dp))
            CardImage(imageResource = player.holeCards.second.image)
        }
        PlayerLabel(playerName = player.name, cash = player.cash, handStrength = handStrength)
    }
}

@Composable
fun PlayerLabel(
    modifier: Modifier = Modifier,
    playerName: String,
    cash: Double,
    handStrength: String
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
            text = playerName,
            modifier = modifier
                .weight(2f)
                .clip(CircleShape),
            style = MaterialTheme.typography.labelLarge,
            color = Color.White
        )
        Text(
            text = handStrength,
            modifier = modifier
                .weight(1f),
            style = MaterialTheme.typography.labelLarge,
            color = Color.White
        )
    }
}


@Composable
fun CommunityCards(
    communityCards: List<Card>,
    modifier: Modifier = Modifier
) {
    Row {
        communityCards.forEach { card ->
            CardImage(imageResource = card.image)
            Spacer(modifier = modifier.width(4.dp))
        }
    }
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
            .offset(x = -(300).dp, y = 140.dp)
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
//    CommunityCards()
}