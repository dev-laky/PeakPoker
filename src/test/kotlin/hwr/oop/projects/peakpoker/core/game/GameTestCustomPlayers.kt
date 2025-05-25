package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.Player
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

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
    shouldThrow<IllegalArgumentException> {
      Game(
        10, 20,
        listOf(Player("Hans"), Player("Hans"))
      )
    }
  }

  @Test
  fun `negative small blind amount throws exceptions`() {
    // negative small blind
    shouldThrow<IllegalArgumentException> {
      Game(
        -10, 20,
        listOf(Player("Hans"), Player("Peter"), Player("Max"))
      )
    }
  }

  @Test
  fun `negative big blind amount throws exceptions`() {
    // negative big blind
    shouldThrow<IllegalArgumentException> {
      Game(
        10, -20,
        listOf(Player("Hans"), Player("Peter"), Player("Max"))
      )
    }
  }

  @Test
  fun `zero small blind amount throws exception`() {
    shouldThrow<IllegalArgumentException> {
      Game(
        0, 20,
        listOf(Player("Hans"), Player("Peter"), Player("Max"))
      )
    }
  }

  @Test
  fun `zero big blind amount throws exception`() {
    shouldThrow<IllegalArgumentException> {
      Game(
        10, 0,
        listOf(Player("Hans"), Player("Peter"), Player("Max"))
      )
    }
  }

  @Test
  fun `big blind smaller than small blind throws exception`() {
    shouldThrow<IllegalArgumentException> {
      Game(
        30, 20,
        listOf(Player("Hans"), Player("Peter"), Player("Max"))
      )
    }
  }

  @Test
  fun `big blind amount must be positive`() {
    val exception = shouldThrow<IllegalArgumentException> {
      Game(
        10, 0,
        listOf(Player("Hans"), Player("Peter"), Player("Max"))
      )
    }

    assertThat(exception.message).isEqualTo("Big blind amount must be positive")
  }

  @Test
  fun `big blind amount must be greater than or equal to small blind amount`() {
    val exception = shouldThrow<IllegalArgumentException> {
      Game(
        20, 10,
        listOf(Player("Hans"), Player("Peter"), Player("Max"))
      )
    }

    assertThat(exception.message).isEqualTo("Big blind amount must be exactly double the small blind amount")
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
}