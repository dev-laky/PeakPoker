package hwr.oop.projects.peakpoker.core.round

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
import hwr.oop.projects.peakpoker.core.hand.HandEvaluator
import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import java.lang.reflect.Method
import io.kotest.core.spec.style.AnnotationSpec

// This file is dedicated towards testing the complicated logic of the separate functions of the PokerRound class.
// Integration tests (so technically the 'testing via a third party') are done in the respective Game tests.
class PokerRoundTest : AnnotationSpec() {
  private lateinit var testRound: PokerRound
  private lateinit var handEvaluator: HandEvaluator
  private lateinit var determineWinnersMethod: Method
  private lateinit var communityCards: CommunityCards

  private lateinit var player1: PokerPlayer
  private lateinit var player2: PokerPlayer
  private lateinit var player3: PokerPlayer

  @BeforeEach
  fun setup() {
    // Create players
    player1 = PokerPlayer("Player 1", 100)
    player2 = PokerPlayer("Player 2", 100)
    player3 = PokerPlayer("Player 3", 100)

    // Create hand evaluator
    handEvaluator = HandEvaluator()

    // Create test round
    testRound = PokerRound(
      players = listOf(player1, player2, player3),
      smallBlindAmount = 5,
      bigBlindAmount = 10,
      smallBlindIndex = 0,
      onRoundComplete = {},
      handEvaluator = handEvaluator
    )

    // Access private determineWinners method using reflection
    determineWinnersMethod = PokerRound::class.java.getDeclaredMethod(
      "determineWinners", Collection::class.java
    ).apply { isAccessible = true }

    // Access communityCards field using reflection
    val communityCardsField =
      PokerRound::class.java.getDeclaredField("communityCards")
    communityCardsField.isAccessible = true
    communityCards = communityCardsField.get(testRound) as CommunityCards

    // Set community cards using reflection
    val communityCardsMemField =
      CommunityCards::class.java.getDeclaredField("cards")
    communityCardsMemField.isAccessible = true
    communityCardsMemField.set(
      communityCards, listOf(
        Card(Suit.HEARTS, Rank.TEN),
        Card(Suit.HEARTS, Rank.JACK),
        Card(Suit.HEARTS, Rank.QUEEN),
        Card(Suit.CLUBS, Rank.TWO),
        Card(Suit.DIAMONDS, Rank.SEVEN)
      )
    )
  }

  @Test
  fun `test single player always wins`() {
    // Only one eligible player
    val eligiblePlayers = listOf(player1)

    // Assign random cards to player1
    player1.assignHand(
      HoleCards(
        listOf(
          Card(Suit.CLUBS, Rank.FIVE),
          Card(Suit.DIAMONDS, Rank.SIX)
        ), player1
      )
    )

    // Call determineWinners
    val winners = determineWinnersMethod.invoke(
      testRound,
      eligiblePlayers
    ) as List<PokerPlayer>

    // Verify player1 wins by default (only eligible player)
    assertEquals(1, winners.size)
    assertEquals(player1, winners.first())
  }

  @Test
  fun `test player with highest hand wins`() {
    // Give player1 a flush (King-high hearts)
    player1.assignHand(
      HoleCards(
        listOf(
          Card(Suit.HEARTS, Rank.KING),
          Card(Suit.HEARTS, Rank.NINE)
        ), player1
      )
    )

    // Give player2 a straight (Ten to Ace)
    player2.assignHand(
      HoleCards(
        listOf(
          Card(Suit.DIAMONDS, Rank.KING),
          Card(Suit.CLUBS, Rank.ACE)
        ), player2
      )
    )

    // Give player3 a pair of sevens
    player3.assignHand(
      HoleCards(
        listOf(
          Card(Suit.CLUBS, Rank.SEVEN),
          Card(Suit.SPADES, Rank.THREE)
        ), player3
      )
    )

    // Call determineWinners
    val winners = determineWinnersMethod.invoke(
      testRound,
      listOf(player1, player2, player3)
    ) as List<PokerPlayer>

    // Player 1 should win with a flush
    assertEquals(1, winners.size)
    assertEquals(player1, winners.first())
  }

  @Test
  fun `test tie results in multiple winners`() {
    // Give player1 and player2 the same straight (Ten to Ace)
    player1.assignHand(
      HoleCards(
        listOf(
          Card(Suit.DIAMONDS, Rank.KING),
          Card(Suit.CLUBS, Rank.ACE)
        ), player1
      )
    )

    player2.assignHand(
      HoleCards(
        listOf(
          Card(Suit.CLUBS, Rank.KING),
          Card(Suit.DIAMONDS, Rank.ACE)
        ), player2
      )
    )

    // Give player3 a pair of sevens
    player3.assignHand(
      HoleCards(
        listOf(
          Card(Suit.CLUBS, Rank.SEVEN),
          Card(Suit.SPADES, Rank.THREE)
        ), player3
      )
    )

    // Call determineWinners
    val winners = determineWinnersMethod.invoke(
      testRound,
      listOf(player1, player2, player3)
    ) as List<PokerPlayer>

    // Both player1 and player2 should win with identical straight
    assertEquals(2, winners.size)
    assertTrue(winners.contains(player1))
    assertTrue(winners.contains(player2))
    assertFalse(winners.contains(player3))
  }
}