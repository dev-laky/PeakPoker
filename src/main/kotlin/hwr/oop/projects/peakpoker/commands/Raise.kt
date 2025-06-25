package hwr.oop.projects.peakpoker.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import hwr.oop.projects.peakpoker.persistence.LoadGamePort
import hwr.oop.projects.peakpoker.persistence.SaveGamePort

class Raise(
  private val loadGamePort: LoadGamePort,
  private val saveGamePort: SaveGamePort,
) : CliktCommand(name = "raise") {

  override fun help(context: Context) = "Raise bet to specified amount."

  val amount by argument().convert {
    it.toIntOrNull()
      ?: throw IllegalArgumentException("Amount must be a number")
  }

  val gameId by option("--gameID")
    .help("Game ID")
    .required()

  val playerName by option("--player")
    .help("Player name")
    .required()

  override fun run() {
    try {
      val game = loadGamePort.loadGame(gameId)

      game.raiseBetTo(playerName, amount)

      saveGamePort.saveGame(game)

      echo("Player $playerName raised bet to $amount chips in game ${game.id.value}")
    } catch (e: Exception) {
      echo("Error raising bet: ${e.message}")
    }
  }
}