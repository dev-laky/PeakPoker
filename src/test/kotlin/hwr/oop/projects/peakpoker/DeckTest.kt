package hwr.oop.projects.peakpoker

import hwr.oop.projects.peakpoker.core.deck.Deck
import io.kotest.core.spec.style.AnnotationSpec
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.assertThrows

class DeckTest : AnnotationSpec() {
    @Test
    fun `deck is initialized with 52 cards`() {
        // given
        val deck = Deck()

        // when
        val cards = deck.show()

        // then
        assertEquals(52, cards.size)
    }

    @Test
    fun `show returns a copy of the cards`() {
        // given
        val deck = Deck()

        // when
        val cards = deck.show()

        // then
        assertNotSame(cards, deck.show())
    }

    @Test
    fun `draw removes a card from the deck`() {
        // given
        val deck = Deck()
        val initialSize = deck.show().size

        // when
        val drawnCard = deck.draw()

        // then
        assertEquals(initialSize - 1, deck.show().size)
        assertFalse(deck.show().contains(drawnCard))
    }

    @Test
    fun `draw throws exception when no cards left`() {
        // given
        val deck = Deck()
        repeat(52) { deck.draw() } // draw all cards

        // when & then
        assertThrows<IllegalStateException> {
            deck.draw()
        }
    }
}

