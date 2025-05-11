package hwr.oop.projects.peakpoker.core.hand


import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

import hwr.oop.projects.peakpoker.core.card.Card
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
import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
import hwr.oop.projects.peakpoker.core.player.PlayerInterface
import hwr.oop.projects.peakpoker.core.game.GameInterface
import io.kotest.matchers.string.shouldContain
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test


class HandEvaluatorAdvancedTest : AnnotationSpec() {


    //think i pressed resolve here accidentally, should  name be MockGame?
    private val dummyGame = object : GameInterface {
        override val id: Int = 0
        //override val name: String = "dummyGame"
    }
    private val dummyPlayer = object : PlayerInterface { override val name: String = "dummyPlayer" }

    @Test
    fun `evaluateAll throws if total cards not equal 7`() {
        // get = 2 cards, community = 0 cards -> totalCards = 2 != 7
        val hole = HoleCards(listOf(
            Card(CLUBS, TWO),
            Card(DIAMONDS, THREE)),
            dummyPlayer
        )
        val community = CommunityCards(emptyList(), dummyGame) // leer erlaubt

        assertThatThrownBy { HandEvaluator.evaluateAll(hole, community) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Total cards must be 7")
    }


    @Test
    fun `evaluateAll recognizes four of a kind`() {
        val community = CommunityCards(
            listOf(
                Card(CLUBS, QUEEN),
                Card(HEARTS, QUEEN),
                Card(SPADES, QUEEN),
                Card(DIAMONDS, TWO),
                Card(HEARTS, THREE)
            ),
            dummyGame
        )
        val hole = HoleCards(
            listOf(
                Card(DIAMONDS, QUEEN),
                Card(CLUBS, FOUR)),
            dummyPlayer
        )

        assertThat(HandEvaluator.evaluateAll(hole, community))
            .isEqualTo(HandRank.FOUR_OF_A_KIND)
    }

    @Test
    fun `evaluateAll recognizes straight flush`() {
        val community = CommunityCards(
            listOf(
                Card(HEARTS, SIX),
                Card(HEARTS, SEVEN),
                Card(HEARTS, EIGHT),
                Card(HEARTS, NINE),
                Card(HEARTS, JACK)
            ),
            dummyGame
        )
        val hole = HoleCards(
            listOf(
                Card(HEARTS, TEN),
                Card(DIAMONDS, TWO)),
            dummyPlayer
        )

        assertThat(HandEvaluator.evaluateAll(hole, community))
            .isEqualTo(HandRank.STRAIGHT_FLUSH)
    }

    @Test
    fun `getHighestHandRank throws if no hands provided`() {
        val community = CommunityCards(
            listOf(
                Card(HEARTS, TWO),
                Card(DIAMONDS, THREE),
                Card(CLUBS, FOUR),
                Card(SPADES, FIVE),
                Card(HEARTS, SIX)),
            dummyGame
        )
        shouldThrow<IllegalStateException> {
            HandEvaluator.getHighestHandRank(emptyList(), community)
        }
    }

    @Test
    fun `getHighestHandRank picks the correct winner`() {
        val community = CommunityCards(
            listOf(
                Card(HEARTS, TEN),
                Card(DIAMONDS, TEN),
                Card(CLUBS, FOUR),
                Card(SPADES, KING),
                Card(HEARTS, TWO)
            ),
            dummyGame
        )

        val twoPairHand  = HoleCards(listOf(
            Card(CLUBS, ACE),
            Card(DIAMONDS, KING)),
            dummyPlayer
        )
        val tripletsHand = HoleCards(listOf(
            Card(SPADES, TEN),
            Card(HEARTS, THREE)),
            dummyPlayer
        )

        assertThat(
            HandEvaluator.getHighestHandRank(
                listOf(twoPairHand, tripletsHand),
                community
            )
        ).isEqualTo(tripletsHand)
    }
}

