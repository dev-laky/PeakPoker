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

  fun setBetAmount(chips: Int) {
    require(chips > 0) { "Chips amount must be greater than zero" }

    this.chips -= chips - bet
    bet = chips
  }

  fun fold() {
    isFolded = true
  }

  fun allIn() {
    setBetAmount(this.chips)
    isAllIn = true
  }
}
