package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class GameTestCustomPlayers : AnnotationSpec() {
    /*@Test
    fun `check if player validity function returns correct boolean`() {
      // given
      val testGame = PokerGame(
        10, 20,
        listOf(PokerPlayer("Hans"), PokerPlayer("Peter"))
      )
      val duplicatePlayer = PokerPlayer("Hans")

      // when/then
      assertThat(testGame.checkPlayerValidity(duplicatePlayer)).isFalse()
    }*/

    @Test
    fun `check if duplicate exception works`() {
        shouldThrow<IllegalArgumentException> {
            PokerGame(
                10, 20,
                listOf(PokerPlayer("Hans"), PokerPlayer("Hans"))
            )
        }
    }

    @Test
    fun `negative small blind amount throws exceptions`() {
        // negative small blind
        shouldThrow<IllegalArgumentException> {
            PokerGame(
                -10, 20,
                listOf(PokerPlayer("Hans"), PokerPlayer("Peter"), PokerPlayer("Max"))
            )
        }
    }

    @Test
    fun `negative big blind amount throws exceptions`() {
        // negative big blind
        shouldThrow<IllegalArgumentException> {
            PokerGame(
                10, -20,
                listOf(PokerPlayer("Hans"), PokerPlayer("Peter"), PokerPlayer("Max"))
            )
        }
    }

    @Test
    fun `zero small blind amount throws exception`() {
        shouldThrow<IllegalArgumentException> {
            PokerGame(
                0, 20,
                listOf(PokerPlayer("Hans"), PokerPlayer("Peter"), PokerPlayer("Max"))
            )
        }
    }

    @Test
    fun `zero big blind amount throws exception`() {
        shouldThrow<IllegalArgumentException> {
            PokerGame(
                10, 0,
                listOf(PokerPlayer("Hans"), PokerPlayer("Peter"), PokerPlayer("Max"))
            )
        }
    }

    @Test
    fun `big blind smaller than small blind throws exception`() {
        shouldThrow<IllegalArgumentException> {
            PokerGame(
                30, 20,
                listOf(PokerPlayer("Hans"), PokerPlayer("Peter"), PokerPlayer("Max"))
            )
        }
    }

    @Test
    fun `big blind amount must be positive`() {
        val exception = shouldThrow<IllegalArgumentException> {
            PokerGame(
                10, 0,
                listOf(PokerPlayer("Hans"), PokerPlayer("Peter"), PokerPlayer("Max"))
            )
        }

        assertThat(exception.message).isEqualTo("Big blind amount must be positive")
    }

    @Test
    fun `big blind amount must be greater than or equal to small blind amount`() {
        val exception = shouldThrow<IllegalArgumentException> {
            PokerGame(
                20, 10,
                listOf(PokerPlayer("Hans"), PokerPlayer("Peter"), PokerPlayer("Max"))
            )
        }

        assertThat(exception.message).isEqualTo("Big blind amount must be exactly double the small blind amount")
    }

    /*@Test
    fun `makeTurn skips all-in players with multiple players`() {
      val player1 = PokerPlayer("Hans")
      val player2 = PokerPlayer("Peter")
      val player3 = PokerPlayer("Max")
      val player4 = PokerPlayer("Anna")
      val testGame =
        PokerGame(
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
      val player1 = PokerPlayer("Hans")
      val player2 = PokerPlayer("Peter")
      val player3 = PokerPlayer("Max")
      val player4 = PokerPlayer("Anna")
      val testGame =
        PokerGame(
          10, 20,
          listOf(player1, player2, player3, player4)
        )

      testGame.fold(player3)
      testGame.fold(player4)

      testGame.call(player1)
      testGame.check(player2)

      assertThat(testGame.getCurrentPlayer()).isEqualTo(player1)
    }*/

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
}