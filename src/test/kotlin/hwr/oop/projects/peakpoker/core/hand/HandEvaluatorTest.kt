package hwr.oop.projects.peakpoker.core.hand

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
import hwr.oop.projects.peakpoker.core.card.Suit.CLUBS
import hwr.oop.projects.peakpoker.core.card.Suit.DIAMONDS
import hwr.oop.projects.peakpoker.core.card.Suit.HEARTS
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

class HandEvaluatorTest : AnnotationSpec() {
    //instanciating cards for testing
    private fun c(suit: Suit, rank: Rank) = Card(suit, rank)

    @Test
    fun `high card is recognized`() {
        val cards = listOf(
            c(CLUBS, TWO),
            c(HEARTS, FIVE),
            c(DIAMONDS, NINE),
            c(SPADES, JACK),
            c(HEARTS, KING)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.HIGH_CARD
    }

    @Test
    fun `one pair is recognized`() {
        val cards = listOf(
            c(HEARTS, TWO),
            c(DIAMONDS, TWO),
            c(CLUBS, FIVE),
            c(SPADES, SEVEN),
            c(HEARTS, NINE)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.ONE_PAIR
    }

    @Test
    fun `two pair is recognized`() {
        val cards = listOf(
            c(HEARTS, TEN),
            c(DIAMONDS, TEN),
            c(CLUBS, FOUR),
            c(SPADES, FOUR),
            c(HEARTS, KING)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.TWO_PAIR
    }

    @Test
    fun `three of a kind is recognized`() {
        val cards = listOf(
            c(HEARTS, ACE),
            c(DIAMONDS, ACE),
            c(CLUBS, ACE),
            c(SPADES, KING),
            c(HEARTS, THREE)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.THREE_OF_A_KIND
    }

    @Test
    fun `straight is recognized`() {
        val cards = listOf(
            c(HEARTS, THREE),
            c(DIAMONDS, FOUR),
            c(CLUBS, FIVE),
            c(SPADES, SIX),
            c(HEARTS, SEVEN)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.STRAIGHT
    }

    @Test
    fun `wheel straight is recognized`() {
        val cards = listOf(
            c(HEARTS, TWO),
            c(DIAMONDS, THREE),
            c(CLUBS, FOUR),
            c(SPADES, FIVE),
            c(HEARTS, ACE)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.STRAIGHT
    }

    @Test
    fun `flush is recognized`() {
        val cards = listOf(
            c(CLUBS, TWO),
            c(CLUBS, FIVE),
            c(CLUBS, NINE),
            c(CLUBS, JACK),
            c(CLUBS, KING)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.FLUSH
    }

    @Test
    fun `full house is recognized`() {
        val cards = listOf(
            c(CLUBS, THREE),
            c(HEARTS, THREE),
            c(SPADES, THREE),
            c(DIAMONDS, QUEEN),
            c(HEARTS, QUEEN)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.FULL_HOUSE
    }

    @Test
    fun `four of a kind is recognized`() {
        val cards = listOf(
            c(DIAMONDS, KING),
            c(HEARTS, KING),
            c(CLUBS, KING),
            c(SPADES, KING),
            c(HEARTS, TWO)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.FOUR_OF_A_KIND
    }

    @Test
    fun `straight flush is recognized`() {
        val cards = listOf(
            c(SPADES, FIVE),
            c(SPADES, SIX),
            c(SPADES, SEVEN),
            c(SPADES, EIGHT),
            c(SPADES, NINE)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.STRAIGHT_FLUSH
    }

    @Test
    fun `royal flush is recognized`() {
        val cards = listOf(
            c(HEARTS, TEN),
            c(HEARTS, JACK),
            c(HEARTS, QUEEN),
            c(HEARTS, KING),
            c(HEARTS, ACE)
        )
        HandEvaluator.evaluate(cards) shouldBe HandRank.ROYAL_FLUSH
    }

    @Test
    fun `evaluate throws on invalid list size`() {
        shouldThrow<IllegalArgumentException> {
            HandEvaluator.evaluate(
                listOf(
                    c(HEARTS, ACE),
                    c(HEARTS, KING)
                )
            )
        }
    }

    @Test
    fun `evaluate throws on duplicates`() {
        shouldThrow<IllegalArgumentException> {
            HandEvaluator.evaluate(
                listOf(
                    c(HEARTS, ACE),
                    c(HEARTS, ACE),
                    c(CLUBS, TWO),
                    c(DIAMONDS, THREE),
                    c(SPADES, FOUR)
                )
            )
        }
    }
}
