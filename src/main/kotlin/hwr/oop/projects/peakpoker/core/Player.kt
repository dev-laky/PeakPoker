package hwr.oop.projects.peakpoker.core

class Player(
    val name: String,
    private var chips: Int = 0,
    private var hand: List<Card> = emptyList(),
    private var bet: Int = 0,
    private var isFolded: Boolean = false,
    private var isAllIn: Boolean = false
){
    val currentBet: Int get() = bet
    val currentChips: Int get() = chips
    val currentHand: List<Card> get() = hand.toList()

    fun raise_bet(amount: Int) {
        if (amount < 0) {
            throw IllegalArgumentException("Bet amount must be positive")
        } else if (isFolded) {
            throw IllegalStateException("Cannot raise bet after folding")
        } else if (isAllIn) {
            throw IllegalStateException("Cannot raise bet after going all-in")
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

    fun all_in() {
        isAllIn = true
    }

    fun isAllIn(): Boolean {
        return isAllIn
    }

    // TODO: Implement the hand functionality and think about it's logic
}
