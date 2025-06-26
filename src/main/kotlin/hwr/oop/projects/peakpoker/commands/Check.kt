package hwr.oop.projects.peakpoker.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import hwr.oop.projects.peakpoker.persistence.LoadGamePort
import hwr.oop.projects.peakpoker.persistence.SaveGamePort

class Check(
  private val loadGamePort: LoadGamePort,
  private val saveGamePort: SaveGamePort,
) : CliktCommand(name = "check") {

  override fun help(context: Context) =
    "Check (pass) if no bet has been made in the current round."

  val gameId by option("--gameID")
    .help("Game ID")
    .required()

  val playerName by option("--player")
    .help("Player name")
    .required()

  override fun run() {
    try {
      val game = loadGamePort.loadGame(gameId)

      game.check(playerName)

      saveGamePort.saveGame(game)

      echo("Player $playerName checked in game ${game.id.value}")
    } catch (e: Exception) {
      echo("Error checking: ${e.message}")
    }
  }
}