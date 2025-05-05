package hwr.oop.projects.peakpoker.core.hand

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat


class HandEvaluatorTest : AnnotationSpec() {

    @Test
    fun `flush is recognized`() {
        // Five cards of the same suit, but not in sequence
        val cards = listOf(
            Card(Suit.HEARTS, Rank.TWO),
            Card(Suit.HEARTS, Rank.FIVE),
            Card(Suit.HEARTS, Rank.NINE),
            Card(Suit.HEARTS, Rank.JACK),
            Card(Suit.HEARTS, Rank.KING)
        )

        val ranking = HandEvaluator.evaluate(cards)
        assertThat(ranking).isEqualTo(HandRank.FLUSH)
    }

    @Test
    fun `straight is recognized`() {
        // Five consecutive values, different suits
        val cards = listOf(
            Card(Suit.CLUBS, Rank.FOUR),
            Card(Suit.HEARTS, Rank.FIVE),
            Card(Suit.DIAMONDS, Rank.SIX),
            Card(Suit.SPADES, Rank.SEVEN),
            Card(Suit.CLUBS, Rank.EIGHT)
        )

        val ranking = HandEvaluator.evaluate(cards)
        assertThat(ranking).isEqualTo(HandRank.STRAIGHT)
    }

    @Test
    fun `four of a kind is recognized`() {
        // Four cards of the same rank + any fifth card
        val cards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.ACE),
            Card(Suit.CLUBS, Rank.ACE),
            Card(Suit.SPADES, Rank.ACE),
            Card(Suit.HEARTS, Rank.THREE)
        )

        val ranking = HandEvaluator.evaluate(cards)
        assertThat(ranking).isEqualTo(HandRank.FOUR_OF_A_KIND)
    }

    @Test
    fun `full house is recognized`() {
        // Three of a kind + a pair
        val cards = listOf(
            Card(Suit.HEARTS, Rank.KING),
            Card(Suit.DIAMONDS, Rank.KING),
            Card(Suit.CLUBS, Rank.KING),
            Card(Suit.HEARTS, Rank.QUEEN),
            Card(Suit.SPADES, Rank.QUEEN)
        )

        val ranking = HandEvaluator.evaluate(cards)
        assertThat(ranking).isEqualTo(HandRank.FULL_HOUSE)
    }

    @Test
    fun `high card is recognized when nothing else fits`() {
        val cards = listOf(
            Card(Suit.HEARTS, Rank.TWO),
            Card(Suit.DIAMONDS, Rank.FIVE),
            Card(Suit.CLUBS, Rank.NINE),
            Card(Suit.SPADES, Rank.JACK),
            Card(Suit.HEARTS, Rank.KING)
        )

        val ranking = HandEvaluator.evaluate(cards)
        assertThat(ranking).isEqualTo(HandRank.HIGH_CARD)
    }

    @Test
    fun `evaluate throws IllegalArgumentException for duplicates`() {
        // Five cards, two are identical
        val duplicateCard = Card(Suit.HEARTS, Rank.ACE)
        val hand = listOf(
            duplicateCard,
            duplicateCard,
            Card(Suit.DIAMONDS, Rank.KING),
            Card(Suit.CLUBS, Rank.QUEEN),
            Card(Suit.SPADES, Rank.JACK)
        )

        shouldThrow<IllegalArgumentException> {
            HandEvaluator.evaluate(hand)
        }
    }
}
