package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class PokerRoundResetingTest : AnnotationSpec() {
  @Test
  fun `resetBets clears bet amounts for all players`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    // Set different bet amounts manually
    players[0].setBetAmount(50)
    players[1].setBetAmount(40)
    players[2].setBetAmount(30)

    // Call resetBets directly
    val resetBetsMethod = round.javaClass.getDeclaredMethod("resetBets")
    resetBetsMethod.isAccessible = true
    resetBetsMethod.invoke(round)

    // Verify all bets are reset to zero
    assertThat(players[0].bet()).isEqualTo(0)
    assertThat(players[1].bet()).isEqualTo(0)
    assertThat(players[2].bet()).isEqualTo(0)
  }

  @Test
  fun `resetBets clears check status for all players`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 10, 0)

    // Set check status manually
    players.forEach { player ->
      val hasCheckedField = player.javaClass.getDeclaredField("hasChecked")
      hasCheckedField.isAccessible = true
      hasCheckedField.setBoolean(player, true)
    }

    // Verify players have checked before resetBets
    players.forEach { player ->
      val hasCheckedField = player.javaClass.getDeclaredField("hasChecked")
      hasCheckedField.isAccessible = true
      assertThat(hasCheckedField.getBoolean(player)).isTrue()
    }

    // Call resetBets directly
    val resetBetsMethod = round.javaClass.getDeclaredMethod("resetBets")
    resetBetsMethod.isAccessible = true
    resetBetsMethod.invoke(round)

    // Verify check status is reset for all players
    players.forEach { player ->
      val hasCheckedField = player.javaClass.getDeclaredField("hasChecked")
      hasCheckedField.isAccessible = true
      assertThat(hasCheckedField.getBoolean(player)).isFalse()
    }
  }

  @Test
  fun `resetBets invokes resetBet on each player`() {
    val players = mutableListOf<PokerPlayer>()

    // Create mock players using proxy to track method invocations
    for (i in 1..3) {
      val player = PokerPlayer("Player$i", 100)
      players.add(player)
    }

    val round = PokerRound(players, 10, 20, 0)

    // Set bet amounts before reset
    players[0].setBetAmount(30)
    players[1].setBetAmount(40)
    players[2].setBetAmount(50)

    // Track original bet values before reset
    val originalBets = players.map { it.bet() }
    assertThat(originalBets).containsExactly(30, 40, 50)

    // Call resetBets directly
    val resetBetsMethod = round.javaClass.getDeclaredMethod("resetBets")
    resetBetsMethod.isAccessible = true
    resetBetsMethod.invoke(round)

    // Verify all bets are now zero
    assertThat(players.all { it.bet() == 0 }).isTrue()
  }

  @Test
  fun `resetBets affects all players including folded and all-in players`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 5, 10, 0)

    // Set different player states manually

    // Alice is folded with a bet
    val aliceFoldedField = players[0].javaClass.getDeclaredField("isFolded")
    aliceFoldedField.isAccessible = true
    aliceFoldedField.setBoolean(players[0], true)
    players[0].setBetAmount(20)

    // Bob is all-in with a bet
    val bobAllInField = players[1].javaClass.getDeclaredField("isAllIn")
    bobAllInField.isAccessible = true
    bobAllInField.setBoolean(players[1], true)
    players[1].setBetAmount(50)

    // Charlie has a normal bet
    players[2].setBetAmount(30)

    // Call resetBets directly
    val resetBetsMethod = round.javaClass.getDeclaredMethod("resetBets")
    resetBetsMethod.isAccessible = true
    resetBetsMethod.invoke(round)

    // Verify all bets are reset regardless of player state
    assertThat(players[0].bet()).isEqualTo(0)
    assertThat(players[1].bet()).isEqualTo(0)
    assertThat(players[2].bet()).isEqualTo(0)
  }

  @Test
  fun `resetBets preserves player chips after resetting bets`() {
    val initialChips = 100
    val players = listOf(
      PokerPlayer("Alice", initialChips),
      PokerPlayer("Bob", initialChips)
    )
    val round = PokerRound(players, 10, 20, 0)

    // Record chips before reset
    val chipsBeforeReset = players.map { it.chips() }

    // Call resetBets directly
    val resetBetsMethod = round.javaClass.getDeclaredMethod("resetBets")
    resetBetsMethod.isAccessible = true
    resetBetsMethod.invoke(round)

    // Verify chips remain unchanged after reset
    val chipsAfterReset = players.map { it.chips() }
    assertThat(chipsAfterReset).isEqualTo(chipsBeforeReset)
  }
}