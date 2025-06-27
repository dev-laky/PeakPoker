package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class GameInfoTest : AnnotationSpec() {
  private fun setPrivateField(obj: Any, fieldName: String, value: Any?) {
    val field = obj.javaClass.getDeclaredField(fieldName)
    field.isAccessible = true
    field.set(obj, value)
  }

  @Test
  fun `getGameInfo returns correct gameId`() {
    val testGameId = GameId("test-game-123")
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val game = PokerGame(10, 20, players, testGameId)
    val gameInfo = game.getGameInfo()

    assertThat(gameInfo.gameId).isEqualTo("test-game-123")
  }

  @Test
  fun `getGameInfo returns correct hasEnded status when game not ended`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val game = PokerGame(10, 20, players)
    val gameInfo = game.getGameInfo()

    assertThat(gameInfo.hasEnded).isFalse()
  }

  @Test
  fun `getGameInfo returns correct hasEnded status when game ended`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val game = PokerGame(10, 20, players)

    // End the game
    game.allIn("Alice")
    game.allIn("Bob")

    val gameInfo = game.getGameInfo()

    assertThat(gameInfo.hasEnded).isTrue()
  }

  @Test
  fun `getGameInfo returns null roundInfo when currentRound is null`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val game = PokerGame(10, 20, players)

    // Force currentRound to null
    setPrivateField(game, "currentRound", null)

    val gameInfo = game.getGameInfo()

    assertThat(gameInfo.roundInfo).isNull()
  }

  @Test
  fun `getGameInfo returns non-null roundInfo when currentRound exists`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val game = PokerGame(10, 20, players)

    val gameInfo = game.getGameInfo()

    assertThat(gameInfo.roundInfo).isNotNull()
  }

  @Test
  fun `getGameInfo with different hasEnded values produces different results`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val game = PokerGame(10, 20, players)

    // Get info when game is active
    val activeGameInfo = game.getGameInfo()

    // Force hasEnded to true
    setPrivateField(game, "hasEnded", true)

    // Get info when game is ended
    val endedGameInfo = game.getGameInfo()

    assertThat(activeGameInfo.hasEnded).isFalse()
    assertThat(endedGameInfo.hasEnded).isTrue()
    assertThat(activeGameInfo.hasEnded).isNotEqualTo(endedGameInfo.hasEnded)
  }

  @Test
  fun `getGameInfo conditional behavior with null currentRound`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val game = PokerGame(10, 20, players)

    // Test with currentRound present
    val gameInfoWithRound = game.getGameInfo()
    assertThat(gameInfoWithRound.roundInfo).isNotNull()

    // Test with currentRound null
    setPrivateField(game, "currentRound", null)
    val gameInfoWithoutRound = game.getGameInfo()
    assertThat(gameInfoWithoutRound.roundInfo).isNull()

    // Verify the conditional produces different results
    assertThat(gameInfoWithRound.roundInfo).isNotEqualTo(gameInfoWithoutRound.roundInfo)
  }

  @Test
  fun `getGameInfo returns same gameId regardless of game state`() {
    val testGameId = GameId("consistent-id")
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val game = PokerGame(10, 20, players, testGameId)

    val initialGameInfo = game.getGameInfo()

    // Change game state
    setPrivateField(game, "hasEnded", true)
    setPrivateField(game, "currentRound", null)

    val changedGameInfo = game.getGameInfo()

    assertThat(initialGameInfo.gameId).isEqualTo(changedGameInfo.gameId)
    assertThat(initialGameInfo.gameId).isEqualTo("consistent-id")
  }
}