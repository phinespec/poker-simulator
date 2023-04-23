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

@Composable
fun CardImage(
    modifier: Modifier = Modifier,
    imageResource: Int
) {
    Image(
        painter = painterResource(imageResource),
        modifier = modifier
            .size(width = 60.dp, height = 90.dp)
            .shadow(elevation = 20.dp, shape = CircleShape, false),
        contentDescription = "",
        contentScale = ContentScale.FillBounds
    )
}