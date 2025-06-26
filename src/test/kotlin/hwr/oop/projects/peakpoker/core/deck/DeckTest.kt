package hwr.oop.projects.peakpoker.core.deck

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
    } catch (_: Deck.InsufficientCardsException) {
      // Expected when deck is empty
    }
    return allCards
  }

  @Test
  fun `deck should be initialized with 52 cards`() {
    // given
    val deck = Deck()

    // when
    val allCards = drawAllCards(deck)

    // then
    assertThat(allCards.size).isEqualTo(52)
  }

  @Test
  fun `draw should return different cards each time`() {
    // given
    val deck = Deck()

    // when
    val firstCard = deck.draw().first()
    val secondCard = deck.draw().first()

    // then
    assertThat(firstCard).isNotEqualTo(secondCard)
  }

  @Test
  fun `draw should remove a card from the deck`() {
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
  fun `draw should return the requested number of cards`() {
    // given
    val deck = Deck()
    val drawAmount = 5

    // when
    val drawnCards = deck.draw(drawAmount)

    // then
    assertThat(drawnCards).hasSize(drawAmount)
  }

  @Test
  fun `draw should remove the drawn cards from the deck`() {
    // given
    val deck = Deck()
    val drawAmount = 5

    // when
    val drawnCards = deck.draw(drawAmount)
    val remainingCards = drawAllCards(deck)

    // then
    assertThat(remainingCards).hasSize(52 - drawAmount)

    drawnCards.forEach { card ->
      assertThat(remainingCards).doesNotContain(card)
    }
  }

  @Test
  fun `draw should return unique cards`() {
    // given
    val deck = Deck()
    val drawAmount = 10

    // when
    val drawnCards = deck.draw(drawAmount)

    // then
    assertThat(drawnCards).hasSize(drawnCards.distinct().size)
  }

  @Test
  fun `draw should throw InsufficientCardsException when no cards left`() {
    // given
    val deck = Deck()
    drawAllCards(deck) // draw all cards

    // when and then
    assertThatThrownBy { deck.draw() }
      .isExactlyInstanceOf(Deck.InsufficientCardsException::class.java)
      .hasMessageContaining("Not enough cards left in the deck")
  }

  @Test
  fun `draw should throw InsufficientCardsException when requesting more cards than available`() {
    // given
    val deck = Deck()
    val remainingCards = 5
    deck.draw(52 - remainingCards) // Draw most cards, leaving only a few

    // when and then
    assertThatThrownBy { deck.draw(remainingCards + 1) }
      .isExactlyInstanceOf(Deck.InsufficientCardsException::class.java)
      .hasMessageContaining("Not enough cards left in the deck")
  }
}
