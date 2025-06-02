package hwr.oop.projects.peakpoker.core.player

import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.exceptions.InsufficientChipsException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidBetAmountException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidPlayerStateException

/**
 * Represents a poker player in a game.
 *
 * A poker player has a name, a certain amount of chips, and can perform
 * various actions during a poker game like betting, folding, and going all-in.
 *
 * @property name The name of the player
 * @property chips The current chip count of the player (default: 100)
 */
class PokerPlayer(
  override val name: String,
  private var chips: Int = 100,
) : Player {
  init {
    if (chips < 0) {
      throw InsufficientChipsException("Chips amount must be non-negative")
    }
    if (name.isBlank()) {
      throw InvalidPlayerStateException("Player name cannot be blank")
    }
  }

  /**
   * Indicates whether the player has folded in the current hand.
   * When true, the player is no longer participating in the current round.
   */
  var isFolded: Boolean = false

  /**
   * Indicates whether the player has gone all-in.
   * When true, the player has committed all their chips to the pot.
   */
  var isAllIn: Boolean = false

  /**
   * The player's current hole cards.
   */
  private var hand: HoleCards = HoleCards(emptyList(), this)

  /**
   * The player's current bet amount in the active round.
   */
  private var bet: Int = 0

  /**
   * Returns the current bet amount for this player.
   *
   * @return The amount of chips the player has bet in the current round
   */
  fun getBet(): Int {
    return bet
  }

  /**
   * Returns the number of chips the player currently has.
   *
   * @return The player's available chip count
   */
  fun getChips(): Int {
    return chips
  }

  /**
   * Returns the player's current hole cards.
   *
   * @return The player's hand
   */
  fun getHand(): HoleCards {
    return hand
  }

  /**
   * Assigns a new hand of cards to the player.
   *
   * @param cards The hole cards to assign to this player
   */
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

  /**
   * Marks the player as having folded their hand.
   * A folded player cannot participate further in the current round.
   */
  fun fold() {
    isFolded = true
  }

  /**
   * Makes the player go all-in, committing all remaining chips to the pot.
   * Updates the player's bet amount and marks them as all-in.
   */
  fun allIn() {
    val totalBet = chips + bet
    chips = 0
    setBetAmount(totalBet)
    isAllIn = true
  }
}
