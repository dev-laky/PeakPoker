package hwr.oop.projects.peakpoker.core.game
import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.player.Player

class Game (
    val ID: Int, // GameManager automatically generates GameID and passes it on Game-creation
    // Both values are set by GameManager and passed on Game-creation
    val smallBlindAmount: Int,
    val bigBlindAmount: Int
) {
    init {
        require(smallBlindAmount > 0) { "Small blind amount must be positive" }
        require(bigBlindAmount > 0) { "Big blind amount must be positive" }
        require(bigBlindAmount >= smallBlindAmount) { "Big blind amount must be greater than or equal to small blind amount" }
    }

    val PlayersOnTable: MutableList<Player> = mutableListOf()
    val pot: Int = 0

    val communityCards: List<Card> = emptyList()
    // Variable to track the index of the small blind player within PlayersOnTable
    val smallBlindIndex: Int = 0 // Changed by GameManager when a round is over (possibly makeTurn() function)

    // TODO: Implement makeTurn function in order to increase/modify currentPlayerIndex
    var currentPlayerIndex : Int = 0

    fun getSmallBlind(): Int {
        return smallBlindAmount
    }

    fun getBigBlind(): Int {
        return bigBlindAmount
    }

    // FIXME: Does not work yet since currentPlayerIndex is not set/modified correctly.
    fun getCurrentPlayer(): Player {
        return PlayersOnTable[currentPlayerIndex]
    }

    // FIXME: Does not work yet since currentPlayerIndex is not set/modified correctly.
    fun getHighestBet(): Int {
        return PlayersOnTable[currentPlayerIndex-1].getBetAmount()
    }

    fun getPot(): Int {
        return pot
    }

    fun calculatePot(): Int {
        return PlayersOnTable.sumOf { it.getBetAmount() }
    }

    fun getPlayersListAsString(): String {
        return PlayersOnTable.joinToString(", ") { it.name }
    }

    fun checkPlayerValidity(player: Player): Boolean {
        return PlayersOnTable.none { it.name == player.name }
    }

    fun addPlayer(player: Player) {
        if (!checkPlayerValidity(player)) {
            throw IllegalArgumentException("Player with name ${player.name} already exists.")
        } else {
            PlayersOnTable.add(player)
        }
    }

    fun removePlayer(player: Player) {
        if (PlayersOnTable.contains(player)) {
            PlayersOnTable.remove(player)
        } else {
            throw IllegalArgumentException("Player ${player.name} does not exist.")
        }
    }
}