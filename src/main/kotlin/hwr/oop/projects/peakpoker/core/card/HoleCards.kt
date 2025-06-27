package hwr.oop.projects.peakpoker.core.card

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class HoleCards(
  val cards: List<Card>,
  @Transient val player: PokerPlayer? = null,
) : Iterable<Card> by cards {
  /**
   * Exception thrown when duplicate cards are found in the hole cards
   */
  class DuplicateCardException(message: String) : IllegalStateException(message)

  /**
   * Exception thrown when the number of hole cards is invalid
   */
  class InvalidCardConfigurationException(message: String) :
    IllegalStateException(message)

  init {
    if (cards.isNotEmpty() && cards.size != 2) {
      throw InvalidCardConfigurationException("Hole cards must be empty or contain exactly two cards.")
    }
    if (cards.distinct().size != cards.size) {
      throw DuplicateCardException("Hole cards must not contain duplicates.")
    }
  }
}
