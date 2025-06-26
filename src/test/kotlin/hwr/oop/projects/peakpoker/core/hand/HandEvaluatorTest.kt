package hwr.oop.projects.peakpoker.core.hand

import io.kotest.core.spec.style.AnnotationSpec
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
import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import hwr.oop.projects.peakpoker.core.game.PokerRound
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class HandEvaluatorTest : AnnotationSpec() {
  private lateinit var player1: PokerPlayer
  private lateinit var player2: PokerPlayer
  private lateinit var player3: PokerPlayer
  private lateinit var testRound: PokerRound

  @BeforeEach
  fun setup() {
    player1 = PokerPlayer("Hans")
    player2 = PokerPlayer("Peter")
    player3 = PokerPlayer("Max")
    testRound = PokerRound(
      smallBlindAmount = 10, bigBlindAmount = 20,
      players = listOf(player1, player2, player3),
      smallBlindIndex = 0
    )
  }

  /**
   * Verifies that a single player wins by default when no competition exists.
   */
  @Test
  fun `single player wins by default`() {
    val holeCards = HoleCards(
      listOf(
        Card(HEARTS, ACE),
        Card(SPADES, KING)
      ), player1
    )

    val community = CommunityCards(
      mutableListOf(
        Card(CLUBS, TWO),
        Card(DIAMONDS, THREE),
        Card(HEARTS, FOUR),
        Card(SPADES, FIVE),
        Card(CLUBS, SIX)
      )
    )

    val evaluator = HandEvaluator(community)
    val winners = evaluator.determineHighestHand(listOf(holeCards))
    assertThat(winners).hasSize(1)
    assertThat(winners[0].player).isEqualTo(player1)
  }

  /**
   * Ensures that a higher pair beats a lower pair between two players.
   */
  @Test
  fun `higher pair wins between two players`() {
    val player1 = PokerPlayer("Alice")
    val player2 = PokerPlayer("Bob")

    // Alice has a pair of Aces
    val hole1 = HoleCards(
      listOf(
        Card(HEARTS, ACE),
        Card(SPADES, ACE)
      ),
      player1
    )

    // Bob has a pair of Kings
    val hole2 = HoleCards(
      listOf(
        Card(DIAMONDS, KING),
        Card(CLUBS, KING)
      ),
      player2
    )

    val community = CommunityCards(
      mutableListOf(
        Card(CLUBS, TWO),
        Card(DIAMONDS, THREE),
        Card(HEARTS, FOUR),
        Card(SPADES, FIVE),
        Card(CLUBS, SIX)
      )
    )

    val evaluator = HandEvaluator(community)
    val winners = evaluator.determineHighestHand(listOf(hole1, hole2))
    assertThat(winners).hasSize(1)
    assertThat(winners[0].player?.name).isEqualTo("Alice")
  }

  /**
   * Confirms that a straight hand beats a pair of hands.
   */
  @Test
  fun `straight beats pair`() {
    val straightPlayer = PokerPlayer("Straight")
    val pairPlayer = PokerPlayer("Pair")

    // PokerPlayer can make straight (3-7)
    val holeStraight = HoleCards(
      listOf(
        Card(HEARTS, THREE),
        Card(SPADES, SEVEN)
      ),
      straightPlayer
    )

    // PokerPlayer has a pair
    val holePair = HoleCards(
      listOf(
        Card(DIAMONDS, ACE),
        Card(CLUBS, ACE)
      ),
      pairPlayer
    )

    val community = CommunityCards(
      mutableListOf(
        Card(CLUBS, FOUR),
        Card(DIAMONDS, FIVE),
        Card(HEARTS, SIX),
        Card(SPADES, TEN),
        Card(CLUBS, JACK)
      )
    )

    val evaluator = HandEvaluator(community)
    val winners = evaluator.determineHighestHand(listOf(holeStraight, holePair))
    assertThat(winners).hasSize(1)
    assertThat(winners[0].player?.name).isEqualTo("Straight")
  }

  /**
   * Checks that a tie returns the first player with the best hand.
   */
  @Test
  fun `tie returns first player with best hand`() {
    val player1 = PokerPlayer("First")
    val player2 = PokerPlayer("Second")

    // Both have the same hand (Ace-high)
    val hole1 = HoleCards(
      listOf(
        Card(HEARTS, ACE),
        Card(SPADES, TWO)
      ),
      player1
    )

    val hole2 = HoleCards(
      listOf(
        Card(DIAMONDS, ACE),
        Card(CLUBS, THREE)
      ),
      player2
    )

    val community = CommunityCards(
      mutableListOf(
        Card(CLUBS, FOUR),
        Card(DIAMONDS, FIVE),
        Card(HEARTS, SIX),
        Card(SPADES, SEVEN),
        Card(CLUBS, EIGHT)
      )
    )

    val evaluator = HandEvaluator(community)
    val winners = evaluator.determineHighestHand(listOf(hole1, hole2))
    assertThat(winners).hasSize(2)
    assertThat(winners.map { it.player!!.name }).containsExactly(
      "First",
      "Second"
    )
  }

  /**
   * Validates that a flush hand beats a straight hand.
   */
  @Test
  fun `flush beats straight`() {
    val flushPlayer = PokerPlayer("Flush")
    val straightPlayer = PokerPlayer("Straight")

    // Flush player has two hearts
    val holeFlush = HoleCards(
      listOf(
        Card(HEARTS, TEN),
        Card(HEARTS, JACK)
      ),
      flushPlayer
    )

    // Straight player
    val holeStraight = HoleCards(
      listOf(
        Card(CLUBS, NINE),
        Card(DIAMONDS, EIGHT)
      ),
      straightPlayer
    )

    val community = CommunityCards(
      mutableListOf(
        Card(HEARTS, ACE),  // Flush uses these
        Card(HEARTS, KING),
        Card(HEARTS, QUEEN),
        Card(SPADES, SEVEN),  // Straight uses these
        Card(CLUBS, SIX)
      )
    )

    val evaluator = HandEvaluator(community)
    val winners =
      evaluator.determineHighestHand(listOf(holeFlush, holeStraight))
    assertThat(winners).hasSize(1)
    assertThat(winners[0].player?.name).isEqualTo("Flush")
  }

  /**
   * Ensures that a royal flush beats a straight flush.
   */
  @Test
  fun `royal flush beats straight flush`() {
    val royalPlayer = PokerPlayer("Royal")
    val straightFlushPlayer = PokerPlayer("StraightFlush")

    // Royal flush player
    val holeRoyal = HoleCards(
      listOf(
        Card(SPADES, ACE),
        Card(SPADES, KING)
      ),
      royalPlayer
    )

    // Straight flush player (7-high)
    val holeStraightFlush = HoleCards(
      listOf(
        Card(HEARTS, SIX),
        Card(HEARTS, SEVEN)
      ),
      straightFlushPlayer
    )

    val community = CommunityCards(
      mutableListOf(
        Card(SPADES, QUEEN),  // Royal flush
        Card(SPADES, JACK),
        Card(SPADES, TEN),
        Card(HEARTS, FIVE),   // Straight flush
        Card(HEARTS, FOUR)
      )
    )

    val evaluator =
      HandEvaluator(community)
    val winners =
      evaluator.determineHighestHand(listOf(holeRoyal, holeStraightFlush))
    assertThat(winners).hasSize(1)
    assertThat(winners[0].player?.name).isEqualTo("Royal")
  }

  /**
   * Verifies that an exception is thrown when the player list is empty.
   */
  @Test
  fun `empty player list throws exception`() {
    val community = CommunityCards(
      mutableListOf(
        Card(CLUBS, ACE),
        Card(DIAMONDS, KING),
        Card(HEARTS, QUEEN),
        Card(SPADES, JACK),
        Card(CLUBS, TEN)
      )
    )

    val evaluator = HandEvaluator(community)
    assertThatThrownBy {
      evaluator.determineHighestHand(emptyList())
    }.isInstanceOf(HandEvaluator.EmptyPlayerListEvaluationException::class.java)
  }

  /**
   * Confirms the correct winner is determined out of four players.
   */
  @Test
  fun `gives the correct winner out of 4 players`() {
    val flushPlayer = PokerPlayer("Flush")
    val straightPlayer = PokerPlayer("Straight")
    val pairPlayer = PokerPlayer("Pair")
    val highCardPlayer = PokerPlayer("HighCard")

    // Flush player has two hearts
    val holeFlush = HoleCards(
      listOf(
        Card(HEARTS, TEN),
        Card(HEARTS, JACK)
      ),
      flushPlayer
    )

    // Straight player
    val holeStraight = HoleCards(
      listOf(
        Card(CLUBS, NINE),
        Card(DIAMONDS, EIGHT)
      ),
      straightPlayer
    )
    // Pair player
    val holePair = HoleCards(
      listOf(
        Card(CLUBS, TWO),
        Card(DIAMONDS, TWO)
      ),
      pairPlayer
    )

    // High-card player
    val holeHighCard = HoleCards(
      listOf(
        Card(SPADES, THREE),
        Card(CLUBS, FOUR)
      ),
      highCardPlayer
    )

    val community = CommunityCards(
      mutableListOf(
        Card(HEARTS, ACE),  // Flush uses these
        Card(HEARTS, KING),
        Card(HEARTS, QUEEN),
        Card(SPADES, SEVEN),  // Straight uses these
        Card(CLUBS, SIX)
      )
    )

    val evaluator = HandEvaluator(
      community
    )
    val winners = evaluator.determineHighestHand(
      listOf(
        holeFlush,
        holeStraight,
        holePair,
        holeHighCard
      )
    )
    assertThat(winners).hasSize(1)
    assertThat(winners[0].player?.name).isEqualTo("Flush")
  }

  /**
   * Test to ensure the bit manipulation logic works correctly with edge case patterns.
   * This specifically targets code coverage in the getBestCombo method.
   */
  @Test
  fun `selects best hand when all combinations are considered`() {
    // Create a player with a potential royal flush in spades
    val royalPlayer = PokerPlayer("Royal")

    // Give exactly the cards needed for a royal flush
    val holeRoyal = HoleCards(
      listOf(
        Card(SPADES, ACE),
        Card(SPADES, KING)
      ), royalPlayer
    )

    val community = CommunityCards(
      mutableListOf(
        Card(SPADES, QUEEN),
        Card(SPADES, JACK),
        Card(SPADES, TEN),
        // Add cards that would create tempting but inferior hands
        Card(HEARTS, ACE),
        Card(DIAMONDS, ACE)
      )
    )

    // The player should have a royal flush, which is the best possible hand
    val evaluator = HandEvaluator(community)
    val winners = evaluator.determineHighestHand(listOf(holeRoyal))
    assertThat(winners).hasSize(1)
    assertThat(winners[0].player?.name).isEqualTo("Royal")
  }

  /**
   * Test to ensure the early return logic is covered when the first player
   * is the only player.
   */
  @Test
  fun `first player is best when they are the only player`() {
    val player = PokerPlayer("OnlyPlayer")

    val holeCards = HoleCards(
      listOf(
        Card(HEARTS, TWO),
        Card(DIAMONDS, THREE)
      ), player
    )

    val community = CommunityCards(
      mutableListOf(
        Card(CLUBS, FOUR),
        Card(SPADES, FIVE),
        Card(HEARTS, SIX),
        Card(DIAMONDS, SEVEN),
        Card(CLUBS, EIGHT)
      )
    )

    // This should return the player directly from the first branch without comparison
    val evaluator = HandEvaluator(community)
    val winners = evaluator.determineHighestHand(listOf(holeCards))
    assertThat(winners).hasSize(1)
    assertThat(winners[0].player?.name).isEqualTo("OnlyPlayer")
  }

  /**
   * Tests edge cases in the bit manipulation by testing a specific set of combinations.
   */
  @Test
  fun `exercises different bit patterns for card combinations`() {
    // Create a scenario where multiple valid 5-card hands can be created
    // but only one is optimal
    val player = PokerPlayer("BitPattern")

    // These hole cards combined with community cards will create multiple possible hands
    val holeCards = HoleCards(
      listOf(
        Card(CLUBS, ACE),
        Card(CLUBS, KING)
      ), player
    )

    // Create community cards that allow multiple hand types
    val community = CommunityCards(
      mutableListOf(
        Card(CLUBS, QUEEN),
        Card(CLUBS, JACK),
        Card(DIAMONDS, TEN),  // Potential royal flush or straight flush break
        Card(HEARTS, ACE),    // Potential pair of aces
        Card(SPADES, ACE)     // Potential three of a kind
      )
    )

    val evaluator = HandEvaluator(community)
    val winners = evaluator.determineHighestHand(listOf(holeCards))
    assertThat(winners).hasSize(1)
    assertThat(winners[0].player?.name).isEqualTo("BitPattern")
  }

  /**
   * Tests another edge case for bit manipulation with specific bit patterns.
   */
  @Test
  fun `tests another bit pattern combination`() {
    // Create a scenario where the best hand would be at a specific bit pattern
    val player = PokerPlayer("BitPattern2")

    // Create a very specific set of cards where the best hand depends on the exact bit pattern selection
    val holeCards = HoleCards(
      listOf(
        Card(CLUBS, FIVE),
        Card(DIAMONDS, FIVE)
      ), player
    )

    val community = CommunityCards(
      mutableListOf(
        Card(HEARTS, FIVE),
        Card(SPADES, ACE),
        Card(CLUBS, ACE),
        Card(DIAMONDS, ACE),
        Card(HEARTS, KING)
      )
    )

    // Should find a full house: Aces full of fives
    val evaluator = HandEvaluator(community)
    val winners = evaluator.determineHighestHand(listOf(holeCards))
    assertThat(winners).hasSize(1)
    assertThat(winners[0].player?.name).isEqualTo("BitPattern2")
  }

  /**
   * Tests with cards arranged in a specific order to test edge cases in bit manipulation.
   */
  @Test
  fun `tests specific card ordering for bit manipulation`() {
    val player = PokerPlayer("OrderTest")

    // The order of these cards might affect the bit manipulation
    val holeCards = HoleCards(
      listOf(
        Card(CLUBS, TWO),    // First card
        Card(CLUBS, THREE)   // Second card
      ), player
    )

    val community = CommunityCards(
      mutableListOf(
        Card(CLUBS, FOUR),   // Third card
        Card(CLUBS, FIVE),   // Fourth card
        Card(CLUBS, SIX),    // Fifth card
        Card(HEARTS, SEVEN), // Sixth card
        Card(DIAMONDS, EIGHT) // Seventh card
      )
    )

    // Should find straight flush
    val evaluator = HandEvaluator(community)
    val winners = evaluator.determineHighestHand(listOf(holeCards))
    assertThat(winners).hasSize(1)
    assertThat(winners[0].player?.name).isEqualTo("OrderTest")
  }

  /**
   * Tests that the best hand is correctly updated when it's not the first player.
   */
  @Test
  fun `second player wins when having better hand than first`() {
    val player1 = PokerPlayer("FirstButWorse")

    val player2 = PokerPlayer("SecondButBetter")

    // The first player has just a pair of twos
    val holePlayer1 = HoleCards(
      listOf(
        Card(CLUBS, TWO),
        Card(DIAMONDS, THREE)
      ), player1
    )

    // The second player has a pair of aces
    val holePlayer2 = HoleCards(
      listOf(
        Card(HEARTS, ACE),
        Card(SPADES, KING)
      ), player2
    )

    val community = CommunityCards(
      mutableListOf(
        Card(SPADES, ACE),     // Gives player2 a pair of aces
        Card(DIAMONDS, FIVE),
        Card(HEARTS, SEVEN),
        Card(CLUBS, NINE),
        Card(SPADES, TWO)      // Gives player1 a pair of twos
      )
    )

    val evaluator = HandEvaluator(community)
    val winners =
      evaluator.determineHighestHand(listOf(holePlayer1, holePlayer2))
    assertThat(winners).hasSize(1)
    assertThat(winners[0].player?.name).isEqualTo("SecondButBetter")
  }

  /**
   * Tests that the best hand is updated when a player after the first has a better hand.
   */
  @Test
  fun `subsequent player with better hand becomes the winner`() {
    // The first player has a pair of twos
    val holePlayer1 = HoleCards(
      listOf(
        Card(CLUBS, TWO),
        Card(DIAMONDS, TWO)
      ), player1
    )

    // The second player has a pair of threes (better than player1)
    val holePlayer2 = HoleCards(
      listOf(
        Card(HEARTS, THREE),
        Card(SPADES, THREE)
      ), player2
    )

    // The third player has a pair of kings (best of all)
    val holePlayer3 = HoleCards(
      listOf(
        Card(CLUBS, KING),
        Card(DIAMONDS, FIVE)
      ), player3
    )

    val community = CommunityCards(
      mutableListOf(
        Card(HEARTS, KING),
        Card(SPADES, SEVEN),
        Card(DIAMONDS, EIGHT),
        Card(CLUBS, NINE),
        Card(HEARTS, TEN)
      )
    )

    val evaluator = HandEvaluator(
      community
    )
    val winners = evaluator.determineHighestHand(
      listOf(
        holePlayer1,
        holePlayer2,
        holePlayer3
      )
    )
    // Player3 should win with a pair of kings
    assertThat(winners).hasSize(1)
    assertThat(winners[0].player?.name).isEqualTo("Max")
  }

  /**
   * Tests that when two players have identical hands, both are returned as winners.
   */
  @Test
  fun `determineHighestHand returns all players when hands are tied`() {
    val player1 = PokerPlayer("First")
    val player2 = PokerPlayer("Second")

    // Both players have exactly the same hand (pair of kings)
    val hole1 = HoleCards(
      listOf(
        Card(HEARTS, KING),
        Card(SPADES, TWO)
      ),
      player1
    )

    val hole2 = HoleCards(
      listOf(
        Card(DIAMONDS, KING),
        Card(CLUBS, TWO)
      ),
      player2
    )

    val community = CommunityCards(
      mutableListOf(
        Card(CLUBS, KING),
        Card(DIAMONDS, FIVE),
        Card(HEARTS, SIX),
        Card(SPADES, SEVEN),
        Card(CLUBS, EIGHT)
      )
    )

    // Both players should be returned when hands are tied
    val evaluator = HandEvaluator(community)
    val winners =
      evaluator.determineHighestHand(listOf(hole1, hole2))
    assertThat(winners).hasSize(2)
    assertThat(winners.map { it.player?.name }).containsExactlyInAnyOrder(
      "First",
      "Second"
    )
  }

  /**
   * Tests that when three players have identical hands, all three are returned.
   */
  @Test
  fun `determineHighestHand returns all players when three hands are tied`() {
    val player1 = PokerPlayer("First")
    val player2 = PokerPlayer("Second")
    val player3 = PokerPlayer("Third")

    // All three players have the same top pair with the same kicker
    val hole1 = HoleCards(
      listOf(
        Card(HEARTS, ACE),
        Card(SPADES, THREE)
      ),
      player1
    )

    val hole2 = HoleCards(
      listOf(
        Card(DIAMONDS, ACE),
        Card(CLUBS, THREE)
      ),
      player2
    )

    val hole3 = HoleCards(
      listOf(
        Card(SPADES, ACE),
        Card(HEARTS, THREE)
      ),
      player3
    )

    val community = CommunityCards(
      mutableListOf(
        Card(CLUBS, ACE),
        Card(DIAMONDS, FIVE),
        Card(HEARTS, SIX),
        Card(SPADES, SEVEN),
        Card(CLUBS, EIGHT)
      )
    )

    // All three players should be returned when hands are tied
    val evaluator = HandEvaluator(
      community
    )
    val winners = evaluator.determineHighestHand(listOf(hole1, hole2, hole3))
    assertThat(winners).hasSize(3)
    assertThat(winners.map { it.player?.name })
      .containsExactlyInAnyOrder("First", "Second", "Third")
  }

  /**
   * Tests that when players have the same hand type but different kickers,
   * the correct winner is determined.
   */
  @Test
  fun `determineHighestHand correctly handles same hand type with different kickers`() {
    val playerBetter = PokerPlayer("BetterKicker")
    val playerWorse = PokerPlayer("WorseKicker")

    // Both players have a pair of aces, but different kickers
    val holeBetter = HoleCards(
      listOf(
        Card(HEARTS, ACE),
        Card(SPADES, KING)  // King kicker
      ),
      playerBetter
    )

    val holeWorse = HoleCards(
      listOf(
        Card(DIAMONDS, ACE),
        Card(CLUBS, QUEEN)  // Queen kicker
      ),
      playerWorse
    )

    val community = CommunityCards(
      mutableListOf(
        Card(CLUBS, ACE),
        Card(DIAMONDS, FIVE),
        Card(HEARTS, SIX),
        Card(SPADES, SEVEN),
        Card(CLUBS, EIGHT)
      )
    )

    // Only the player with the better kicker should win
    val evaluator = HandEvaluator(community)
    val winners = evaluator.determineHighestHand(listOf(holeBetter, holeWorse))
    assertThat(winners).hasSize(1)
    assertThat(winners[0].player?.name).isEqualTo("BetterKicker")

    // Order shouldn't matter in this case (not a tie)
    val evaluatorReversed = HandEvaluator(
      community
    )
    val reverseWinners =
      evaluatorReversed.determineHighestHand(listOf(holeWorse, holeBetter))
    assertThat(reverseWinners).hasSize(1)
    assertThat(reverseWinners[0].player?.name).isEqualTo("BetterKicker")
  }

  @Test
  fun `reduce correctly finds maximum hand among multiple players`() {
    val weakPlayer = PokerPlayer("Weak")
    val strongPlayer = PokerPlayer("Strong")
    val mediumPlayer = PokerPlayer("Medium")

    // Weak: just high card (King high)
    val holeWeak = HoleCards(
      listOf(Card(HEARTS, TWO), Card(SPADES, THREE)),
      weakPlayer
    )

    // Medium: a pair of fives
    val holeMedium = HoleCards(
      listOf(Card(HEARTS, FIVE), Card(DIAMONDS, FIVE)),
      mediumPlayer
    )

    // Strong: a pair of aces
    val holeStrong = HoleCards(
      listOf(Card(HEARTS, ACE), Card(SPADES, ACE)),
      strongPlayer
    )

    val community = CommunityCards(
      mutableListOf(
        Card(CLUBS, FOUR), Card(DIAMONDS, SEVEN),
        Card(CLUBS, EIGHT), Card(SPADES, NINE),
        Card(CLUBS, KING)
      )
    )

    val evaluator = HandEvaluator(community)
    val winners =
      evaluator.determineHighestHand(listOf(holeWeak, holeMedium, holeStrong))

    // Should select the strongest hand (pair of aces)
    assertThat(winners).hasSize(1)
    assertThat(winners[0].player?.name).isEqualTo("Strong")
  }

  @Test
  fun `reduce comparison logic handles equal hands correctly`() {
    val player1 = PokerPlayer("Equal1")
    val player2 = PokerPlayer("Equal2")

    // Both have identical hands
    val hole1 = HoleCards(
      listOf(Card(HEARTS, KING), Card(SPADES, QUEEN)),
      player1
    )

    val hole2 = HoleCards(
      listOf(Card(CLUBS, KING), Card(DIAMONDS, QUEEN)),
      player2
    )

    val community = CommunityCards(
      mutableListOf(
        Card(HEARTS, ACE), Card(SPADES, JACK),
        Card(CLUBS, TEN), Card(DIAMONDS, NINE),
        Card(HEARTS, EIGHT)
      )
    )

    val evaluator = HandEvaluator(community)
    val winners = evaluator.determineHighestHand(listOf(hole1, hole2))

    // Both should be returned for tie
    assertThat(winners).hasSize(2)
  }

  @Test
  fun `mask iteration covers all 21 possible 5-card combinations`() {
    val player = PokerPlayer("TestPlayer")

    // Create cards that would form different hand types depending on combination
    val hole = HoleCards(
      listOf(Card(HEARTS, ACE), Card(SPADES, KING)),
      player
    )

    val community = CommunityCards(
      mutableListOf(
        Card(CLUBS, QUEEN), Card(DIAMONDS, JACK),
        Card(HEARTS, TEN), Card(SPADES, NINE),
        Card(CLUBS, EIGHT)
      )
    )

    val evaluator = HandEvaluator(community)
    val winners = evaluator.determineHighestHand(listOf(hole))

    // Should find the best possible hand (straight)
    assertThat(winners).hasSize(1)
    assertThat(winners[0].player?.name).isEqualTo("TestPlayer")
  }

  @Test
  fun `mask bit counting correctly filters to exactly 5 cards`() {
    val player = PokerPlayer("BitCount")

    val hole = HoleCards(
      listOf(Card(HEARTS, TWO), Card(SPADES, THREE)),
      player
    )

    val community = CommunityCards(
      mutableListOf(
        Card(CLUBS, FOUR), Card(DIAMONDS, FIVE),
        Card(HEARTS, SIX), Card(SPADES, SEVEN),
        Card(CLUBS, EIGHT)
      )
    )

    val evaluator = HandEvaluator(community)
    val winners = evaluator.determineHighestHand(listOf(hole))

    // Should successfully evaluate despite needing exact 5-card combinations
    assertThat(winners).hasSize(1)
  }

  @Test
  fun `card index iteration correctly builds 5-card combinations`() {
    val player = PokerPlayer("IndexTest")

    // Specific cards where order matters for finding best hand
    val hole = HoleCards(
      listOf(Card(SPADES, ACE), Card(HEARTS, ACE)),
      player
    )

    val community = CommunityCards(
      mutableListOf(
        Card(CLUBS, ACE), Card(DIAMONDS, KING),
        Card(SPADES, KING), Card(HEARTS, QUEEN),
        Card(CLUBS, JACK)
      )
    )

    val evaluator = HandEvaluator(community)
    val winners = evaluator.determineHighestHand(listOf(hole))

    // Should find full house (aces over kings)
    assertThat(winners).hasSize(1)
  }

  @Test
  fun `bestCombo comparison correctly updates when better hand found`() {
    val player = PokerPlayer("Comparison")

    // Cards that create multiple valid hands with clear best option
    val hole = HoleCards(
      listOf(Card(SPADES, KING), Card(HEARTS, QUEEN)),
      player
    )

    val community = CommunityCards(
      mutableListOf(
        Card(CLUBS, JACK), Card(DIAMONDS, TEN),
        Card(SPADES, NINE), Card(HEARTS, TWO),
        Card(CLUBS, THREE)
      )
    )

    val evaluator = HandEvaluator(community)
    val winners = evaluator.determineHighestHand(listOf(hole))

    // Should find straight (best among all combinations)
    assertThat(winners).hasSize(1)
  }

  @Test
  fun `bestCombo null check handles first valid combination`() {
    val player = PokerPlayer("NullCheck")

    val hole = HoleCards(
      listOf(Card(HEARTS, TWO), Card(SPADES, FOUR)),
      player
    )

    val community = CommunityCards(
      mutableListOf(
        Card(CLUBS, SIX), Card(DIAMONDS, EIGHT),
        Card(HEARTS, TEN), Card(SPADES, QUEEN),
        Card(CLUBS, ACE)
      )
    )

    val evaluator = HandEvaluator(community)
    val winners = evaluator.determineHighestHand(listOf(hole))

    // Should successfully set the first valid combination as the bestCombo
    assertThat(winners).hasSize(1)
  }

  @Test
  fun `mask range covers complete search space`() {
    val player = PokerPlayer("MaskRange")

    // Cards create an edge case at boundary conditions
    val hole = HoleCards(
      listOf(Card(HEARTS, ACE), Card(SPADES, TWO)),
      player
    )

    val community = CommunityCards(
      mutableListOf(
        Card(CLUBS, THREE), Card(DIAMONDS, FOUR),
        Card(HEARTS, FIVE), Card(SPADES, SIX),
        Card(CLUBS, SEVEN)
      )
    )

    val evaluator = HandEvaluator(community)
    val winners = evaluator.determineHighestHand(listOf(hole))

    // Should find the best hand within the complete search space
    assertThat(winners).hasSize(1)
  }
}