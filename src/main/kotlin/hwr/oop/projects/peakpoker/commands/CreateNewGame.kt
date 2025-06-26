package hwr.oop.projects.peakpoker.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import hwr.oop.projects.peakpoker.core.game.PokerGame
import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import hwr.oop.projects.peakpoker.persistence.SaveGamePort

class CreateNewGame(private val saveGamePort: SaveGamePort) :
  CliktCommand(name = "new-game") {
  override fun help(context: Context) = "Create a new Game."

  val players: List<String>? by option("--players")
    .convert { input -> input.split(":").map { it.trim() } }
    .required()
    .help("Colon-separated list of player names")

  override fun run() {
    if (players.isNullOrEmpty()) {
      echo("No players provided. Use --players=<player1:player2:...>")
      return
    }

    try {
      val game = PokerGame(
        smallBlindAmount = 10,
        bigBlindAmount = 20,
        players = players!!.map { PokerPlayer(name = it, chips = 100) })

      val gameId = saveGamePort.saveGame(game)

      echo("Game ID: ${gameId.value}")
      echo("New game created with players: ${game.players.joinToString(", ") { it.name }}")
    } catch (e: IllegalStateException) {
      echo("Error creating game: ${e.message}")
    }
  }
}