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
import hwr.oop.projects.peakpoker.core.exceptions.EmptyPlayerListException
import hwr.oop.projects.peakpoker.core.game.GameId
import hwr.oop.projects.peakpoker.core.game.Game
import hwr.oop.projects.peakpoker.core.player.Player
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class HandEvaluatorTest : AnnotationSpec() {
  private val mockPlayer = object : Player {
    override val name: String = "dummy"
    val id: String = "dummyId"
  }

  private val mockGame = object : Game {
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

    val winner = HandEvaluator.determineHighestHand(listOf(holeCards), community)
    assertThat(winner.player).isEqualTo(mockPlayer)
  }

  /**
   * Verifies that a single player wins by default when no competition exists.
   */
  @Test
  fun `higher pair wins between two players`() {
    val player1 = object : Player {
      override val name = "Alice"
      val id = "1"
    }
    val player2 = object : Player {
      override val name = "Bob"
      val id = "2"
    }

    // Alice has pair of Aces
    val hole1 = HoleCards(
      listOf(Card(HEARTS, ACE),
                    Card(SPADES, ACE)),
      player1
    )

    // Bob has pair of Kings
    val hole2 = HoleCards(
      listOf(Card(DIAMONDS, KING),
                    Card(CLUBS, KING)),
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

    val winner = HandEvaluator.determineHighestHand(listOf(hole1, hole2), community)
    assertThat(winner.player.name).isEqualTo("Alice")
  }

  /**
   * Confirms that a straight hand beats a pair hand.
   */
  @Test
  fun `straight beats pair`() {
    val straightPlayer = object : Player {
      override val name = "Straight"
      val id = "1"
    }
    val pairPlayer = object : Player {
      override val name = "Pair"
      val id = "2"
    }

    // Player can make straight (3-7)
    val holeStraight = HoleCards(
      listOf(Card(HEARTS, THREE),
                    Card(SPADES, SEVEN)),
      straightPlayer
    )

    // Player has pair
    val holePair = HoleCards(
      listOf(Card(DIAMONDS, ACE),
                    Card(CLUBS, ACE)),
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

    val winner = HandEvaluator.determineHighestHand(
      listOf(holeStraight, holePair), community
    )
    assertThat(winner.player.name).isEqualTo("Straight")
  }

  /**
   * Checks that a tie returns the first player with the best hand.
   */
  @Test
  fun `tie returns first player with best hand`() {
    val player1 = object : Player {
      override val name = "First"
      val id = "1"
    }
    val player2 = object : Player {
      override val name = "Second"
      val id = "2"
    }

    // Both have same hand (Ace-high)
    val hole1 = HoleCards(
      listOf(Card(HEARTS, ACE),
                    Card(SPADES, TWO)),
      player1
    )

    val hole2 = HoleCards(
      listOf(Card(DIAMONDS, ACE),
                    Card(CLUBS, THREE)),
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

    val winner = HandEvaluator.determineHighestHand(
      listOf(hole1, hole2), community
    )
    assertThat(winner.player.name).isEqualTo("First")
  }

  /**
   * Validates that a flush hand beats a straight hand.
   */
  @Test
  fun `flush beats straight`() {
    val flushPlayer = object : Player {
      override val name = "Flush"
      val id = "1"
    }
    val straightPlayer = object : Player {
      override val name = "Straight"
      val id = "2"
    }

    // Flush player has two hearts
    val holeFlush = HoleCards(
      listOf(Card(HEARTS, TEN),
                    Card(HEARTS, JACK)),
      flushPlayer
    )

    // Straight player
    val holeStraight = HoleCards(
      listOf(Card(CLUBS, NINE),
                    Card(DIAMONDS, EIGHT)),
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

    val winner = HandEvaluator.determineHighestHand(
      listOf(holeFlush, holeStraight), community
    )
    assertThat(winner.player.name).isEqualTo("Flush")
  }

  /**
   * Ensures that a royal flush beats a straight flush.
   */
  @Test
  fun `royal flush beats straight flush`() {
    val royalPlayer = object : Player {
      override val name = "Royal"
      val id = "1"
    }
    val straightFlushPlayer = object : Player {
      override val name = "StraightFlush"
      val id = "2"
    }

    // Royal flush player
    val holeRoyal = HoleCards(
      listOf(Card(SPADES, ACE),
                    Card(SPADES, KING)),
      royalPlayer
    )

    // Straight flush player (7-high)
    val holeStraightFlush = HoleCards(
      listOf(Card(HEARTS, SIX),
                    Card(HEARTS, SEVEN)),
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

    val winner = HandEvaluator.determineHighestHand(
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
      HandEvaluator.determineHighestHand(emptyList(), community)
    }
      .isExactlyInstanceOf(EmptyPlayerListException::class.java)
      .hasMessageContaining("Cannot determine highest hand with empty player list")
  }

  /**
   * Confirms the correct winner is determined out of four players.
   */
  @Test
  fun `gives the correct winner out of four players`() {
    val flushPlayer = object : Player {
      override val name = "Flush"
      val id = "1"
    }
    val straightPlayer = object : Player {
      override val name = "Straight"
      val id = "2"
    }
    val pairPlayer = object : Player {
      override val name = "Pair"
      val id = "3"
    }
    val highCardPlayer = object : Player {
      override val name = "HighCard"
      val id = "4"
    }

    // Flush player has two hearts
    val holeFlush = HoleCards(
      listOf(Card(HEARTS, TEN),
                    Card(HEARTS, JACK)),
      flushPlayer
    )

    // Straight player
    val holeStraight = HoleCards(
      listOf(Card(CLUBS, NINE),
        Card(DIAMONDS, EIGHT)),
      straightPlayer
    )

    // Pair player
    val holePair = HoleCards(
      listOf(Card(CLUBS, TWO),
                    Card(DIAMONDS, TWO)),
      pairPlayer
    )

    // High card player
    val holeHighCard = HoleCards(
      listOf(Card(SPADES, THREE),
                    Card(CLUBS, FOUR)),
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

    val winner = HandEvaluator.determineHighestHand(
      listOf(holeFlush, holeStraight, holePair, holeHighCard), community
    )
    assertThat(winner.player.name).isEqualTo("Flush")
  }
}