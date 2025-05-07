package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.Player
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions

class GameTest : AnnotationSpec() {
    @Test
    fun `test if game throws exception if too little players are added` () {
        // when/then
        shouldThrow<IllegalArgumentException> {
            Game(
                1001, 10, 20,
                listOf(Player("Hans"), Player("Peter"))
            )
        }
    }

    @Test
    fun `check if duplicate exception works` () {
        // given
        val testGame = Game(1002, 10, 20,
            listOf(Player("Hans"), Player("Peter"), Player("Max")))
        val duplicatePlayer = Player("Hans")

        // when/then
        Assertions.assertThat(testGame.checkPlayerValidity(duplicatePlayer)).isFalse()
    }

    @Test
    fun `invalid blind amounts throw exceptions`() {
        // negative small blind
        shouldThrow<IllegalArgumentException> {
            Game(
                1010, -10, 20,
                listOf(Player("Hans"), Player("Peter"), Player("Max"))
            )
        }

        // negative big blind
        shouldThrow<IllegalArgumentException> {
            Game(
                1011, 10, -20,
                listOf(Player("Hans"), Player("Peter"), Player("Max"))
            )
        }

        // zero small blind
        shouldThrow<IllegalArgumentException> {
            Game(
                1012, 0, 20,
                listOf(Player("Hans"), Player("Peter"), Player("Max"))
            )
        }

        // zero big blind
        shouldThrow<IllegalArgumentException> {
            Game(
                1013, 10, 0,
                listOf(Player("Hans"), Player("Peter"), Player("Max"))
            )
        }

        // big blind smaller than small blind
        shouldThrow<IllegalArgumentException> {
            Game(
                1014, 30, 20,
                listOf(Player("Hans"), Player("Peter"), Player("Max"))
            )
        }
    }

    @Test
    fun `check if highest bet is correct`() {
        // given
        val testGame = Game(1005, 10, 20,
            listOf(Player("Hans"), Player("Peter"), Player("Max")))

        // when
        testGame.playersOnTable[testGame.currentPlayerIndex].raiseBet(10)
        testGame.makeTurn()
        testGame.playersOnTable[testGame.currentPlayerIndex].raiseBet(20)

        // then
        Assertions.assertThat(testGame.getHighestBet()).isEqualTo(20)
    }

    @Test
    fun `check if get current player works correctly`() {
        // given
        val testGame = Game(1006, 10, 20,
            listOf(Player("Hans"), Player("Peter"), Player("Max")))

        // when
        val currentPlayer = testGame.getCurrentPlayer()

        // then
        Assertions.assertThat(currentPlayer.name).isEqualTo("Max")
    }
}
