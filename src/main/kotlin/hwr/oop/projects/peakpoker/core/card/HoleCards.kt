package hwr.oop.projects.peakpoker.core.card

import hwr.oop.projects.peakpoker.core.exceptions.DuplicateCardException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidCardConfigurationException
import hwr.oop.projects.peakpoker.core.player.PlayerInterface

class HoleCards(
  private val cards: List<Card>,
  val player: PlayerInterface,
) : Iterable<Card> by cards {
  init {
    if (cards.isNotEmpty() && cards.size != 2) {
      throw InvalidCardConfigurationException("Hole cards must be empty or contain exactly two cards.")
    }
    if (cards.distinct().size != cards.size) {
      throw DuplicateCardException("Hole cards must not contain duplicates.")
    }
  }

  fun getCards(): List<Card> {
    return cards
  }
}
