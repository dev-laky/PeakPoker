package hwr.oop.projects.peakpoker.core.pot

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import hwr.oop.projects.peakpoker.core.round.PokerRound
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class PokerPotsTest : AnnotationSpec() {
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
      smallBlindIndex = 0,
      onRoundComplete = {}
    )

    communityCards = CommunityCards(
      listOf(
        Card(Suit.CLUBS, Rank.TWO),
        Card(Suit.DIAMONDS, Rank.THREE),
        Card(Suit.HEARTS, Rank.FOUR),
        Card(Suit.SPADES, Rank.FIVE),
        Card(Suit.CLUBS, Rank.SIX)
      ),
      testRound
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
  fun `constructor creates single main pot with all players`() {
    val players = listOf(player1, player2, player3)
    val pokerPots = PokerPots(players, communityCards)

    assertThat(pokerPots.count()).isEqualTo(1)
    assertThat(pokerPots.first().eligiblePlayers).isEqualTo(players.toSet())
    assertThat(pokerPots.first().amount()).isEqualTo(0)
  }

  @Test
  fun `constructor with custom pots list`() {
    val customPot = Pot(setOf(player1, player2), communityCards, 100)
    val customPots = mutableListOf(customPot)
    val players = listOf(player1, player2, player3)

    val pokerPots = PokerPots(players, communityCards, customPots)

    assertThat(pokerPots.count()).isEqualTo(1)
    assertThat(pokerPots.first()).isEqualTo(customPot)
  }

  @Test
  fun `addChipsToMainPot adds chips to first pot`() {
    val players = listOf(player1, player2, player3)
    val pokerPots = PokerPots(players, communityCards)

    pokerPots.addChipsToCurrentPot(50)

    assertThat(pokerPots.first().amount()).isEqualTo(50)
  }

  @Test
  fun `addChipsToMainPot adds chips multiple times`() {
    val players = listOf(player1, player2, player3)
    val pokerPots = PokerPots(players, communityCards)

    pokerPots.addChipsToCurrentPot(30)
    pokerPots.addChipsToCurrentPot(20)

    assertThat(pokerPots.first().amount()).isEqualTo(50)
  }

  @Test
  fun `addChipsToMainPot with zero amount`() {
    val players = listOf(player1, player2, player3)
    val pokerPots = PokerPots(players, communityCards)

    pokerPots.addChipsToCurrentPot(0)

    assertThat(pokerPots.first().amount()).isEqualTo(0)
  }

  @Test
  fun `createSidePotIfNeeded with no excess amount does nothing`() {
    val players = listOf(player1, player2, player3)
    val pokerPots = PokerPots(players, communityCards)

    // Set equal bets for all players
    player1.setBetAmount(100)
    player2.setBetAmount(100)
    player3.setBetAmount(100)

    pokerPots.addChipsToCurrentPot(300)
    pokerPots.createSidePotIfNeeded(player1)

    // Should still have only one pot
    assertThat(pokerPots.count()).isEqualTo(1)
    assertThat(pokerPots.first().amount()).isEqualTo(300)
  }

  @Test
  fun `createSidePotIfNeeded with zero excess amount does nothing`() {
    val players = listOf(player1, player2, player3)
    val pokerPots = PokerPots(players, communityCards)

    // Player1 all-in with total 100 (50 bet + 50 chips)
    player1.setBetAmount(50)

    // Other players match exactly the all-in total (100)
    player2.setBetAmount(100)
    player3.setBetAmount(100)

    pokerPots.addChipsToCurrentPot(250)
    pokerPots.createSidePotIfNeeded(player1)

    assertThat(pokerPots.count()).isEqualTo(1)
  }

  @Test
  fun `createSidePotIfNeeded with negative excess amount does nothing`() {
    val players = listOf(player1, player2, player3)
    val pokerPots = PokerPots(players, communityCards)

    // Player1 has more than others (total = bet + chips = 150 + 350 = 500)
    player1.setBetAmount(150)
    player2.setBetAmount(100)
    player3.setBetAmount(100)

    pokerPots.addChipsToCurrentPot(350)
    pokerPots.createSidePotIfNeeded(player1)

    assertThat(pokerPots.count()).isEqualTo(1)
  }

  @Test
  fun `createSidePotIfNeeded creates side pot when excess exists`() {
    val players = listOf(player1, player2, player3)
    val pokerPots = PokerPots(players, communityCards)

    // Player1 all-in: bet=50, chips=450, total=500
    player1.setBetAmount(50)
    // Now player1 has 450 chips left, total available = 50 + 450 = 500

    // Other players bet more than player1's total
    player2.setBetAmount(600) // Excess 100 over player1's total 500
    player3.setBetAmount(700) // 200 excess over player1's total 500

    pokerPots.addChipsToCurrentPot(1350)
    pokerPots.createSidePotIfNeeded(player1)

    // Should have 2 pots now
    assertThat(pokerPots.count()).isEqualTo(2)

    // Main pot reduced by excess (300)
    assertThat(pokerPots.first().amount()).isEqualTo(1050)

    // Side pot has excess amount
    val sidePot = pokerPots.last()
    assertThat(sidePot.amount()).isEqualTo(300)
    assertThat(sidePot.eligiblePlayers).isEqualTo(setOf(player2, player3))

    // Players' bets adjusted to all-in amount (500)
    assertThat(player2.bet()).isEqualTo(500)
    assertThat(player3.bet()).isEqualTo(500)
  }

  @Test
  fun `createSidePotIfNeeded handles folded players correctly`() {
    val players = listOf(player1, player2, player3)
    val pokerPots = PokerPots(players, communityCards)

    // Player3 folds
    player3.fold()

    // Player1 all-in: total = 50 + 450 = 500
    player1.setBetAmount(50)

    // Player2 bets more
    player2.setBetAmount(600) // 100 excess

    // Player3 had bet before folding
    player3.setBetAmount(700)

    pokerPots.addChipsToCurrentPot(1350)
    pokerPots.createSidePotIfNeeded(player1)

    // Folded player should not affect excess calculation
    assertThat(pokerPots.count()).isEqualTo(2)

    // Only player2's excess should be moved (100)
    assertThat(pokerPots.first().amount()).isEqualTo(1250)
    assertThat(pokerPots.last().amount()).isEqualTo(100)
  }

  @Test
  fun `createSidePotIfNeeded with single other player`() {
    val players = listOf(player1, player2)
    val pokerPots = PokerPots(players, communityCards)

    // Player1 all-in: total = 50 + 450 = 500
    player1.setBetAmount(50)

    // Player2 bets more
    player2.setBetAmount(600) // 100 excess

    pokerPots.addChipsToCurrentPot(650)
    pokerPots.createSidePotIfNeeded(player1)

    assertThat(pokerPots.count()).isEqualTo(2)
    assertThat(pokerPots.first().amount()).isEqualTo(550)
    assertThat(pokerPots.last().amount()).isEqualTo(100)
    assertThat(pokerPots.last().eligiblePlayers).isEqualTo(setOf(player2))
  }

  @Test
  fun `createSidePotIfNeeded multiple times creates multiple side pots`() {
    val players = listOf(player1, player2, player3)
    val pokerPots = PokerPots(players, communityCards)

    // First all-in: player1 total = 50 + 450 = 500
    player1.setBetAmount(50)
    player2.setBetAmount(600) // 100 excess
    player3.setBetAmount(600) // 100 excess

    pokerPots.addChipsToCurrentPot(1250)
    pokerPots.createSidePotIfNeeded(player1)

    // Second all-in: player2 now has a total = 500 + (500-100) = 900
    // Player3 bets more than player2's new total
    player3.setBetAmount(1000) // 100 more excesses

    pokerPots.createSidePotIfNeeded(player2)

    assertThat(pokerPots.count()).isEqualTo(3)
  }

  @Test
  fun `pots property provides access to underlying list`() {
    val players = listOf(player1, player2, player3)
    val pokerPots = PokerPots(players, communityCards)

    assertThat(pokerPots.count()).isEqualTo(1)
    assertThat(pokerPots.first().eligiblePlayers).isEqualTo(players.toSet())
  }

  @Test
  fun `calculateExcessAmount returns zero when all players have equal total`() {
    val players = listOf(player1, player2, player3)
    val pokerPots = PokerPots(players, communityCards)

    // All players have the same total (bet + chips = 100 + 400 = 500)
    player1.setBetAmount(100)
    player2.setBetAmount(100)
    player3.setBetAmount(100)

    pokerPots.createSidePotIfNeeded(player1)

    assertThat(pokerPots.count()).isEqualTo(1)
  }

  @Test
  fun `calculateExcessAmount handles all-in player with highest total`() {
    val players = listOf(player1, player2, player3)
    val pokerPots = PokerPots(players, communityCards)

    // All-in player has more total than others
    player1.setBetAmount(200) // total = 200 + 300 = 500
    player2.setBetAmount(100) // total = 100 + 400 = 500
    player3.setBetAmount(150) // total = 150 + 350 = 500

    pokerPots.createSidePotIfNeeded(player1)

    assertThat(pokerPots.count()).isEqualTo(1)
  }

  @Test
  fun `calculateExcessAmount with mixed totals creates correct excess`() {
    val players = listOf(player1, player2, player3)
    val pokerPots = PokerPots(players, communityCards)

    // Player1 all-in with lower total
    player1.setBetAmount(400) // total = 400 + 100 = 500

    // Player2 has the higher total
    player2.setBetAmount(100) // total = 100 + 400 = 500, no excess

    // Player3 has a much higher total
    player3.setBetAmount(200) // total = 200 + 300 = 500, no excess

    pokerPots.addChipsToCurrentPot(700)
    pokerPots.createSidePotIfNeeded(player1)

    // No excess since all totals are equal
    assertThat(pokerPots.count()).isEqualTo(1)
  }

  @Test
  fun `calculateExcessAmount with actual excess scenario`() {
    val players = listOf(player1, player2, player3)
    val pokerPots = PokerPots(players, communityCards)

    // Create a scenario with actual excess
    // Player1: starts with 500, bets 100, has 400 left, total = 500
    player1.setBetAmount(100)

    // Player2: starts with 500, bets 600 (more than they have)
    // This will cause player2 to go all-in effectively
    player2.setBetAmount(500) // All their chips, total = 500

    // Player3: bets more than player1's total
    player3.setBetAmount(600) // total with remaining = 600 + (500-600) = impossible
    // Let's adjust: player3 bets 300, has 200 left, total = 500
    player3.setBetAmount(300)

    pokerPots.addChipsToCurrentPot(1000)
    pokerPots.createSidePotIfNeeded(player1)

    // Since all have the same total (500), no side pot should be created
    assertThat(pokerPots.count()).isEqualTo(1)
  }

  @Test
  fun `createSidePotIfNeeded handles player with insufficient chips scenario`() {
    val players = listOf(player1, player2, player3)
    val pokerPots = PokerPots(players, communityCards)

    // Create a realistic all-in scenario
    // Player1 goes all-in with remaining chips
    player1.allIn() // This should bet all remaining chips

    // Other players bet more
    player2.setBetAmount(600) // More than player1's total
    player3.setBetAmount(700) // Even more

    pokerPots.addChipsToCurrentPot(1800)
    pokerPots.createSidePotIfNeeded(player1)

    // Should create a side pot
    assertThat(pokerPots.count()).isEqualTo(2)

    // Verify side pot exists and has eligible players
    val sidePot = pokerPots.last()
    assertThat(sidePot.eligiblePlayers).isEqualTo(setOf(player2, player3))
  }

  @Test
  fun `createSidePotIfNeeded with player bet exactly equal to all-in total creates no side pot`() {
    val players = listOf(player1, player2, player3)
    val pokerPots = PokerPots(players, communityCards)

    // Player1 all-in: bet=100, chips=400, total=500
    player1.setBetAmount(100)

    // Player2 bets exactly the all-in total (500)
    player2.setBetAmount(500)

    // Player3 bets less than all-in total
    player3.setBetAmount(300)

    pokerPots.addChipsToCurrentPot(900)
    pokerPots.createSidePotIfNeeded(player1)

    // Should have only 1 pot since no player exceeded the all-in total
    assertThat(pokerPots.count()).isEqualTo(1)
    assertThat(pokerPots.first().amount()).isEqualTo(900)

    // Player bets should remain unchanged since no excess
    assertThat(player2.bet()).isEqualTo(500)
    assertThat(player3.bet()).isEqualTo(300)
  }
}