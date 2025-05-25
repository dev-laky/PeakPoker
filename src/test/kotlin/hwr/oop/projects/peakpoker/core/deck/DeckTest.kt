package hwr.oop.projects.peakpoker.core.deck

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class DeckTest : AnnotationSpec() {
  @Test
  fun `deck is initialized with 52 cards`() {
    // given
    val deck = Deck()

    // when
    val cards = deck.show()

    // then
    assertThat(cards).hasSize(52)
  }

  @Test
  fun `show returns a copy of the cards`() {
    // given
    val deck = Deck()

    // when
    val cards = deck.show()

    // then
    assertThat(cards).isNotSameAs(deck.show())
  }

  @Test
  fun `draw removes a card from the deck`() {
    // given
    val deck = Deck()
    val initialSize = deck.show().size

    // when
    val drawnCards = deck.draw()

    // then
    assertThat(deck.show()).hasSize(initialSize - 1)
    assertThat(drawnCards).hasSize(1)
    assertThat(deck.show()).doesNotContain(drawnCards[0])
  }

  @Test
  fun `draw multiple cards from the deck`() {
    // given
    val deck = Deck()
    val initialSize = deck.show().size
    val drawAmount = 5

    // when
    val drawnCards = deck.draw(drawAmount)

    // then
    assertThat(drawnCards)
      .hasSize(drawAmount)
      .describedAs("Should draw exactly the requested number of cards")

    assertThat(deck.show())
      .hasSize(initialSize - drawAmount)
      .describedAs("Deck size should be reduced by drawn amount")

    // Verify none of the drawn cards remain in the deck
    drawnCards.forEach { card ->
      assertThat(deck.show())
        .doesNotContain(card)
        .describedAs("Drawn card should not remain in the deck")
    }

    // Verify all drawn cards are unique
    assertThat(drawnCards)
      .hasSize(drawnCards.distinct().size)
      .describedAs("All drawn cards should be unique")
  }

  @Test
  fun `draw throws exception when no cards left`() {
    // given
    val deck = Deck()
    repeat(52) { deck.draw() } // draw all cards

    // when & then
    assertThat(deck.show()).isEmpty() // Check if the list is actually empty

    assertThatThrownBy { deck.draw() }
      .isInstanceOf(IllegalStateException::class.java)
  }
}
