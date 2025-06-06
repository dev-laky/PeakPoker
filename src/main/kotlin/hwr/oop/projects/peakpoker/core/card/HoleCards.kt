package hwr.oop.projects.peakpoker.core.card

import hwr.oop.projects.peakpoker.core.exceptions.DuplicateCardException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidCardConfigurationException
import hwr.oop.projects.peakpoker.core.player.Player

class HoleCards(
  val cards: List<Card>,
  val player: Player, // TODO: Evalate if player is needed (@timwidmoser might use this)
) : Iterable<Card> by cards {
  init {
    if (cards.isNotEmpty() && cards.size != 2) {
      throw InvalidCardConfigurationException("Hole cards must be empty or contain exactly two cards.")
    }
    if (cards.distinct().size != cards.size) {
      throw DuplicateCardException("Hole cards must not contain duplicates.")
    }
  }
}
