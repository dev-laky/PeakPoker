package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import hwr.oop.projects.peakpoker.core.round.PokerRound

class PokerGame(
  private val smallBlindAmount: Int,
  private val bigBlindAmount: Int,
  val players: List<PokerPlayer> = listOf(),
  override val id: GameId = GameId.generate(),
) : Game {

  // Variable to track the index of the small blind player within players
  private var smallBlindIndex: Int = 0

  private lateinit var round: PokerRound

  init {
    require(smallBlindAmount > 0) { "Small blind amount must be positive" }
    require(bigBlindAmount > 0) { "Big blind amount must be positive" }
    require(bigBlindAmount == smallBlindAmount * 2) { "Big blind amount must be exactly double the small blind amount" }
    require(players.size >= 2) { "Minimum number of players is 2" }
    require(players.distinctBy { it.name }.size == players.size) { "All players must be unique" }

    // Initialize the first round
    newRound()
  }

  private fun checkForGameEnd(): Boolean {
    // Check if only one player has chips left
    val activePlayers = players.filter { it.getChips() > 0 }
    return activePlayers.size == 1
  }

  fun newRound() {
    // Reset player states (fold, allIn) for the new round
    players.forEach { it.resetRoundState() }

    // Evaluate ending of game
    val hasGameEnded = checkForGameEnd()
    if (hasGameEnded) return

    round = PokerRound(
      players = players,
      smallBlindIndex = smallBlindIndex,
      smallBlindAmount = smallBlindAmount,
      bigBlindAmount = bigBlindAmount,
      onRoundComplete = { newRound() }
    )
    smallBlindIndex = (smallBlindIndex + 1) % players.size
  }

  // Game-level functions and delegation to Round
  fun raiseBetTo(player: PokerPlayer, chips: Int) =
    round.raiseBetTo(player, chips)

  fun call(player: PokerPlayer) = round.call(player)
  fun check(player: PokerPlayer) = round.check(player)
  fun fold(player: PokerPlayer) = round.fold(player)
  fun allIn(player: PokerPlayer) = round.allIn(player)
}
