package hwr.oop.projects.peakpoker

import hwr.oop.projects.peakpoker.core.game.Game
import hwr.oop.projects.peakpoker.core.player.Player
import hwr.oop.projects.peakpoker.core.player.PlayerStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class GameTest : AnnotationSpec() {

    lateinit var game: Game
    lateinit var alice: Player
    lateinit var bob: Player

    @Test
    fun `test add player to game` () {
        // given
        val testGame = Game(1001, 10, 20)
        val player1 = Player("Hans")

        // when
        testGame.addPlayer(player1)

        // then
        assertThat(testGame.playersOnTable).contains(player1)
    }
    // TODO: Check if the player name does not exist already

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
        assertThat(testGame.playersOnTable).doesNotContain(player1)
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
        assertThat(testGame.playersOnTable).doesNotContain(player2)
        assertThat(exception.message).isEqualTo("Player ${player2.name} does not exist.")
    }

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
    @BeforeEach
    fun setup() {
        // given
        game = Game(1)
        alice = Player(name = "Alice", 1000 )
        bob = Player(name = "Bob", 1000)
        game.playersOnTable.addAll(listOf(alice, bob))
    }

    @Test
    fun`player can fold`() {
        // when
        game.bet(alice, "fold")
        // then
        assertThat(alice.status).isEqualTo(PlayerStatus.FOLDED)
    }
}