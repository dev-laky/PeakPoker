package hwr.oop.projects.peakpoker.commands.game

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import hwr.oop.projects.peakpoker.core.game.PokerGame
import hwr.oop.projects.peakpoker.core.player.PokerPlayer

class GameNew : CliktCommand(name = "new") {
  override fun help(context: Context) = "Create a new PokerGame."

  val players: List<String>? by option("--players")
    .convert { input -> input.split(":").map { it.trim() } }
    .help("Colon-separated list of player names")

  override fun run() {
    if (players.isNullOrEmpty()) {
      echo("No players provided. Use --players=<player1:player2:...>")
      return
    }

    val game = PokerGame(
      smallBlindAmount = 10,
      bigBlindAmount = 20,
      players = players!!.map { PokerPlayer(name = it, chips = 100) })

    // TODO: Save game to file

    echo("PokerGame ID: ${game.id}")
    echo("New game created with players: ${game.players.joinToString(", ") { it.name }}")
  }
}
