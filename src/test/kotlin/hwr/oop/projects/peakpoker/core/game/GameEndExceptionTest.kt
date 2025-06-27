package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class GameEndExceptionTest : AnnotationSpec() {
  private fun setPrivateField(obj: Any, fieldName: String, value: Any?) {
    val field = obj.javaClass.getDeclaredField(fieldName)
    field.isAccessible = true
    field.set(obj, value)
  }

  @Test
  fun `withGameEndCheck throws exception when hasEnded is true`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val game = PokerGame(10, 20, players)

    // Force hasEnded to true
    setPrivateField(game, "hasEnded", true)

    // Any game action should throw GameEndedException
    assertThatThrownBy { game.raiseBetTo("Alice", 50) }
      .isExactlyInstanceOf(PokerGame.GameEndedException::class.java)
      .hasMessageContaining("Game has ended - no more actions allowed")
  }

  @Test
  fun `withGameEndCheck allows action when hasEnded is false`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val game = PokerGame(10, 20, players)

    // Ensure hasEnded is false
    setPrivateField(game, "hasEnded", false)

    // Game action should succeed without throwing exception
    game.raiseBetTo("Alice", 50)

    // Verify the action was processed (Alice should have less chips)
    assertThat(players[0].chips()).isLessThan(100)
  }

  @Test
  fun `hasEnded conditional behavior affects all game actions`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val game = PokerGame(10, 20, players)

    // Test when hasEnded is false - actions should work
    setPrivateField(game, "hasEnded", false)
    game.call("Alice") // Should work

    // Test when hasEnded is true - all actions should throw
    setPrivateField(game, "hasEnded", true)

    assertThatThrownBy { game.call("Bob") }
      .isInstanceOf(PokerGame.GameEndedException::class.java)

    assertThatThrownBy { game.check("Bob") }
      .isInstanceOf(PokerGame.GameEndedException::class.java)

    assertThatThrownBy { game.fold("Bob") }
      .isInstanceOf(PokerGame.GameEndedException::class.java)

    assertThatThrownBy { game.allIn("Bob") }
      .isInstanceOf(PokerGame.GameEndedException::class.java)

    assertThatThrownBy { game.raiseBetTo("Bob", 30) }
      .isInstanceOf(PokerGame.GameEndedException::class.java)
  }
}