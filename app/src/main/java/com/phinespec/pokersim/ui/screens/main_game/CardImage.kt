package com.phinespec.pokersim.ui.screens.main_game

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.phinespec.pokersim.R
import com.phinespec.pokersim.model.Card

@Composable
fun CardImage(
    card: Card,
    isWinner: Boolean = false,
    isFlipped: Boolean = false,
    isFaded: Boolean = false,
    transitionTrigger: Int = 0,
    modifier: Modifier = Modifier
        .padding(horizontal = 2.dp),
) {

    var flipRotation by remember(transitionTrigger) { mutableStateOf(0f) }
    val animationSpec = tween<Float>(1000, easing = CubicBezierEasing(0.4f, 0.0f, 0.8f, 0.8f))

    var animationPlayed by remember {
        mutableStateOf(false)
    }

    val raisedValue by animateDpAsState(
        targetValue = if (animationPlayed) (-10).dp else 0.dp,
        animationSpec = tween()
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true

        // Flip card
        animate(initialValue = 0f, targetValue = 180f, animationSpec = animationSpec) { value: Float, _: Float ->
            flipRotation = value
        }
    }

    Image(
        painter = painterResource(if (isFlipped) R.drawable.card_back_red else card.image),
        modifier = modifier
            .size(width = 60.dp, height = 90.dp)
            .offset(y = if (isWinner) raisedValue else 0.dp)
            .alpha(if (isFaded) 0.5f else 1.0f)
            .shadow(elevation = 5.dp, shape = CircleShape, false),
        contentDescription = "",
        contentScale = ContentScale.FillBounds
    )
}