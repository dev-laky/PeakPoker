package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class GameEndCheckTest : AnnotationSpec() {
  private fun setPrivateField(obj: Any, fieldName: String, value: Any?) {
    val field = obj.javaClass.getDeclaredField(fieldName)
    field.isAccessible = true
    field.set(obj, value)
  }

  @Test
  fun `withGameEndCheck returns actual action result not null`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val game = PokerGame(10, 20, players)

    setPrivateField(game, "hasEnded", false)

    val initialAliceChips = players[0].chips()

    val result = game.raiseBetTo("Alice", 50)

    assertThat(players[0].chips()).isLessThan(initialAliceChips)

    assertThat(result).isNotNull()
    assertThat(result).isEqualTo(Unit)
  }

  @Test
  fun `withGameEndCheck preserves action return value`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val game = PokerGame(10, 20, players)

    setPrivateField(game, "hasEnded", false)

    val callResult = game.call("Alice")
    val foldResult = game.fold("Alice")

    assertThat(callResult).isNotNull()
    assertThat(foldResult).isNotNull()

    assertThat(callResult).isEqualTo(Unit)
    assertThat(foldResult).isEqualTo(Unit)
  }

  @Test
  fun `withGameEndCheck executes action and returns result when game not ended`() {
    val players = listOf(
      PokerPlayer("Alice", 200),
      PokerPlayer("Bob", 200)
    )

    val game = PokerGame(10, 20, players)

    setPrivateField(game, "hasEnded", false)

    val initialAliceChips = players[0].chips()
    val raiseAmount = 75

    val actionResult = game.raiseBetTo("Alice", raiseAmount)

    val finalAliceChips = players[0].chips()
    assertThat(finalAliceChips).isNotEqualTo(initialAliceChips)

    assertThat(actionResult).isNotNull()
    assertThat(actionResult).isInstanceOf(Unit::class.java)
  }

  @Test
  fun `withGameEndCheck return value type consistency`() {
    val players = listOf(
      PokerPlayer("Alice", 150),
      PokerPlayer("Bob", 150)
    )

    val game = PokerGame(10, 20, players)

    setPrivateField(game, "hasEnded", false)

    val results = mutableListOf<Any?>()

    results.add(game.allIn("Alice"))
    results.add(game.call("Bob"))

    results.forEach { result ->
      assertThat(result).isNotNull()
      assertThat(result).isEqualTo(Unit)
    }

    val firstResult = results[0]
    results.forEach { result ->
      assertThat(result).isSameAs(firstResult)
    }
  }
}