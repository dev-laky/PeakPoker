package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class PokerRoundInfoTest : AnnotationSpec() {

  @Test
  fun `getRoundInfo returns correct smallBlindAmount`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 200)
    )
    val round = PokerRound(players, 15, 30, 0)

    val roundInfo = round.getRoundInfo()

    assertThat(roundInfo.smallBlindAmount).isEqualTo(15)
  }

  @Test
  fun `getRoundInfo returns correct bigBlindAmount`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 200)
    )
    val round = PokerRound(players, 25, 50, 0)

    val roundInfo = round.getRoundInfo()

    assertThat(roundInfo.bigBlindAmount).isEqualTo(50)
  }

  @Test
  fun `getRoundInfo returns correct player names`() {
    val players = listOf(
      PokerPlayer("Charlie", 100),
      PokerPlayer("David", 200)
    )
    val round = PokerRound(players, 10, 20, 0)

    val roundInfo = round.getRoundInfo()

    assertThat(roundInfo.players).hasSize(2)
    assertThat(roundInfo.players[0].name).isEqualTo("Charlie")
    assertThat(roundInfo.players[1].name).isEqualTo("David")
  }

  @Test
  fun `getRoundInfo returns correct player chips`() {
    val players = listOf(
      PokerPlayer("Alice", 150),
      PokerPlayer("Bob", 300)
    )
    val round = PokerRound(players, 10, 20, 0)

    val roundInfo = round.getRoundInfo()

    assertThat(roundInfo.players[0].chips).isEqualTo(140)
    assertThat(roundInfo.players[1].chips).isEqualTo(280)
  }

  @Test
  fun `getRoundInfo returns correct player bets`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 200)
    )
    val round = PokerRound(players, 10, 20, 0)

    val roundInfo = round.getRoundInfo()

    assertThat(roundInfo.players[0].bet).isEqualTo(10)
    assertThat(roundInfo.players[1].bet).isEqualTo(20)
  }

  @Test
  fun `getRoundInfo returns correct player folded status`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 200)
    )
    val round = PokerRound(players, 10, 20, 0)

    round.fold("Alice")
    val roundInfo = round.getRoundInfo()

    assertThat(roundInfo.players[0].isFolded).isTrue()
    assertThat(roundInfo.players[1].isFolded).isFalse()
  }

  @Test
  fun `getRoundInfo returns correct player all-in status`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 200)
    )
    val round = PokerRound(players, 10, 20, 0)

    round.allIn("Alice")
    val roundInfo = round.getRoundInfo()

    assertThat(roundInfo.players[0].isAllIn).isTrue()
    assertThat(roundInfo.players[1].isAllIn).isFalse()
  }

  @Test
  fun `getRoundInfo returns correct smallBlindPlayerName`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 200),
      PokerPlayer("Charlie", 300)
    )
    val round = PokerRound(players, 10, 20, 1)

    val roundInfo = round.getRoundInfo()

    assertThat(roundInfo.smallBlindPlayerName).isEqualTo("Bob")
  }

  @Test
  fun `getRoundInfo returns correct roundPhase`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 200)
    )
    val round = PokerRound(players, 10, 20, 0)

    val roundInfo = round.getRoundInfo()

    assertThat(roundInfo.roundPhase).isEqualTo(RoundPhase.PRE_FLOP)
  }

  @Test
  fun `getRoundInfo returns correct communityCards`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 200)
    )
    val round = PokerRound(players, 10, 20, 0)

    val roundInfo = round.getRoundInfo()

    assertThat(roundInfo.communityCards).isEmpty()
  }

  @Test
  fun `getRoundInfo returns correct pot amounts`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 200)
    )
    val round = PokerRound(players, 10, 20, 0)

    val roundInfo = round.getRoundInfo()

    assertThat(roundInfo.pots).hasSize(1)
    assertThat(roundInfo.pots[0].amount).isEqualTo(30)
  }

  @Test
  fun `getRoundInfo returns correct pot eligible player names`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 200)
    )
    val round = PokerRound(players, 10, 20, 0)

    val roundInfo = round.getRoundInfo()

    assertThat(roundInfo.pots[0].eligiblePlayerNames).containsExactly(
      "Alice",
      "Bob"
    )
  }

  @Test
  fun `getRoundInfo returns correct currentPlayerName`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 200)
    )
    val round = PokerRound(players, 10, 20, 0)

    val roundInfo = round.getRoundInfo()

    assertThat(roundInfo.currentPlayerName).isEqualTo("Alice")
  }

  @Test
  fun `getRoundInfo with different smallBlindIndex affects smallBlindPlayerName`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 200),
      PokerPlayer("Charlie", 300)
    )
    val round = PokerRound(players, 10, 20, 2)

    val roundInfo = round.getRoundInfo()

    assertThat(roundInfo.smallBlindPlayerName).isEqualTo("Charlie")
  }

  @Test
  fun `getRoundInfo maps all players correctly`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 200),
      PokerPlayer("Charlie", 300)
    )
    val round = PokerRound(players, 5, 10, 0)

    val roundInfo = round.getRoundInfo()

    assertThat(roundInfo.players).hasSize(3)
    assertThat(roundInfo.players.map { it.name }).containsExactly(
      "Alice",
      "Bob",
      "Charlie"
    )
  }

  @Test
  fun `getRoundInfo maps all pots correctly`() {
    val players = listOf(
      PokerPlayer("Alice", 50),
      PokerPlayer("Bob", 200),
      PokerPlayer("Charlie", 300)
    )
    val round = PokerRound(players, 5, 10, 0)

    round.allIn("Charlie")
    val roundInfo = round.getRoundInfo()

    assertThat(roundInfo.pots.size).isGreaterThanOrEqualTo(1)
    assertThat(roundInfo.pots.all { it.amount > 0 }).isTrue()
  }

  @Test
  fun `getRoundInfo with modified bet amounts`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 200)
    )
    val round = PokerRound(players, 10, 20, 0)

    round.raiseBetTo("Alice", 50)
    val roundInfo = round.getRoundInfo()

    assertThat(roundInfo.players[0].bet).isEqualTo(50)
  }

  @Test
  fun `getRoundInfo with checked player state`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 200)
    )
    val round = PokerRound(players, 10, 10, 0)

    round.check("Alice")
    val roundInfo = round.getRoundInfo()

    assertThat(roundInfo.currentPlayerName).isEqualTo("Bob")
  }
}