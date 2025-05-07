package hwr.oop.projects.peakpoker

import hwr.oop.projects.peakpoker.core.game.Game
import hwr.oop.projects.peakpoker.core.player.Player
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class GameTest : AnnotationSpec() {
    @Test
    fun `test add player to game` () {
        // given
        val testGame = Game(1001, 10, 20)
        val player1 = Player("Hans")

        // when
        testGame.addPlayer(player1)

        // then
        assertThat(testGame.PlayersOnTable).contains(player1)
    }

    @Test
    fun `check if duplicate exception works` () {
        // given
        val testGame = Game(1002, 10, 20)
        val player1 = Player("Hans")
        val player2 = Player("Hans")

        // when
        testGame.addPlayer(player1)

        // then
        val exception = shouldThrow<IllegalArgumentException> {
            testGame.addPlayer(player2)
        }
        assertThat(exception.message).isEqualTo("Player with name ${player2.name} already exists.")
    }

    @Test
    fun `test remove player from game` () {
        // given
        val testGame = Game(1003, 10, 20)
        val player1 = Player("Hans")
        testGame.addPlayer(player1)

        // when
        testGame.removePlayer(player1)

        // then
        assertThat(testGame.PlayersOnTable).doesNotContain(player1)
    }

    @Test
    fun `test player not found exception` () {
        // given
        val testGame = Game(1004, 10, 20)
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        testGame.addPlayer(player1)

        // when
        val exception = shouldThrow<IllegalArgumentException> {
            testGame.removePlayer(player2)
        }

        // then
        assertThat(testGame.PlayersOnTable).doesNotContain(player2)
        assertThat(exception.message).isEqualTo("Player ${player2.name} does not exist.")
    }

    @Test
    fun `invalid blind amounts throw exceptions`() {
        // negative small blind
        shouldThrow<IllegalArgumentException> {
            Game(1010, -10, 20)
        }

        // negative big blind
        shouldThrow<IllegalArgumentException> {
            Game(1011, 10, -20)
        }

        // zero small blind
        shouldThrow<IllegalArgumentException> {
            Game(1012, 0, 20)
        }

        // zero big blind
        shouldThrow<IllegalArgumentException> {
            Game(1013, 10, 0)
        }

        // big blind smaller than small blind
        shouldThrow<IllegalArgumentException> {
            Game(1014, 30, 20)
        }
    }

//    @Test
//    fun `showGameInfo prints correct game information`() {
//        // given
//        val testGame = Game(1020, 10, 20)
//        val player1 = Player("Alice")
//        val player2 = Player("Bob")
//        testGame.addPlayer(player1)
//        testGame.addPlayer(player2)
//
//        // Redirect System.out to capture output
//        val originalOut = System.out
//        val outContent = ByteArrayOutputStream()
//        System.setOut(PrintStream(outContent))
//
//        try {
//            // when
//            testGame.showGameInfo()
//
//            // then
//            val output = outContent.toString()
//            assertThat(output).contains("Game ID: 1020")
//            assertThat(output).contains("Small Blind Amount: 10")
//            assertThat(output).contains("Big Blind Amount: 20")
//            assertThat(output).contains("Players on Table: Alice, Bob")
//            assertThat(output).contains("Pot: 0")
//            assertThat(output).contains("Community Cards: []")
//        } finally {
//            // Reset System.out
//            System.setOut(originalOut)
//        }
//    }

//    @Test
//    fun `check if highest bet is correct`() {
//        // given
//        val testGame = Game(1005, 10, 20)
//        val player1 = Player("Hans")
//        val player2 = Player("Peter")
//        testGame.addPlayer(player1)
//        testGame.addPlayer(player2)
//
//        // when
//        player1.raiseBet(10)
//        player2.raiseBet(20)
//
//        // then
//        assertThat(testGame.getHighestBet()).isEqualTo(20)
//    }
}