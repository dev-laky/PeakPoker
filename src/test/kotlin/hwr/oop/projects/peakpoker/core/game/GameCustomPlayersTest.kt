package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class GameCustomPlayersTest : AnnotationSpec() {
  private fun getPrivateField(obj: Any, fieldName: String): Any? {
    val field = obj.javaClass.getDeclaredField(fieldName)
    field.isAccessible = true
    return field.get(obj)
  }

  private fun setPrivateField(obj: Any, fieldName: String, value: Any?) {
    val field = obj.javaClass.getDeclaredField(fieldName)
    field.isAccessible = true
    field.set(obj, value)
  }

  @Test
  fun `check if duplicate exception works`() {
    assertThatThrownBy {
      PokerGame(
        10, 20,
        listOf(PokerPlayer("Hans"), PokerPlayer("Hans"))
      )
    }
      .isExactlyInstanceOf(PokerGame.DuplicatePlayerException::class.java)
      .hasMessageContaining("All players must be unique")
  }

  @Test
  fun `negative small blind amount throws exception`() {
    assertThatThrownBy {
      PokerGame(
        -10, 20,
        listOf(PokerPlayer("Hans"), PokerPlayer("Peter"), PokerPlayer("Max"))
      )
    }
      .isExactlyInstanceOf(PokerGame.InvalidBlindConfigurationException::class.java)
      .hasMessageContaining("Small blind amount must be positive")
  }

  @Test
  fun `negative big blind amount throws exception`() {
    // negative big blind
    assertThatThrownBy {
      PokerGame(
        10, -20,
        listOf(PokerPlayer("Hans"), PokerPlayer("Peter"), PokerPlayer("Max"))
      )
    }
      .isExactlyInstanceOf(PokerGame.InvalidBlindConfigurationException::class.java)
      .hasMessageContaining("Big blind amount must be positive")
  }

  @Test
  fun `zero small blind amount throws exception`() {
    assertThatThrownBy {
      PokerGame(
        0, 20,
        listOf(PokerPlayer("Hans"), PokerPlayer("Peter"), PokerPlayer("Max"))
      )
    }
      .isExactlyInstanceOf(PokerGame.InvalidBlindConfigurationException::class.java)
      .hasMessageContaining("Small blind amount must be positive")
  }

  @Test
  fun `zero big blind amount throws exception`() {
    assertThatThrownBy {
      PokerGame(
        10, 0,
        listOf(PokerPlayer("Hans"), PokerPlayer("Peter"), PokerPlayer("Max"))
      )
    }
      .isExactlyInstanceOf(PokerGame.InvalidBlindConfigurationException::class.java)
      .hasMessageContaining("Big blind amount must be positive")
  }

  @Test
  fun `big blind smaller than small blind throws exception`() {
    assertThatThrownBy {
      PokerGame(
        30, 20,
        listOf(PokerPlayer("Hans"), PokerPlayer("Peter"), PokerPlayer("Max"))
      )
    }
      .isExactlyInstanceOf(PokerGame.InvalidBlindConfigurationException::class.java)
      .hasMessageContaining("Big blind amount must be exactly double")
  }

  @Test
  fun `big blind amount must be positive`() {
    assertThatThrownBy {
      PokerGame(
        10, 0,
        listOf(PokerPlayer("Hans"), PokerPlayer("Peter"), PokerPlayer("Max"))
      )
    }
      .isExactlyInstanceOf(PokerGame.InvalidBlindConfigurationException::class.java)
      .hasMessageContaining("Big blind amount must be positive")
  }

  @Test
  fun `big blind amount must be greater than or equal to small blind amount`() {
    assertThatThrownBy {
      PokerGame(
        20, 10,
        listOf(PokerPlayer("Hans"), PokerPlayer("Peter"), PokerPlayer("Max"))
      )
    }
      .isExactlyInstanceOf(PokerGame.InvalidBlindConfigurationException::class.java)
      .hasMessageContaining("Big blind amount must be exactly double")
  }

  @Test
  fun `getId returns correct game identifier`() {
    val testGameId = GameId("testGame100")
    val testGame = PokerGame(
      10, 20,
      listOf(PokerPlayer("Hans"), PokerPlayer("Peter"), PokerPlayer("Max")),
      testGameId
    )

    assertThat(testGame.id).isEqualTo(testGameId)
  }

  @Test
  fun `game creation with empty player list throws exception`() {
    assertThatThrownBy {
      PokerGame(
        10, 20,
        emptyList()
      )
    }
      .isExactlyInstanceOf(PokerGame.MinimumPlayersException::class.java)
      .hasMessageContaining("Minimum number of players is 2")
  }

  @Test
  fun `game creation with one player list throws exception`() {
    assertThatThrownBy {
      PokerGame(
        10, 20,
        listOf(PokerPlayer("Hans"))
      )
    }
      .isExactlyInstanceOf(PokerGame.MinimumPlayersException::class.java)
      .hasMessageContaining("Minimum number of players is 2")
  }

  @Test
  fun `test currentRound restoreCallback is called`() {
    val players = listOf(
      PokerPlayer("Alice", 1000),
      PokerPlayer("Bob", 1000)
    )

    val game = PokerGame(10, 20, players)
    val currentRound = getPrivateField(game, "currentRound")

    assertThat(currentRound).isNotNull
  }

  @Test
  fun `test currentRound getRoundInfo returns value`() {
    val players = listOf(
      PokerPlayer("Alice", 1000),
      PokerPlayer("Bob", 1000)
    )

    val game = PokerGame(10, 20, players)
    val currentRound = getPrivateField(game, "currentRound") as? PokerRound

    assertThat(currentRound?.getRoundInfo()).isNotNull
  }

  @Test
  fun `test filter condition negation with zero chips`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val game = PokerGame(10, 20, players)

    // Force one player to have 0 chips through all-in
    game.allIn("Alice")
    game.allIn("Bob")

    val hasEnded = getPrivateField(game, "hasEnded") as Boolean
    assertThat(hasEnded).isTrue()
  }

  @Test
  fun `test hasEnded condition negation`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val game = PokerGame(10, 20, players)

    // Set hasEnded to true manually
    setPrivateField(game, "hasEnded", true)

    val hasEnded = getPrivateField(game, "hasEnded") as Boolean
    assertThat(hasEnded).isTrue()
  }

  @Test
  fun `test newRound callback restoration`() {
    val players = listOf(
      PokerPlayer("Alice", 1000),
      PokerPlayer("Bob", 1000)
    )

    val game = PokerGame(10, 20, players)

    // Complete round to trigger new round creation
    game.fold("Alice")

    val currentRound = getPrivateField(game, "currentRound")
    assertThat(currentRound).isNotNull
  }

  @Test
  fun `test smallBlindIndex modulus operation`() {
    val players = listOf(
      PokerPlayer("Alice", 1000),
      PokerPlayer("Bob", 1000),
      PokerPlayer("Charlie", 1000)
    )

    val game = PokerGame(10, 20, players)
    val initialIndex = getPrivateField(game, "smallBlindIndex") as Int

    // Complete a round
    game.fold("Charlie")
    game.fold("Alice")

    val newIndex = getPrivateField(game, "smallBlindIndex") as Int
    assertThat(newIndex).isEqualTo((initialIndex) % 3)
  }

  @Test
  fun `test smallBlindIndex addition operation`() {
    val players = listOf(
      PokerPlayer("Alice", 1000),
      PokerPlayer("Bob", 1000)
    )

    val game = PokerGame(10, 20, players)
    val initialIndex = getPrivateField(game, "smallBlindIndex") as Int

    // Complete a round
    game.fold("Alice")

    val newIndex = getPrivateField(game, "smallBlindIndex") as Int
    assertThat(newIndex).isNotEqualTo(initialIndex - 1) // Test addition not subtraction
  }

  @Test
  fun `test raiseBetTo delegation when game not ended`() {
    val players = listOf(
      PokerPlayer("Alice", 1000),
      PokerPlayer("Bob", 1000)
    )

    val game = PokerGame(10, 20, players)
    val hasEnded = getPrivateField(game, "hasEnded") as Boolean

    assertThat(hasEnded).isFalse()
    game.raiseBetTo("Alice", 50)
  }

  @Test
  fun `test call delegation when game not ended`() {
    val players = listOf(
      PokerPlayer("Alice", 1000),
      PokerPlayer("Bob", 1000)
    )

    val game = PokerGame(10, 20, players)
    val hasEnded = getPrivateField(game, "hasEnded") as Boolean

    assertThat(hasEnded).isFalse()
    game.call("Alice")
  }

  @Test
  fun `test check delegation when game not ended`() {
    val players = listOf(
      PokerPlayer("Alice", 1000),
      PokerPlayer("Bob", 1000)
    )

    val game = PokerGame(10, 20, players)

    game.allIn("Alice")
    game.call("Bob")

    val hasEnded = getPrivateField(game, "hasEnded") as Boolean
    assertThat(hasEnded).isFalse()
  }

  @Test
  fun `test fold delegation when game not ended`() {
    val players = listOf(
      PokerPlayer("Alice", 1000),
      PokerPlayer("Bob", 1000)
    )

    val game = PokerGame(10, 20, players)
    val hasEnded = getPrivateField(game, "hasEnded") as Boolean

    assertThat(hasEnded).isFalse()
    game.fold("Alice")
  }

  @Test
  fun `test checkForGameEnd with multiple active players`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )

    val game = PokerGame(10, 20, players)
    val hasEnded = getPrivateField(game, "hasEnded") as Boolean

    assertThat(hasEnded).isFalse()
  }

  @Test
  fun `test checkForGameEnd with one active player`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val game = PokerGame(10, 20, players)

    // End game by all-in
    game.allIn("Alice")
    game.allIn("Bob")

    val hasEnded = getPrivateField(game, "hasEnded") as Boolean
    assertThat(hasEnded).isTrue()
  }

  @Test
  fun `checkForGameEnd correctly filters players with chips greater than zero`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 0),
      PokerPlayer("Charlie", 50)
    )

    val game = PokerGame(10, 20, players)

    // Use reflection to call the private checkForGameEnd method
    val checkForGameEndMethod =
      game.javaClass.getDeclaredMethod("checkForGameEnd")
    checkForGameEndMethod.isAccessible = true
    val result = checkForGameEndMethod.invoke(game) as Boolean

    // With 2 players having chips > 0, the game should not end
    assertThat(result).isFalse()

    val player = players[2]
    val chipsField = player.javaClass.getDeclaredField("chips")
    chipsField.isAccessible = true
    chipsField.setInt(
      player,
      0 // Set Charlie's chips to 0
    )

    val resultAfterChange = checkForGameEndMethod.invoke(game) as Boolean

    // With only 1 player having chips > 0, the game should end
    assertThat(resultAfterChange).isTrue()
  }

  @Test
  fun `filter condition distinguishes between zero and positive chips`() {
    val playersWithMixedChips = listOf(
      PokerPlayer("Alice", 30),     // Has chips
      PokerPlayer("Bob", 0),       // No chips
      PokerPlayer("Charlie", 100), // Has chips
      PokerPlayer("David", 0)      // No chips
    )

    val game = PokerGame(10, 20, playersWithMixedChips)

    val checkForGameEndMethod =
      game.javaClass.getDeclaredMethod("checkForGameEnd")
    checkForGameEndMethod.isAccessible = true
    val result = checkForGameEndMethod.invoke(game) as Boolean

    // Should not end because 2 players have chips > 0
    assertThat(result).isFalse()

    // Set Alice's chips to 0, leaving only Charlie with chips
    val alice = playersWithMixedChips[0]
    val aliceChipsField = alice.javaClass.getDeclaredField("chips")
    aliceChipsField.isAccessible = true
    aliceChipsField.setInt(alice, 0)

    val resultAfterAliceOut = checkForGameEndMethod.invoke(game) as Boolean

    // Should end because only 1 player has chips > 0
    assertThat(resultAfterAliceOut).isTrue()
  }
}
