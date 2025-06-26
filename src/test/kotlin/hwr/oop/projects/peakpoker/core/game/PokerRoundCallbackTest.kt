package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import java.util.concurrent.atomic.AtomicBoolean

class PokerRoundCallbackTest : AnnotationSpec() {

  @Test
  fun `callback does not execute before round completion`() {
    val callbackExecuted = AtomicBoolean(false)
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val round = PokerRound(players, 10, 20, 0)
    round.restoreCallback { callbackExecuted.set(true) }

    round.call("Alice")

    assertThat(callbackExecuted.get()).isFalse()
  }

  @Test
  fun `default callback does not throw exception when invoked`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val round = PokerRound(players, 10, 20, 0)

    round.allIn("Alice")
    round.allIn("Bob")
  }

  @Test
  fun `callback executes with side effects`() {
    val sideEffectList = mutableListOf<String>()
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val round = PokerRound(players, 10, 20, 0)
    round.restoreCallback {
      sideEffectList.add("round_completed")
      sideEffectList.add("cleanup_done")
    }

    round.allIn("Alice")
    round.allIn("Bob")

    assertThat(sideEffectList).containsExactly(
      "round_completed",
      "cleanup_done"
    )
  }
}