package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class GameTestThreePlayers : AnnotationSpec() {
  private lateinit var player1: PokerPlayer
  private lateinit var player2: PokerPlayer
  private lateinit var player3: PokerPlayer
  private lateinit var testGame: Game

  @BeforeEach
  fun setup() {
    player1 = PokerPlayer("Hans")
    player2 = PokerPlayer("Peter")
    player3 = PokerPlayer("Max")
    testGame = PokerGame(
      10, 20,
      listOf(player1, player2, player3),
      GameId("testGame100")
    )
  }

  /*@Test
  fun `test one whole specific game round`() {
    val players = listOf(player1, player2, player3)

    // check if the deck is shuffled
    val deck = testGame.deck.show()
    assertThat(deck.size).isEqualTo(52 - players.size * 2)
    assertThat(deck).doesNotContainAnyElementsOf(players.flatMap { it.getHand().cards })

    // check if blinds where set correctly
    assertThat(testGame.getSmallBlind()).isEqualTo(10)
    assertThat(testGame.getBigBlind()).isEqualTo(20)

    assertThat(players[0].getBet()).isEqualTo(10)
    assertThat(players[0].getChips()).isEqualTo(90)

    assertThat(players[1].getBet()).isEqualTo(20)
    assertThat(players[1].getChips()).isEqualTo(80)

    assertThat(testGame.getCurrentPlayer()).isEqualTo(players[2])
    assertThat(testGame.getPot()).isEqualTo(30)

    // check if cards are dealt correctly
    players.forEach { player ->
      val hand = player.getHand()
      assertThat(hand.cards.size).isEqualTo(2)
      assertThat(hand.player).isEqualTo(player)
    }

    // PRE FLOP
    assertThat(testGame.gameState).isEqualTo(GameState.PRE_FLOP)
    assertThat(testGame.communityCards.cards).isEmpty()

    testGame.call(players[2])
    testGame.call(players[0])

    // FLOP
    assertThat(testGame.gameState).isEqualTo(GameState.FLOP)

    assertThat(testGame.getCurrentPlayer()).isEqualTo(players[0]) // check if the current player is set to small blind
    assertThat(testGame.communityCards.cards.size).isEqualTo(3)

    testGame.check(players[0])
    testGame.check(players[1])
    testGame.check(players[2])

    // TURN
    assertThat(testGame.gameState).isEqualTo(GameState.TURN)
    assertThat(testGame.getCurrentPlayer()).isEqualTo(players[0]) // check if the current player is set to small blind
    assertThat(testGame.communityCards.cards.size).isEqualTo(4)

    testGame.raiseBetTo(players[0], 50)
    testGame.call(players[1])
    testGame.allIn(players[2])
    testGame.allIn(players[0])
    testGame.allIn(players[1])

    assertThat(testGame.getPot()).isEqualTo(300)

    // SKIP RIVER GOES TO SHOWDOWN (everybody All-In)
    assertThat(testGame.gameState).isEqualTo(GameState.SHOWDOWN)
    assertThat(testGame.communityCards.cards.size).isEqualTo(5)
    assertThat(testGame.getPot()).isEqualTo(300)

    // SHOWDOWN

  }

  @Test
  fun `check if get current player works correctly`() {
    // when
    val currentPlayer = testGame.getCurrentPlayer()

    // then
    assertThat(currentPlayer.name).isEqualTo("Max")
  }

  @Test
  fun `makeTurn advances currentPlayerIndex to next player`() {
    val initialPlayer = testGame.getCurrentPlayer()
    testGame.call(player3)
    val nextPlayer = testGame.getCurrentPlayer()

    assertThat(nextPlayer.name).isNotEqualTo(initialPlayer.name)
    assertThat(testGame.currentPlayerIndex).isEqualTo(0)
  }

  @Test
  fun `checkPlayerValidity returns false for existing players and true for new players`() {
    val existingPlayer = PokerPlayer("Hans")
    val newPlayer = PokerPlayer("Sara")

    assertThat(testGame.checkPlayerValidity(existingPlayer)).isFalse()
    assertThat(testGame.checkPlayerValidity(newPlayer)).isTrue()
  }

  @Test
  fun `getHighestBet is equal to big blind amount on init`() {
    assertThat(testGame.getHighestBet()).isEqualTo(20)
  }

  @Test
  fun `big blind amount must be twice the smallBlind amount`() {
    assertThat(testGame.bigBlindAmount).isEqualTo(testGame.smallBlindAmount * 2)
  }

  @Test
  fun `getSmallBlind returns correct small blind amount`() {
    assertThat(testGame.getSmallBlind()).isEqualTo(10)
  }

  @Test
  fun `getBigBlind returns correct big blind amount`() {
    assertThat(testGame.getBigBlind()).isEqualTo(20)
  }

  @Test
  fun `makeTurn cycles to first player at the end of table`() {
    testGame.call(player3)

    // then
    assertThat(testGame.getCurrentPlayer()).isEqualTo(player1)
  }*/
}