package hwr.oop.projects.peakpoker.core.hand



import hwr.oop.projects.peakpoker.core.hand.compareOnePair
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
import hwr.oop.projects.peakpoker.core.player.PlayerInterface
import io.kotest.core.spec.style.AnnotationSpec
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HandComparatorTest: AnnotationSpec() {

    private val dummyPlayer = object : PlayerInterface { override val name = "dummy" }
    private val dummyGame   = object : GameInterface { override val id = 0 }

    @Test
    fun highCard_secondKickerWins() {
        val h1 = HoleCards(listOf(
            Card(CLUBS, ACE),
            Card(DIAMONDS, KING)
        ), dummyPlayer)
        val h2 = HoleCards(listOf(
            Card(CLUBS, ACE),
            Card(DIAMONDS, QUEEN)
        ), dummyPlayer)
        val community = CommunityCards(listOf(
            Card(HEARTS, TEN),
            Card(SPADES, NINE),
            Card(HEARTS, EIGHT),
            Card(SPADES, SEVEN),
            Card(HEARTS, SIX)
        ), dummyGame)

        assertTrue(compareHighCard(h1, h2, community) > 0)
        assertTrue(compareHighCard(h2, h1, community) < 0)
    }

    @Test fun onePair_higherPairWins() {
        val h1 = HoleCards(listOf(
            Card(CLUBS, ACE),
            Card(DIAMONDS, ACE)
        ), dummyPlayer)
        val h2 = HoleCards(listOf(
            Card(CLUBS, KING),
            Card(DIAMONDS, KING)
        ), dummyPlayer)
        val community = CommunityCards(listOf(
            Card(HEARTS, TWO),
            Card(SPADES, THREE),
            Card(HEARTS, FOUR),
            Card(SPADES, FIVE),
            Card(HEARTS, SIX)
        ), dummyGame)

        assertTrue(compareOnePair(h1, h2, community) > 0)
        assertTrue(compareOnePair(h2, h1, community) < 0)
    }

    @Test fun onePair_kickerBreaksTie() {
        val h1 = HoleCards(listOf(
            Card(CLUBS, ACE),
            Card(DIAMONDS, THREE)
        ), dummyPlayer)
        val h2 = HoleCards(listOf(
            Card(CLUBS, KING),
            Card(DIAMONDS, FOUR)
        ), dummyPlayer)
        val community = CommunityCards(listOf(
            Card(HEARTS, TWO),
            Card(SPADES, TWO),
            Card(HEARTS, FIVE),
            Card(SPADES, SIX),
            Card(HEARTS, SEVEN)
        ), dummyGame)

        assertTrue(compareOnePair(h1, h2, community) > 0)
        assertTrue(compareOnePair(h2, h1, community) < 0)
    }

    @Test fun twoPair_higherTopPairWins() {
        val h1 = HoleCards(listOf(
            Card(CLUBS, ACE),
            Card(DIAMONDS, KING)
        ), dummyPlayer)
        val h2 = HoleCards(listOf(
            Card(CLUBS, QUEEN),
            Card(DIAMONDS, JACK)
        ), dummyPlayer)
        val community = CommunityCards(listOf(
            Card(HEARTS, ACE),
            Card(SPADES, KING),
            Card(HEARTS, QUEEN),
            Card(SPADES, JACK),
            Card(HEARTS, TWO)
        ), dummyGame)

        assertTrue(compareTwoPair(h1, h2, community) > 0)
        assertTrue(compareTwoPair(h2, h1, community) < 0)
    }

    @Test
    fun twoPair_secondPairBreaksTie() {
        // Both share top pair of Kings from the board
        // h1 makes his second pair with A + A
        val h1 = HoleCards(listOf(
            Card(CLUBS, ACE),
            Card(DIAMONDS, ACE)
        ), dummyPlayer)

        // h2 makes his second pair with Q + Q
        val h2 = HoleCards(listOf(
            Card(CLUBS, QUEEN),
            Card(DIAMONDS, QUEEN)
        ), dummyPlayer)

        // Board: only one pair (Kings), and three unrelated cards
        val community = CommunityCards(listOf(
            Card(HEARTS, KING),
            Card(SPADES, KING),
            Card(HEARTS, TWO),
            Card(SPADES, THREE),
            Card(HEARTS, FOUR)
        ), dummyGame)

        // h1 has K-K & A-A, h2 has K-K & Q-Q -> Aces beat Queens
        assertTrue(compareTwoPair(h1, h2, community) > 0)
        assertTrue(compareTwoPair(h2, h1, community) < 0)
    }

    @Test
    fun twoPair_kickerBreaksTie() {
        // Both share Q-Q and J-J from the board…
        val community = CommunityCards(listOf(
            Card(HEARTS, QUEEN),
            Card(SPADES, QUEEN),
            Card(HEARTS, JACK),
            Card(SPADES, JACK),
            Card(HEARTS, TWO)
        ), dummyGame)

        // h1’s hole cards: A + 3 → kicker = Ace
        val h1 = HoleCards(listOf(
            Card(CLUBS, ACE),
            Card(DIAMONDS, THREE)
        ), dummyPlayer)

        // h2’s hole cards: K + 4 → kicker = King
        val h2 = HoleCards(listOf(
            Card(CLUBS, KING),
            Card(DIAMONDS, FOUR)
        ), dummyPlayer)

        // h1 has two‐pair (Qs & Js) with A kicker; h2 has same two‐pair with K kicker
        assertTrue(compareTwoPair(h1, h2, community) > 0)
        assertTrue(compareTwoPair(h2, h1, community) < 0)
    }

    @Test
    fun threeOfAKind_tripsRankWins() {
        // h1: one Queen in the hole + TWO Queens on board → exactly three Queens
        val h1 = HoleCards(listOf(
            Card(CLUBS, QUEEN),
            Card(DIAMONDS, THREE)    // kicker, doesn’t affect the trips comparison
        ), dummyPlayer)

        // h2: one Jack in the hole + TWO Jacks on board → exactly three Jacks
        val h2 = HoleCards(listOf(
            Card(CLUBS, JACK),
            Card(DIAMONDS, FOUR)     // kicker
        ), dummyPlayer)

        // Board has QQ, JJ and a small card
        val community = CommunityCards(listOf(
            Card(HEARTS, QUEEN),
            Card(SPADES, QUEEN),
            Card(HEARTS, JACK),
            Card(SPADES, JACK),
            Card(HEARTS, TWO)
        ), dummyGame)

        // Three Queens > three Jacks
        assertTrue(compareThreeOfAKind(h1, h2, community) > 0)
        assertTrue(compareThreeOfAKind(h2, h1, community) < 0)
    }

    @Test
    fun threeOfAKind_kickerBreaksTie() {
        // both make exactly three Tens
        // h1’s hole cards give him an Ace kicker
        val h1 = HoleCards(listOf(
            Card(CLUBS, TEN),
            Card(DIAMONDS, ACE)
        ), dummyPlayer)

        // h2’s hole cards give him a King kicker
        val h2 = HoleCards(listOf(
            Card(CLUBS, TEN),
            Card(DIAMONDS, KING)
        ), dummyPlayer)

        // board now has TWO more Tens and two side cards
        val community = CommunityCards(listOf(
            Card(HEARTS, TEN),
            Card(SPADES, TEN),
            Card(HEARTS, NINE),
            Card(SPADES, EIGHT),
            Card(HEARTS, SEVEN)
        ), dummyGame)

        // trips(T) + {A,9}  vs. trips(T) + {K,9}
        assertTrue(compareThreeOfAKind(h1, h2, community) > 0)
        assertTrue(compareThreeOfAKind(h2, h1, community) < 0)
    }

    @Test
    fun straight_normalHighWins() {
        val h1 = HoleCards(listOf(
            Card(CLUBS, SIX),
            Card(DIAMONDS, SEVEN)
        ), dummyPlayer)
        val h2 = HoleCards(listOf(
            Card(CLUBS, FIVE),
            Card(DIAMONDS, SIX)
        ), dummyPlayer)
        val community = CommunityCards(listOf(
            Card(HEARTS, EIGHT),
            Card(SPADES, NINE),
            Card(HEARTS, TEN),
            Card(SPADES, JACK),
            Card(HEARTS, THREE)
        ), dummyGame)

        assertTrue(compareStraight(h1, h2, community) > 0)
    }

    @Test fun straight_wheelVsNormal() {
        // P1 makes the wheel:  A-2-3-4-5
        val h1 = HoleCards(listOf(
            Card(CLUBS, TWO),
            Card(DIAMONDS, THREE)
        ), dummyPlayer)

        // P2 makes a 6-high straight: 2-3-4-5-6
        val h2 = HoleCards(listOf(
            Card(CLUBS, FIVE),
            Card(DIAMONDS, SIX)
        ), dummyPlayer)

        // Board gives A-2-3-4-5
        val community = CommunityCards(listOf(
            Card(HEARTS, ACE),
            Card(SPADES, TWO),
            Card(HEARTS, THREE),
            Card(SPADES, FOUR),
            Card(HEARTS, FIVE)
        ), dummyGame)

        // Now 6-high > 5-high
        assertTrue(compareStraight(h1, h2, community) < 0)
        assertTrue(compareStraight(h2, h1, community) > 0)
    }

    @Test fun flush_highestFlushWins() {
        val h1 = HoleCards(listOf(
            Card(HEARTS, ACE),
            Card(HEARTS, TWO)
        ), dummyPlayer)
        val h2 = HoleCards(listOf(
            Card(HEARTS, KING),
            Card(HEARTS, THREE)
        ), dummyPlayer)
        val community = CommunityCards(listOf(
            Card(HEARTS, QUEEN),
            Card(HEARTS, JACK),
            Card(HEARTS, TEN),
            Card(CLUBS, TWO),
            Card(DIAMONDS, THREE)
        ), dummyGame)

        assertTrue(compareFlush(h1, h2, community) > 0)
    }

    @Test
    fun fullHouse_tripsBreaksTie() {
        val h1 = HoleCards(listOf(
            Card(CLUBS, JACK),
            Card(DIAMONDS, JACK)
        ), dummyPlayer)
        val h2 = HoleCards(listOf(
            Card(CLUBS, TEN),
            Card(DIAMONDS, TEN)
        ), dummyPlayer)
        val community = CommunityCards(listOf(
            Card(HEARTS, JACK),
            Card(SPADES, TEN),
            Card(HEARTS, TWO),
            Card(SPADES, TWO),
            Card(HEARTS, THREE)
        ), dummyGame)

        assertTrue(compareFullHouse(h1, h2, community) > 0)
        assertTrue(compareFullHouse(h2, h1, community) < 0)
    }

    @Test
    fun fullHouse_pairBreaksTie() {
        // both get  Trips and Jacks from the Board
        val community = CommunityCards(listOf(
            Card(HEARTS, JACK),
            Card(SPADES,  JACK),
            Card(DIAMONDS, JACK),
            Card(HEARTS, TWO),   // Filler
            Card(SPADES, THREE)  // Filler
        ), dummyGame)


        val h1 = HoleCards(listOf(
            Card(CLUBS, KING),
            Card(DIAMONDS, KING)
        ), dummyPlayer)


        val h2 = HoleCards(listOf(
            Card(CLUBS, QUEEN),
            Card(DIAMONDS, QUEEN)
        ), dummyPlayer)

        // King > Queen
        assertTrue(compareFullHouse(h1, h2, community) > 0)
        assertTrue(compareFullHouse(h2, h1, community) < 0)
    }

    @Test
    fun fourOfAKind_quadRankWins() {
        val h1 = HoleCards(listOf(
            Card(CLUBS, NINE),
            Card(DIAMONDS, NINE)
        ), dummyPlayer)
        val h2 = HoleCards(listOf(
            Card(CLUBS, EIGHT),
            Card(DIAMONDS, EIGHT)
        ), dummyPlayer)

        val community = CommunityCards(listOf(
            Card(HEARTS, NINE),
            Card(SPADES, NINE),
            Card(HEARTS, EIGHT),
            Card(SPADES, EIGHT),
            Card(HEARTS, TWO)
        ), dummyGame)


        assertTrue(compareFourOfAKind(h1, h2, community) > 0)
        assertTrue(compareFourOfAKind(h2, h1, community) < 0)
    }

    @Test
    fun fourOfAKind_kickerBreaksTie() {

        val community = CommunityCards(listOf(
            Card(CLUBS, SEVEN),
            Card(DIAMONDS, SEVEN),
            Card(HEARTS, SEVEN),
            Card(SPADES, SEVEN),
            Card(HEARTS, TWO)
        ), dummyGame)

        val h1 = HoleCards(listOf(
            Card(CLUBS, ACE),
            Card(DIAMONDS, THREE)
        ), dummyPlayer)

        val h2 = HoleCards(listOf(
            Card(CLUBS, KING),
            Card(DIAMONDS, FOUR)
        ), dummyPlayer)

        assertTrue(compareFourOfAKind(h1, h2, community) > 0)
        assertTrue(compareFourOfAKind(h2, h1, community) < 0)
    }

    @Test
    fun straightFlush_highWins() {
        // h1 makes the A-high SF: A♠ + {10♥, J♥, Q♥, K♥} from the board
        val h1 = HoleCards(listOf(
            Card(HEARTS, ACE),
            Card(CLUBS, THREE)      // filler, off-suit
        ), dummyPlayer)

        // h2 makes the K-high SF: 9♥ + {10♥, J♥, Q♥, K♥} from the board
        val h2 = HoleCards(listOf(
            Card(HEARTS, NINE),
            Card(DIAMONDS, FOUR)    // filler, off-suit
        ), dummyPlayer)

        // board has exactly four hearts in sequence (10–J–Q–K) plus a non-heart
        val community = CommunityCards(listOf(
            Card(HEARTS, TEN),
            Card(HEARTS, JACK),
            Card(HEARTS, QUEEN),
            Card(HEARTS, KING),
            Card(CLUBS, TWO)
        ), dummyGame)

        // h1: ♥A-10-J-Q-K  vs  h2: ♥9-10-J-Q-K
        assertTrue(compareStraightFlush(h1, h2, community) > 0)
        assertTrue(compareStraightFlush(h2, h1, community) < 0)
    }

    @Test
    fun straightFlush_wheelVsNormal() {
        // h1 plus 6♥+7♥ to 7-high SF (3-4-5-6-7)
        val h1 = HoleCards(listOf(
            Card(HEARTS, SIX),
            Card(HEARTS, SEVEN)
        ), dummyPlayer)

        // h2 uses Board for Wheel SF A-2-3-4-5
        val h2 = HoleCards(listOf(
            Card(CLUBS, TWO),
            Card(DIAMONDS, THREE)
        ), dummyPlayer)

        // Board A♥,2♥,3♥,4♥,5♥
        val community = CommunityCards(listOf(
            Card(HEARTS, ACE),
            Card(HEARTS, TWO),
            Card(HEARTS, THREE),
            Card(HEARTS, FOUR),
            Card(HEARTS, FIVE)
        ), dummyGame)

        // 7-high SF beats Wheel SF
        assertTrue(compareStraightFlush(h1, h2, community) > 0)
        assertTrue(compareStraightFlush(h2, h1, community) < 0)
    }
}
