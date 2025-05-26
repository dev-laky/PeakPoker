package hwr.oop.projects.peakpoker.core.deck

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
import hwr.oop.projects.peakpoker.core.exceptions.InsufficientCardsException
import hwr.oop.projects.peakpoker.core.exceptions.InsufficientChipsException

class Deck() {
  // Create list of all possible cards and shuffle it right away
  private val cards: MutableList<Card> = Suit.entries.flatMap { suit ->
    Rank.entries.map { rank ->
      Card(suit, rank)
    }
  }.toMutableList().apply { shuffle() }

  private val dealtCards: MutableList<Card> = mutableListOf()

  /**
   * Returns the current cards in the deck without modifying the deck.
   *
   * @return A list of cards currently in the deck
   */
  fun show(): List<Card> {
    return cards.toList()
  }

  /**
   * Draws a specified number of cards from the deck.
   * Drawn cards are removed from the deck and added to dealt cards.
   *
   * @param amount The number of cards to draw, defaults to 1
   * @return A list of drawn cards
   * @throws InsufficientCardsException If there aren't enough cards left in the deck
   */
  fun draw(amount: Int = 1): List<Card> {
    check(cards.size >= amount) { throw InsufficientChipsException("Not enough cards left in the deck") }
    val drawnCards = mutableListOf<Card>()

    repeat(amount) {
      val drawnCard = cards.removeAt(cards.size - 1)
      dealtCards.add(drawnCard)
      drawnCards.add(drawnCard)
    }

    return drawnCards
  }
}
