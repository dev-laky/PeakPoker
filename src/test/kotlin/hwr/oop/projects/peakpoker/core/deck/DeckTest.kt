package hwr.oop.projects.peakpoker.core.deck

import hwr.oop.projects.peakpoker.core.exceptions.InsufficientCardsException
import hwr.oop.projects.peakpoker.core.card.Card
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class DeckTest : AnnotationSpec() {

  // Helper method to draw all remaining cards from the deck
  private fun drawAllCards(deck: Deck): List<Card> {
    val allCards = mutableListOf<Card>()
    try {
      while (true) {
        allCards.addAll(deck.draw())
      }
    } catch (e: IllegalStateException) {
      // Expected when deck is empty
    }
    return allCards
  }

  @Test
  fun `deck is initialized with 52 cards`() {
    // given
    val deck = Deck()

    // when
    val allCards = drawAllCards(deck)

    // then
    assertThat(allCards.size).isEqualTo(52)
  }

  @Test
  fun `draw returns different cards each time`() {
    // given
    val deck = Deck()

    // when
    val firstCard = deck.draw().first()
    val secondCard = deck.draw().first()

    // then
    assertThat(firstCard).isNotEqualTo(secondCard)
  }

  @Test
  fun `draw removes a card from the deck`() {
    // given
    val deck = Deck()

    // when
    val drawnCard = deck.draw().first()
    val remainingCards = drawAllCards(deck)

    // then
    assertThat(remainingCards.size).isEqualTo(51)
    assertThat(remainingCards).doesNotContain(drawnCard)
  }

  @Test
  fun `draw multiple cards from the deck`() {
    // given
    val deck = Deck()
    val drawAmount = 5

    // when
    val drawnCards = deck.draw(drawAmount)
    val remainingCards = drawAllCards(deck)

    // then
    assertThat(drawnCards.size)
      .isEqualTo(drawAmount)
      .describedAs("Should draw exactly the requested number of cards")

    assertThat(remainingCards.size)
      .isEqualTo(52 - drawAmount)
      .describedAs("Deck should have 52 - drawAmount cards remaining")

    // Verify none of the drawn cards remain in the deck
    drawnCards.forEach { card ->
      assertThat(remainingCards)
        .doesNotContain(card)
        .describedAs("Drawn card should not remain in the deck")
    }

    // Verify all drawn cards are unique
    assertThat(drawnCards.size)
      .isEqualTo(drawnCards.distinct().size)
      .describedAs("All drawn cards should be unique")
  }

  @Test
  fun `draw throws exception when no cards left`() {
    // given
    val deck = Deck()
    repeat(52) { deck.draw() } // draw all cards

    // when and then
    assertThatThrownBy { deck.draw() }
      .isExactlyInstanceOf(InsufficientCardsException::class.java)
  }
}
