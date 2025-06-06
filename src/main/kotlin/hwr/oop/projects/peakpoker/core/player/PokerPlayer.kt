package hwr.oop.projects.peakpoker.core.player

import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.exceptions.InsufficientChipsException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidBetAmountException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidPlayerStateException

class PokerPlayer(
  override val name: String,
  private var chips: Int = 100,
) : Player {

  init {
    if (chips < 0) {
      throw InsufficientChipsException("Chips amount must be non-negative")
    }
    if (name.isBlank()) {
      throw InvalidPlayerStateException("PokerPlayer name cannot be blank")
    }
  }

  private var isFolded: Boolean = false
  private var isAllIn: Boolean = false
  private var hasChecked: Boolean = false

  private var hand: HoleCards = HoleCards(emptyList(), this)
  private var bet: Int = 0

  override fun chips(): Int {
    return chips
  }

  override fun hand(): HoleCards {
    return hand
  }

  override fun bet(): Int {
    return bet
  }

  override fun isFolded(): Boolean {
    return isFolded
  }

  override fun isAllIn(): Boolean {
    return isAllIn
  }

  override fun hasChecked(): Boolean {
    return hasChecked
  }

  override fun resetRoundState() {
    isFolded = false
    isAllIn = false
  }

  override fun resetBet() {
    bet = 0
    hasChecked = false
  }

  override fun assignHand(cards: HoleCards) {
    hand = cards
  }

  override fun setBetAmount(chips: Int) {
    if (chips <= 0) {
      throw InvalidBetAmountException("Chips amount must be greater than zero")
    }

    this.chips -= chips - bet
    bet = chips
  }

  override fun check() {
    hasChecked = true
  }

  override fun fold() {
    isFolded = true
  }

  override fun allIn() {
    setBetAmount(this.chips + this.bet)
    isAllIn = true
  }
}
