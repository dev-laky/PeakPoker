package hwr.oop.projects.peakpoker.core.card

import hwr.oop.projects.peakpoker.core.deck.Deck
import hwr.oop.projects.peakpoker.core.exceptions.DuplicateCardException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidCardConfigurationException
import hwr.oop.projects.peakpoker.core.game.RoundPhase
import kotlinx.serialization.Serializable

@Serializable
class CommunityCards(
  private val cards: MutableList<Card> = mutableListOf(),
) : Iterable<Card> by cards {

  init {
    if (cards.isNotEmpty() && cards.size !in listOf(3, 4, 5)) {
      throw InvalidCardConfigurationException("Community cards must be empty or contain exactly 3, 4, or 5 cards.")
    }
    if (cards.distinct().size != cards.size) {
      throw DuplicateCardException("Community cards must not contain duplicates.")
    }
  }

  fun dealCommunityCards(roundPhase: RoundPhase, deck: Deck) {
    when (roundPhase) {
      RoundPhase.PRE_FLOP -> throw IllegalStateException("Cannot deal community cards before the flop")
      RoundPhase.FLOP -> {
        cards.addAll(deck.draw(3))
      }

      RoundPhase.TURN -> {
        cards.addAll(deck.draw(1))
      }

      RoundPhase.RIVER -> {
        cards.addAll(deck.draw(1))
      }

      RoundPhase.SHOWDOWN -> throw IllegalStateException("Cannot deal community cards after the showdown")
    }
  }

  fun cards(): List<Card> {
    return cards.toList()
  }
}
