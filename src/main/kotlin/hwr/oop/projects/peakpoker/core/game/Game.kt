package hwr.oop.projects.peakpoker.core.game
import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.player.Player

class Game (
    // TODO: GameManager should automatically generate GameID
    val ID: Int,
    val smallBlindAmount: Int,
    val bigBlindAmount: Int
) {
    val PlayersOnTable: MutableList<Player> = mutableListOf()
    val pot: Int = 0

    val communityCards: List<Card> = emptyList()
    val smallBlindIndex: Int = 0 // Variable to track the index of the small blind player within PlayersOnTable

    // TODO: Implement makeTurn function in order to increase/modify currentPlayerIndex
    var currentPlayerIndex : Int = 0

    fun checkPlayerValidity(player: Player): Boolean {
//        println("Player valid?: " + PlayersOnTable.none { it.name == player.name })
        return PlayersOnTable.none { it.name == player.name }
    }

    fun addPlayer(player: Player) {
        if (!checkPlayerValidity(player)) {
//            println("Player invalid!")
            throw IllegalArgumentException("Player with name ${player.name} already exists.")
        } else {
//            println("Adding successful? " + PlayersOnTable.add(player))
            PlayersOnTable.add(player)
//            println("Successfully added player ${player.name} to the game.")
//            println("New player list: " + PlayersOnTable.joinToString (", ") { it.name })
//            PlayersOnTable.joinToString (", ") { it.name }
        }
    }

    fun removePlayer(player: Player) {
        if (PlayersOnTable.contains(player)) {
            PlayersOnTable.remove(player)
//            println("Successfully removed player ${player.name} from the game.")
        } else {
//            println("Player ${player.name} not found in the game.")
            throw IllegalArgumentException("Player ${player.name} does not exist.")
        }
    }

    fun playersListAsString(): String {
        return PlayersOnTable.joinToString(", ") { it.name }
    }

    // FIXME: Does not work yet since currentPlayerIndex is not set/modified correctly.
    fun getHighestBet(): Int {
        return PlayersOnTable[currentPlayerIndex-1].getBet()
    }
}