package hwr.oop

import hwr.oop.core.Deck
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class DeckTest : AnnotationSpec() {

   /* @Test
    fun `Deck is shuffled` () {
        val deck = Deck()
        val isShuffled = deck.deckShuffle()
        assertThat(isShuffled).isTrue()
    } */

    @Test
    fun `shuffled deck is not repeated twice` () {
        // given
        val deck = Deck()

        //when
        val deckBefore = deck.peak()
        deck.shuffle()
        val deckAfter = deck.peak()

        // then
        val differentPositions = deckBefore.zip(deckAfter).count { (before, after) -> before != after }
        assertThat(differentPositions).isGreaterThan(0)
    }
}

