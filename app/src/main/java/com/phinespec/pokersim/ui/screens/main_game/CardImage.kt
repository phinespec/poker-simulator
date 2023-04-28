package com.phinespec.pokersim.ui.screens.main_game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
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
    modifier: Modifier = Modifier,
    card: Card
) {
    Image(
        painter = painterResource(card.image),
        modifier = modifier
            .size(width = 60.dp, height = 90.dp)
            .shadow(elevation = 5.dp, shape = CircleShape, false),
        contentDescription = "",
        contentScale = ContentScale.FillBounds
    )
}