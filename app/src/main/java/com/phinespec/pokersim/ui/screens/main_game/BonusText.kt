package com.phinespec.pokersim.ui.screens.main_game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.phinespec.pokersim.ui.theme.ProgressBarGreen
import kotlinx.coroutines.delay

@Composable
fun BonusText(
    modifier: Modifier = Modifier,
    text: String = "",
    color: Color = ProgressBarGreen
) {
    var isVisible by remember {
        mutableStateOf(false)
    }

    var animationPlayed by remember {
        mutableStateOf(false)
    }

    val offsetX by animateDpAsState(
        targetValue = if (animationPlayed) 20.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 2000,
            easing = LinearEasing
        )
    )
    val offsetY by animateDpAsState(
        targetValue = if (animationPlayed) (-20).dp else 0.dp,
        animationSpec = tween(
            durationMillis = 2000,
            easing = LinearEasing
        )
    )

     AnimatedVisibility(
         modifier = modifier,
         visible = isVisible
     ) {
         Text(
             modifier = modifier
                 .offset(x = offsetX, y = offsetY),
             text = text,
             color = color,
             style = MaterialTheme.typography.titleLarge,
             fontWeight = FontWeight.Bold
         )
     }

    LaunchedEffect(key1 = true) {
        isVisible = true
        animationPlayed = true
        delay(2000)
        isVisible = false
    }
}

@Preview(showBackground = true)
@Composable
fun BonusTextPreview() {

}