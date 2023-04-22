package com.phinespec.pokersim.utils

import com.phinespec.pokersim.R
import timber.log.Timber


fun mapNameToImage(cardName: String): Int {
    cardNameToImageMap[cardName]?.let {
        Timber.d("Card image resource => $it")
        return it
    }
    return -1
}

private val cardNameToImageMap = mapOf(
    "2d" to R.drawable.two_of_diamonds, "2h" to R.drawable.two_of_hearts, "2c" to R.drawable.two_of_clubs, "2s" to R.drawable.two_of_spades,
    "3d" to R.drawable.three_of_diamonds, "3h" to R.drawable.three_of_hearts, "3c" to R.drawable.three_of_clubs, "3s" to R.drawable.three_of_spades,
    "4d" to R.drawable.four_of_diamonds, "4h" to R.drawable.four_of_hearts, "4c" to R.drawable.four_of_clubs, "4s" to R.drawable.four_of_spades,
    "5d" to R.drawable.five_of_diamonds, "5h" to R.drawable.five_of_hearts, "5c" to R.drawable.five_of_clubs, "5s" to R.drawable.five_of_spades,
    "6d" to R.drawable.six_of_diamonds, "6h" to R.drawable.six_of_hearts, "6c" to R.drawable.six_of_clubs, "6s" to R.drawable.six_of_spades,
    "7d" to R.drawable.seven_of_diamonds, "7h" to R.drawable.seven_of_hearts, "7c" to R.drawable.seven_of_clubs, "7s" to R.drawable.seven_of_spades,
    "8d" to R.drawable.eight_of_diamonds, "8h" to R.drawable.eight_of_hearts, "8c" to R.drawable.eight_of_clubs, "8s" to R.drawable.eight_of_spades,
    "9d" to R.drawable.nine_of_diamonds, "9h" to R.drawable.nine_of_hearts, "9c" to R.drawable.nine_of_clubs, "9s" to R.drawable.nine_of_spades,
    "10d" to R.drawable.ten_of_diamonds, "10h" to R.drawable.ten_of_hearts, "10c" to R.drawable.ten_of_clubs, "10s" to R.drawable.ten_of_spades,
    "jd" to R.drawable.jack_of_diamonds, "jh" to R.drawable.jack_of_hearts, "jc" to R.drawable.jack_of_clubs, "js" to R.drawable.jack_of_spades,
    "qd" to R.drawable.queen_of_diamonds, "qh" to R.drawable.queen_of_hearts, "qc" to R.drawable.queen_of_clubs, "qs" to R.drawable.queen_of_spades,
    "kd" to R.drawable.king_of_diamonds, "kh" to R.drawable.king_of_hearts, "kc" to R.drawable.king_of_clubs, "ks" to R.drawable.king_of_spades,
    "ad" to R.drawable.ace_of_diamonds, "ah" to R.drawable.ace_of_hearts, "ac" to R.drawable.ace_of_clubs, "as" to R.drawable.ace_of_spades,
)