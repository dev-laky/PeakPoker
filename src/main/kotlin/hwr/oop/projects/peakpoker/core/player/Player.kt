package hwr.oop.projects.peakpoker.core.player

import hwr.oop.projects.peakpoker.core.card.HoleCards

class Player(
    override val name: String,
    private var chips: Int = 0,
) : PlayerInterface {
    init {
        require(chips >= 0) { "Chips amount must be non-negative" }
        require(name.isNotBlank()) { "Player name cannot be blank" }
    }

    var isFolded: Boolean = false
    var isAllIn: Boolean = false
    private var hand: HoleCards = HoleCards(emptyList(), this)
    private var bet: Int = 0

    fun getBet(): Int {
        return bet
    }

    fun getChips(): Int {
        return chips
    }

    fun getHand(): HoleCards {
        return hand
    }

    fun assignHand(cards: HoleCards) {
        hand = cards
    }

    fun raiseBet(amount: Int) {
        when {
            amount < 0 -> throw IllegalArgumentException("Bet amount must be positive")
            isFolded -> throw IllegalStateException("Cannot raise bet after folding")
            isAllIn -> throw IllegalStateException("Cannot raise bet after going all-in")
        }
        bet += amount
        chips -= amount
    }
}
