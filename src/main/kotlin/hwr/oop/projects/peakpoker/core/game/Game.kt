package hwr.oop.projects.peakpoker.core.game
import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.player.Player
import hwr.oop.projects.peakpoker.core.player.PlayerStatus

class Game (
    // TODO: GameManager should automatically generate GameID
    val ID: Int,
    val smallBlindAmount: Int,
    val bigBlindAmount: Int,
    val currentBetAmount: Int = 0
    val betHistory: MutableList<String> = mutableListOf<String>()
) {
    val playersOnTable: MutableList<Player> = mutableListOf()
    var pot: Int = 0

    val communityCards: List<Card> = emptyList()
    val smallBlindIndex: Int = 0 // Variable to track the index of the small blind player within PlayersOnTable

    // TODO: Implement makeTurn function in order to increase/modify currentPlayerIndex
    var currentPlayerIndex : Int = 0

    fun checkPlayerValidity(player: Player): Boolean {
//        println("Player valid?: " + PlayersOnTable.none { it.name == player.name })
        return playersOnTable.none { it.name == player.name }
    }

    fun addPlayer(player: Player) {
        if (!checkPlayerValidity(player)) {
//            println("Player invalid!")
            throw IllegalArgumentException("Player with name ${player.name} already exists.")
        } else {
//            println("Adding successful? " + PlayersOnTable.add(player))
            playersOnTable.add(player)
//            println("Successfully added player ${player.name} to the game.")
//            println("New player list: " + PlayersOnTable.joinToString (", ") { it.name })
//            PlayersOnTable.joinToString (", ") { it.name }
        }
    }

    fun removePlayer(player: Player) {
        if (playersOnTable.contains(player)) {
            playersOnTable.remove(player)
//            println("Successfully removed player ${player.name} from the game.")
        } else {
//            println("Player ${player.name} not found in the game.")
            throw IllegalArgumentException("Player ${player.name} does not exist.")
        }
    }

    fun playersListAsString(): String {
        return playersOnTable.joinToString(", ") { it.name }
    }

    // FIXME: Does not work yet since currentPlayerIndex is not set/modified correctly.
    fun getHighestBet(): Int {
        return playersOnTable[currentPlayerIndex-1].getBet()
    }

    fun bet(player: Player, action: String, amount: Int = 0) {
        if (player.status != PlayerStatus.ACTIVE) {
            println("${player.name} cannot place bet - status: ${player.status}.")
            return
        }
        private fun applyBet(player: Player, amount: Int) {
            player.stack -= amount
            player.bet += amount
            player.totalBet += amount
            pot += amount
        }
        when (action.lowercase()) {
            "fold" -> {
                player.status = PlayerStatus.FOLDED
                println("${player.name} folded.")
            }

            "check" -> {
                if (player.bet < currentBetAmount) {
                    println("${player.name} bet is less than $currentBetAmount.")
                } else {
                    println("${player.name} checks.")
                }
            }

            "call" -> {
                val toCall = currentBetAmount - player.currentBet
                if (toCall <= 0) {
                    println("${player.name} already has the highest bet.")
                    return
                }

                if(player.stack <= toCall) {
                    //player is all-in
                    val allInAmount = player.stack
                    player.status = PlayerStatus.ALL_IN
                    println("${player.name} is all-in with $allInAmount.")
                    applyBet(player,allInAmount)
                } else {
                    println("${player.name} called $toCall.")
                    applyBet(player,toCall)
                }
            }

            "raise" -> {
                val raiseTo = amount
                if (raiseTo <= currentBetAmount) {
                    println("invalid raise: $raiseTo is not higher than current bet: $currentBetAmount.")
                    return
                }

                //val toPutIn stands for the amount the Player has to add to the pot to execute his action
                val toPutIn = raiseTo - player.currentBet
                if (player.stack <= toPutIn) {
                    // all-in raise
                    val allInAmount = player.stack
                    val newBet = player.bet + allInAmount
                    player.status = PlayerStatus.ALL_IN
                    println("${player.name} is all-in with $newBet.")
                    applyBet(player, allInAmount)
                    currentBetAmount = newBet
                } else {
                    println("${player.name} raised to $toPutIn.")
                    applyBet(player,toPutIn)
                    currentBetAmount = raiseTo
                }
            }

            else -> {
                println("unknown action: $action")
            }
        }
        betHistory.add("${player.name} $action ${if (amount > 0) "($amount)" else ""}".trim())
    }
}