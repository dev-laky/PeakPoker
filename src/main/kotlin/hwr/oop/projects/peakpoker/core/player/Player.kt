package hwr.oop.projects.peakpoker.core.player

import hwr.oop.projects.peakpoker.core.card.Card

class Player(
    val name: String,
    private var chips: Int = 0,
    private var hand: List<Card> = emptyList(),
    private var bet: Int = 0,
    private var isFolded: Boolean = false,
    private var isAllIn: Boolean = false
) {
    val currentBet: Int get() = bet
    val currentChips: Int get() = chips
    val currentHand: List<Card> get() = hand.toList()

    fun raiseBet(amount: Int) {
        when {
            amount < 0 -> throw IllegalArgumentException("Bet amount must be positive")
            isFolded -> throw IllegalStateException("Cannot raise bet after folding")
            isAllIn -> throw IllegalStateException("Cannot raise bet after going all-in")
        }
        bet += amount
        chips -= amount
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
}
