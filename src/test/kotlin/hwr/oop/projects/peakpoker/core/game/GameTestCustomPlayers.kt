package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class GameTestCustomPlayers : AnnotationSpec() {
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
  fun `check if game ends correctly`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val game = PokerGame(
      smallBlindAmount = 10,
      bigBlindAmount = 20,
      players = players
    )

    game.allIn("Alice")
    game.allIn("Bob")

    val winners = players.filter { it.chips() > 0 }
    val losers = players.filter { it.chips() == 0 }

    assertThat(winners).hasSize(1)
    assertThat(losers).hasSize(1)
    assertThat(winners[0].chips()).isEqualTo(200)

    val winnerName = winners[0].name

    // Assert that all game actions throw IllegalStateException after the game has ended
    assertThatThrownBy { game.allIn(winnerName) }
      .isInstanceOf(IllegalStateException::class.java)
      .hasMessageContaining("Game has ended")

    assertThatThrownBy { game.call(winnerName) }
      .isInstanceOf(IllegalStateException::class.java)
      .hasMessageContaining("Game has ended")

    assertThatThrownBy { game.check(winnerName) }
      .isInstanceOf(IllegalStateException::class.java)
      .hasMessageContaining("Game has ended")

    assertThatThrownBy { game.fold(winnerName) }
      .isInstanceOf(IllegalStateException::class.java)
      .hasMessageContaining("Game has ended")

    assertThatThrownBy { game.raiseBetTo(winnerName, 50) }
      .isInstanceOf(IllegalStateException::class.java)
      .hasMessageContaining("Game has ended")
  }
}