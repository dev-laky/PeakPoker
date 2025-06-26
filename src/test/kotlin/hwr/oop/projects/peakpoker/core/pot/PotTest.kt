package hwr.oop.projects.peakpoker.core.pot

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import hwr.oop.projects.peakpoker.core.game.PokerRound
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class PotTest : AnnotationSpec() {
  private lateinit var player1: PokerPlayer
  private lateinit var player2: PokerPlayer
  private lateinit var player3: PokerPlayer
  private lateinit var communityCards: CommunityCards
  private lateinit var testRound: PokerRound

  @BeforeEach
  fun setup() {
    player1 = PokerPlayer("Hans", 500)
    player2 = PokerPlayer("Peter", 500)
    player3 = PokerPlayer("Max", 500)

    testRound = PokerRound(
      smallBlindAmount = 10,
      bigBlindAmount = 20,
      players = listOf(player1, player2, player3),
      smallBlindIndex = 0
    )

    communityCards = CommunityCards(
      mutableListOf(
        Card(Suit.CLUBS, Rank.TWO),
        Card(Suit.DIAMONDS, Rank.THREE),
        Card(Suit.HEARTS, Rank.FOUR),
        Card(Suit.SPADES, Rank.FIVE),
        Card(Suit.CLUBS, Rank.SIX)
      )
    )

    // Assign hole cards to players
    player1.assignHand(
      HoleCards(
        listOf(
          Card(Suit.HEARTS, Rank.ACE),
          Card(Suit.SPADES, Rank.KING)
        ), player1
      )
    )

    player2.assignHand(
      HoleCards(
        listOf(
          Card(Suit.DIAMONDS, Rank.ACE),
          Card(Suit.CLUBS, Rank.QUEEN)
        ), player2
      )
    )

    player3.assignHand(
      HoleCards(
        listOf(
          Card(Suit.SPADES, Rank.ACE),
          Card(Suit.HEARTS, Rank.JACK)
        ), player3
      )
    )
  }

  @Test
  fun `pot can be created with initial amount`() {
    val initialAmount = 100
    val pot = Pot(setOf(player1, player2), communityCards, initialAmount)

    assertThat(pot.amount()).isEqualTo(initialAmount)
  }

  @Test
  fun `pot can be created with default zero amount`() {
    val pot = Pot(setOf(player1, player2), communityCards)

    assertThat(pot.amount()).isEqualTo(0)
  }

  @Test
  fun `pot stores eligible players correctly`() {
    val players = setOf(player1, player2)
    val pot = Pot(players, communityCards)

    assertThat(pot.eligiblePlayers).isEqualTo(players)
  }

  @Test
  fun `addChips increases pot amount`() {
    val pot = Pot(setOf(player1, player2), communityCards, 50)
    pot.addChips(25)

    assertThat(pot.amount()).isEqualTo(75)
  }

  @Test
  fun `addChips with zero does not change pot amount`() {
    val initialAmount = 50
    val pot = Pot(setOf(player1, player2), communityCards, initialAmount)
    pot.addChips(0)

    assertThat(pot.amount()).isEqualTo(initialAmount)
  }

  @Test
  fun `removeChips decreases pot amount`() {
    val pot = Pot(setOf(player1, player2), communityCards, 100)
    pot.removeChips(25)

    assertThat(pot.amount()).isEqualTo(75)
  }

  @Test
  fun `removeChips throws exception when removing more than available`() {
    val pot = Pot(setOf(player1, player2), communityCards, 50)

    assertThatThrownBy { pot.removeChips(75) }
      .isExactlyInstanceOf(IllegalArgumentException::class.java)
      .hasMessageContaining("Cannot remove more chips than are in the pot")
  }

  @Test
  fun `removeChips works with exact amount`() {
    val initialAmount = 50
    val pot = Pot(setOf(player1, player2), communityCards, initialAmount)
    pot.removeChips(initialAmount)

    assertThat(pot.amount()).isEqualTo(0)
  }

  @Test
  fun `single winner gets full pot`() {
    // Set up a pot with one active player and one folded player
    val initialPotAmount = 100
    player2.fold()
    val pot = Pot(setOf(player1, player2), communityCards, initialPotAmount)
    val initialPlayer1Chips = player1.chips()

    // Execute payout
    pot.payoutWinnings()

    // Verify winner gets entire pot
    assertThat(player1.chips()).isEqualTo(initialPlayer1Chips + initialPotAmount)
  }

  @Test
  fun `pot is split evenly between multiple winners`() {
    // Ensure player1 and player2 have identical hands
    player1.assignHand(
      HoleCards(
        listOf(
          Card(Suit.HEARTS, Rank.ACE),
          Card(Suit.SPADES, Rank.KING)
        ), player1
      )
    )

    player2.assignHand(
      HoleCards(
        listOf(
          Card(Suit.DIAMONDS, Rank.ACE),
          Card(Suit.CLUBS, Rank.KING)
        ), player2
      )
    )

    // Player3 has folded
    player3.fold()

    val initialPotAmount = 100
    val pot =
      Pot(setOf(player1, player2, player3), communityCards, initialPotAmount)

    val initialPlayer1Chips = player1.chips()
    val initialPlayer2Chips = player2.chips()

    pot.payoutWinnings()

    // Each player should get half the pot
    assertThat(player1.chips()).isEqualTo(initialPlayer1Chips + initialPotAmount / 2)
    assertThat(player2.chips()).isEqualTo(initialPlayer2Chips + initialPotAmount / 2)
    assertThat(player3.chips()).isEqualTo(500) // Unchanged
  }

  @Test
  fun `pot with uneven split gives remainder to first winner`() {
    // Ensure player1, player2, and player3 have identical hands
    val commonCard1 = Card(Suit.HEARTS, Rank.ACE)
    val commonCard2 = Card(Suit.SPADES, Rank.KING)

    player1.assignHand(HoleCards(listOf(commonCard1, commonCard2), player1))
    player2.assignHand(
      HoleCards(
        listOf(
          Card(Suit.DIAMONDS, Rank.ACE),
          Card(Suit.CLUBS, Rank.KING)
        ), player2
      )
    )
    player3.assignHand(
      HoleCards(
        listOf(
          Card(Suit.SPADES, Rank.ACE),
          Card(Suit.HEARTS, Rank.KING)
        ), player3
      )
    )

    val initialPotAmount = 100 // Will result in 33 per player with 1 remainder
    val pot =
      Pot(setOf(player1, player2, player3), communityCards, initialPotAmount)

    val initialPlayer1Chips = player1.chips()
    val initialPlayer2Chips = player2.chips()
    val initialPlayer3Chips = player3.chips()

    pot.payoutWinnings()

    // Should split 100 into 33 + 33 + 33, with the remainder 1 going to player1
    val expected1 =
      initialPlayer1Chips + (initialPotAmount / 3) + (initialPotAmount % 3)
    val expected2 = initialPlayer2Chips + (initialPotAmount / 3)
    val expected3 = initialPlayer3Chips + (initialPotAmount / 3)

    assertThat(player1.chips()).isEqualTo(expected1)
    assertThat(player2.chips()).isEqualTo(expected2)
    assertThat(player3.chips()).isEqualTo(expected3)
  }

  @Test
  fun `empty pot has no effect on player chips`() {
    val pot = Pot(setOf(player1, player2), communityCards, 0)
    val initialPlayer1Chips = player1.chips()
    val initialPlayer2Chips = player2.chips()

    pot.payoutWinnings()

    assertThat(player1.chips()).isEqualTo(initialPlayer1Chips)
    assertThat(player2.chips()).isEqualTo(initialPlayer2Chips)
  }

  @Test
  fun `payoutWinnings with no eligible players has no effect`() {
    player1.fold()
    player2.fold()
    player3.fold()

    val pot = Pot(setOf(player1, player2, player3), communityCards, 100)
    val initialPlayer1Chips = player1.chips()
    val initialPlayer2Chips = player2.chips()
    val initialPlayer3Chips = player3.chips()

    pot.payoutWinnings()

    // No chips should be distributed
    assertThat(player1.chips()).isEqualTo(initialPlayer1Chips)
    assertThat(player2.chips()).isEqualTo(initialPlayer2Chips)
    assertThat(player3.chips()).isEqualTo(initialPlayer3Chips)
  }

  @Test
  fun `payoutWinnings handles empty eligible players set`() {
    val pot = Pot(emptySet(), communityCards, 100)

    // This should not throw an exception
    pot.payoutWinnings()

    // Since there are no eligible players, the pot amount remains unchanged
    assertThat(pot.amount()).isEqualTo(100)
  }

  @Test
  fun `pot maintains correct amount after multiple add and remove operations`() {
    val pot = Pot(setOf(player1, player2), communityCards, 100)

    pot.addChips(50)
    assertThat(pot.amount()).isEqualTo(150)

    pot.removeChips(30)
    assertThat(pot.amount()).isEqualTo(120)

    pot.addChips(10)
    pot.removeChips(5)
    assertThat(pot.amount()).isEqualTo(125)
  }

  @Test
  fun `pot with even split has no remainder for first winner`() {
    // Ensure player1 and player2 have identical hands
    player1.assignHand(
      HoleCards(
        listOf(
          Card(Suit.HEARTS, Rank.ACE),
          Card(Suit.SPADES, Rank.KING)
        ), player1
      )
    )

    player2.assignHand(
      HoleCards(
        listOf(
          Card(Suit.DIAMONDS, Rank.ACE),
          Card(Suit.CLUBS, Rank.KING)
        ), player2
      )
    )

    player3.fold()

    val initialPotAmount = 100 // Evenly divisible by 2
    val pot =
      Pot(setOf(player1, player2, player3), communityCards, initialPotAmount)

    val initialPlayer1Chips = player1.chips()
    val initialPlayer2Chips = player2.chips()

    pot.payoutWinnings()

    // Both players should get exactly half, no remainder bonus
    assertThat(player1.chips()).isEqualTo(initialPlayer1Chips + 50)
    assertThat(player2.chips()).isEqualTo(initialPlayer2Chips + 50)
  }

  @Test
  fun `pot with odd amount gives remainder to first winner only`() {
    // Ensure player1 and player2 have identical hands
    player1.assignHand(
      HoleCards(
        listOf(
          Card(Suit.HEARTS, Rank.ACE),
          Card(Suit.SPADES, Rank.KING)
        ), player1
      )
    )

    player2.assignHand(
      HoleCards(
        listOf(
          Card(Suit.DIAMONDS, Rank.ACE),
          Card(Suit.CLUBS, Rank.KING)
        ), player2
      )
    )

    player3.fold()

    val initialPotAmount = 101 // Odd number: 50 + 50 + 1 remainder
    val pot =
      Pot(setOf(player1, player2, player3), communityCards, initialPotAmount)

    val initialPlayer1Chips = player1.chips()
    val initialPlayer2Chips = player2.chips()

    pot.payoutWinnings()

    // Player1 (index 0) gets base amount + remainder
    // Player2 gets only base amount
    assertThat(player1.chips()).isEqualTo(initialPlayer1Chips + 50 + 1)
    assertThat(player2.chips()).isEqualTo(initialPlayer2Chips + 50)
  }

  @Test
  fun `pot with amount 1 and single winner covers boundary condition`() {
    player2.fold()
    player3.fold()

    val pot = Pot(setOf(player1, player2, player3), communityCards, 1)
    val initialPlayer1Chips = player1.chips()

    pot.payoutWinnings()

    // Single winner gets all chips, index == 0 && remainder > 0 condition tested
    assertThat(player1.chips()).isEqualTo(initialPlayer1Chips + 1)
  }

  @Test
  fun `index equals 0 condition with remainder 0`() {
    // index == 0 && remainder == 0 (should not give extra chips)
    player1.assignHand(
      HoleCards(
        listOf(
          Card(Suit.HEARTS, Rank.ACE),
          Card(Suit.SPADES, Rank.KING)
        ), player1
      )
    )

    player2.assignHand(
      HoleCards(
        listOf(
          Card(Suit.DIAMONDS, Rank.ACE),
          Card(Suit.CLUBS, Rank.KING)
        ), player2
      )
    )

    player3.fold()

    val initialPotAmount = 100 // Even split, remainder = 0
    val pot =
      Pot(setOf(player1, player2, player3), communityCards, initialPotAmount)

    val initialPlayer1Chips = player1.chips()
    val initialPlayer2Chips = player2.chips()

    pot.payoutWinnings()

    // Both players get equal amounts, no bonus for the first player
    assertThat(player1.chips()).isEqualTo(initialPlayer1Chips + 50)
    assertThat(player2.chips()).isEqualTo(initialPlayer2Chips + 50)
  }

  @Test
  fun `index not equals 0 with remainder greater than 0`() {
    // index != 0 && remainder > 0 (the second player should not get a remainder)
    player1.assignHand(
      HoleCards(
        listOf(
          Card(Suit.HEARTS, Rank.ACE),
          Card(Suit.SPADES, Rank.KING)
        ), player1
      )
    )

    player2.assignHand(
      HoleCards(
        listOf(
          Card(Suit.DIAMONDS, Rank.ACE),
          Card(Suit.CLUBS, Rank.KING)
        ), player2
      )
    )

    player3.fold()

    val initialPotAmount = 101 // Odd split, remainder = 1
    val pot =
      Pot(setOf(player1, player2, player3), communityCards, initialPotAmount)

    val initialPlayer1Chips = player1.chips()
    val initialPlayer2Chips = player2.chips()

    pot.payoutWinnings()

    // Only the first player gets a remainder
    assertThat(player1.chips()).isEqualTo(initialPlayer1Chips + 50 + 1)
    assertThat(player2.chips()).isEqualTo(initialPlayer2Chips + 50)
  }

  @Test
  fun `single winner with remainder`() {
    // index == 0 && remainder > 0 with single winner
    player2.fold()
    player3.fold()

    val initialPotAmount = 77 // Single winner gets all
    val pot =
      Pot(setOf(player1, player2, player3), communityCards, initialPotAmount)

    val initialPlayer1Chips = player1.chips()

    pot.payoutWinnings()

    // Single winner gets entire pot (including any "remainder")
    assertThat(player1.chips()).isEqualTo(initialPlayer1Chips + 77)
  }

  @Test
  fun `three way tie with remainder`() {
    // index positions 0, 1, 2 with the remainder > 0
    player1.assignHand(
      HoleCards(
        listOf(
          Card(Suit.HEARTS, Rank.ACE),
          Card(Suit.SPADES, Rank.KING)
        ), player1
      )
    )

    player2.assignHand(
      HoleCards(
        listOf(
          Card(Suit.DIAMONDS, Rank.ACE),
          Card(Suit.CLUBS, Rank.KING)
        ), player2
      )
    )

    player3.assignHand(
      HoleCards(
        listOf(
          Card(Suit.SPADES, Rank.ACE),
          Card(Suit.HEARTS, Rank.KING)
        ), player3
      )
    )

    val initialPotAmount = 103 // 34 + 34 + 34 + 1 remainder
    val pot =
      Pot(setOf(player1, player2, player3), communityCards, initialPotAmount)

    val initialPlayer1Chips = player1.chips()
    val initialPlayer2Chips = player2.chips()
    val initialPlayer3Chips = player3.chips()

    pot.payoutWinnings()

    // Only the first player (index 0) gets a remainder
    assertThat(player1.chips()).isEqualTo(initialPlayer1Chips + 34 + 1)
    assertThat(player2.chips()).isEqualTo(initialPlayer2Chips + 34)
    assertThat(player3.chips()).isEqualTo(initialPlayer3Chips + 34)
  }

  @Test
  fun `boundary condition with remainder equals 1`() {
    // remainder == 1 edge case
    player1.assignHand(
      HoleCards(
        listOf(
          Card(Suit.HEARTS, Rank.ACE),
          Card(Suit.SPADES, Rank.KING)
        ), player1
      )
    )

    player2.assignHand(
      HoleCards(
        listOf(
          Card(Suit.DIAMONDS, Rank.ACE),
          Card(Suit.CLUBS, Rank.KING)
        ), player2
      )
    )

    player3.fold()

    val initialPotAmount = 3 // 1 + 1 + 1 remainder
    val pot =
      Pot(setOf(player1, player2, player3), communityCards, initialPotAmount)

    val initialPlayer1Chips = player1.chips()
    val initialPlayer2Chips = player2.chips()

    pot.payoutWinnings()

    // First player gets base and remainder
    assertThat(player1.chips()).isEqualTo(initialPlayer1Chips + 1 + 1)
    assertThat(player2.chips()).isEqualTo(initialPlayer2Chips + 1)
  }

  @Test
  fun `large remainder value`() {
    // remainder > 1 to ensure the condition works for larger remainders
    player1.assignHand(
      HoleCards(
        listOf(
          Card(Suit.HEARTS, Rank.ACE),
          Card(Suit.SPADES, Rank.KING)
        ), player1
      )
    )

    player2.assignHand(
      HoleCards(
        listOf(
          Card(Suit.DIAMONDS, Rank.ACE),
          Card(Suit.CLUBS, Rank.KING)
        ), player2
      )
    )

    player3.assignHand(
      HoleCards(
        listOf(
          Card(Suit.SPADES, Rank.ACE),
          Card(Suit.HEARTS, Rank.KING)
        ), player3
      )
    )

    val initialPotAmount = 100 // 33 + 33 + 33 + 1 remainder
    val pot =
      Pot(setOf(player1, player2, player3), communityCards, initialPotAmount)

    val initialPlayer1Chips = player1.chips()
    val initialPlayer2Chips = player2.chips()
    val initialPlayer3Chips = player3.chips()

    pot.payoutWinnings()

    // First player gets base and remainder
    assertThat(player1.chips()).isEqualTo(initialPlayer1Chips + 33 + 1)
    assertThat(player2.chips()).isEqualTo(initialPlayer2Chips + 33)
    assertThat(player3.chips()).isEqualTo(initialPlayer3Chips + 33)
  }
}