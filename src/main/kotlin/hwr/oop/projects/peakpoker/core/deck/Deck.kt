package hwr.oop.projects.peakpoker.core.deck

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit

class Deck() {
  // Create a list of all possible cards and shuffle it right away
  private val cards: MutableList<Card> = Suit.entries.flatMap { suit ->
    Rank.entries.map { rank ->
      Card(suit, rank)
    }
  }.toMutableList().apply { shuffle() }

  private val dealtCards: MutableList<Card> = mutableListOf()

  fun draw(amount: Int = 1): List<Card> {
    check(cards.size >= amount) { throw IllegalStateException("Not enough cards left in the deck") }
    val drawnCards = mutableListOf<Card>()

    repeat(amount) {
      val drawnCard = cards.removeAt(cards.size - 1)
      dealtCards.add(drawnCard)
      drawnCards.add(drawnCard)
    }

    return drawnCards
  }
}
