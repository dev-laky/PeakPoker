package hwr.oop.projects.peakpoker.core.card

import hwr.oop.projects.peakpoker.core.exceptions.InvalidCardConfigurationException
import hwr.oop.projects.peakpoker.core.game.GameInterface
import hwr.oop.projects.peakpoker.core.exceptions.DuplicateCardException

class CommunityCards(
  cards: List<Card>,
  val game: GameInterface,
) : Iterable<Card> by cards {

  var cards: List<Card> = cards
    private set

  init {
    if (cards.isNotEmpty() && cards.size != 5) {
      throw InvalidCardConfigurationException("Community cards must be empty or contain exactly five cards.")
    }
    if (cards.distinct().size != cards.size) {
      throw DuplicateCardException("Community cards must not contain duplicates.")
    }
  }
}
