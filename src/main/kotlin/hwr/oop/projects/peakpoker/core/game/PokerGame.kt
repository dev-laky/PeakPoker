package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import kotlinx.serialization.Serializable

@Serializable
class PokerGame(
  private val smallBlindAmount: Int,
  private val bigBlindAmount: Int,
  val players: List<PokerPlayer> = listOf(),
  val id: GameId = GameId.generate(),
) : GameActionable {
  /**
   * Exception thrown when duplicate player names are found
   */
  class DuplicatePlayerException(message: String) :
    IllegalStateException(message)

  /**
   * Exception thrown when blind configuration is invalid
   */
  class InvalidBlindConfigurationException(message: String) :
    IllegalStateException(message)

  /**
   * Exception thrown when there are not enough players
   */
  class MinimumPlayersException(message: String) :
    IllegalStateException(message)

  /**
   * Exception thrown when trying to access a round when none is active
   */
  class NoActiveRoundException(message: String) : IllegalStateException(message)

  /**
   * Exception thrown when trying to perform actions after the game has ended
   */
  class GameEndedException(message: String) : IllegalStateException(message)

  // Variable to track the index of the small blind player within players
  private var smallBlindIndex: Int = 0

  private var currentRound: PokerRound? = null

  private val round: GameActionable
    get() = currentRound ?: throw NoActiveRoundException("No active round")

  private var hasEnded: Boolean = false

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

    // Initialize first round or restore callback for loaded round
    if (currentRound == null) {
      newRound()
    } else {
      // Restore callback after deserialization
      currentRound?.restoreCallback { newRound() }
    }
  }

  fun getGameInfo(): GameInfo {
    return GameInfo(
      gameId = id.value,
      hasEnded = hasEnded,
      roundInfo = currentRound?.getRoundInfo()
    )
  }

  private fun checkForGameEnd(): Boolean {
    // Check if only one player has chips left
    val activePlayers = players.filter { it.chips() > 0 }
    return activePlayers.size == 1
  }

  private inline fun <T> withGameEndCheck(action: () -> T): T {
    if (hasEnded) {
      throw GameEndedException("Game has ended - no more actions allowed")
    }
    return action()
  }

  private fun newRound() {
    val hasGameEnded = checkForGameEnd()
    if (hasGameEnded) {
      hasEnded = true; return
    }

    currentRound = PokerRound(
      players = players,
      smallBlindIndex = smallBlindIndex,
      smallBlindAmount = smallBlindAmount,
      bigBlindAmount = bigBlindAmount
    )

    // Set the callback after creation
    currentRound?.restoreCallback { newRound() }

    smallBlindIndex = (smallBlindIndex + 1) % players.size
  }

  // Game-level functions and delegation to Round
  override fun raiseBetTo(playerName: String, chips: Int) =
    withGameEndCheck { round.raiseBetTo(playerName, chips) }

  override fun call(playerName: String) =
    withGameEndCheck { round.call(playerName) }

  override fun check(playerName: String) =
    withGameEndCheck { round.check(playerName) }

  override fun fold(playerName: String) =
    withGameEndCheck { round.fold(playerName) }

  override fun allIn(playerName: String) =
    withGameEndCheck { round.allIn(playerName) }
}
