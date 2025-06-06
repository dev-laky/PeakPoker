package hwr.oop.projects.peakpoker.core.card

import hwr.oop.projects.peakpoker.core.deck.Deck
import hwr.oop.projects.peakpoker.core.round.Round
import hwr.oop.projects.peakpoker.core.round.RoundPhase

class CommunityCards(
  cards: List<Card>,
  val round: Round, // TODO: Evaluate if round is needed here (see at dealCommunityCards)
) : Iterable<Card> by cards {

  var cards = cards
    private set

  init {
    require(cards.isEmpty() || cards.size == 5) { "Community cards must be empty or contain exactly five cards." }
    require(cards.distinct().size == cards.size) { "Community cards must not contain duplicates." }
  }

  fun dealCommunityCards(roundPhase: RoundPhase, deck: Deck) {
    when (roundPhase) {
      RoundPhase.PRE_FLOP -> throw IllegalStateException("Cannot deal community cards before the flop")
      RoundPhase.FLOP -> {
        cards = cards + deck.draw(3)
      }

      RoundPhase.TURN -> {
        cards = cards + deck.draw(1)
      }

      RoundPhase.RIVER -> {
        cards = cards + deck.draw(1)
      }

      RoundPhase.SHOWDOWN -> throw IllegalStateException("Cannot deal community cards after the showdown")
    }
  }
}
