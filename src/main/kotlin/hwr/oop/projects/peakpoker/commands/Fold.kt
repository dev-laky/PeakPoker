package hwr.oop.projects.peakpoker.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import hwr.oop.projects.peakpoker.persistence.LoadGamePort
import hwr.oop.projects.peakpoker.persistence.SaveGamePort

class Fold(
  private val loadGamePort: LoadGamePort,
  private val saveGamePort: SaveGamePort,
) : CliktCommand(name = "fold") {

  override fun help(context: Context) = "Fold your hand in the game."

  val gameId by option("--gameID")
    .help("Game ID")
    .required()

  val playerName by option("--player")
    .help("Player name")
    .required()

  override fun run() {
    try {
      val game = loadGamePort.loadGame(gameId)

      game.fold(playerName)

      saveGamePort.saveGame(game)

      echo("Player $playerName folded their hand in game ${game.id.value}")
    } catch (e: Exception) {
      echo("Error folding hand: ${e.message}")
    }
  }
}