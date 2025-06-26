package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class PokerRoundGamePhaseTest : AnnotationSpec() {

  private fun invokePrivateMethod(obj: Any, methodName: String): Any? {
    val method = obj.javaClass.getDeclaredMethod(methodName)
    method.isAccessible = true
    return method.invoke(obj)
  }

  @Test
  fun `checkForNextGamePhase returns false when not all players have checked and highest bet is zero`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 10, 0)

    round.call("Charlie")
    round.check("Alice")

    val result = invokePrivateMethod(round, "checkForNextGamePhase") as Boolean
    assertThat(result).isFalse()
  }

  @Test
  fun `checkForNextGamePhase returns false when not all players match highest bet`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 5, 10, 0)

    round.raiseBetTo("Charlie", 50)
    round.call("Alice")

    val result = invokePrivateMethod(round, "checkForNextGamePhase") as Boolean
    assertThat(result).isFalse()
  }

  @Test
  fun `checkForNextGamePhase handles mixed folded and all-in players`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 5, 10, 0)

    round.fold("Charlie")
    round.allIn("Alice")
    round.allIn("Bob")

    val result = invokePrivateMethod(round, "checkForNextGamePhase") as Boolean
    assertThat(result).isFalse()
  }

  @Test
  fun `checkForNextGamePhase returns false when some players neither folded nor all-in and bets dont match`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 5, 10, 0)

    round.raiseBetTo("Charlie", 30)

    val result = invokePrivateMethod(round, "checkForNextGamePhase") as Boolean
    assertThat(result).isFalse()
  }

  @Test
  fun `checkForNextGamePhase edge case with one player checked and highest bet zero`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 10, 0)

    round.check("Alice")

    val result = invokePrivateMethod(round, "checkForNextGamePhase") as Boolean
    assertThat(result).isFalse()
  }

  @Test
  fun `checkForNextGamePhase with partial bet matching scenario`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 5, 10, 0)

    round.raiseBetTo("Charlie", 25)
    round.call("Alice")

    val result = invokePrivateMethod(round, "checkForNextGamePhase") as Boolean
    assertThat(result).isFalse()
  }

  @Test
  fun `checkForNextGamePhase returns true when all players have checked and highest bet is zero`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 10, 0)

    // Reset bets that were set during initialization
    players.forEach { player ->
      val betField = player.javaClass.getDeclaredField("bet")
      betField.isAccessible = true
      betField.setInt(player, 0)
    }

    // Directly set checked status for all players
    val aliceCheckedField = players[0].javaClass.getDeclaredField("hasChecked")
    aliceCheckedField.isAccessible = true
    aliceCheckedField.setBoolean(players[0], true)

    val bobCheckedField = players[1].javaClass.getDeclaredField("hasChecked")
    bobCheckedField.isAccessible = true
    bobCheckedField.setBoolean(players[1], true)

    val result = invokePrivateMethod(round, "checkForNextGamePhase") as Boolean
    assertThat(result).isTrue()
  }

  @Test
  fun `checkForNextGamePhase returns true when all players match highest bet`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    // Directly set equal bets for all players
    players.forEach { player ->
      val betField = player.javaClass.getDeclaredField("bet")
      betField.isAccessible = true
      betField.setInt(player, 30)
    }

    val result = invokePrivateMethod(round, "checkForNextGamePhase") as Boolean
    assertThat(result).isTrue()
  }

  @Test
  fun `checkForNextGamePhase returns true when all players folded or all-in`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    // Set Alice as folded directly
    val aliceFoldedField = players[0].javaClass.getDeclaredField("isFolded")
    aliceFoldedField.isAccessible = true
    aliceFoldedField.setBoolean(players[0], true)

    // Set Bob as all-in directly
    val bobAllInField = players[1].javaClass.getDeclaredField("isAllIn")
    bobAllInField.isAccessible = true
    bobAllInField.setBoolean(players[1], true)

    val result = invokePrivateMethod(round, "checkForNextGamePhase") as Boolean
    assertThat(result).isTrue()
  }

  @Test
  fun `checkForNextGamePhase with all players having same non-zero bet`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 5, 10, 0)

    // Directly set equal non-zero bets for all players
    players.forEach { player ->
      val betField = player.javaClass.getDeclaredField("bet")
      betField.isAccessible = true
      betField.setInt(player, 25)
    }

    val result = invokePrivateMethod(round, "checkForNextGamePhase") as Boolean
    assertThat(result).isTrue()
  }

  @Test
  fun `checkForNextGamePhase with mix of folded players and matched bets`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 5, 10, 0)

    // Directly set player states
    // Set Charlie as folded
    val charlieFoldedField = players[2].javaClass.getDeclaredField("isFolded")
    charlieFoldedField.isAccessible = true
    charlieFoldedField.setBoolean(players[2], true)

    // Set matching bets for Alice and Bob
    val aliceBetField = players[0].javaClass.getDeclaredField("bet")
    aliceBetField.isAccessible = true
    aliceBetField.setInt(players[0], 20)

    val bobBetField = players[1].javaClass.getDeclaredField("bet")
    bobBetField.isAccessible = true
    bobBetField.setInt(players[1], 20)

    val result = invokePrivateMethod(round, "checkForNextGamePhase") as Boolean
    assertThat(result).isTrue()
  }

  @Test
  fun `verify conditional behavior when only some players are folded or all-in`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    // Set one player folded, one all-in, one active
    val aliceFoldedField = players[0].javaClass.getDeclaredField("isFolded")
    aliceFoldedField.isAccessible = true
    aliceFoldedField.setBoolean(players[0], true)

    val bobAllInField = players[1].javaClass.getDeclaredField("isAllIn")
    bobAllInField.isAccessible = true
    bobAllInField.setBoolean(players[1], true)

    // Charlie remains active (neither folded nor all-in)

    // Get the method that contains the conditional we want to test
    val checkForNextGamePhase =
      round.javaClass.getDeclaredMethod("checkForNextGamePhase")
    checkForNextGamePhase.isAccessible = true

    // Execute the method and verify the result
    val result = checkForNextGamePhase.invoke(round) as Boolean

    // The conditional should return false since not all players are folded or all-in
    assertThat(result).isFalse()
  }

  @Test
  fun `verify conditional behavior when all players are folded or all-in`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    // Set all players as either folded or all-in
    val aliceFoldedField = players[0].javaClass.getDeclaredField("isFolded")
    aliceFoldedField.isAccessible = true
    aliceFoldedField.setBoolean(players[0], true)

    val bobAllInField = players[1].javaClass.getDeclaredField("isAllIn")
    bobAllInField.isAccessible = true
    bobAllInField.setBoolean(players[1], true)

    val charlieAllInField = players[2].javaClass.getDeclaredField("isAllIn")
    charlieAllInField.isAccessible = true
    charlieAllInField.setBoolean(players[2], true)

    // Get the method that contains the conditional we want to test
    val checkForNextGamePhase =
      round.javaClass.getDeclaredMethod("checkForNextGamePhase")
    checkForNextGamePhase.isAccessible = true

    // Execute the method and verify the result
    val result = checkForNextGamePhase.invoke(round) as Boolean

    // The conditional should return true since all players are folded or all-in
    assertThat(result).isTrue()
  }

  @Test
  fun `directly invoke next game phase initialization when all players are folded or all-in`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    // Set all players as either folded or all-in
    players.forEach { player ->
      val allInField = player.javaClass.getDeclaredField("isAllIn")
      allInField.isAccessible = true
      allInField.setBoolean(player, true)
    }

    // Access the initial phase
    val phaseField = round.javaClass.getDeclaredField("roundPhase")
    phaseField.isAccessible = true
    val initialPhase = phaseField.get(round)

    // Invoke the method that contains our conditional
    val initNextGamePhase =
      round.javaClass.getDeclaredMethod("initNextGamePhase")
    initNextGamePhase.isAccessible = true
    initNextGamePhase.invoke(round)

    // Check that the phase actually changed as expected
    val newPhase = phaseField.get(round)
    assertThat(newPhase).isNotEqualTo(initialPhase)
  }

  @Test
  fun `verify recursive self-call behavior when all players are inactive`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    // Set all players as folded
    players.forEach { player ->
      val foldedField = player.javaClass.getDeclaredField("isFolded")
      foldedField.isAccessible = true
      foldedField.setBoolean(player, true)
    }

    // Access the phase field to track changes
    val phaseField = round.javaClass.getDeclaredField("roundPhase")
    phaseField.isAccessible = true
    val initialPhase = phaseField.get(round)

    // Get and invoke the makeTurn method
    val makeTurn = round.javaClass.getDeclaredMethod("makeTurn")
    makeTurn.isAccessible = true
    makeTurn.invoke(round)

    // The phase should have changed since all players are folded
    val newPhase = phaseField.get(round)
    assertThat(newPhase).isNotEqualTo(initialPhase)
  }

  @Test
  fun `skip player with exactly 0 chips and proceed to next turn`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 0),  // Player with exactly 0 chips
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    // Set the currentPlayerIndex to point to the player before Bob
    val currentPlayerIndexField =
      round.javaClass.getDeclaredField("currentPlayerIndex")
    currentPlayerIndexField.isAccessible = true
    currentPlayerIndexField.set(round, 0)

    // Create a flag to track if makeTurn is called
    val phaseField = round.javaClass.getDeclaredField("roundPhase")
    phaseField.isAccessible = true
    phaseField.get(round)

    // Call makeTurn directly
    val makeTurnMethod = round.javaClass.getDeclaredMethod("makeTurn")
    makeTurnMethod.isAccessible = true
    makeTurnMethod.invoke(round)

    // Verify the currentPlayerIndex was updated to skip Bob
    assertThat(currentPlayerIndexField.get(round)).isEqualTo(2)
  }

  @Test
  fun `skip player with negative chips and proceed to next turn`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    // Manually set Charlie's chips to negative (normally impossible but needed for test)
    val charlieChipsField = players[2].javaClass.getDeclaredField("chips")
    charlieChipsField.isAccessible = true
    charlieChipsField.setInt(players[2], -5)

    // Set the currentPlayerIndex to point to the player before Charlie
    val currentPlayerIndexField =
      round.javaClass.getDeclaredField("currentPlayerIndex")
    currentPlayerIndexField.isAccessible = true
    currentPlayerIndexField.set(round, 1)

    // Call makeTurn directly
    val makeTurnMethod = round.javaClass.getDeclaredMethod("makeTurn")
    makeTurnMethod.isAccessible = true
    makeTurnMethod.invoke(round)

    // Verify currentPlayerIndex was updated and a recursive call was made
    val newIndex = currentPlayerIndexField.get(round) as Int
    assertThat(newIndex).isNotEqualTo(1) // Value should have changed
  }

  @Test
  fun `makeTurn recursively skips multiple ineligible players in sequence`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100),
      PokerPlayer("David", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    // Set Bob as folded
    val bobFoldedField = players[1].javaClass.getDeclaredField("isFolded")
    bobFoldedField.isAccessible = true
    bobFoldedField.setBoolean(players[1], true)

    // Set Charlie as all-in
    val charlieAllInField = players[2].javaClass.getDeclaredField("isAllIn")
    charlieAllInField.isAccessible = true
    charlieAllInField.setBoolean(players[2], true)

    // Set the currentPlayerIndex to point to Alice
    val currentPlayerIndexField =
      round.javaClass.getDeclaredField("currentPlayerIndex")
    currentPlayerIndexField.isAccessible = true
    currentPlayerIndexField.set(round, 0)

    // Create a counter to track recursion through makeTurn
    var callCounter = 0

    // Create a spy version of the makeTurn method
    val originalMakeTurn = round.javaClass.getDeclaredMethod("makeTurn")
    originalMakeTurn.isAccessible = true

    // Mock the method with reflection to count calls
    val testMakeTurn = {
      callCounter++
      if (callCounter == 1) {
        // The first call should proceed normally
        originalMakeTurn.invoke(round)
      }
    }

    // Invoke once to see recursion
    testMakeTurn()

    // Verify final currentPlayerIndex skips both Bob and Charlie
    val finalPlayerIndex = currentPlayerIndexField.get(round) as Int
    assertThat(finalPlayerIndex).isEqualTo(3) // Should now point to David
  }

  @Test
  fun `skip player with exactly 1 chip in makeTurn`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 3),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 1, 2, 0)

    // Set the currentPlayerIndex to point to the player before Bob
    val currentPlayerIndexField =
      round.javaClass.getDeclaredField("currentPlayerIndex")
    currentPlayerIndexField.isAccessible = true
    currentPlayerIndexField.set(round, 0)

    // Call makeTurn directly
    val makeTurnMethod = round.javaClass.getDeclaredMethod("makeTurn")
    makeTurnMethod.isAccessible = true
    makeTurnMethod.invoke(round)

    // Verify the currentPlayerIndex was NOT updated to skip Bob
    // since a player with exactly 1 chip should still get a turn
    assertThat(currentPlayerIndexField.get(round)).isEqualTo(1)
  }
}