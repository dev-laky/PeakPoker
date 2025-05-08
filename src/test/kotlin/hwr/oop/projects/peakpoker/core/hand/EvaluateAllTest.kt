package hwr.oop.projects.peakpoker.core.hand

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.Suit.*
import hwr.oop.projects.peakpoker.core.card.Rank.*
import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
import hwr.oop.projects.peakpoker.core.player.PlayerInterface
import hwr.oop.projects.peakpoker.core.game.GameInterface
import io.kotest.matchers.string.shouldContain


class HandEvaluatorAdvancedTest : AnnotationSpec() {
    private fun c(s: Suit, r: Rank) = Card(s, r)
    private val dummyGame = object : GameInterface {
        override val id: Int = 0
        //override val name: String = "dummyGame"
    }
    private val dummyPlayer = object : PlayerInterface { override val name: String = "dummyPlayer" }

    @Test
    fun `evaluateAll throws if total cards not equal 7`() {
        // get = 2 cards, community = 0 cards -> totalCards = 2 != 7
        val hole = HoleCards(listOf(c(CLUBS, TWO), c(DIAMONDS, THREE)), dummyPlayer)
        val community = CommunityCards(emptyList(), dummyGame) // leer erlaubt
        shouldThrow<IllegalArgumentException> {
            HandEvaluator.evaluateAll(hole, community)
        }.message shouldContain "Total cards must be 7"
    }

    @Test
    fun `evaluateAll recognizes four of a kind`() {
        val community = CommunityCards(
            listOf(c(CLUBS, QUEEN), c(HEARTS, QUEEN), c(SPADES, QUEEN), c(DIAMONDS, TWO), c(HEARTS, THREE)),
            dummyGame
        )
        val hole = HoleCards(listOf(c(DIAMONDS, QUEEN), c(CLUBS, FOUR)), dummyPlayer)
        HandEvaluator.evaluateAll(hole, community) shouldBe HandRank.FOUR_OF_A_KIND
    }

    @Test
    fun `evaluateAll recognizes straight flush`() {
        val community = CommunityCards(
            listOf(c(HEARTS, SIX), c(HEARTS, SEVEN), c(HEARTS, EIGHT), c(HEARTS, NINE), c(HEARTS, JACK)),
            dummyGame
        )
        val hole = HoleCards(listOf(c(HEARTS, TEN), c(DIAMONDS, TWO)), dummyPlayer)
        HandEvaluator.evaluateAll(hole, community) shouldBe HandRank.STRAIGHT_FLUSH
    }

    @Test
    fun `getHighestHandRank throws if no hands provided`() {
        val community = CommunityCards(
            listOf(c(HEARTS, TWO), c(DIAMONDS, THREE), c(CLUBS, FOUR), c(SPADES, FIVE), c(HEARTS, SIX)),
            dummyGame
        )
        shouldThrow<IllegalStateException> {
            HandEvaluator.getHighestHandRank(emptyList(), community)
        }
    }

    @Test
    fun `getHighestHandRank picks the correct winner`() {
        val community = CommunityCards(
            listOf(c(HEARTS, TEN), c(DIAMONDS, TEN), c(CLUBS, FOUR), c(SPADES, KING), c(HEARTS, TWO)),
            dummyGame
        )
        val twoPairHand = HoleCards(listOf(c(CLUBS, ACE), c(DIAMONDS, KING)), dummyPlayer)
        val tripletsHand   = HoleCards(listOf(c(SPADES, TEN), c(HEARTS, THREE)), dummyPlayer)
        // 3 of a kind beats 2 pair
        HandEvaluator.getHighestHandRank(listOf(twoPairHand, tripletsHand), community) shouldBe tripletsHand
    }
}

