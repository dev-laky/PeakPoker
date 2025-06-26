package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class PokerRoundSpecificPlayerTest : AnnotationSpec() {
  @Test
  fun `allow player with exactly 21 chip to make a move`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 21),  // Player with exactly 1 chip
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    // Set the currentPlayerIndex to Bob
    val currentPlayerIndexField =
      round.javaClass.getDeclaredField("currentPlayerIndex")
    currentPlayerIndexField.isAccessible = true
    currentPlayerIndexField.set(round, 1)

    // Get the next player and verify it's Charlie
    val getNextPlayerMethod = round.javaClass.getDeclaredMethod("getNextPlayer")
    getNextPlayerMethod.isAccessible = true
    val nextPlayer = getNextPlayerMethod.invoke(round) as PokerPlayer

    assertThat(nextPlayer.name).isEqualTo("Charlie")

    // Call makeTurn and verify Bob is allowed to act
    round.allIn("Bob")

    // Bob should now be all-in
    assertThat(players[1].isAllIn()).isTrue()
  }

  @Test
  fun `player with exactly 2 chip can make valid move`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 3),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 1, 2, 2) // Start with Bob who has 1 chip

    // Directly observe Bob's chip count
    assertThat(players[1].chips()).isEqualTo(3)

    // Bob should be able to go all-in with his 1 chip
    round.allIn("Bob")

    // Verify Bob is now all-in and his chip count is 0
    assertThat(players[1].isAllIn()).isTrue()
    assertThat(players[1].chips()).isEqualTo(0)
  }

  @Test
  fun `maxOf should select correct value when two players have same bet`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    // Set equal bets for both players
    players[0].setBetAmount(50)
    players[1].setBetAmount(50)

    val round = PokerRound(players, 10, 20, 0)

    // Access the method that contains maxOf operation
    val getMaxBetMethod = round.javaClass.getDeclaredMethod("getHighestBet")
    getMaxBetMethod.isAccessible = true

    val maxBet = getMaxBetMethod.invoke(round) as Int

    // Verify the correct maximum is returned at the boundary
    assertThat(maxBet).isEqualTo(50)
  }

  @Test
  fun `maxOf should select higher value when bets differ by 1`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )

    // Set bets with boundary difference (50 vs. 51)
    players[0].setBetAmount(50)
    players[1].setBetAmount(51) // Boundary: just 1 higher
    players[2].setBetAmount(40)

    val round = PokerRound(players, 10, 20, 0)

    val getMaxBetMethod = round.javaClass.getDeclaredMethod("getHighestBet")
    getMaxBetMethod.isAccessible = true

    val maxBet = getMaxBetMethod.invoke(round) as Int

    // Should select the higher value at the boundary
    assertThat(maxBet).isEqualTo(51)
  }

  @Test
  fun `maxOf should handle negative bets correctly at boundary`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val round = PokerRound(players, 10, 20, 0)

    // Use reflection to set negative bets (normally impossible)
    players.forEach { player ->
      val betField = player.javaClass.getDeclaredField("bet")
      betField.isAccessible = true
      betField.setInt(player, -1)
    }

    // Set the second player's bet to 0
    val bobBetField = players[1].javaClass.getDeclaredField("bet")
    bobBetField.isAccessible = true
    bobBetField.setInt(players[1], 0)

    val getMaxBetMethod = round.javaClass.getDeclaredMethod("getHighestBet")
    getMaxBetMethod.isAccessible = true

    val maxBet = getMaxBetMethod.invoke(round) as Int

    // Should correctly identify 0 as greater than -1
    assertThat(maxBet).isEqualTo(0)
  }

  @Test
  fun `maxOf should handle largest possible bet values`() {
    val players = listOf(
      PokerPlayer("Alice", Int.MAX_VALUE),
      PokerPlayer("Bob", Int.MAX_VALUE)
    )

    val round = PokerRound(players, 10, 20, 0)

    // Set maximum possible bets
    val aliceBetField = players[0].javaClass.getDeclaredField("bet")
    aliceBetField.isAccessible = true
    aliceBetField.setInt(players[0], Int.MAX_VALUE)

    val bobBetField = players[1].javaClass.getDeclaredField("bet")
    bobBetField.isAccessible = true
    bobBetField.setInt(players[1], Int.MAX_VALUE - 1)

    val getMaxBetMethod = round.javaClass.getDeclaredMethod("getHighestBet")
    getMaxBetMethod.isAccessible = true

    val maxBet = getMaxBetMethod.invoke(round) as Int

    // Should correctly identify the maximum value
    assertThat(maxBet).isEqualTo(Int.MAX_VALUE)
  }

  @Test
  fun `getPlayerByName throws IllegalStateException with correct message when player not found`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    // Access the private method via reflection
    val getPlayerByNameMethod =
      round.javaClass.getDeclaredMethod("getPlayerByName", String::class.java)
    getPlayerByNameMethod.isAccessible = true

    // Test with a name that doesn't exist
    val nonExistentName = "Charlie"

    // Verify exception is thrown with a correct message
    assertThatThrownBy {
      getPlayerByNameMethod.invoke(round, nonExistentName)
    }
      .cause()
      .isInstanceOf(IllegalStateException::class.java)
      .hasMessage("Player with name $nonExistentName not found")
  }

  @Test
  fun `setBlinds detects current bets correctly with non-zero values`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    // Set non-zero bets manually through reflection
    val aliceBetField = players[0].javaClass.getDeclaredField("bet")
    aliceBetField.isAccessible = true
    aliceBetField.setInt(players[0], 5)

    val bobBetField = players[1].javaClass.getDeclaredField("bet")
    bobBetField.isAccessible = true
    bobBetField.setInt(players[1], 10)

    // Create a round with these players
    val round = PokerRound(players, 20, 40, 0)

    // Access setBlinds method
    val setBlindsMethod = round.javaClass.getDeclaredMethod("setBlinds")
    setBlindsMethod.isAccessible = true

    // Execute setBlinds
    setBlindsMethod.invoke(round)

    // Verify blinds weren't set again (current bets should still be 5 and 10)
    assertThat(players[0].bet()).isEqualTo(5)
    assertThat(players[1].bet()).isEqualTo(10)
  }

  @Test
  fun `setBlinds correctly handles the sum of player bets`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    // Create the round
    val round = PokerRound(players, 10, 20, 0)

    // Reset any existing bets
    val resetBetsMethod = round.javaClass.getDeclaredMethod("resetBets")
    resetBetsMethod.isAccessible = true
    resetBetsMethod.invoke(round)

    // Set bets to opposite values that would produce different results with addition vs. subtraction
    val aliceBetField = players[0].javaClass.getDeclaredField("bet")
    aliceBetField.isAccessible = true
    aliceBetField.setInt(players[0], 5)

    val bobBetField = players[1].javaClass.getDeclaredField("bet")
    bobBetField.isAccessible = true
    bobBetField.setInt(
      players[1],
      -5
    )  // Negative value to test addition vs. subtraction

    // Call setBlinds
    val setBlindsMethod = round.javaClass.getDeclaredMethod("setBlinds")
    setBlindsMethod.isAccessible = true
    setBlindsMethod.invoke(round)

    // Expecting addition, so a sum should be 0, and blinds should be set
    assertThat(players[0].bet()).isNotEqualTo(5)
    assertThat(players[1].bet()).isNotEqualTo(-5)
  }

  @Test
  fun `setBlinds with equal blinds properly initializes player bets and advances turn`() {
    // Arrange
    val players = listOf(
      PokerPlayer("Alice", 200),
      PokerPlayer("Bob", 200),
      PokerPlayer("Charlie", 200)
    )

    // Create a round with equal small and big blind amounts
    val round = PokerRound(players, 30, 30, 0)

    // Reset the round state
    val resetBetsMethod = round.javaClass.getDeclaredMethod("resetBets")
    resetBetsMethod.isAccessible = true
    resetBetsMethod.invoke(round)

    val currentPlayerIndexField =
      round.javaClass.getDeclaredField("currentPlayerIndex")
    currentPlayerIndexField.isAccessible = true
    val initialIndex = 0
    currentPlayerIndexField.set(round, initialIndex)

    // Act - execute setBlinds which should call call() when blinds are equal
    val setBlindsMethod = round.javaClass.getDeclaredMethod("setBlinds")
    setBlindsMethod.isAccessible = true
    setBlindsMethod.invoke(round)

    // Assert
    // First player should have the small blind amount bet
    assertThat(players[initialIndex].bet()).isEqualTo(30)

    // The second player should have the same bet due to the call
    assertThat(players[(initialIndex + 1) % players.size].bet()).isEqualTo(30)

    // Player index should have advanced beyond small and big blind positions
    val newIndex = currentPlayerIndexField.get(round) as Int
    assertThat(newIndex).isGreaterThan(initialIndex)
  }
}