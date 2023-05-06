package com.phinespec.pokersim.ui.screens.main_game

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.phinespec.pokersim.model.PlayingCard
import com.phinespec.pokersim.utils.CardFace

@Composable
fun FlipCard(
    face: CardFace = CardFace.Front,
//    isWinner: Boolean = false,
    isFaded: Boolean = false,
    modifier: Modifier = Modifier
        .padding(horizontal = 2.dp),
    front: @Composable () -> Unit = {},
    back: @Composable () -> Unit = {}
) {

    var animationPlayed by remember {
        mutableStateOf(false)
    }

    val rotation by animateFloatAsState(
        targetValue = face.angle,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        )
    )

//    val raisedValue by animateDpAsState(
//        targetValue = if (animationPlayed) (-10).dp else 0.dp,
//        animationSpec = tween()
//    )

    val fadeValue by animateFloatAsState(
        targetValue = if (animationPlayed) 0.3f else 1f,
        animationSpec = tween(
            durationMillis = 2000
        )
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Card(
        modifier = modifier
            .size(width = 60.dp, height = 90.dp)
//            .offset(y = if (isWinner) raisedValue else 0.dp)
            .graphicsLayer { rotationY = rotation }
            .alpha(if (isFaded) 0.3f else 1.0f)
            .shadow(elevation = 5.dp, shape = CircleShape, false),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(5.dp)

    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (rotation <= 90f) {
                front()
            } else {
                back()
            }
        }
    }
}