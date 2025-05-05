package hwr.oop.projects.peakpoker

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
import hwr.oop.projects.peakpoker.core.hand.HandEvaluator
import hwr.oop.projects.peakpoker.core.hand.HandRank
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class HandEvaluatorTest : AnnotationSpec() {

    @Test
    fun `flush is recognized`() {
        // given
        // Five cards of the same suit, but not in sequence
        val cards = listOf(
            Card(Suit.HEARTS.name, Rank.TWO.name),
            Card(Suit.HEARTS.name, Rank.FIVE.name),
            Card(Suit.HEARTS.name, Rank.NINE.name),
            Card(Suit.HEARTS.name, Rank.JACK.name),
            Card(Suit.HEARTS.name, Rank.KING.name)
        )

        // when
        val ranking = HandEvaluator.evaluate(cards)

        //then
        assertThat(ranking).isEqualTo(HandRank.FLUSH)
    }

    @Test
    fun `straight is recognized`() {
        // given
        // Five consecutive values, different suits
        val cards = listOf(
            Card(Suit.CLUBS.name, Rank.FOUR.name),
            Card(Suit.HEARTS.name, Rank.FIVE.name),
            Card(Suit.DIAMONDS.name, Rank.SIX.name),
            Card(Suit.SPADES.name, Rank.SEVEN.name),
            Card(Suit.CLUBS.name, Rank.EIGHT.name)
        )

        // when
        val ranking = HandEvaluator.evaluate(cards)

        // then
        assertThat(ranking).isEqualTo(HandRank.STRAIGHT)
    }

    @Test
    fun `four of a kind is recognized`() {
        // given
        // Four cards of the same rank + any fifth card
        val cards = listOf(
            Card(Suit.HEARTS.name, Rank.ACE.name),
            Card(Suit.DIAMONDS.name, Rank.ACE.name),
            Card(Suit.CLUBS.name, Rank.ACE.name),
            Card(Suit.SPADES.name, Rank.ACE.name),
            Card(Suit.HEARTS.name, Rank.THREE.name)
        )

        // when
        val ranking = HandEvaluator.evaluate(cards)

        // then
        assertThat(ranking).isEqualTo(HandRank.FOUR_OF_A_KIND)
    }

    @Test
    fun `full house is recognized`() {
        // given
        // Three of a kind + a pair
        val cards = listOf(
            Card(Suit.HEARTS.name, Rank.KING.name),
            Card(Suit.DIAMONDS.name, Rank.KING.name),
            Card(Suit.CLUBS.name, Rank.KING.name),
            Card(Suit.HEARTS.name, Rank.QUEEN.name),
            Card(Suit.SPADES.name, Rank.QUEEN.name)
        )

        // when
        val ranking = HandEvaluator.evaluate(cards)

        // then
        assertThat(ranking).isEqualTo(HandRank.FULL_HOUSE)
    }

    @Test
    fun `high card is recognized when nothing else fits`() {
        // given
        val cards = listOf(
            Card(Suit.HEARTS.name, Rank.TWO.name),
            Card(Suit.DIAMONDS.name, Rank.FIVE.name),
            Card(Suit.CLUBS.name, Rank.NINE.name),
            Card(Suit.SPADES.name, Rank.JACK.name),
            Card(Suit.HEARTS.name, Rank.KING.name)
        )

        // when
        val ranking = HandEvaluator.evaluate(cards)

        // then
        assertThat(ranking).isEqualTo(HandRank.HIGH_CARD)
    }

    @Test
    fun `evaluate throws IllegalArgumentException for duplicates`() {
        // given
        // Five cards, two are identical
        val duplicateCard = Card(Suit.HEARTS.name, Rank.ACE.name)
        val hand = listOf(
            duplicateCard,
            duplicateCard,
            Card(Suit.DIAMONDS.name, Rank.KING.name),
            Card(Suit.CLUBS.name, Rank.QUEEN.name),
            Card(Suit.SPADES.name, Rank.JACK.name)
        )

        // when & then
        shouldThrow<IllegalArgumentException> {
            HandEvaluator.evaluate(hand)
        }
    }

    @Test
    fun `evaluate throws IllegalArgumentException for less than five cards`() {
        // given
        val hand = listOf(
            Card(Suit.HEARTS.name, Rank.ACE.name),
            Card(Suit.DIAMONDS.name, Rank.KING.name)
        )

        // when & then
        shouldThrow<IllegalArgumentException> {
            HandEvaluator.evaluate(hand)
        }
    }
}
