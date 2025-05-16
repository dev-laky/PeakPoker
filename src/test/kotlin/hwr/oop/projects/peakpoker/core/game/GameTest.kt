package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.Player
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

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
        assertThat(testGame.checkPlayerValidity(duplicatePlayer)).isFalse()
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
    fun `check if get current player works correctly`() {
        // given
        val testGame = Game(1006, 10, 20,
            listOf(Player("Hans"), Player("Peter"), Player("Max")))

        // when
        val currentPlayer = testGame.getCurrentPlayer()

        // then
        assertThat(currentPlayer.name).isEqualTo("Max")
    }

    @Test
    fun `big blind amount must be positive`() {
        val exception = shouldThrow<IllegalArgumentException> {
            Game(
                1020, 10, 0,
                listOf(Player("Hans"), Player("Peter"), Player("Max"))
            )
        }

        assertThat(exception.message).isEqualTo("Big blind amount must be positive")
    }

    @Test
    fun `big blind amount must be greater than or equal to small blind amount`() {
        val exception = shouldThrow<IllegalArgumentException> {
            Game(
                1021, 20, 10,
                listOf(Player("Hans"), Player("Peter"), Player("Max"))
            )
        }

        assertThat(exception.message).isEqualTo("Big blind amount must be greater than or equal to small blind amount")
    }

    @Test
    fun `makeTurn advances currentPlayerIndex to next player`() {
        val testGame = Game(
            1032, 10, 20,
            listOf(Player("Hans"), Player("Peter"), Player("Max"))
        )

        val initialPlayer = testGame.getCurrentPlayer()
        testGame.makeTurn()
        val nextPlayer = testGame.getCurrentPlayer()

        assertThat(nextPlayer.name).isNotEqualTo(initialPlayer.name)
        assertThat(testGame.currentPlayerIndex).isEqualTo(0)
    }

    @Test
    fun `checkPlayerValidity returns false for existing players and true for new players`() {
        val testGame = Game(
            1033, 10, 20,
            listOf(Player("Hans"), Player("Peter"), Player("Max"))
        )

        val existingPlayer = Player("Hans")
        val newPlayer = Player("Sara")

        assertThat(testGame.checkPlayerValidity(existingPlayer)).isFalse()
        assertThat(testGame.checkPlayerValidity(newPlayer)).isTrue()
    }

    @Test
    fun `getHighestBet is equal to big blind amount on init`() {
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(1041, 10, 20, listOf(player1, player2, player3))

        assertThat(testGame.getHighestBet()).isEqualTo(20)
    }

    @Test
    fun `big blind amount can be equal to small blind amount`() {
        val game = Game(
            1050, 20, 20,
            listOf(Player("Hans"), Player("Peter"), Player("Max"))
        )

        assertThat(game.smallBlindAmount).isEqualTo(20)
        assertThat(game.bigBlindAmount).isEqualTo(20)
    }

    @Test
    fun `getSmallBlindIndex returns correct value`() {
        val testGame = Game(
            1060, 10, 20,
            listOf(Player("Hans"), Player("Peter"), Player("Max"))
        )

        assertThat(testGame.smallBlindIndex).isEqualTo(0)
    }

    @Test
    fun `getSmallBlind returns correct small blind amount`() {
        val testGame = Game(
            1062, 15, 30,
            listOf(Player("Hans"), Player("Peter"), Player("Max"))
        )

        assertThat(testGame.getSmallBlind()).isEqualTo(15)
    }

    @Test
    fun `getBigBlind returns correct big blind amount`() {
        val testGame = Game(
            1063, 15, 30,
            listOf(Player("Hans"), Player("Peter"), Player("Max"))
        )

        assertThat(testGame.getBigBlind()).isEqualTo(30)
    }

    @Test
    fun `getId returns correct game identifier`() {
        val gameId = 1070
        val testGame = Game(
            gameId, 10, 20,
            listOf(Player("Hans"), Player("Peter"), Player("Max"))
        )

        assertThat(testGame.id).isEqualTo(gameId)
        assertThat(testGame.id).isNotEqualTo(0)
        assertThat(testGame.id).isGreaterThan(0)
    }

    @Test
    fun `smallBlindIndex is correctly initialized and maintained`() {
        val testGame = Game(
            1071, 10, 20,
            listOf(Player("Hans"), Player("Peter"), Player("Max"))
        )

        assertThat(testGame.smallBlindIndex).isEqualTo(0)
        assertThat(testGame.smallBlindIndex).isGreaterThanOrEqualTo(0)
        assertThat(testGame.smallBlindIndex).isLessThan(testGame.playersOnTable.size)

        val smallBlindPlayer = testGame.playersOnTable[testGame.smallBlindIndex]
        assertThat(smallBlindPlayer.name).isEqualTo("Hans")
    }

    @Test
    fun `smallBlindIndex boundary conditions`() {
        val players = listOf(Player("Hans"), Player("Peter"), Player("Max"))
        val testGame = Game(1072, 10, 20, players)

        assertThat(testGame.smallBlindIndex).isNotEqualTo(-1)
        assertThat(testGame.smallBlindIndex).isLessThan(players.size)
        assertThat(testGame.smallBlindIndex).isGreaterThanOrEqualTo(0)
    }

    @Test
    fun `makeTurn advances to next active player`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(1200, 10, 20, listOf(player1, player2, player3))

        // Initial state after game creation
        val initialPlayer = testGame.getCurrentPlayer()

        // when
        testGame.makeTurn()

        // then
        val nextPlayer = testGame.getCurrentPlayer()
        assertThat(nextPlayer).isEqualTo(player1)
        assertThat(nextPlayer).isNotEqualTo(initialPlayer)
    }

    @Test
    fun `makeTurn skips folded players`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(1201, 10, 20, listOf(player1, player2, player3))

        while (testGame.getCurrentPlayer() != player1) {
            testGame.makeTurn()
        }
        player2.fold()

        // when
        testGame.makeTurn()

        // then
        assertThat(testGame.getCurrentPlayer()).isEqualTo(player3)
    }

    @Test
    fun `makeTurn skips all-in players`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(1202, 10, 20, listOf(player1, player2, player3))

        while (testGame.getCurrentPlayer() != player1) {
            testGame.makeTurn()
        }
        player2.allIn()

        // when
        testGame.makeTurn()

        // then
        assertThat(testGame.getCurrentPlayer()).isEqualTo(player3)
    }

    @Test
    fun `makeTurn skips folded players with multiple players`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val player4 = Player("Anna")
        val testGame = Game(1203, 10, 20, listOf(player1, player2, player3, player4))

        player2.fold()

        while (testGame.getCurrentPlayer() != player1) {
            testGame.makeTurn()
        }

        // when
        testGame.makeTurn()

        // then
        assertThat(testGame.getCurrentPlayer()).isEqualTo(player3)
    }

    @Test
    fun `makeTurn skips all-in players with multiple players`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val player4 = Player("Anna")
        val testGame = Game(1204, 10, 20, listOf(player1, player2, player3, player4))

        player3.allIn()

        while (testGame.getCurrentPlayer() != player2) {
            testGame.makeTurn()
        }

        // when
        testGame.makeTurn()

        // then
        assertThat(testGame.getCurrentPlayer()).isEqualTo(player4)
    }

    @Test
    fun `makeTurn cycles to first player at the end of table`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(1204, 10, 20, listOf(player1, player2, player3))

        while (testGame.getCurrentPlayer() != player3) {
            testGame.makeTurn()
        }

        // when
        testGame.makeTurn()

        // then
        assertThat(testGame.getCurrentPlayer()).isEqualTo(player1)
    }

    @Test
    fun `makeTurn stops when returning to same player with all others inactive`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(1205, 10, 20, listOf(player1, player2, player3))

        while (testGame.getCurrentPlayer() != player1) {
            testGame.makeTurn()
        }
        player2.fold()
        player3.allIn()

        // when/then
        testGame.makeTurn()
        assertThat(testGame.getCurrentPlayer()).isEqualTo(player1)

        testGame.makeTurn()
        assertThat(testGame.getCurrentPlayer()).isEqualTo(player1)
    }
}

