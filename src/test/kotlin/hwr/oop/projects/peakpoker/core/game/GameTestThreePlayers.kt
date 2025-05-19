package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.Player
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class GameTestThreePlayers : AnnotationSpec() {
  lateinit var player1: Player
  lateinit var player2: Player
  lateinit var player3: Player
  lateinit var testGame: Game

  @BeforeEach
  fun setup() {
    player1 = Player("Hans")
    player2 = Player("Peter")
    player3 = Player("Max")
    testGame = Game(
      10, 20,
      listOf(player1, player2, player3),
      GameId("testGame100")
    )
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
    testGame.makeTurn()
    val nextPlayer = testGame.getCurrentPlayer()

    assertThat(nextPlayer.name).isNotEqualTo(initialPlayer.name)
    assertThat(testGame.currentPlayerIndex).isEqualTo(0)
  }

  @Test
  fun `checkPlayerValidity returns false for existing players and true for new players`() {
    val existingPlayer = Player("Hans")
    val newPlayer = Player("Sara")

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
  fun `getSmallBlindIndex returns correct value`() {
    assertThat(testGame.smallBlindIndex).isEqualTo(0)
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
  fun `smallBlindIndex is correctly initialized and maintained`() {
    assertThat(testGame.smallBlindIndex).isEqualTo(0)
    assertThat(testGame.smallBlindIndex).isGreaterThanOrEqualTo(0)
    assertThat(testGame.smallBlindIndex).isLessThan(testGame.playersOnTable.size)

    val smallBlindPlayer = testGame.playersOnTable[testGame.smallBlindIndex]
    assertThat(smallBlindPlayer.name).isEqualTo("Hans")
  }

  @Test
  fun `smallBlindIndex boundary conditions`() {
    assertThat(testGame.smallBlindIndex).isNotEqualTo(-1)
    assertThat(testGame.smallBlindIndex).isLessThan(testGame.playersOnTable.size)
    assertThat(testGame.smallBlindIndex).isGreaterThanOrEqualTo(0)
  }

  @Test
  fun `makeTurn advances to next active player`() {
    // Initial state after game creation
    val initialPlayer = testGame.getCurrentPlayer()

    // when
    testGame.makeTurn()

    // then
    val nextPlayer = testGame.getCurrentPlayer()
    assertThat(nextPlayer).isEqualTo(player1)
    assertThat(nextPlayer).isNotEqualTo(initialPlayer)
  }

  @Test
  fun `makeTurn skips folded players`() {
    while (testGame.getCurrentPlayer() != player1) {
      testGame.makeTurn()
    }
    player2.fold()

    // when
    testGame.makeTurn()

    // then
    assertThat(testGame.getCurrentPlayer()).isEqualTo(player3)
  }

  @Test
  fun `makeTurn skips all-in players`() {
    while (testGame.getCurrentPlayer() != player1) {
      testGame.makeTurn()
    }
    player2.allIn()

    // when
    testGame.makeTurn()

    // then
    assertThat(testGame.getCurrentPlayer()).isEqualTo(player3)
  }

  @Test
  fun `makeTurn cycles to first player at the end of table`() {
    while (testGame.getCurrentPlayer() != player3) {
      testGame.makeTurn()
    }

    // when
    testGame.makeTurn()

    // then
    assertThat(testGame.getCurrentPlayer()).isEqualTo(player1)
  }

  @Test
  fun `makeTurn stops when returning to same player with all others inactive`() {
    while (testGame.getCurrentPlayer() != player1) {
      testGame.makeTurn()
    }
    player2.fold()
    player3.allIn()

    // when/then
    testGame.makeTurn()
    assertThat(testGame.getCurrentPlayer()).isEqualTo(player1)

    testGame.makeTurn()
    assertThat(testGame.getCurrentPlayer()).isEqualTo(player1)
  }
}

