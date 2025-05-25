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
    testGame.call(player3)
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
  }
}