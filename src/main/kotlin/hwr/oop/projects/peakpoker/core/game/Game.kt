package hwr.oop.projects.peakpoker.core.game
import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.player.Player

class Game (
    val id: Int, // GameManager automatically generates GameID and passes it on Game-creation
    // Both values are set by GameManager and passed on Game-creation
    val smallBlindAmount: Int,
    val bigBlindAmount: Int,
    val playersOnTable: List<Player> = listOf()
) {
    init {
        require(smallBlindAmount > 0) { "Small blind amount must be positive" }
        require(bigBlindAmount > 0) { "Big blind amount must be positive" }
        require(bigBlindAmount >= smallBlindAmount) { "Big blind amount must be greater than or equal to small blind amount" }
        require(playersOnTable.size >= 3) { "Minimum number of players is 3" }
    }

    val pot: Int = 0

    val communityCards: List<Card> = emptyList()
    // Variable to track the index of the small blind player within PlayersOnTable
    val smallBlindIndex: Int = 0 // Changed by GameManager when a round is over (possibly makeTurn() function)

    var currentPlayerIndex : Int = 2

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
        return playersOnTable[currentPlayerIndex].getBetAmount()
    }

    fun calculatePot(): Int {
        return playersOnTable.sumOf { it.getBetAmount() }
    }

    fun makeTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % playersOnTable.size
    }

    fun checkPlayerValidity(player: Player): Boolean {
        return playersOnTable.none { it.name == player.name }
    }
}