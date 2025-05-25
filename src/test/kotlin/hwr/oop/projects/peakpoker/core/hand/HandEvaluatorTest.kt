package hwr.oop.projects.peakpoker.core.hand

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.assertions.throwables.shouldThrow

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.HoleCards

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
import hwr.oop.projects.peakpoker.core.game.GameInterface
import hwr.oop.projects.peakpoker.core.hand.HandEvaluator.evaluate
import hwr.oop.projects.peakpoker.core.hand.HandEvaluator.evaluateAll
import hwr.oop.projects.peakpoker.core.hand.HandEvaluator.getBestCombo
import hwr.oop.projects.peakpoker.core.player.PlayerInterface
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy


class HandEvaluatorTest : AnnotationSpec() {

    private val mockGame = object : GameInterface {
        override val id: Int = 0
        //override val name: String = "dummyGame"
    }
    private val dummyPlayer = object : PlayerInterface { override val name: String = "dummyPlayer" }

    @Test
    fun `high card is recognized`() {
        val cards = listOf(
            Card(CLUBS, TWO),
            Card(HEARTS, FIVE),
            Card(DIAMONDS, NINE),
            Card(SPADES, JACK),
            Card(HEARTS, KING)
        )
        assertThat(evaluate(cards)).isEqualTo(HandRank.HIGH_CARD)
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
        assertThat(evaluate(cards)).isEqualTo(HandRank.ONE_PAIR)
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
        assertThat(evaluate(cards)).isEqualTo(HandRank.TWO_PAIR)
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
        assertThat(evaluate(cards)).isEqualTo(HandRank.THREE_OF_A_KIND)
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
        assertThat(evaluate(cards)).isEqualTo(HandRank.STRAIGHT)
    }

    @Test
    fun `wheel straight is recognized`() {
        val cards = listOf(
            Card(SPADES, FIVE),
            Card(DIAMONDS, THREE),
            Card(CLUBS, FOUR),
            Card(HEARTS, TWO),
            Card(SPADES, ACE)
        )
        assertThat(evaluate(cards)).isEqualTo(HandRank.STRAIGHT)
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
        assertThat(evaluate(cards)).isEqualTo(HandRank.FLUSH)
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
        assertThat(evaluate(cards)).isEqualTo(HandRank.FULL_HOUSE)
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
        assertThat(evaluate(cards)).isEqualTo(HandRank.FOUR_OF_A_KIND)
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
        assertThat(evaluate(cards)).isEqualTo(HandRank.STRAIGHT_FLUSH)
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
        assertThat(evaluate(cards)).isEqualTo(HandRank.ROYAL_FLUSH)
    }

    @Test
    fun `evaluate throws exception on invalid list size`() {
        assertThatThrownBy {
            HandEvaluator.evaluate(
                listOf(
                    Card(HEARTS, ACE),
                    Card(HEARTS, KING)
                )
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `evaluate throws exception on duplicates`() {
        assertThatThrownBy {
            HandEvaluator.evaluate(
                listOf(
                    Card(HEARTS, ACE),
                    Card(HEARTS, ACE),
                    Card(CLUBS, TWO),
                    Card(DIAMONDS, THREE),
                    Card(SPADES, FOUR)
                )
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }




    @Test
    fun `evaluateAll throws exception if total cards unequal 7`() {
        // get = 2 cards, community = 0 cards -> totalCards = 2 != 7
        val hole = HoleCards(listOf(
            Card(CLUBS, TWO),
            Card(DIAMONDS, THREE)),
            dummyPlayer
        )
        val community = CommunityCards(emptyList(), mockGame) // empty possible

        assertThatThrownBy { evaluateAll(hole, community) }
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
            mockGame
        )
        val hole = HoleCards(
            listOf(
                Card(DIAMONDS, QUEEN),
                Card(CLUBS, FOUR)),
            dummyPlayer
        )

        assertThat(evaluateAll(hole, community))
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
            mockGame
        )
        val hole = HoleCards(
            listOf(
                Card(HEARTS, TEN),
                Card(DIAMONDS, TWO)),
            dummyPlayer
        )

        assertThat(evaluateAll(hole, community))
            .isEqualTo(HandRank.STRAIGHT_FLUSH)
    }

    @Test
    fun `getHighestHandRank throws exception if no hands provided`() {
        val community = CommunityCards(
            listOf(
                Card(HEARTS, TWO),
                Card(DIAMONDS, THREE),
                Card(CLUBS, FOUR),
                Card(SPADES, FIVE),
                Card(HEARTS, SIX)),
            mockGame
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
            mockGame
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

    // NEW SECTION: Additional Tests for evaluateAll

    /*
     * These tests cover various scenarios for the updated(25.05.2025) evaluateAll function,since
     * in the past it couldn't decide between two of the same hand ranks which
     * one to return, so I added the logic for it to always return the one with the
     * higher kicker.
     */

    /**
     * Ensures that when two different three-of-a-kind combinations are possible,
     * the evaluator correctly selects the one that forms a full house with the higher triplet.
     * In this case, trips of 3 and trips of 4 are both present, so the best hand is 4♣ 4♦ 4♥ 3♠ 3♥.
     */
    @Test
    fun `detects best of two three-of-a-kinds`() {
        val hole = HoleCards(
            player = dummyPlayer,
            cards = listOf(Card(HEARTS, THREE), Card(SPADES, THREE))
        )
        val community = CommunityCards(
            game = mockGame,
            cards = listOf(
                Card(CLUBS, THREE),
                Card(DIAMONDS, FOUR),
                Card(HEARTS, FOUR),
                Card(CLUBS, FOUR),
                Card(SPADES, SEVEN)
            )
        )

        val rank = evaluateAll(hole, community)
        assertThat(rank).isEqualTo(HandRank.FULL_HOUSE)

        val bestCombo = getBestCombo(hole, community)
        val grouped = bestCombo.groupBy { it.rank }
        assertThat(grouped.filter { it.value.size == 3 }.keys).contains(FOUR)
        assertThat(grouped.filter { it.value.size == 2 }.keys).contains(THREE)
    }

    /**
     * Verifies that when no pairs or combinations exist, the hand rank is High Card,
     * and the highest kicker (KING) is correctly recognized in the best hand.
     */
    @Test
    fun `selects correct kicker in tie high card hands`() {
        val hole = HoleCards(
            player = dummyPlayer,
            cards = listOf(Card(CLUBS, TWO), Card(HEARTS, FIVE))
        )
        val community = CommunityCards(
            game = mockGame,
            cards = listOf(
                Card(SPADES, NINE),
                Card(DIAMONDS, JACK),
                Card(HEARTS, KING),
                Card(HEARTS, THREE),
                Card(CLUBS, FOUR)
            )
        )

        val rank = evaluateAll(hole, community)
        assertThat(rank).isEqualTo(HandRank.HIGH_CARD)

        val bestCombo = getBestCombo(hole, community).map { it.rank.value }.sortedDescending()
        assertThat(bestCombo.first()).isEqualTo(KING.value)
        assertThat(bestCombo).containsAll(listOf(KING.value, JACK.value, NINE.value, FIVE.value, FOUR.value).map { it })
    }

    /**
     * Confirms that in a one-pair scenario with tied pair values (pair of FIVES),
     * the evaluator uses kickers to determine the stronger hand.
     * Here, the hole card ACE should be included as the highest kicker.
     */
    @Test
    fun `correctly resolves tie with better kickers in one pair`() {
        val hole = HoleCards(
            player = dummyPlayer,
            cards = listOf(Card(CLUBS, ACE), Card(HEARTS, FIVE))
        )
        val community = CommunityCards(
            game = mockGame,
            cards = listOf(
                Card(DIAMONDS, FIVE),
                Card(SPADES, SEVEN),
                Card(CLUBS, EIGHT),
                Card(HEARTS, TEN),
                Card(CLUBS, QUEEN)
            )
        )

        val rank = evaluateAll(hole, community)
        assertThat(rank).isEqualTo(HandRank.ONE_PAIR)

        val bestCombo = getBestCombo(hole, community)
        val groupedRanks = bestCombo.groupBy { it.rank }

        assertThat(groupedRanks.filter { it.value.size == 2 }.keys).containsOnly(FIVE)

        val kickerRanks = groupedRanks.filter { it.value.size == 1 }.keys.map { it.value }
        assertThat(kickerRanks).contains(ACE.value)
        assertThat(kickerRanks.maxOrNull()).isEqualTo(ACE.value)
    }
}


