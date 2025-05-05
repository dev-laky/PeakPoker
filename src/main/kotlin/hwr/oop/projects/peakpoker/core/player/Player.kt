package hwr.oop.projects.peakpoker.core.player

import hwr.oop.projects.peakpoker.core.card.Card

class Player(
    val name: String,
    var stack: Int,
    private var hand: List<Card> = emptyList(),
    private var bet: Int = 0,
    var totalBet: Int = 0,
    var status: PlayerStatus = PlayerStatus.ACTIVE,
    private var isFolded: Boolean = false,
    private var isAllIn: Boolean = false
) {
    val currentBet: Int get() = bet
    val currentStack: Int get() = stack
    val currentHand: List<Card> get() = hand.toList()

    fun raiseBet(amount: Int) {
        when {
            amount < 0 -> throw IllegalArgumentException("Bet amount must be positive")
            isFolded -> throw IllegalStateException("Cannot raise bet after folding")
            isAllIn -> throw IllegalStateException("Cannot raise bet after going all-in")
        }
        bet += amount
        stack -= amount
    }

    fun getBet(): Int {
        return currentBet
    }

    fun fold() {
        isFolded = true
    }

    fun isFolded(): Boolean {
        return isFolded
    }

    fun allIn() {
        isAllIn = true
    }

    fun isAllIn(): Boolean {
        return isAllIn
    }

    // TODO: Implement the hand functionality and think about it's logic

    fun bet(player: Player, action: String, amount: Int = 0) {
        if (player.status != PlayerStatus.ACTIVE) {
            println("${player.name} cannot place bet - status: ${player.status}.")
            return
        }
        fun applyBet(player: Player, amount: Int) {
            player.stack -= amount
            player.bet += amount
            player.totalBet += amount
            pot += amount
        }
        when (action.lowercase()) {
            "fold" -> {
                player.status = PlayerStatus.FOLDED
                println("${player.name} folded.")
            }

            "check" -> {
                if (player.bet < currentBetAmount) {
                    println("${player.name} bet is less than $currentBetAmount.")
                } else {
                    println("${player.name} checks.")
                }
            }

            "call" -> {
                val toCall = currentBetAmount - player.currentBet
                if (toCall <= 0) {
                    println("${player.name} already bet the necessary amount.")
                    return
                }

                if(player.stack <= toCall) {
                    //player is all-in
                    val allInAmount = player.stack
                    player.status = PlayerStatus.ALL_IN
                    println("${player.name} is all-in with $allInAmount.")
                    applyBet(player,allInAmount)
                } else {
                    println("${player.name} called $toCall.")
                    applyBet(player,toCall)
                }
            }

            "raise" -> {
                val raiseTo = amount
                if (raiseTo <= currentBetAmount) {
                    println("invalid raise: $raiseTo is not higher than current bet: $currentBetAmount.")
                    return
                }

                //val toPutIn stands for the amount the Player has to add to the pot to execute his action
                val toPutIn = raiseTo - player.currentBet
                if (player.stack <= toPutIn) {
                    // all-in raise
                    val allInAmount = player.stack
                    val newBet = player.bet + allInAmount
                    player.status = PlayerStatus.ALL_IN
                    println("${player.name} is all-in with $newBet.")
                    applyBet(player, allInAmount)
                    currentBetAmount = newBet
                } else {
                    println("${player.name} raised to $toPutIn.")
                    applyBet(player,toPutIn)
                    currentBetAmount = raiseTo
                }
            }

            else -> {
                println("unknown action: $action")
            }
        }
        betHistory.add("${player.name} $action ${if (amount > 0) "($amount)" else ""}".trim())
    }
    // TODO setBlinds() Method + Phasemanagement (Preflop, ...) + sidePot for allIn
}

enum class PlayerStatus {
    ACTIVE, FOLDED, ALL_IN
}
