package hwr.oop.projects.peakpoker.core.hand

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit.CLUBS
import hwr.oop.projects.peakpoker.core.card.Suit.HEARTS
import hwr.oop.projects.peakpoker.core.card.Suit.DIAMONDS
import hwr.oop.projects.peakpoker.core.card.Suit.SPADES

import hwr.oop.projects.peakpoker.core.card.Rank.TWO
import hwr.oop.projects.peakpoker.core.card.Rank.THREE
import hwr.oop.projects.peakpoker.core.card.Rank.FOUR
import hwr.oop.projects.peakpoker.core.card.Rank.FIVE
import hwr.oop.projects.peakpoker.core.card.Rank.SIX
import hwr.oop.projects.peakpoker.core.card.Rank.SEVEN
import hwr.oop.projects.peakpoker.core.card.Rank.EIGHT
import hwr.oop.projects.peakpoker.core.card.Rank.NINE
import hwr.oop.projects.peakpoker.core.card.Rank.TEN
import hwr.oop.projects.peakpoker.core.card.Rank.JACK
import hwr.oop.projects.peakpoker.core.card.Rank.QUEEN
import hwr.oop.projects.peakpoker.core.card.Rank.KING
import hwr.oop.projects.peakpoker.core.card.Rank.ACE
import hwr.oop.projects.peakpoker.core.card.Suit


class HandEvaluatorTest : AnnotationSpec() {


    @Test
    fun `high card is recognized`() {
        val cards = listOf(
            Card(CLUBS, TWO),
            Card(HEARTS, FIVE),
            Card(DIAMONDS, NINE),
            Card(SPADES, JACK),
            Card(HEARTS, KING)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.HIGH_CARD
    }

    @Test
    fun `one pair is recognized`() {
        val cards = listOf(
            Card(HEARTS, TWO),
            Card(DIAMONDS, TWO),
            Card(CLUBS, FIVE),
            Card(SPADES, SEVEN),
            Card(HEARTS, NINE)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.ONE_PAIR
    }

    @Test
    fun `two pair is recognized`() {
        val cards = listOf(
            Card(HEARTS, TEN),
            Card(DIAMONDS, TEN),
            Card(CLUBS, FOUR),
            Card(SPADES, FOUR),
            Card(HEARTS, KING)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.TWO_PAIR
    }

    @Test
    fun `three of a kind is recognized`() {
        val cards = listOf(
            Card(HEARTS, ACE),
            Card(DIAMONDS, ACE),
            Card(CLUBS, ACE),
            Card(SPADES, KING),
            Card(HEARTS, THREE)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.THREE_OF_A_KIND
    }

    @Test
    fun `straight is recognized`() {
        val cards = listOf(
            Card(HEARTS, THREE),
            Card(DIAMONDS, FOUR),
            Card(CLUBS, FIVE),
            Card(SPADES, SIX),
            Card(HEARTS, SEVEN)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.STRAIGHT
    }

    @Test
    fun `wheel straight is recognized`() {
        val cards = listOf(
            Card(HEARTS, TWO),
            Card(DIAMONDS, THREE),
            Card(CLUBS, FOUR),
            Card(SPADES, FIVE),
            Card(HEARTS, ACE)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.STRAIGHT
    }

    @Test
    fun `flush is recognized`() {
        val cards = listOf(
            Card(CLUBS, TWO),
            Card(CLUBS, FIVE),
            Card(CLUBS, NINE),
            Card(CLUBS, JACK),
            Card(CLUBS, KING)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.FLUSH
    }

    @Test
    fun `full house is recognized`() {
        val cards = listOf(
            Card(CLUBS, THREE),
            Card(HEARTS, THREE),
            Card(SPADES, THREE),
            Card(DIAMONDS, QUEEN),
            Card(HEARTS, QUEEN)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.FULL_HOUSE
    }

    @Test
    fun `four of a kind is recognized`() {
        val cards = listOf(
            Card(DIAMONDS, KING),
            Card(HEARTS, KING),
            Card(CLUBS, KING),
            Card(SPADES, KING),
            Card(HEARTS, TWO)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.FOUR_OF_A_KIND
    }

    @Test
    fun `straight flush is recognized`() {
        val cards = listOf(
            Card(SPADES, FIVE),
            Card(SPADES, SIX),
            Card(SPADES, SEVEN),
            Card(SPADES, EIGHT),
            Card(SPADES, NINE)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.STRAIGHT_FLUSH
    }

    @Test
    fun `royal flush is recognized`() {
        val cards = listOf(
            Card(HEARTS, TEN),
            Card(HEARTS, JACK),
            Card(HEARTS, QUEEN),
            Card(HEARTS, KING),
            Card(HEARTS, ACE)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.ROYAL_FLUSH
    }

    @Test
    fun `evaluate throws on invalid list size`() {
        shouldThrow<IllegalArgumentException> {
            HandEvaluator.evaluate(
                listOf(
                    Card(HEARTS, ACE),
                    Card(HEARTS, KING)
                )
            )
        }
    }

    @Test
    fun `evaluate throws on duplicates`() {
        shouldThrow<IllegalArgumentException> {
            HandEvaluator.evaluate(
                listOf(
                    Card(HEARTS, ACE),
                    Card(HEARTS, ACE),
                    Card(CLUBS, TWO),
                    Card(DIAMONDS, THREE),
                    Card(SPADES, FOUR)
                )
            )
        }
    }
}
