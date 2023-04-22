package com.phinespec.pokersim.model

//class Game(val player: Player) {
//
//    fun restart() {
//
//    }
//
//    fun checkHand(completion: (String) -> Unit) {
//        if (player.hand.size != 2) { return }
//        val result = if (player.leftCard.rank == player.rightCard.rank) {
//            val pocketRank = player.leftCard.rank
//            "Nice!! Pocket ${pocketRank}s"
//        } else {
//            "Nothing special..."
//        }
//        completion(result)
//    }
//
//    fun help() {
//        println("I'm here to help")
//    }
//
//    fun printCards() {
//        player.hand.forEach { card ->
//            println(card.asFormattedString())
//        }
//    }
//}