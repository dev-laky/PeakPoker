package hwr.oop.projects.peakpoker.core.deck

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit

class Deck() {
  // Create list of all possible cards and shuffle it right away
  private val cards: MutableList<Card> = Suit.values().flatMap { suit ->
    Rank.values().map { rank ->
      Card(suit, rank)
    }
  }.toMutableList().apply { shuffle() }

  private val dealtCards: MutableList<Card> = mutableListOf()

  fun show(): List<Card> {
    return cards.toList()
  }

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
