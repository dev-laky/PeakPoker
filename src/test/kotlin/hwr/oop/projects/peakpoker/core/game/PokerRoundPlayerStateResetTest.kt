package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class PokerRoundPlayerStateResetTest : AnnotationSpec() {

  @Test
  fun `player folded state is reset after showdown`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    round.fold("Charlie")
    round.allIn("Alice")
    round.allIn("Bob")

    assertThat(players[2].isFolded()).isFalse()
  }

  @Test
  fun `player all-in state is reset after showdown`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    round.allIn("Alice")
    round.allIn("Bob")

    assertThat(players[0].isAllIn()).isFalse()
    assertThat(players[1].isAllIn()).isFalse()
  }

  @Test
  fun `player states are completely reset after complex betting sequence`() {
    val players = listOf(
      PokerPlayer("Alice", 200),
      PokerPlayer("Bob", 150),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 5, 10, 0)

    round.raiseBetTo("Charlie", 50)
    round.fold("Alice")
    round.allIn("Bob")
    round.allIn("Charlie")

    assertThat(players[0].bet()).isEqualTo(0)
    assertThat(players[1].bet()).isEqualTo(0)
    assertThat(players[2].bet()).isEqualTo(0)
    assertThat(players[0].isFolded()).isFalse()
    assertThat(players[1].isAllIn()).isFalse()
    assertThat(players[2].isAllIn()).isFalse()
  }

  @Test
  fun `player bet is reset to zero after showdown`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    round.allIn("Alice")
    round.allIn("Bob")

    assertThat(players[0].bet()).isEqualTo(0)
    assertThat(players[1].bet()).isEqualTo(0)
  }

  @Test
  fun `player isFolded flag is reset after showdown via resetRoundState`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    round.fold("Charlie")
    round.allIn("Alice")
    round.allIn("Bob")

    assertThat(players[2].isFolded()).isFalse()
  }

  @Test
  fun `player isAllIn flag is reset after showdown via resetRoundState`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    round.allIn("Alice")
    round.allIn("Bob")

    assertThat(players[0].isAllIn()).isFalse()
    assertThat(players[1].isAllIn()).isFalse()
  }

  @Test
  fun `both resetBet and resetRoundState effects are applied after showdown`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 10, 0)

    round.call("Charlie")
    round.fold("Alice")
    round.allIn("Bob")
    round.allIn("Charlie")

    assertThat(players[2].bet()).isEqualTo(0)
    assertThat(players[2].hasChecked()).isFalse()
    assertThat(players[0].isFolded()).isFalse()
    assertThat(players[1].isAllIn()).isFalse()
    assertThat(players[2].isAllIn()).isFalse()
  }

  @Test
  fun `multiple check states are cleared after showdown`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 10, 0)

    round.call("Charlie")
    round.check("Alice")
    round.check("Bob")
    round.check("Charlie")

    assertThat(players[2].hasChecked()).isFalse()
    assertThat(players[0].hasChecked()).isFalse()
  }

  @Test
  fun `side pot scenario with bet and state resets`() {
    val players = listOf(
      PokerPlayer("Alice", 50),
      PokerPlayer("Bob", 200),
      PokerPlayer("Charlie", 200)
    )
    val round = PokerRound(players, 5, 10, 0)

    round.allIn("Charlie")
    round.allIn("Alice")
    round.allIn("Bob")

    players.forEach { player ->
      assertThat(player.bet()).isEqualTo(0)
      assertThat(player.isAllIn()).isFalse()
    }
  }

  @Test
  fun `resetBet clears both bet amount and hasChecked flag`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 20, 20, 0)

    round.check("Alice")
    round.check("Bob")

    assertThat(players[0].bet()).isEqualTo(0)
    assertThat(players[0].hasChecked()).isFalse()
  }

  @Test
  fun `resetRoundState clears both isFolded and isAllIn flags`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    round.fold("Charlie")
    round.allIn("Alice")
    round.allIn("Bob")

    assertThat(players[2].isFolded()).isFalse()
    assertThat(players[0].isAllIn()).isFalse()
    assertThat(players[1].isAllIn()).isFalse()
  }

  @Test
  fun `player state completely clean after showdown regardless of prior actions`() {
    val players = listOf(
      PokerPlayer("Alice", 150),
      PokerPlayer("Bob", 150),
      PokerPlayer("Charlie", 150)
    )
    val round = PokerRound(players, 5, 10, 0)

    round.raiseBetTo("Charlie", 50)
    round.fold("Alice")
    round.allIn("Bob")
    round.allIn("Charlie")

    players.forEach { player ->
      assertThat(player.bet()).isEqualTo(0)
      assertThat(player.hasChecked()).isFalse()
      assertThat(player.isFolded()).isFalse()
      assertThat(player.isAllIn()).isFalse()
    }
  }
}