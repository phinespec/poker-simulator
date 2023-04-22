package com.phinespec.pokersim.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.phinespec.pokersim.R
import com.phinespec.pokersim.model.Player
import com.phinespec.pokersim.ui.MainViewModel
import com.phinespec.pokersim.ui.screens.main_game.CardImage
import com.phinespec.pokersim.ui.theme.DarkFeltBlue
import com.phinespec.pokersim.ui.theme.LightFeltBlue
import com.phinespec.pokersim.ui.theme.PokerSimTheme


@Composable
fun MainGameScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {

    val gameUiState by viewModel.uiState.collectAsState()

    GameLayout(players = gameUiState.players, onClickReset = { viewModel.resetGame() })

}

@Composable
fun GameLayout(
    modifier: Modifier = Modifier,
    players: List<Player>,
    onClickReset: () -> Unit
) {
    TableTop(players) { onClickReset() }
}

@Composable
fun TableTop(
    players: List<Player>,
    onClickReset: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkFeltBlue),
        contentAlignment = Alignment.Center
    ) {
        ResetButton { onClickReset() }
        CommunityTemplate()
        HoleCards(player = players.first())
    }
}

@Composable
fun CommunityTemplate(modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    Box(
        modifier = modifier
            .width(screenWidth / 1.5f)
            .height(screenHeight / 2)
            .offset(y = -20.dp)
            .alpha(0.5f)
            .border(
                width = 2.dp,
                color = LightFeltBlue,
                shape = RoundedCornerShape(100.dp)
            )
            .clip(RoundedCornerShape(50)),
        contentAlignment = Alignment.Center
    ) {

    }
}

@Composable
fun HoleCards(
    modifier: Modifier = Modifier,
    player: Player
) {
    Column(
        modifier
            .offset(y = 115.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            CardImage(imageResource = player.holeCards.first.image)
            Spacer(modifier.width(4.dp))
            CardImage(imageResource = player.holeCards.second.image)
        }
        PlayerLabel(playerName = player.name, cash = player.cash)
    }
}

@Composable
fun PlayerLabel(
    modifier: Modifier = Modifier,
    playerName: String,
    cash: Double
) {
    Button(
        modifier = modifier
            .width(148.dp)
            .height(32.dp)
            .offset(y = (-16).dp),
        onClick = {}
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
            text = cash.toString(),
            modifier = modifier
                .weight(1f),
            style = MaterialTheme.typography.labelLarge,
            color = Color.White
        )
    }
}


@Composable
fun communityCards(modifier: Modifier = Modifier) {
    Row {
        repeat(5) {
            Box(
                modifier
                    .border(
                        width = 1.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(0.dp)
                    )
            ) {
                Image(painter = painterResource(R.drawable.eight_of_clubs), contentDescription = "")
            }
        }
    }
}

@Composable
fun ResetButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    ElevatedButton(
        onClick = { onClick() },
        modifier = modifier
            .offset(x = -(300).dp, y = 140.dp)
    ) {
        Text("Reset")
    }
}


@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape")
@Composable
fun TablePreview() {
    PokerSimTheme {
        PlayerLabel(
            playerName = "Some Name",
            cash = 10.75
        )
    }
}