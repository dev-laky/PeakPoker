package hwr.oop.projects.peakpoker.core.player

import hwr.oop.projects.peakpoker.core.card.HoleCards

class Player(
    override val name: String,
    private var chips: Int = 100,
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
        require(cards.cards.size == 2) { "A player must have exactly 2 hole cards" }
        hand = cards
    }

    /**
     * Raises the player's bet to the specified amount.
     * CAUTION: Should only be called from Game.
     *
     * This method increases the player's bet to the given amount, deducting only the difference
     * between the new bet and current bet from the player's chips.
     *
     * @param chips The total amount to bet (not the additional amount)
     */
    fun raiseBetTo(chips: Int) {
        require(chips >= 0) { "Bet amount must be positive" }

        this@Player.chips -= chips - bet
        bet = chips
    }

    fun call(chips: Int) {
        require(chips > 0) { "Call amount must be positive" }

        this@Player.chips -= chips - bet
        bet = chips
    }
}
