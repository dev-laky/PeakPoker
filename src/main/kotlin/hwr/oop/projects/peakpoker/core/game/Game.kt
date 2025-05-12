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
    var pot: Int = 0
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
        currentPlayerIndex = (currentPlayerIndex + 1) % playersOnTable.size
    }

    /**
     * Sets the player's bet to the specified amount.
     *
     * This method validates that the bet is higher than the current highest bet
     * and that it's the player's turn before raising their bet.
     *
     * @param player The player who is raising their bet
     * @param chips The total amount to bet (not the additional amount)
     * @throws IllegalStateException If the bet is not higher than the current highest bet,
     *                               or if it's not the player's turn
     */
    fun raiseBetTo(player: Player, chips: Int) {
        val currentPlayer = getCurrentPlayer()
        when {
            chips < 0 -> throw IllegalArgumentException("Bet amount must be positive")
            chips <= getHighestBet() -> throw IllegalStateException("Bet must be higher than the current highest bet")
            currentPlayer != player -> throw IllegalStateException("It's not your turn to bet")
            player.isFolded -> throw IllegalStateException("Cannot raise bet after folding")
            player.isAllIn -> throw IllegalStateException("Cannot raise bet after going all-in")
            chips > player.getChips() -> throw IllegalStateException("Not enough chips to raise bet")
        }
        player.raiseBetTo(chips)
    }

    fun call(player: Player) {
        val highestBet = getHighestBet()
        when {
            getCurrentPlayer() != player -> throw IllegalStateException("It's not your turn to call")
            highestBet <= player.getBet() -> throw IllegalStateException("You are already at the highest bet")
            player.isFolded -> throw IllegalStateException("Cannot call after folding")
            player.isAllIn -> throw IllegalStateException("Cannot call after going all-in")

            // needs to go all-in or fold
            player.getChips() < highestBet -> throw IllegalStateException("Not enough chips to call.")
        }
        player.call(highestBet)
    }

    fun check(player: Player) {
        TODO("The check function is not implemented yet.")
    }

    fun fold(player: Player) {
        TODO("The fold function is not implemented yet.")
    }

    fun allIn(player: Player) {
        TODO("The allIn function is not implemented yet.")
    }

    private fun setBlinds() {
        raiseBetTo(getCurrentPlayer(), smallBlindAmount)
        makeTurn()

        // Check for same blind amounts --> call
        if (bigBlindAmount == smallBlindAmount) {
            call(getCurrentPlayer())
            return
        }

        raiseBetTo(getCurrentPlayer(), bigBlindAmount)
        makeTurn()
    }
}
