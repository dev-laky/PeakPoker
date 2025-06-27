package hwr.oop.projects.peakpoker.core.deck

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Deck() {
  /**
   * Thrown when trying to draw more cards than are available in the deck
   */
  class InsufficientCardsException(message: String) :
    IllegalStateException(message)

  // Create a list of all possible cards and shuffle it right away
  @Transient
  private val cards: MutableList<Card> = Suit.entries.flatMap { suit ->
    Rank.entries.map { rank ->
      Card(suit, rank)
    }
  }.toMutableList().apply { shuffle() }

  private val dealtCards: MutableList<Card> = mutableListOf()

  /**
   * Draws a specified number of cards from the deck.
   * Drawn cards are removed from the deck and added to dealt cards.
   *
   * @param amount The number of cards to draw, defaults to 1
   * @return A list of drawn cards
   * @throws InsufficientCardsException If there aren't enough cards left in the deck
   */
  fun draw(amount: Int = 1): List<Card> {
    check(cards.size >= amount) { throw InsufficientCardsException("Not enough cards left in the deck") }
    val drawnCards = mutableListOf<Card>()

    while (drawnCards.size < amount) {
      val drawnCard = cards.removeAt(cards.size - 1)
      if (dealtCards.contains(drawnCard)) continue
      dealtCards.add(drawnCard)
      drawnCards.add(drawnCard)
    }

    return drawnCards
  }
}
