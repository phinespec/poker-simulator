package com.phinespec.pokersim.ui.screens.main_game

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.phinespec.pokersim.ui.theme.ProgressBarGreen
import com.phinespec.pokersim.ui.theme.ProgressBarOrange
import com.phinespec.pokersim.ui.theme.ProgressBarRed
import com.phinespec.pokersim.ui.theme.ProgressBarYellow
import kotlinx.coroutines.delay


@Composable
fun CountdownBar(
    modifier: Modifier = Modifier,
    didFinish: (Float) -> Unit
) {

    var progress by remember { mutableStateOf(0f) }
    val progressAnimation by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(2000, easing = LinearEasing),
    ) {
        result -> didFinish(result)

    }

    Box(
        modifier
    ) {
        CircularProgressIndicator(
            progress = progressAnimation,
            color = when {
                progress < .45f -> ProgressBarGreen
                progress >= .45f && progress <= .75f -> ProgressBarYellow
                progress >= .75f && progress <= .95f -> ProgressBarOrange
                else -> ProgressBarRed
            },
            strokeWidth = 8.dp,
            modifier = modifier
                .width(100.dp)
                .height(100.dp)
        )
    }

    LaunchedEffect(key1 = Unit) {
        while (progress < 1f) {
            delay(1000)
            progress += .05f
        }
    }
}

@Preview
@Composable
fun PreviewCountdown() {
    CountdownBar {}
}