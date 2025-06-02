package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.exceptions.DuplicatePlayerException
import hwr.oop.projects.peakpoker.core.player.Player
import hwr.oop.projects.peakpoker.core.exceptions.InvalidBlindConfigurationException
import hwr.oop.projects.peakpoker.core.exceptions.MinimumPlayersException
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class GameTestCustomPlayers : AnnotationSpec() {
  @Test
  fun `check if player validity function returns correct boolean`() {
    // given
    val testGame = Game(
      10, 20,
      listOf(Player("Hans"), Player("Peter"))
    )
    val duplicatePlayer = Player("Hans")

    // when/then
    assertThat(testGame.checkPlayerValidity(duplicatePlayer)).isFalse()
  }

  @Test
  fun `check if duplicate exception works`() {
    assertThatThrownBy {
      Game(
        10, 20,
        listOf(Player("Hans"), Player("Hans"))
      )
    }
      .isExactlyInstanceOf(DuplicatePlayerException::class.java)
      .hasMessageContaining("All players must be unique")
  }

  @Test
  fun `negative small blind amount throws exceptions`() {
    assertThatThrownBy {
      Game(
        -10, 20,
        listOf(Player("Hans"), Player("Peter"), Player("Max"))
      )
    }
      .isExactlyInstanceOf(InvalidBlindConfigurationException::class.java)
      .hasMessageContaining("Small blind amount must be positive")
  }

  @Test
  fun `negative big blind amount throws exceptions`() {
    // negative big blind
    assertThatThrownBy {
      Game(
        10, -20,
        listOf(Player("Hans"), Player("Peter"), Player("Max"))
      )
    }
      .isExactlyInstanceOf(InvalidBlindConfigurationException::class.java)
      .hasMessageContaining("Big blind amount must be positive")
  }

  @Test
  fun `zero small blind amount throws exception`() {
    assertThatThrownBy {
      Game(
        0, 20,
        listOf(Player("Hans"), Player("Peter"), Player("Max"))
      )
    }
      .isExactlyInstanceOf(InvalidBlindConfigurationException::class.java)
      .hasMessageContaining("Small blind amount must be positive")
  }

  @Test
  fun `zero big blind amount throws exception`() {
    assertThatThrownBy {
      Game(
        10, 0,
        listOf(Player("Hans"), Player("Peter"), Player("Max"))
      )
    }
      .isExactlyInstanceOf(InvalidBlindConfigurationException::class.java)
      .hasMessageContaining("Big blind amount must be positive")
  }

  @Test
  fun `big blind smaller than small blind throws exception`() {
    assertThatThrownBy {
      Game(
        30, 20,
        listOf(Player("Hans"), Player("Peter"), Player("Max"))
      )
    }
      .isExactlyInstanceOf(InvalidBlindConfigurationException::class.java)
      .hasMessageContaining("Big blind amount must be exactly double")
  }

  @Test
  fun `big blind amount must be positive`() {
    assertThatThrownBy {
      Game(
        10, 0,
        listOf(Player("Hans"), Player("Peter"), Player("Max"))
      )
    }
      .isExactlyInstanceOf(InvalidBlindConfigurationException::class.java)
      .hasMessageContaining("Big blind amount must be positive")
  }

  @Test
  fun `big blind amount must be greater than or equal to small blind amount`() {
    assertThatThrownBy {
      Game(
        20, 10,
        listOf(Player("Hans"), Player("Peter"), Player("Max"))
      )
    }
      .isExactlyInstanceOf(InvalidBlindConfigurationException::class.java)
      .hasMessageContaining("Big blind amount must be exactly double")
  }

  @Test
  fun `makeTurn skips all-in players with multiple players`() {
    val player1 = Player("Hans")
    val player2 = Player("Peter")
    val player3 = Player("Max")
    val player4 = Player("Anna")
    val testGame =
      Game(
        10, 20,
        listOf(player1, player2, player3, player4)
      )

    testGame.allIn(player3)
    testGame.allIn(player4)

    testGame.call(player1)
    testGame.call(player2)

    assertThat(testGame.getCurrentPlayer()).isEqualTo(player1)
  }

  @Test
  fun `makeTurn skips folded players with multiple players`() {
    val player1 = Player("Hans")
    val player2 = Player("Peter")
    val player3 = Player("Max")
    val player4 = Player("Anna")
    val testGame =
      Game(
        10, 20,
        listOf(player1, player2, player3, player4)
      )

    testGame.fold(player3)
    testGame.fold(player4)

    testGame.call(player1)
    testGame.check(player2)

    assertThat(testGame.getCurrentPlayer()).isEqualTo(player1)
  }

  @Test
  fun `getId returns correct game identifier`() {
    val testGameId = GameId("testGame100")
    val testGame = Game(
      10, 20,
      listOf(Player("Hans"), Player("Peter"), Player("Max")),
      testGameId
    )

    assertThat(testGame.id).isEqualTo(testGameId)
  }

  @Test
  fun `game creation with empty player list throws exception`() {
    assertThatThrownBy {
      Game(
        10, 20,
        emptyList()
      )
    }
      .isExactlyInstanceOf(MinimumPlayersException::class.java)
      .hasMessageContaining("Minimum number of players is 2")
  }

  @Test
  fun `game creation with one player list throws exception`() {
    assertThatThrownBy {
      Game(
        10, 20,
        listOf(Player("Hans"))
      )
    }
      .isExactlyInstanceOf(MinimumPlayersException::class.java)
      .hasMessageContaining("Minimum number of players is 2")
  }

  @Test
  fun `getHighestBet updates when player goes all-in with higher amount`() {
    // given
    val player1 = Player("Hans", 100)
    val player2 = Player("Peter", 200)
    val richPlayer = Player("Rich", 500)
    val customGame = Game(10, 20, listOf(player1, player2, richPlayer))

    // when
    customGame.allIn(richPlayer)

    // then
    assertThat(customGame.getHighestBet()).isEqualTo(500)
    assertThat(richPlayer.isAllIn).isTrue()
  }

  @Test
  fun `pot includes bets from all-in players`() {
    val player1 = Player("Hans", 100)
    val player2 = Player("Peter", 200)
    val smallStackPlayer = Player("Poor", 40)
    val customGame = Game(10, 20, listOf(player1, player2, smallStackPlayer))

    customGame.allIn(smallStackPlayer)

    assertThat(customGame.getPot()).isEqualTo(70) // 10 + 20 + 40
    assertThat(smallStackPlayer.isAllIn).isTrue()
  }

  @Test
  fun `pot is calculated correctly with multiple different bet amounts`() {
    // given
    val richPlayer = Player("Rich", 200)
    val poorPlayer = Player("Poor", 50)
    val mediumPlayer = Player("Medium", 100)
    val customGame = Game(10, 20, listOf(richPlayer, poorPlayer, mediumPlayer))

    // when
    customGame.call(mediumPlayer) // Calls to 20
    customGame.raiseBetTo(richPlayer, 40) // Raises to 40
    customGame.allIn(poorPlayer) // All-in with 50

    // then
    assertThat(customGame.getPot()).isEqualTo(110) // 40 + 50 + 20
    assertThat(customGame.getPot()).isEqualTo(richPlayer.getBet() + poorPlayer.getBet() + mediumPlayer.getBet())
  }
}