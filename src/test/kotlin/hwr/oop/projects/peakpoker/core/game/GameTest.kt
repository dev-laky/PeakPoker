package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.Player
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class GameTest : AnnotationSpec() {
    @Test
    fun `test if game throws exception if too little players are added`() {
        // when/then
        shouldThrow<IllegalArgumentException> {
            Game(
                1001, 10, 20,
                listOf(Player("Hans"), Player("Peter"))
            )
        }
    }

    @Test
    fun `check if duplicate exception works`() {
        // given
        val testGame = Game(
            1002, 10, 20,
            listOf(Player("Hans"), Player("Peter"), Player("Max"))
        )
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
        val testGame = Game(
            1006, 10, 20,
            listOf(Player("Hans"), Player("Peter"), Player("Max"))
        )

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
    fun `calculatePot returns correct pot amount`() {
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(1090, 10, 20, listOf(player1, player2, player3))

        player1.setBetAmount(30)
        player2.setBetAmount(50)
        player3.setBetAmount(20)

        assertThat(testGame.calculatePot()).isEqualTo(100)
    }

    @Test
    fun `calculatePot returns correct pot amount after new bets`() {
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(1093, 10, 20, listOf(player1, player2, player3))

        player1.setBetAmount(30)
        player2.setBetAmount(50)
        player3.setBetAmount(20)

        assertThat(testGame.calculatePot()).isEqualTo(100)

        // setBetAmount overwrites previous bet
        player1.setBetAmount(40) // 30 -> 40 (+10)
        player2.setBetAmount(60) // 50 -> 60 (+10)
        player3.setBetAmount(30) // 20 -> 30 (+10)

        assertThat(testGame.calculatePot()).isEqualTo(130)
    }

    @Test
    fun `pot contains Blinds at game start`() {
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(1091, 10, 20, listOf(player1, player2, player3))

        assertThat(testGame.pot).isEqualTo(30)
    }

    @Test
    fun `makeTurn cycles back to the first player after the last player`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(1095, 10, 20, listOf(player1, player2, player3))

        println("Start index: ${testGame.currentPlayerIndex}")
        // start Index returns 2, because of the blinds

        // when
        testGame.makeTurn()
        testGame.makeTurn()
        testGame.makeTurn()

        // then
        assertThat(testGame.currentPlayerIndex).isEqualTo(2)
    }

    @Test
    fun `getHighestBet updates after player bets more`() {
        // given
        val player1 = Player("Hans")
        val player2 = Player("Peter")
        val player3 = Player("Max")
        val testGame = Game(1094, 10, 20, listOf(player1, player2, player3))

        // when
        player1.setBetAmount(30)
        player2.setBetAmount(50)
        player3.setBetAmount(40)

        // then
        assertThat(testGame.getHighestBet()).isEqualTo(50)
    }

    @Test
    fun `call throws exception if player cannot match highest bet`() {
        val p1 = Player("A", 500)
        val p2 = Player("B", 20)
        val p3 = Player("C", 500)

        val game = Game(1, 10, 20, listOf(p1, p2, p3))

        // simulate that p1 already raised
        p1.setBetAmount(100)
        p2.setBetAmount(20)


        // force turn to p2
        while (game.getCurrentPlayer() != p2) {
            game.makeTurn()
        }

        val exception = shouldThrow<IllegalStateException> {
            game.call(p2)
        }

        assertThat(exception.message).isEqualTo("You do not have enough chips to call.")
    }

    @Test
    fun `smallBlindIndex is initialized to 0`() {
        val testGame = Game(
            1100, 10, 20,
            listOf(Player("Hans"), Player("Peter"), Player("Max"))
        )

        assertThat(testGame.smallBlindIndex).isEqualTo(0)
    }

    @Test
    fun `setBetAmount throws exception for negative values`() {
        val player = Player("Hans", 500)

        shouldThrow<IllegalArgumentException> {
            player.setBetAmount(-10)
        }
    }

    @Test
    fun `raiseBetTo throws if not player's turn`() {
        val p1 = Player("Hans", 500)
        val p2 = Player("Peter", 500)
        val p3 = Player("Max",   500)

        val g = Game(1, 10, 20, listOf(p1, p2, p3))


        val ex = shouldThrow<IllegalStateException> {
            g.raiseBetTo(p1, 100)
        }

        assertThat(ex.message).isEqualTo("It's not your turn to bet")
    }

    @Test
    fun `raiseBetTo throws if player is folded`() {

        val p = Player("Hans", 500)
        val g = Game(1, 10, 20, listOf(p, Player("Peter"), Player("Max")))


        while (g.getCurrentPlayer() != p) {
            g.makeTurn()
        }

        p.fold()

        val exception = shouldThrow<IllegalStateException> {
            g.raiseBetTo(p, 100)
        }

        assertThat(exception.message).isEqualTo("Cannot raise bet after folding")
    }

    @Test
    fun `raiseBetTo throws if chips is negative`() {
        val p = Player("Hans", 500)
        val g = Game(1, 10, 20, listOf(p, Player("Peter"), Player("Max")))
        while (g.getCurrentPlayer() != p) g.makeTurn()

        val ex = shouldThrow<IllegalArgumentException> {
            g.raiseBetTo(p, -1)
        }
        assertThat(ex.message).isEqualTo("Bet amount must be positive")
    }

    @Test
    fun `raiseBetTo throws if player is all-in`() {
        val p = Player("Hans", 100)
        val g = Game(1, 10, 20, listOf(p, Player("Peter"), Player("Max")))

        while (g.getCurrentPlayer() != p) g.makeTurn()

        p.allIn(10)

        val ex = shouldThrow<IllegalStateException> {
            g.raiseBetTo(p, 50)
        }
        assertThat(ex.message).isEqualTo("Cannot raise bet after going all-in")
    }

    @Test
    fun `raiseBetTo throws if not enough chips to raise`() {
        val p = Player("Hans", 50)
        val g = Game(1, 10, 20, listOf(p, Player("Peter"), Player("Max")))
        while (g.getCurrentPlayer() != p) g.makeTurn()

        val ex = shouldThrow<IllegalStateException> {
            g.raiseBetTo(p, 100)
        }
        assertThat(ex.message).isEqualTo("Not enough chips to raise bet")
    }

    @Test
    fun `check throws if player has folded`() {
        val p = Player("Hans", 100)
        val g = Game(1, 10, 20, listOf(p, Player("Peter"), Player("Max")))
        while (g.getCurrentPlayer() != p) g.makeTurn()
        p.fold()

        shouldThrow<IllegalStateException> {
            g.check(p)
        }.also {
            assertThat(it.message).isEqualTo("You can not check after having folded")
        }
    }

    @Test
    fun `check throws if not player's turn`() {
        val p1 = Player("Hans", 500)
        val p2 = Player("Peter", 500)
        val p3 = Player("Max", 500)

        val g = Game(1, 10, 20, listOf(p1, p2, p3))

        val ex = shouldThrow<IllegalStateException> { g.check(p1) }
        assertThat(ex.message).isEqualTo("It's not your turn to check")
    }


    @Test
    fun `check throws if player is all-in`() {
        val p = Player("Hans", 100)
        val g = Game(1, 10, 20, listOf(p, Player("Peter"), Player("Max")))
        while (g.getCurrentPlayer() != p) g.makeTurn()
        p.allIn(10)

        val ex = shouldThrow<IllegalStateException> {
            g.check(p)
        }
        assertThat(ex.message).isEqualTo("You can not check after having gone all-in")
    }

    @Test
    fun `check throws if bet not at highest`() {
        val p1 = Player("A", 500)
        val p2 = Player("B", 500)
        val p3 = Player("C", 500)
        val g = Game(1, 10, 20, listOf(p1, p2, p3))

        while (g.getCurrentPlayer() != p1) g.makeTurn()
        g.raiseBetTo(p1, 100)

        while (g.getCurrentPlayer() != p2) g.makeTurn()

        val ex = shouldThrow<IllegalStateException> {
            g.check(p2)
        }
        assertThat(ex.message).isEqualTo("You can not check if you are not at the highest bet")
    }

    @Test
    fun `fold throws if player has already folded`() {
        val p = Player("Hans", 100)
        val g = Game(1, 10, 20, listOf(p, Player("Peter"), Player("Max")))
        while (g.getCurrentPlayer() != p) g.makeTurn()
        p.fold()

        shouldThrow<IllegalStateException> {
            g.fold(p)
        }.also {
            assertThat(it.message).isEqualTo("You have already folded")
        }
    }

    @Test
    fun `fold throws if not player's turn`() {
        val p1 = Player("Hans", 500)
        val p2 = Player("Peter",500)
        val p3 = Player("Max",   500)
        val g = Game(1, 10, 20, listOf(p1,p2,p3))

        val ex = shouldThrow<IllegalStateException> { g.fold(p2) }

        assertThat(ex.message).isEqualTo("It's not your turn to fold")
    }

    @Test
    fun `fold throws if player is all-in`() {
        val p = Player("Hans", 100)
        val g = Game(1, 10, 20, listOf(p, Player("Peter"), Player("Max")))
        while (g.getCurrentPlayer() != p) g.makeTurn()
        p.allIn(10)

        val ex = shouldThrow<IllegalStateException> {
            g.fold(p)
        }
        assertThat(ex.message).isEqualTo("You can not fold after having gone all-in")
    }

    @Test
    fun `allIn throws if player already all-in`() {
        val p = Player("Hans", 100)
        val g = Game(1, 10, 20, listOf(p, Player("Peter"), Player("Max")))
        while (g.getCurrentPlayer() != p) g.makeTurn()
        p.allIn(100)

        shouldThrow<IllegalStateException> {
            g.allIn(p)
        }.also {
            assertThat(it.message).isEqualTo("You have already gone all-in")
        }
    }

    @Test
    fun `allIn throws if not player's turn`() {
        val p1 = Player("Hans", 500)
        val p2 = Player("Peter",500)
        val p3 = Player("Max",   500)
        val g = Game(1, 10, 20, listOf(p1,p2,p3))
        // current = Max → benutze z.B. Hans
        val ex = shouldThrow<IllegalStateException> { g.allIn(p1) }
        assertThat(ex.message).isEqualTo("It's not your turn to all in")
    }

    @Test
    fun `allIn throws if player has folded`() {
        val p = Player("Hans", 100)
        val g = Game(1, 10, 20, listOf(p, Player("Peter"), Player("Max")))
        while (g.getCurrentPlayer() != p) g.makeTurn()
        p.fold()

        val ex = shouldThrow<IllegalStateException> {
            g.allIn(p)
        }
        assertThat(ex.message).isEqualTo("You can not go all-in after having folded")
    }
}