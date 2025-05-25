package hwr.oop.projects.peakpoker.core.hand

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
import hwr.oop.projects.peakpoker.core.game.GameId
import hwr.oop.projects.peakpoker.core.game.GameInterface
import hwr.oop.projects.peakpoker.core.player.PlayerInterface
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class HandComparatorTest : AnnotationSpec() {
    private val mockPlayer = object : PlayerInterface {
        override val name = "dummy"
    }

    private val mockGame = object : GameInterface {
        override val id: GameId = GameId("dummyId")
    }

    @Test
    fun `high card with higher second kicker wins`() {
        val h1 = HoleCards(
            listOf(
                Card(CLUBS, ACE),
                Card(DIAMONDS, KING)
            ), mockPlayer
        )
        val h2 = HoleCards(
            listOf(
                Card(CLUBS, ACE),
                Card(DIAMONDS, QUEEN)
            ), mockPlayer
        )
        val community = CommunityCards(
            listOf(
                Card(HEARTS, TEN),
                Card(SPADES, NINE),
                Card(HEARTS, EIGHT),
                Card(SPADES, SEVEN),
                Card(HEARTS, SIX)
            ), mockGame
        )

        assertThat(compareHighCard(h1, h2, community)).isGreaterThan(0)
        assertThat(compareHighCard(h2, h1, community)).isLessThan(0)
    }

    @Test
    fun `one pair with higher pair value wins`() {
        val h1 = HoleCards(
            listOf(
                Card(CLUBS, ACE),
                Card(DIAMONDS, ACE)
            ), mockPlayer
        )
        val h2 = HoleCards(
            listOf(
                Card(CLUBS, KING),
                Card(DIAMONDS, KING)
            ), mockPlayer
        )
        val community = CommunityCards(
            listOf(
                Card(HEARTS, TWO),
                Card(SPADES, THREE),
                Card(HEARTS, FOUR),
                Card(SPADES, FIVE),
                Card(HEARTS, SIX)
            ), mockGame
        )

        assertThat(compareOnePair(h1, h2, community)).isGreaterThan(0)
        assertThat(compareOnePair(h2, h1, community)).isLessThan(0)
    }

    @Test
    fun `one pair with same value uses kicker to break tie`() {
        val h1 = HoleCards(
            listOf(
                Card(CLUBS, ACE),
                Card(DIAMONDS, THREE)
            ), mockPlayer
        )
        val h2 = HoleCards(
            listOf(
                Card(CLUBS, KING),
                Card(DIAMONDS, FOUR)
            ), mockPlayer
        )
        val community = CommunityCards(
            listOf(
                Card(HEARTS, TWO),
                Card(SPADES, TWO),
                Card(HEARTS, FIVE),
                Card(SPADES, SIX),
                Card(HEARTS, SEVEN)
            ), mockGame
        )

        assertThat(compareOnePair(h1, h2, community)).isGreaterThan(0)
        assertThat(compareOnePair(h2, h1, community)).isLessThan(0)
    }

    @Test
    fun `two pair with higher top pair wins`() {
        val h1 = HoleCards(
            listOf(
                Card(CLUBS, ACE),
                Card(DIAMONDS, KING)
            ), mockPlayer
        )
        val h2 = HoleCards(
            listOf(
                Card(CLUBS, QUEEN),
                Card(DIAMONDS, JACK)
            ), mockPlayer
        )
        val community = CommunityCards(
            listOf(
                Card(HEARTS, ACE),
                Card(SPADES, KING),
                Card(HEARTS, QUEEN),
                Card(SPADES, JACK),
                Card(HEARTS, TWO)
            ), mockGame
        )

        assertThat(compareTwoPair(h1, h2, community)).isGreaterThan(0)
        assertThat(compareTwoPair(h2, h1, community)).isLessThan(0)
    }

    @Test
    fun `two pair with same top pair uses second pair to break tie`() {
        // Both share top pair of Kings from the board
        // h1 makes his second pair with A + A
        val h1 = HoleCards(
            listOf(
                Card(CLUBS, ACE),
                Card(DIAMONDS, ACE)
            ), mockPlayer
        )

        // h2 makes his second pair with Q + Q
        val h2 = HoleCards(
            listOf(
                Card(CLUBS, QUEEN),
                Card(DIAMONDS, QUEEN)
            ), mockPlayer
        )

        // Board: only one pair (Kings), and three unrelated cards
        val community = CommunityCards(
            listOf(
                Card(HEARTS, KING),
                Card(SPADES, KING),
                Card(HEARTS, TWO),
                Card(SPADES, THREE),
                Card(HEARTS, FOUR)
            ), mockGame
        )

        // h1 has K-K & A-A, h2 has K-K & Q-Q -> Aces beat Queens
        assertThat(compareTwoPair(h1, h2, community)).isGreaterThan(0)
        assertThat(compareTwoPair(h2, h1, community)).isLessThan(0)
    }

    @Test
    fun `two pair with identical pairs uses kicker to break tie`() {
        // Both share Q-Q and J-J from the board…
        val community = CommunityCards(
            listOf(
                Card(HEARTS, QUEEN),
                Card(SPADES, QUEEN),
                Card(HEARTS, JACK),
                Card(SPADES, JACK),
                Card(HEARTS, TWO)
            ), mockGame
        )

        // h1’s hole cards: A + 3 → kicker = Ace
        val h1 = HoleCards(
            listOf(
                Card(CLUBS, ACE),
                Card(DIAMONDS, THREE)
            ), mockPlayer
        )

        // h2’s hole cards: K + 4 → kicker = King
        val h2 = HoleCards(
            listOf(
                Card(CLUBS, KING),
                Card(DIAMONDS, FOUR)
            ), mockPlayer
        )

        // h1 has two‐pair (Qs & Js) with A kicker; h2 has same two‐pair with K kicker
        assertThat(compareTwoPair(h1, h2, community)).isGreaterThan(0)
        assertThat(compareTwoPair(h2, h1, community)).isLessThan(0)
    }

    @Test
    fun `three of a kind with higher trips rank wins`() {
        // h1: one Queen in the hole + TWO Queens on board → exactly three Queens
        val h1 = HoleCards(
            listOf(
                Card(CLUBS, QUEEN),
                Card(DIAMONDS, THREE)    // kicker, doesn’t affect the trips comparison
            ), mockPlayer
        )

        // h2: one Jack in the hole + TWO Jacks on board → exactly three Jacks
        val h2 = HoleCards(
            listOf(
                Card(CLUBS, JACK),
                Card(DIAMONDS, FOUR)     // kicker
            ), mockPlayer
        )

        // Board has QQ, JJ and a small card
        val community = CommunityCards(
            listOf(
                Card(HEARTS, QUEEN),
                Card(SPADES, QUEEN),
                Card(HEARTS, JACK),
                Card(SPADES, JACK),
                Card(HEARTS, TWO)
            ), mockGame
        )

        // Three Queens > three Jacks
        assertThat(compareThreeOfAKind(h1, h2, community)).isGreaterThan(0)
        assertThat(compareThreeOfAKind(h2, h1, community)).isLessThan(0)
    }

    @Test
    fun `three of a kind with same trips uses kicker to break tie`() {
        // both make exactly three Tens
        // h1’s hole cards give him an Ace kicker
        val h1 = HoleCards(
            listOf(
                Card(CLUBS, TEN),
                Card(DIAMONDS, ACE)
            ), mockPlayer
        )

        // h2’s hole cards give him a King kicker
        val h2 = HoleCards(
            listOf(
                Card(CLUBS, TEN),
                Card(DIAMONDS, KING)
            ), mockPlayer
        )

        // board now has TWO more Tens and two side cards
        val community = CommunityCards(
            listOf(
                Card(HEARTS, TEN),
                Card(SPADES, TEN),
                Card(HEARTS, NINE),
                Card(SPADES, EIGHT),
                Card(HEARTS, SEVEN)
            ), mockGame
        )

        // trips(T) + {A,9}  vs. trips(T) + {K,9}
        assertThat(compareThreeOfAKind(h1, h2, community)).isGreaterThan(0)
        assertThat(compareThreeOfAKind(h2, h1, community)).isLessThan(0)
    }

    @Test
    fun `straight with higher top card wins`() {
        val h1 = HoleCards(
            listOf(
                Card(CLUBS, SIX),
                Card(DIAMONDS, SEVEN)
            ), mockPlayer
        )
        val h2 = HoleCards(
            listOf(
                Card(CLUBS, FIVE),
                Card(DIAMONDS, SIX)
            ), mockPlayer
        )
        val community = CommunityCards(
            listOf(
                Card(HEARTS, EIGHT),
                Card(SPADES, NINE),
                Card(HEARTS, TEN),
                Card(SPADES, JACK),
                Card(HEARTS, THREE)
            ), mockGame
        )

        assertThat(compareStraight(h1, h2, community)).isGreaterThan(0)
    }

    @Test
    fun `straight wheel ranks lower than normal straight`() {
        // P1 makes the wheel:  A-2-3-4-5
        val h1 = HoleCards(
            listOf(
                Card(CLUBS, TWO),
                Card(DIAMONDS, THREE)
            ), mockPlayer
        )

        // P2 makes a 6-high straight: 2-3-4-5-6
        val h2 = HoleCards(
            listOf(
                Card(CLUBS, FIVE),
                Card(DIAMONDS, SIX)
            ), mockPlayer
        )

        // Board gives A-2-3-4-5
        val community = CommunityCards(
            listOf(
                Card(HEARTS, ACE),
                Card(SPADES, TWO),
                Card(HEARTS, THREE),
                Card(SPADES, FOUR),
                Card(HEARTS, FIVE)
            ), mockGame
        )

        // Now 6-high > 5-high
        assertThat(compareStraight(h1, h2, community)).isLessThan(0)
        assertThat(compareStraight(h2, h1, community)).isGreaterThan(0)
    }

    @Test
    fun `flush with higher top card wins`() {
        val h1 = HoleCards(
            listOf(
                Card(HEARTS, ACE),
                Card(HEARTS, TWO)
            ), mockPlayer
        )
        val h2 = HoleCards(
            listOf(
                Card(HEARTS, KING),
                Card(HEARTS, THREE)
            ), mockPlayer
        )
        val community = CommunityCards(
            listOf(
                Card(HEARTS, QUEEN),
                Card(HEARTS, JACK),
                Card(HEARTS, TEN),
                Card(CLUBS, TWO),
                Card(DIAMONDS, THREE)
            ), mockGame
        )

        assertThat(compareFlush(h1,h2, community)).isGreaterThan(0)
    }

    @Test
    fun `full house with higher three of a kind wins`() {
        val h1 = HoleCards(
            listOf(
                Card(CLUBS, JACK),
                Card(DIAMONDS, JACK)
            ), mockPlayer
        )
        val h2 = HoleCards(
            listOf(
                Card(CLUBS, TEN),
                Card(DIAMONDS, TEN)
            ), mockPlayer
        )
        val community = CommunityCards(
            listOf(
                Card(HEARTS, JACK),
                Card(SPADES, TEN),
                Card(HEARTS, TWO),
                Card(SPADES, TWO),
                Card(HEARTS, THREE)
            ), mockGame
        )

        assertThat(compareFullHouse(h1, h2, community)).isGreaterThan(0)
        assertThat(compareFullHouse(h2, h1, community)).isLessThan(0)
    }

    @Test
    fun `full house with same trips uses pair to break tie`() {
        // both get  Trips and Jacks from the Board
        val community = CommunityCards(
            listOf(
                Card(HEARTS, JACK),
                Card(SPADES, JACK),
                Card(DIAMONDS, JACK),
                Card(HEARTS, TWO),   // Filler
                Card(SPADES, THREE)  // Filler
            ), mockGame
        )


        val h1 = HoleCards(
            listOf(
                Card(CLUBS, KING),
                Card(DIAMONDS, KING)
            ), mockPlayer
        )


        val h2 = HoleCards(
            listOf(
                Card(CLUBS, QUEEN),
                Card(DIAMONDS, QUEEN)
            ), mockPlayer
        )

        // King > Queen
        assertThat(compareFullHouse(h1, h2, community)).isGreaterThan(0)
        assertThat(compareFullHouse(h2, h1, community)).isLessThan(0)
    }

    @Test
    fun `four of a kind with higher quad rank wins`() {
        val h1 = HoleCards(
            listOf(
                Card(CLUBS, NINE),
                Card(DIAMONDS, NINE)
            ), mockPlayer
        )
        val h2 = HoleCards(
            listOf(
                Card(CLUBS, EIGHT),
                Card(DIAMONDS, EIGHT)
            ), mockPlayer
        )

        val community = CommunityCards(
            listOf(
                Card(HEARTS, NINE),
                Card(SPADES, NINE),
                Card(HEARTS, EIGHT),
                Card(SPADES, EIGHT),
                Card(HEARTS, TWO)
            ), mockGame
        )

        assertThat(compareFourOfAKind(h1, h2, community)).isGreaterThan(0)
        assertThat(compareFourOfAKind(h2, h1, community)).isLessThan(0)
    }

    @Test
    fun `four of a kind with same quads uses kicker to break tie`() {
        val community = CommunityCards(
            listOf(
                Card(CLUBS, SEVEN),
                Card(DIAMONDS, SEVEN),
                Card(HEARTS, SEVEN),
                Card(SPADES, SEVEN),
                Card(HEARTS, TWO)
            ), mockGame
        )

        val h1 = HoleCards(
            listOf(
                Card(CLUBS, ACE),
                Card(DIAMONDS, THREE)
            ), mockPlayer
        )

        val h2 = HoleCards(
            listOf(
                Card(CLUBS, KING),
                Card(DIAMONDS, FOUR)
            ), mockPlayer
        )

        assertThat(compareFourOfAKind(h1, h2, community)).isGreaterThan(0)
        assertThat(compareFourOfAKind(h2, h1, community)).isLessThan(0)
    }

    @Test
    fun `straight flush with higher top card wins`() {
        // h1 makes the A-high SF: A♠ + {10♥, J♥, Q♥, K♥} from the board
        val h1 = HoleCards(
            listOf(
                Card(HEARTS, ACE),
                Card(CLUBS, THREE)      // filler, off-suit
            ), mockPlayer
        )

        // h2 makes the K-high SF: 9♥ + {10♥, J♥, Q♥, K♥} from the board
        val h2 = HoleCards(
            listOf(
                Card(HEARTS, NINE),
                Card(DIAMONDS, FOUR)    // filler, off-suit
            ), mockPlayer
        )

        // board has exactly four hearts in sequence (10–J–Q–K) plus a non-heart
        val community = CommunityCards(
            listOf(
                Card(HEARTS, TEN),
                Card(HEARTS, JACK),
                Card(HEARTS, QUEEN),
                Card(HEARTS, KING),
                Card(CLUBS, TWO)
            ), mockGame
        )

        // h1: ♥A-10-J-Q-K  vs  h2: ♥9-10-J-Q-K
        assertThat(compareStraightFlush(h1, h2, community)).isGreaterThan(0)
        assertThat(compareStraightFlush(h2, h1, community)).isLessThan(0)
    }

    @Test
    fun `straight flush wheel ranks lower than normal straight flush`() {
        // h1 plus 6♥+7♥ to 7-high SF (3-4-5-6-7)
        val h1 = HoleCards(
            listOf(
                Card(HEARTS, SIX),
                Card(HEARTS, SEVEN)
            ), mockPlayer
        )

        // h2 uses Board for Wheel SF A-2-3-4-5
        val h2 = HoleCards(
            listOf(
                Card(CLUBS, TWO),
                Card(DIAMONDS, THREE)
            ), mockPlayer
        )

        // Board A♥,2♥,3♥,4♥,5♥
        val community = CommunityCards(
            listOf(
                Card(HEARTS, ACE),
                Card(HEARTS, TWO),
                Card(HEARTS, THREE),
                Card(HEARTS, FOUR),
                Card(HEARTS, FIVE)
            ), mockGame
        )

        // 7-high SF beats Wheel SF
        assertThat(compareStraightFlush(h1, h2, community)).isGreaterThan(0)
        assertThat(compareStraightFlush(h2, h1, community)).isLessThan(0)
    }
}
