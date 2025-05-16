package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.player.Player

class Game(
    override val id: Int,
    val smallBlindAmount: Int,
    val bigBlindAmount: Int,
    val playersOnTable: List<Player> = listOf()
) : GameInterface {
    init {
        require(smallBlindAmount > 0) { "Small blind amount must be positive" }
        require(bigBlindAmount > 0) { "Big blind amount must be positive" }
        require(bigBlindAmount >= smallBlindAmount) { "Big blind amount must be greater than or equal to small blind amount" }
        require(playersOnTable.size >= 3) { "Minimum number of players is 3" }
        require(playersOnTable.distinctBy { it.name }.size == playersOnTable.size) { "All players must be unique" }

        // Set the blinds for the players at the table
        setBlinds()
    }

    // Variable to track the index of the small blind player within PlayersOnTable
    val smallBlindIndex: Int = 0
    val pot get() = calculatePot()
    val communityCards: CommunityCards = CommunityCards(emptyList(), this)

    // Will be = 2 after "blind" init
    var currentPlayerIndex: Int = 0

    fun getSmallBlind(): Int {
        return smallBlindAmount
    }

    fun getBigBlind(): Int {
        return bigBlindAmount
    }

    fun getCurrentPlayer(): Player {
        return playersOnTable[currentPlayerIndex]
    }

    fun getNextPlayer(): Player {
        return playersOnTable[(currentPlayerIndex + 1) % playersOnTable.size]
    }

    fun getHighestBet(): Int {
        return playersOnTable.maxOf { it.getBet() }
    }

    fun calculatePot(): Int {
        return playersOnTable.sumOf { it.getBet() }
    }

    fun checkPlayerValidity(player: Player): Boolean {
        return playersOnTable.none { it.name == player.name }
    }

    fun makeTurn() {
        val nextPlayer = getNextPlayer()

        // Skip any folded / all-in players
        if (nextPlayer.isFolded || nextPlayer.isAllIn) {
            currentPlayerIndex = (playersOnTable.indexOf(nextPlayer))
            makeTurn()
            return
        }

        currentPlayerIndex = (playersOnTable.indexOf(nextPlayer))
    }

    /**
     * Sets the player's bet to the specified amount.
     *
     * This method validates that the bet is higher than the current highest bet
     * and that it's the player's turn before raising their bet.
     *
     * @param player The player who is raising their bet
     * @param chips The total amount to bet (not the additional amount)
     * @throws IllegalArgumentException If the bet amount is negative.
     * @throws IllegalStateException If any of the following conditions are true:
     *                               - The bet is not higher than the current highest bet.
     *                               - It is not the player's turn.
     *                               - The player has already folded.
     *                               - The player has already gone all-in.
     *                               - The player does not have enough chips.
     */
    fun raiseBetTo(player: Player, chips: Int) {
        val currentPlayer = getCurrentPlayer()
        val highestBet = getHighestBet()
        when {
            chips < 0 -> throw IllegalArgumentException("Bet amount must be positive")
            highestBet >= chips -> throw IllegalStateException("Bet must be higher than the current highest bet")
            currentPlayer != player -> throw IllegalStateException("It's not your turn to bet")
            player.isFolded -> throw IllegalStateException("Cannot raise bet after folding")
            player.isAllIn -> throw IllegalStateException("Cannot raise bet after going all-in")

            // The player needs to go all-in or fold
            (chips - player.getBet()) > player.getChips() -> throw IllegalStateException("Not enough chips to raise bet")
        }
        player.setBetAmount(chips)
        makeTurn()
    }

    fun call(player: Player) {
        val currentPlayer = getCurrentPlayer()
        val highestBet = getHighestBet()
        when {
            currentPlayer != player -> throw IllegalStateException("It's not your turn to call")
            player.getBet() == highestBet -> throw IllegalStateException("You are already at the highest bet")
            player.isFolded -> throw IllegalStateException("You can not call after having folded")
            player.isAllIn -> throw IllegalStateException("You can not call after having gone all-in")

            // The player needs to go all-in or fold
            player.getChips() < (highestBet - player.getBet()) -> throw IllegalStateException("You do not have enough chips to call.")
        }
        player.setBetAmount(highestBet)
        makeTurn()
    }

    fun check(player: Player) {
        val currentPlayer = getCurrentPlayer()
        when {
            currentPlayer != player -> throw IllegalStateException("It's not your turn to check")
            player.isFolded -> throw IllegalStateException("You can not check after having folded")
            player.isAllIn -> throw IllegalStateException("You can not check after having gone all-in")
            player.getBet() != getHighestBet() -> throw IllegalStateException("You can not check if you are not at the highest bet")
        }
        makeTurn()
    }

    fun fold(player: Player) {
        val currentPlayer = getCurrentPlayer()
        when {
            currentPlayer != player -> throw IllegalStateException("It's not your turn to fold")
            player.isFolded -> throw IllegalStateException("You have already folded")
            player.isAllIn -> throw IllegalStateException("You can not fold after having gone all-in")
        }
        player.fold()
        makeTurn()
    }

    fun allIn(player: Player) {
        val currentPlayer = getCurrentPlayer()
        val highestBet = getHighestBet()
        when {
            currentPlayer != player -> throw IllegalStateException("It's not your turn to all in")
            player.isFolded -> throw IllegalStateException("You can not go all-in after having folded")
            player.isAllIn -> throw IllegalStateException("You have already gone all-in")
        }
        player.allIn(highestBet)
        makeTurn()
    }

    private fun setBlinds() {
        raiseBetTo(getCurrentPlayer(), smallBlindAmount)

        // Check for same blind amounts --> call
        if (bigBlindAmount == smallBlindAmount) {
            call(getCurrentPlayer())
            return
        }

        raiseBetTo(getCurrentPlayer(), bigBlindAmount)
    }
}
