package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.exceptions.DuplicatePlayerException
import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import hwr.oop.projects.peakpoker.core.exceptions.InvalidBlindConfigurationException
import hwr.oop.projects.peakpoker.core.exceptions.MinimumPlayersException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidPlayerStateException
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy

class GameTestCustomPlayers : AnnotationSpec() {
  @Test
  fun `check if player validity function returns correct boolean`() {
    // given
    val testPokerGame = PokerGame(
      10, 20,
      listOf(PokerPlayer("Hans"), PokerPlayer("Peter"))
    )
    val duplicatePokerPlayer = PokerPlayer("Hans")

    // when/then
    assertThat(testPokerGame.checkPlayerValidity(duplicatePokerPlayer)).isFalse()
  }

  @Test
  fun `check if duplicate exception works`() {
    assertThatThrownBy {
      PokerGame(
        10, 20,
        listOf(PokerPlayer("Hans"), PokerPlayer("Hans"))
      )
    }
      .isExactlyInstanceOf(DuplicatePlayerException::class.java)
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
      .isExactlyInstanceOf(InvalidBlindConfigurationException::class.java)
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
      .isExactlyInstanceOf(InvalidBlindConfigurationException::class.java)
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
      .isExactlyInstanceOf(InvalidBlindConfigurationException::class.java)
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
      .isExactlyInstanceOf(InvalidBlindConfigurationException::class.java)
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
      .isExactlyInstanceOf(InvalidBlindConfigurationException::class.java)
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
      .isExactlyInstanceOf(InvalidBlindConfigurationException::class.java)
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
      .isExactlyInstanceOf(InvalidBlindConfigurationException::class.java)
      .hasMessageContaining("Big blind amount must be exactly double")
  }

  @Test
  fun `makeTurn skips all-in players with multiple players`() {
    val pokerPlayer1 = PokerPlayer("Hans")
    val pokerPlayer2 = PokerPlayer("Peter")
    val pokerPlayer3 = PokerPlayer("Max")
    val pokerPlayer4 = PokerPlayer("Anna")
    val testPokerGame =
      PokerGame(
        10, 20,
        listOf(pokerPlayer1, pokerPlayer2, pokerPlayer3, pokerPlayer4)
      )

    testPokerGame.allIn(pokerPlayer3)
    testPokerGame.allIn(pokerPlayer4)

    testPokerGame.call(pokerPlayer1)
    testPokerGame.call(pokerPlayer2)

    assertThat(testPokerGame.getCurrentPlayer()).isEqualTo(pokerPlayer1)
  }

  @Test
  fun `makeTurn skips folded players with multiple players`() {
    val pokerPlayer1 = PokerPlayer("Hans")
    val pokerPlayer2 = PokerPlayer("Peter")
    val pokerPlayer3 = PokerPlayer("Max")
    val pokerPlayer4 = PokerPlayer("Anna")
    val testPokerGame =
      PokerGame(
        10, 20,
        listOf(pokerPlayer1, pokerPlayer2, pokerPlayer3, pokerPlayer4)
      )

    testPokerGame.fold(pokerPlayer3)
    testPokerGame.fold(pokerPlayer4)

    testPokerGame.call(pokerPlayer1)
    testPokerGame.check(pokerPlayer2)

    assertThat(testPokerGame.getCurrentPlayer()).isEqualTo(pokerPlayer1)
  }

  @Test
  fun `getId returns correct game identifier`() {
    val testGameId = GameId("testGame100")
    val testPokerGame = PokerGame(
      10, 20,
      listOf(PokerPlayer("Hans"), PokerPlayer("Peter"), PokerPlayer("Max")),
      testGameId
    )

    assertThat(testPokerGame.id).isEqualTo(testGameId)
  }

  @Test
  fun `game creation with empty player list throws exception`() {
    assertThatThrownBy {
      PokerGame(
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
      PokerGame(
        10, 20,
        listOf(PokerPlayer("Hans"))
      )
    }
      .isExactlyInstanceOf(MinimumPlayersException::class.java)
      .hasMessageContaining("Minimum number of players is 2")
  }

  @Test
  fun `getHighestBet updates when player goes all-in with higher amount`() {
    // given
    val pokerPlayer1 = PokerPlayer("Hans", 100)
    val pokerPlayer2 = PokerPlayer("Peter", 200)
    val richPokerPlayer = PokerPlayer("Rich", 500)
    val customPokerGame = PokerGame(10, 20, listOf(pokerPlayer1, pokerPlayer2, richPokerPlayer))

    // when
    customPokerGame.allIn(richPokerPlayer)

    // then
    assertThat(customPokerGame.getHighestBet()).isEqualTo(500)
    assertThat(richPokerPlayer.isAllIn).isTrue()
  }

  @Test
  fun `pot includes bets from all-in players`() {
    val pokerPlayer1 = PokerPlayer("Hans", 100)
    val pokerPlayer2 = PokerPlayer("Peter", 200)
    val smallStackPokerPlayer = PokerPlayer("Poor", 40)
    val customPokerGame = PokerGame(10, 20, listOf(pokerPlayer1, pokerPlayer2, smallStackPokerPlayer))

    customPokerGame.allIn(smallStackPokerPlayer)

    assertThat(customPokerGame.calculatePot()).isEqualTo(70) // 10 + 20 + 40
    assertThat(smallStackPokerPlayer.isAllIn).isTrue()
  }

  @Test
  fun `pot is calculated correctly with multiple different bet amounts`() {
    // given
    val richPokerPlayer = PokerPlayer("Rich", 200)
    val poorPokerPlayer = PokerPlayer("Poor", 50)
    val mediumPokerPlayer = PokerPlayer("Medium", 100)
    val customPokerGame = PokerGame(10, 20, listOf(richPokerPlayer, poorPokerPlayer, mediumPokerPlayer))

    // when
    customPokerGame.call(mediumPokerPlayer) // Calls to 20
    customPokerGame.raiseBetTo(richPokerPlayer, 40) // Raises to 40
    customPokerGame.allIn(poorPokerPlayer) // All-in with 50

    // then
    assertThat(customPokerGame.calculatePot()).isEqualTo(110) // 40 + 50 + 20
    assertThat(customPokerGame.calculatePot()).isEqualTo(richPokerPlayer.getBet() + poorPokerPlayer.getBet() + mediumPokerPlayer.getBet())
  }

  @Test
  fun `raiseBetTo rejects player not in game`() {
      // given
      val player1 = PokerPlayer("Alice", 100)
      val player2 = PokerPlayer("Bob", 100)
      val outsidePlayer = PokerPlayer("Charlie", 100)
      val game = PokerGame(10, 20, listOf(player1, player2))

      // when/then
      assertThatThrownBy {
          game.raiseBetTo(outsidePlayer, 30)
      }
          .isExactlyInstanceOf(InvalidPlayerStateException::class.java)
          .hasMessageContaining("Player is not part of this game")
  }

  @Test
  fun `player with same name but different instance is rejected`() {
      // given
      val player1 = PokerPlayer("Alice", 100)
      val player2 = PokerPlayer("Bob", 100)
      val sameNameDifferentInstance = PokerPlayer("Alice", 100)
      val game = PokerGame(10, 20, listOf(player1, player2))

      // when/then
      assertThatThrownBy {
          game.raiseBetTo(sameNameDifferentInstance, 30)
      }
          .isExactlyInstanceOf(InvalidPlayerStateException::class.java)
          .hasMessageContaining("Player is not part of this game")
  }

  @Test
  fun `player validation happens before other validations`() {
      // given
      val player1 = PokerPlayer("Alice", 100)
      val player2 = PokerPlayer("Bob", 100)
      val outsidePlayer = PokerPlayer("Charlie", 100)
      val game = PokerGame(10, 20, listOf(player1, player2))

      // Pass an invalid bet amount which would normally trigger different exception
      // But player validation should happen first
      assertThatThrownBy {
          game.raiseBetTo(outsidePlayer, -30)
      }
          .isExactlyInstanceOf(InvalidPlayerStateException::class.java)
          .hasMessageContaining("Player is not part of this game")
  }

  @Test
  fun `valid player in game can raise bet`() {
      // given
      val player1 = PokerPlayer("Alice", 100)
      val player2 = PokerPlayer("Bob", 100)
      val game = PokerGame(10, 20, listOf(player1, player2))

      // Player1 should be the current player after initialization

      // when/then - no exception should be thrown
      assertThatCode {
          game.raiseBetTo(player1, 40)
      }.doesNotThrowAnyException()
  }
}