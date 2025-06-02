package hwr.oop.projects.peakpoker.core.player

import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.exceptions.InsufficientChipsException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidBetAmountException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidPlayerStateException

class Player(
  override val name: String,
  private var chips: Int = 100,
) : PlayerInterface {
  init {
    if (chips < 0) {
      throw InsufficientChipsException("Chips amount must be non-negative")
    }
    if (name.isBlank()) {
      throw InvalidPlayerStateException("Player name cannot be blank")
    }
  }

  var isFolded: Boolean = false
    private set
  var isAllIn: Boolean = false
    private set

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

  /**
   * Sets the bet amount for the player.
   *
   * @param chips The amount of chips to bet
   * @throws InvalidBetAmountException If the chips amount is not greater than zero
   */
  fun setBetAmount(chips: Int) {
    if (chips <= 0) {
      throw InvalidBetAmountException("Chips amount must be greater than zero")
    }
    this.chips -= chips - bet
    bet = chips
  }

  fun fold() {
    isFolded = true
  }

  fun allIn() {
    val totalBet = chips + bet
    chips = 0
    setBetAmount(totalBet)
    isAllIn = true
  }
}
