package com.phinespec.pokersim.ui.screens.main_game

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.phinespec.pokersim.model.Card

@Composable
fun CardImage(
    modifier: Modifier = Modifier
        .padding(horizontal = 2.dp),
    card: Card,
    winner: Boolean = false
) {

    var animationPlayed by remember {
        mutableStateOf(false)
    }

    val raisedValue by animateDpAsState(
        targetValue = if (animationPlayed) (-10).dp else 0.dp,
        animationSpec = tween(
            delayMillis = 1000
        )
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Image(
        painter = painterResource(card.image),
        modifier = modifier
            .size(width = 60.dp, height = 90.dp)
            .offset(y = if (winner) raisedValue else 0.dp)
            .shadow(elevation = 5.dp, shape = CircleShape, false),
        contentDescription = "",
        contentScale = ContentScale.FillBounds
    )
}