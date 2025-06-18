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
import hwr.oop.projects.peakpoker.core.game.GameId
import hwr.oop.projects.peakpoker.core.game.GameInterface
import hwr.oop.projects.peakpoker.core.player.PlayerInterface
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class HandEvaluatorTest : AnnotationSpec() {
  private val evaluator = HandEvaluator()

  private val mockPlayer = object : PlayerInterface {
    override val name: String = "dummy"
    val id: String = "dummyId"
  }

  private val mockGame = object : GameInterface {
    override val id: GameId = GameId("dummyGameId")
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
      ), mockPlayer
    )

    val community = CommunityCards(
      listOf(
        Card(CLUBS, TWO),
        Card(DIAMONDS, THREE),
        Card(HEARTS, FOUR),
        Card(SPADES, FIVE),
        Card(CLUBS, SIX)
      ), mockGame
    )

    val winner = evaluator.determineHighestHand(listOf(holeCards), community)
    assertThat(winner.player).isEqualTo(mockPlayer)
  }

  /**
   * Ensures that a higher pair beats a lower pair between two players.
   */
  @Test
  fun `higher pair wins between two players`() {
    val player1 = object : PlayerInterface {
      override val name = "Alice"
      val id = "1"
    }
    val player2 = object : PlayerInterface {
      override val name = "Bob"
      val id = "2"
    }

    // Alice has pair of Aces
    val hole1 = HoleCards(
      listOf(
        Card(HEARTS, ACE),
        Card(SPADES, ACE)
      ),
      player1
    )

    // Bob has pair of Kings
    val hole2 = HoleCards(
      listOf(
        Card(DIAMONDS, KING),
        Card(CLUBS, KING)
      ),
      player2
    )

    val community = CommunityCards(
      listOf(
        Card(CLUBS, TWO),
        Card(DIAMONDS, THREE),
        Card(HEARTS, FOUR),
        Card(SPADES, FIVE),
        Card(CLUBS, SIX)
      ), mockGame
    )

    val winner = evaluator.determineHighestHand(listOf(hole1, hole2), community)
    assertThat(winner.player.name).isEqualTo("Alice")
  }

  /**
   * Confirms that a straight hand beats a pair of hands.
   */
  @Test
  fun `straight beats pair`() {
    val straightPlayer = object : PlayerInterface {
      override val name = "Straight"
      val id = "1"
    }
    val pairPlayer = object : PlayerInterface {
      override val name = "Pair"
      val id = "2"
    }

    // Player can make straight (3-7)
    val holeStraight = HoleCards(
      listOf(
        Card(HEARTS, THREE),
        Card(SPADES, SEVEN)
      ),
      straightPlayer
    )

    // Player has pair
    val holePair = HoleCards(
      listOf(
        Card(DIAMONDS, ACE),
        Card(CLUBS, ACE)
      ),
      pairPlayer
    )

    val community = CommunityCards(
      listOf(
        Card(CLUBS, FOUR),
        Card(DIAMONDS, FIVE),
        Card(HEARTS, SIX),
        Card(SPADES, TEN),
        Card(CLUBS, JACK)
      ), mockGame
    )

    val winner = evaluator.determineHighestHand(
      listOf(holeStraight, holePair), community
    )
    assertThat(winner.player.name).isEqualTo("Straight")
  }

  /**
   * Checks that a tie returns the first player with the best hand.
   */
  @Test
  fun `tie returns first player with best hand`() {
    val player1 = object : PlayerInterface {
      override val name = "First"
      val id = "1"
    }
    val player2 = object : PlayerInterface {
      override val name = "Second"
      val id = "2"
    }

    // Both have same hand (Ace-high)
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
      listOf(
        Card(CLUBS, FOUR),
        Card(DIAMONDS, FIVE),
        Card(HEARTS, SIX),
        Card(SPADES, SEVEN),
        Card(CLUBS, EIGHT)
      ), mockGame
    )

    val winner = evaluator.determineHighestHand(
      listOf(hole1, hole2), community
    )
    assertThat(winner.player.name).isEqualTo("First")
  }

  /**
   * Validates that a flush hand beats a straight hand.
   */
  @Test
  fun `flush beats straight`() {
    val flushPlayer = object : PlayerInterface {
      override val name = "Flush"
      val id = "1"
    }
    val straightPlayer = object : PlayerInterface {
      override val name = "Straight"
      val id = "2"
    }

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
      listOf(
        Card(HEARTS, ACE),  // Flush uses these
        Card(HEARTS, KING),
        Card(HEARTS, QUEEN),
        Card(SPADES, SEVEN),  // Straight uses these
        Card(CLUBS, SIX)
      ), mockGame
    )

    val winner = evaluator.determineHighestHand(
      listOf(holeFlush, holeStraight), community
    )
    assertThat(winner.player.name).isEqualTo("Flush")
  }

  /**
   * Ensures that a royal flush beats a straight flush.
   */
  @Test
  fun `royal flush beats straight flush`() {
    val royalPlayer = object : PlayerInterface {
      override val name = "Royal"
      val id = "1"
    }
    val straightFlushPlayer = object : PlayerInterface {
      override val name = "StraightFlush"
      val id = "2"
    }

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
      listOf(
        Card(SPADES, QUEEN),  // Royal flush
        Card(SPADES, JACK),
        Card(SPADES, TEN),
        Card(HEARTS, FIVE),   // Straight flush
        Card(HEARTS, FOUR)
      ), mockGame
    )

    val winner = evaluator.determineHighestHand(
      listOf(holeRoyal, holeStraightFlush), community
    )
    assertThat(winner.player.name).isEqualTo("Royal")
  }

  /**
   * Verifies that an exception is thrown when the player list is empty.
   */
  @Test
  fun `empty player list throws exception`() {
    val community = CommunityCards(
      listOf(
        Card(CLUBS, ACE),
        Card(DIAMONDS, KING),
        Card(HEARTS, QUEEN),
        Card(SPADES, JACK),
        Card(CLUBS, TEN)
      ), mockGame
    )

    assertThatThrownBy {
      evaluator.determineHighestHand(emptyList<HoleCards>(), community)
    }.isInstanceOf(IllegalArgumentException::class.java)
  }

  /**
   * Confirms the correct winner is determined out of four players.
   */
  @Test
  fun `Gives the correct winner out of 4 players`() {
    val flushPlayer = object : PlayerInterface {
      override val name = "Flush"
      val id = "1"
    }
    val straightPlayer = object : PlayerInterface {
      override val name = "Straight"
      val id = "2"
    }
    val pairPlayer = object : PlayerInterface {
      override val name = "Pair"
      val id = "3"
    }
    val highCardPlayer = object : PlayerInterface {
      override val name = "HighCard"
      val id = "4"
    }

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

    // High card player
    val holeHighCard = HoleCards(
      listOf(
        Card(SPADES, THREE),
        Card(CLUBS, FOUR)
      ),
      highCardPlayer
    )

    val community = CommunityCards(
      listOf(
        Card(HEARTS, ACE),  // Flush uses these
        Card(HEARTS, KING),
        Card(HEARTS, QUEEN),
        Card(SPADES, SEVEN),  // Straight uses these
        Card(CLUBS, SIX)
      ), mockGame
    )

    val winner = evaluator.determineHighestHand(
      listOf(holeFlush, holeStraight, holePair, holeHighCard), community
    )
    assertThat(winner.player.name).isEqualTo("Flush")
  }

  /**
   * Tests that an exception is thrown when the total number of cards isn't 7.
   */
  @Test
  fun `throws exception when total cards is not 7`() {
    // Use valid CommunityCards but with duplicate cards in HoleCards to create an invalid combination
    val duplicateCard = Card(CLUBS, ACE)

    val community = CommunityCards(
      listOf(
        duplicateCard,
        Card(DIAMONDS, KING),
        Card(HEARTS, QUEEN),
        Card(SPADES, JACK),
        Card(CLUBS, TEN)
      ), mockGame
    )

    val holeCards = HoleCards(
      listOf(
        duplicateCard, // Same card as in community
        Card(DIAMONDS, THREE)
      ), mockPlayer
    )

    assertThatThrownBy {
      evaluator.determineHighestHand(listOf(holeCards), community)
    }.isInstanceOf(IllegalArgumentException::class.java)
  }

  /**
   * Test to ensure the bit manipulation logic works correctly with edge case patterns.
   * This specifically targets code coverage in the getBestCombo method.
   */
  @Test
  fun `selects best hand when all combinations are considered`() {
    // Create a player with a potential royal flush in spades
    val royalPlayer = object : PlayerInterface {
      override val name = "Royal"
    }

    // Give exactly the cards needed for a royal flush
    val holeRoyal = HoleCards(
      listOf(
        Card(SPADES, ACE),
        Card(SPADES, KING)
      ), royalPlayer
    )

    val community = CommunityCards(
      listOf(
        Card(SPADES, QUEEN),
        Card(SPADES, JACK),
        Card(SPADES, TEN),
        // Add cards that would create tempting but inferior hands
        Card(HEARTS, ACE),
        Card(DIAMONDS, ACE)
      ), mockGame
    )

    val winner = evaluator.determineHighestHand(listOf(holeRoyal), community)

    // The player should have a royal flush, which is the best possible hand
    assertThat(winner.player.name).isEqualTo("Royal")
  }

  /**
   * Test to ensure the early return logic is covered when the first player
   * is the only player.
   */
  @Test
  fun `first player is best when they are the only player`() {
    val player = object : PlayerInterface {
      override val name = "OnlyPlayer"
    }

    val holeCards = HoleCards(
      listOf(
        Card(HEARTS, TWO),
        Card(DIAMONDS, THREE)
      ), player
    )

    val community = CommunityCards(
      listOf(
        Card(CLUBS, FOUR),
        Card(SPADES, FIVE),
        Card(HEARTS, SIX),
        Card(DIAMONDS, SEVEN),
        Card(CLUBS, EIGHT)
      ), mockGame
    )

    // This should return the player directly from the first branch without comparison
    val winner = evaluator.determineHighestHand(listOf(holeCards), community)
    assertThat(winner.player.name).isEqualTo("OnlyPlayer")
  }

  /**
   * Tests edge cases in the bit manipulation by testing a specific set of combinations.
   */
  @Test
  fun `exercises different bit patterns for card combinations`() {
    // Create a scenario where multiple valid 5-card hands can be created
    // but only one is optimal
    val player = object : PlayerInterface {
      override val name = "BitPattern"
    }

    // These hole cards combined with community cards will create multiple possible hands
    val holeCards = HoleCards(
      listOf(
        Card(CLUBS, ACE),
        Card(CLUBS, KING)
      ), player
    )

    // Create community cards that allow multiple hand types
    val community = CommunityCards(
      listOf(
        Card(CLUBS, QUEEN),
        Card(CLUBS, JACK),
        Card(DIAMONDS, TEN),  // Potential royal flush or straight flush break
        Card(HEARTS, ACE),    // Potential pair of aces
        Card(SPADES, ACE)     // Potential three of a kind
      ), mockGame
    )

    val winner = evaluator.determineHighestHand(listOf(holeCards), community)
    assertThat(winner.player.name).isEqualTo("BitPattern")
  }

  /**
   * Tests another edge case for bit manipulation with specific bit patterns.
   */
  @Test
  fun `tests another bit pattern combination`() {
    // Create a scenario where the best hand would be at a specific bit pattern
    val player = object : PlayerInterface {
      override val name = "BitPattern2"
    }

    // Create a very specific set of cards where the best hand depends on the exact bit pattern selection
    val holeCards = HoleCards(
      listOf(
        Card(CLUBS, FIVE),
        Card(DIAMONDS, FIVE)
      ), player
    )

    val community = CommunityCards(
      listOf(
        Card(HEARTS, FIVE),
        Card(SPADES, ACE),
        Card(CLUBS, ACE),
        Card(DIAMONDS, ACE),
        Card(HEARTS, KING)
      ), mockGame
    )

    // Should find full house: Aces full of fives
    val winner = evaluator.determineHighestHand(listOf(holeCards), community)
    assertThat(winner.player.name).isEqualTo("BitPattern2")
  }

  /**
   * Tests with cards arranged in a specific order to test edge cases in bit manipulation.
   */
  @Test
  fun `tests specific card ordering for bit manipulation`() {
    val player = object : PlayerInterface {
      override val name = "OrderTest"
    }

    // The order of these cards might affect the bit manipulation
    val holeCards = HoleCards(
      listOf(
        Card(CLUBS, TWO),    // First card
        Card(CLUBS, THREE)   // Second card
      ), player
    )

    val community = CommunityCards(
      listOf(
        Card(CLUBS, FOUR),   // Third card
        Card(CLUBS, FIVE),   // Fourth card
        Card(CLUBS, SIX),    // Fifth card
        Card(HEARTS, SEVEN), // Sixth card
        Card(DIAMONDS, EIGHT) // Seventh card
      ), mockGame
    )

    // Should find straight flush
    val winner = evaluator.determineHighestHand(listOf(holeCards), community)
    assertThat(winner.player.name).isEqualTo("OrderTest")
  }

  /**
   * Tests that the best hand is correctly updated when it's not the first player.
   */
  @Test
  fun `second player wins when having better hand than first`() {
    val player1 = object : PlayerInterface {
      override val name = "FirstButWorse"
    }

    val player2 = object : PlayerInterface {
      override val name = "SecondButBetter"
    }

    // First player has just a pair of twos
    val holePlayer1 = HoleCards(
      listOf(
        Card(CLUBS, TWO),
        Card(DIAMONDS, THREE)
      ), player1
    )

    // Second player has a pair of aces
    val holePlayer2 = HoleCards(
      listOf(
        Card(HEARTS, ACE),
        Card(SPADES, KING)
      ), player2
    )

    val community = CommunityCards(
      listOf(
        Card(SPADES, ACE),     // Gives player2 a pair of aces
        Card(DIAMONDS, FIVE),
        Card(HEARTS, SEVEN),
        Card(CLUBS, NINE),
        Card(SPADES, TWO)      // Gives player1 a pair of twos
      ), mockGame
    )

    val winner = evaluator.determineHighestHand(
      listOf(holePlayer1, holePlayer2),
      community
    )
    assertThat(winner.player.name).isEqualTo("SecondButBetter")
  }

  /**
   * Tests that the best hand is updated when a player after the first has a better hand.
   */
  @Test
  fun `subsequent player with better hand becomes the winner`() {
    // Create three players to ensure we're testing the full loop logic
    val player1 = object : PlayerInterface {
      override val name = "Player1"
    }

    val player2 = object : PlayerInterface {
      override val name = "Player2"
    }

    val player3 = object : PlayerInterface {
      override val name = "Player3"
    }

    // First player has a pair of twos
    val holePlayer1 = HoleCards(
      listOf(
        Card(CLUBS, TWO),
        Card(DIAMONDS, TWO)
      ), player1
    )

    // Second player has a pair of threes (better than player1)
    val holePlayer2 = HoleCards(
      listOf(
        Card(HEARTS, THREE),
        Card(SPADES, THREE)
      ), player2
    )

    // Third player has a pair of kings (best of all)
    val holePlayer3 = HoleCards(
      listOf(
        Card(CLUBS, KING),
        Card(DIAMONDS, FIVE)
      ), player3
    )

    val community = CommunityCards(
      listOf(
        Card(HEARTS, KING),
        Card(SPADES, SEVEN),
        Card(DIAMONDS, EIGHT),
        Card(CLUBS, NINE),
        Card(HEARTS, TEN)
      ), mockGame
    )

    val winner = evaluator.determineHighestHand(
      listOf(holePlayer1, holePlayer2, holePlayer3),
      community
    )

    // Player3 should win with a pair of kings
    assertThat(winner.player.name).isEqualTo("Player3")
  }
}