package hwr.oop.projects.peakpoker.core.deck

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit

class Deck(
    private val cards: MutableList<Card> = Suit.values().flatMap { suit ->
        Rank.values().map { rank ->
            Card(suit, rank)
        }
    }.toMutableList().apply { shuffle() }
) {
    private val dealtCards: MutableList<Card> = mutableListOf()

    fun show(): List<Card> {
        return cards.toList()
    }

    fun draw(): Card {
        check(cards.isNotEmpty()) { throw IllegalStateException("No cards left in the deck") }
        val drawnCard = cards.removeAt(cards.size - 1)
        dealtCards.add(drawnCard)
        return drawnCard
    }
}
