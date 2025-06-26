package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.exceptions.DuplicatePlayerException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidBlindConfigurationException
import hwr.oop.projects.peakpoker.core.exceptions.MinimumPlayersException
import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import hwr.oop.projects.peakpoker.core.round.PokerRound

class PokerGame(
  private val smallBlindAmount: Int,
  private val bigBlindAmount: Int,
  val players: List<PokerPlayer> = listOf(),
  val id: GameId = GameId.generate(),
) : GameActionable {

  // Variable to track the index of the small blind player within players
  private var smallBlindIndex: Int = 0

  private lateinit var round: GameActionable

  init {
    if (smallBlindAmount <= 0) {
      throw InvalidBlindConfigurationException("Small blind amount must be positive")
    }
    if (bigBlindAmount <= 0) {
      throw InvalidBlindConfigurationException("Big blind amount must be positive")
    }
    if (bigBlindAmount != smallBlindAmount * 2) {
      throw InvalidBlindConfigurationException("Big blind amount must be exactly double the small blind amount")
    }
    if (players.size < 2) {
      throw MinimumPlayersException("Minimum number of players is 2")
    }
    if (players.distinctBy { it.name }.size != players.size) {
      throw DuplicatePlayerException("All players must be unique")
    }

    // Initialize the first round
    newRound()
  }

  private fun checkForGameEnd(): Boolean {
    // Check if only one player has chips left
    val activePlayers = players.filter { it.chips() > 0 }
    return activePlayers.size == 1
  }

  fun newRound() {
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
  override fun raiseBetTo(playerName: String, chips: Int) =
    round.raiseBetTo(playerName, chips)

  override fun call(playerName: String) = round.call(playerName)
  override fun check(playerName: String) = round.check(playerName)
  override fun fold(playerName: String) = round.fold(playerName)
  override fun allIn(playerName: String) = round.allIn(playerName)
}
