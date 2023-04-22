package com.phinespec.pokersim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.phinespec.pokersim.ui.screens.MainGameScreen
import com.phinespec.pokersim.ui.theme.PokerSimTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PokerSimTheme {
                MainGameScreen()
            }
        }
    }
}