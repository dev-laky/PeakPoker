package hwr.oop.projects.peakpoker.core.player

import hwr.oop.projects.peakpoker.core.card.Card

class Player(
    val name: String,
    private var chips: Int = 0,
    private var bet: Int = 0,
) {
    init {
        require(chips >= 0) { "Chips amount must be non-negative" }
        require(bet >= 0) { "Bet amount must be non-negative" }
        require(name.isNotBlank()) { "Player name cannot be blank" }
    }

    private var isFolded: Boolean = false
    private var isAllIn: Boolean = false
    private var hand: List<Card> = emptyList()

    fun getBetAmount(): Int {
        return bet
    }

    fun getChipsAmount(): Int {
        return chips
    }

    fun isFolded(): Boolean {
        return isFolded
    }

    fun isAllIn(): Boolean {
        return isAllIn
    }

    fun assignCards(cards: List<Card>) {
        require(hand.isEmpty()) { "Cannot assign cards to a player who already has cards" }
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

    fun fold() {
        isFolded = true
    }

    fun allIn() {
        isAllIn = true
    }
}
