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
            val player1 = Player("Hans")
            val player2 = Player("Peter")
            Game(
                1010, 10, 20,
                listOf(player1, player2)
            )
        }
    }

    @Test
    fun `check if function for player validity works` () {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(
            1011, 10, 20,
            listOf(player1, player2, player3)
        )
        val duplicatePlayer = Player("Hans")

        // when/then
        assertThat(testGame.checkPlayerValidity(duplicatePlayer)).isFalse()
    }

    @Test
    fun `negative small blind throws exception`() {
        // when/then
        shouldThrow<IllegalArgumentException> {
            val player1 = Player("Hans")
            val player2 = Player("Peter")
            val player3 = Player("Max")
            Game(
                1012, -10, 20,
                listOf(player1, player2, player3)
            )
        }
    }

    @Test
    fun `negative big blind throws exception`() {
        // when/then
        val exception = shouldThrow<IllegalArgumentException> {
            val player1 = Player("Hans")
            val player2 = Player("Peter")
            val player3 = Player("Max")
            Game(
                1013, 10, -20,
                listOf(player1, player2, player3)
            )
        }
        assertThat(exception.message).isEqualTo("Big blind amount must be positive")
    }

    @Test
    fun `zero small blind throws exception`() {
        // when/then
        shouldThrow<IllegalArgumentException> {
            Game(
                1014, 0, 20,
                listOf(Player("Hans"), Player("Peter"), Player("Max"))
            )
        }
    }

    @Test
    fun `zero big blind throws exception`() {
        // when/then
        shouldThrow<IllegalArgumentException> {
            val player1 = Player("Hans")
            val player2 = Player("Peter")
            val player3 = Player("Max")
            Game(
                1015, 10, 0,
                listOf(player1, player2, player3)
            )
        }
    }

    @Test
    fun `big blind smaller than small blind throws exception`() {
        // when/then
        shouldThrow<IllegalArgumentException> {
            val player1 = Player("Hans")
            val player2 = Player("Peter")
            val player3 = Player("Max")
            Game(
                1016, 30, 20,
                listOf(player1, player2, player3)
            )
        }
    }

    @Test
    fun `check if get current player works correctly`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(
            1017, 10, 20,
            listOf(player1, player2, player3)
        )

        // when
        val currentPlayer = testGame.getCurrentPlayer()

        // then
        assertThat(currentPlayer.name).isEqualTo("Max")
    }

    @Test
    fun `big blind amount must be greater than or equal to small blind amount`() {
        val exception = shouldThrow<IllegalArgumentException> {
            Game(
                1018, 20, 10,
                listOf(Player("Hans"), Player("Peter"), Player("Max"))
            )
        }
        assertThat(exception.message).isEqualTo("Big blind amount must be greater than or equal to small blind amount")
    }

    @Test
    fun `call advances currentPlayerIndex to next active player`() {
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(
            1019, 10, 20,
            listOf(player1, player2, player3)
        )

        val initialPlayer = testGame.getCurrentPlayer()
        testGame.call(initialPlayer)
        val nextPlayer = testGame.getCurrentPlayer()

        assertThat(nextPlayer.name).isNotEqualTo(initialPlayer.name)
        assertThat(testGame.currentPlayerIndex).isEqualTo(0)
    }

    @Test
    fun `checkPlayerValidity returns false for existing players and true for new players`() {
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(
            1020, 10, 20,
            listOf(player1, player2, player3)
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
        val testGame = Game(
            1021, 10, 20,
            listOf(player1, player2, player3)
        )

        assertThat(testGame.getHighestBet()).isEqualTo(20)
    }

    @Test
    fun `big blind amount can be equal to small blind amount`() {
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(
            1022, 20, 20,
            listOf(player1, player2, player3)
        )

        assertThat(testGame.getSmallBlind()).isEqualTo(20)
        assertThat(testGame.getBigBlind()).isEqualTo(20)
    }

    @Test
    fun `getSmallBlindIndex returns correct value`() {
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(
            1023, 10, 20,
            listOf(player1, player2, player3)
        )

        assertThat(testGame.getSmallBlindIndex()).isEqualTo(1)
    }

    @Test
    fun `getSmallBlind returns correct small blind amount`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(
            1024, 15, 20,
            listOf(player1, player2, player3)
        )

        assertThat(testGame.getSmallBlind()).isEqualTo(15)
    }

    @Test
    fun `getBigBlind returns correct big blind amount`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(
            1025, 10, 20,
            listOf(player1, player2, player3)
        )

        assertThat(testGame.getBigBlind()).isEqualTo(20)
    }

    @Test
    fun `getId returns correct game identifier`() {
        // given
        val gameId = 1026
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(
            gameId, 10, 20,
            listOf(player1, player2, player3)
        )

        assertThat(testGame.id).isEqualTo(gameId)
        assertThat(testGame.id).isNotEqualTo(0)
        assertThat(testGame.id).isGreaterThan(0)
    }

    @Test
    fun `calculatePot returns correct pot amount`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(
            1028, 10, 20,
            listOf(player1, player2, player3)
        )

        testGame.call(testGame.getCurrentPlayer())
        testGame.call(testGame.getCurrentPlayer())
        testGame.raiseBetTo(testGame.getCurrentPlayer(), 100)
        testGame.call(testGame.getCurrentPlayer())
        testGame.call(testGame.getCurrentPlayer())

        assertThat(testGame.calculatePot()).isEqualTo(300)
    }

    @Test
    fun `calculatePot returns correct pot amount after new bets`() {
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(
            1029, 10, 20,
            listOf(player1, player2, player3)
        )

        testGame.raiseBetTo(player3, 30)
        testGame.raiseBetTo(player1, 40)
        testGame.call(player2)
        testGame.call(player3)

        assertThat(testGame.calculatePot()).isEqualTo(120)

        testGame.raiseBetTo(player1, 50)
        testGame.call(player2)
        testGame.call(player3)

        assertThat(testGame.calculatePot()).isEqualTo(150)
    }

    @Test
    fun `pot contains Blinds at game start`() {
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(
            1030, 10, 20,
            listOf(player1, player2, player3)
        )

        assertThat(testGame.pot).isEqualTo(30)
    }

    @Test
    fun `makeTurn cycles back to the first player after the last player`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(
            1031, 10, 20,
            listOf(player1, player2, player3)
        )

        // when
        testGame.call(player3)

        // then
        assertThat(testGame.currentPlayerIndex).isEqualTo(0)
    }

    @Test
    fun `getHighestBet updates after player bets more`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(
            1032, 10, 20,
            listOf(player1, player2, player3)
        )

        // when
        testGame.raiseBetTo(player3, 50)
        testGame.call(player1)
        testGame.call(player2)
        testGame.check(player3)

        // then
        assertThat(testGame.getHighestBet()).isEqualTo(50)
    }

    @Test
    fun `makeTurn skips folded players`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(
            1033, 10, 20,
            listOf(player1, player2, player3)
        )

        // when
        testGame.fold(player3)
        testGame.call(player1)
        testGame.check(player2)

        // then
        assertThat(testGame.getCurrentPlayer()).isEqualTo(player1)
    }

    @Test
    fun `game turn logic skips all-in players`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(
            1034, 10, 20,
            listOf(player1, player2, player3)
        )

        // when
        testGame.allIn(player3)
        testGame.call(player1)
        testGame.call(player2)

        // then
        assertThat(testGame.getCurrentPlayer()).isEqualTo(player1)
    }

    @Test
    fun `game turn logic skips folded players multiple times`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val player4 = Player("Sonja")
        val testGame = Game(
            1035, 10, 20,
            listOf(player1, player2, player3, player4)
        )

        // when
        testGame.fold(player3)
        testGame.fold(player4)
        testGame.call(player1)
        testGame.check(player2)

        // then
        assertThat(testGame.getCurrentPlayer()).isEqualTo(player1)
    }

    @Test
    fun `makeTurn skips all-in players with multiple players`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val player4 = Player("Sonja")
        val testGame = Game(
            1036, 10, 20,
            listOf(player1, player2, player3, player4)
        )

        // when
        testGame.allIn(player3)
        testGame.allIn(player4)
        testGame.call(player1)
        testGame.call(player2)

        // then
        assertThat(testGame.getCurrentPlayer()).isEqualTo(player1)
    }

    @Test
    fun `makeTurn cycles to first player at the end of table`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(
            1037, 10, 20,
            listOf(player1, player2, player3)
        )

        // when
        testGame.call(testGame.getCurrentPlayer())

        // then
        assertThat(testGame.getCurrentPlayer()).isEqualTo(player1)
    }
}

