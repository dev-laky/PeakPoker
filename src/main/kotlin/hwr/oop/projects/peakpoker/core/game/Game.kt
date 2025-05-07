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
    }

    // Variable to track the index of the small blind player within PlayersOnTable
    val smallBlindIndex: Int = 0
    var pot: Int = 0
    val communityCards: CommunityCards = CommunityCards(emptyList(), this)
    var currentPlayerIndex: Int = 2

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

    fun makeTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % playersOnTable.size
    }

    fun checkPlayerValidity(player: Player): Boolean {
        return playersOnTable.none { it.name == player.name }
    }
}
