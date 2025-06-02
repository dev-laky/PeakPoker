package hwr.oop.projects.peakpoker.core.player

import hwr.oop.projects.peakpoker.core.card.HoleCards

class PokerPlayer(
    override val name: String,
    private var chips: Int = 100,
) : Player {

    init {
        require(chips >= 0) { "Chips amount must be non-negative" }
        require(name.isNotBlank()) { "PokerPlayer name cannot be blank" }
    }

    var isFolded: Boolean = false
        private set
    var isAllIn: Boolean = false
        private set
    var hasChecked: Boolean = false
        private set

    private var hand: HoleCards = HoleCards(emptyList(), this)
    private var bet: Int = 0

    fun resetRoundState() {
        isFolded = false
        isAllIn = false
    }

    fun getBet(): Int {
        return bet
    }

    fun resetBet() {
        bet = 0
        hasChecked = false
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

    fun setBetAmount(chips: Int) {
        require(chips > 0) { "Chips amount must be greater than zero" }

        this.chips -= chips - bet
        bet = chips
    }

    fun check() {
        hasChecked = true
    }

    fun fold() {
        isFolded = true
    }

    fun allIn() {
        setBetAmount(this.chips + this.bet)
        isAllIn = true
    }
}
