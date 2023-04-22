package com.phinespec.pokersim.ui.screens.main_game

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        modifier = modifier.size(width = 50.dp, height = 80.dp),
        contentDescription = "",
        contentScale = ContentScale.FillBounds
    )
}