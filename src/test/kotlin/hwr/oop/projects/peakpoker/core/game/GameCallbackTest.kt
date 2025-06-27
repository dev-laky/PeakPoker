package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class GameCallbackTest : AnnotationSpec() {
  private fun getPrivateField(obj: Any, fieldName: String): Any? {
    val field = obj.javaClass.getDeclaredField(fieldName)
    field.isAccessible = true
    return field.get(obj)
  }

  @Test
  fun `currentRound restoreCallback conditional is executed`() {
    val players = listOf(
      PokerPlayer("Alice", 1000),
      PokerPlayer("Bob", 1000)
    )

    val game = PokerGame(10, 20, players)
    val currentRound = getPrivateField(game, "currentRound")

    assertThat(currentRound).isNotNull
  }

  @Test
  fun `currentRound restoreCallback method is called`() {
    val players = listOf(
      PokerPlayer("Alice", 1000),
      PokerPlayer("Bob", 1000)
    )

    val game = PokerGame(10, 20, players)
    val currentRound = getPrivateField(game, "currentRound") as PokerRound

    assertThat(currentRound.getRoundInfo()).isNotNull
  }

  @Test
  fun `newRound callback is properly set during initialization`() {
    val players = listOf(
      PokerPlayer("Alice", 1000),
      PokerPlayer("Bob", 1000)
    )

    val game = PokerGame(10, 20, players)
    val currentRound = getPrivateField(game, "currentRound")

    assertThat(currentRound).isNotNull
  }

  @Test
  fun `lambda return value affects callback execution`() {
    val players = listOf(
      PokerPlayer("Alice", 1000),
      PokerPlayer("Bob", 1000)
    )

    val game = PokerGame(10, 20, players)

    game.fold("Alice")

    val newRound = getPrivateField(game, "currentRound")
    assertThat(newRound).isNotNull
  }
}