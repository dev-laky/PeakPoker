package hwr.oop.projects.peakpoker.core.player

import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.exceptions.InsufficientChipsException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidBetAmountException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidPlayerStateException

class PokerPlayer(
  val name: String,
  private var chips: Int = 100,
) {

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

  fun chips(): Int {
    return chips
  }

  fun hand(): HoleCards {
    return hand
  }

  fun bet(): Int {
    return bet
  }

  fun isFolded(): Boolean {
    return isFolded
  }

  fun isAllIn(): Boolean {
    return isAllIn
  }

  fun hasChecked(): Boolean {
    return hasChecked
  }

  fun resetRoundState() {
    isFolded = false
    isAllIn = false
  }

  fun resetBet() {
    bet = 0
    hasChecked = false
  }

  fun assignHand(cards: HoleCards) {
    hand = cards
  }

  fun setBetAmount(chips: Int) {
    if (chips <= 0) {
      throw InvalidBetAmountException("Chips amount must be greater than zero")
    }

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

  /**
   * Adds the specified amount of chips to the player's stack.
   *
   * @param amount The amount of chips to add
   * @throws InvalidBetAmountException If the amount is negative
   */
  fun addChips(amount: Int) {
    if (amount < 0) throw InvalidBetAmountException("Cannot add negative amount of chips")
    chips += amount
  }
}
