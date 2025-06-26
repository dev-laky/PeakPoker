package hwr.oop.projects.peakpoker.core.deck

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
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

  @Test
  fun `deck should be shuffled on initialization`() {
    // given - create multiple decks to check for randomness
    val deck1 = Deck()
    val deck2 = Deck()

    // when
    val cards1 = deck1.draw(10)
    val cards2 = deck2.draw(10)

    // then - decks should have different order (very high probability)
    // This test may rarely fail due to randomness, but mutation of shuffle() removal would always fail
    assertThat(cards1).isNotEqualTo(cards2)
  }

  @Test
  fun `deck initialization creates mutable list`() {
    // given
    val deck = Deck()

    // when - draw a card (this modifies the internal list)
    val drawnCard = deck.draw(1)

    // then
    assertThat(drawnCard).hasSize(1)

    val secondCard = deck.draw(1)
    assertThat(secondCard).hasSize(1)
    assertThat(drawnCard.first()).isNotEqualTo(secondCard.first())
  }

  @Test
  fun `deck contains all 52 unique cards after initialization`() {
    // given
    val deck = Deck()

    // when
    val allCards = drawAllCards(deck)

    // then - should have exactly 52 unique cards
    assertThat(allCards).hasSize(52)
    assertThat(allCards.distinct()).hasSize(52)

    // and should contain all possible combinations
    val expectedCards = Suit.entries.flatMap { suit ->
      Rank.entries.map { rank ->
        Card(suit, rank)
      }
    }
    assertThat(allCards).containsExactlyInAnyOrderElementsOf(expectedCards)
  }

  @Test
  fun `multiple deck instances have different card orders`() {
    // given - create 5 decks to increase confidence in randomness
    val decks = (1..5).map { Deck() }

    // when - get first 5 cards from each deck
    val cardSequences = decks.map { it.draw(5) }

    // then - at least some sequences should be different
    // If shuffle() is removed, all decks would have identical order
    val uniqueSequences = cardSequences.distinct()
    assertThat(uniqueSequences.size).isGreaterThan(1)
  }

  @Test
  fun `deck shuffling affects card distribution`() {
    // given - create many decks and track first card positions
    val iterations = 20
    val firstCards = mutableListOf<Card>()

    // when
    repeat(iterations) {
      val deck = Deck()
      firstCards.add(deck.draw(1).first())
    }

    // then - should see variety in first cards (shuffle working)
    // Without shuffle, the first card would always be the same
    val uniqueFirstCards = firstCards.distinct()
    assertThat(uniqueFirstCards.size).isGreaterThan(1)
  }
}
