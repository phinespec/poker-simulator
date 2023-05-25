package com.phinespec.pokersim.ui.screens.main_game

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.phinespec.pokersim.ui.theme.ProgressBarGreen
import com.phinespec.pokersim.ui.theme.ProgressBarOrange
import com.phinespec.pokersim.ui.theme.ProgressBarRed
import com.phinespec.pokersim.ui.theme.ProgressBarYellow


@Composable
fun CountdownBar(
    seconds: Int,
    modifier: Modifier = Modifier,
) {
    val twentySecondsMillis = 1 * 30 * 1000
    val millisUntilFinished = seconds * 1000
    val finishedSeconds = twentySecondsMillis - millisUntilFinished
    val totalProgress = ((finishedSeconds.toFloat() / twentySecondsMillis.toFloat()))

    val progressAnimation by animateFloatAsState(
        targetValue = totalProgress,
        animationSpec = tween(1000, easing = LinearEasing),
    ) { result ->

    }
    Box(
        modifier
    ) {
        CircularProgressIndicator(
            progress = progressAnimation,
            color = when {
                totalProgress < .45f -> ProgressBarGreen
                totalProgress >= .45f && totalProgress <= .75f -> ProgressBarYellow
                totalProgress >= .75f && totalProgress <= .95f -> ProgressBarOrange
                else -> ProgressBarRed
            },
            strokeWidth = 8.dp,
            modifier = modifier
                .width(100.dp)
                .height(100.dp)
        )
    }
}