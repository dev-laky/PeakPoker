package hwr.oop.projects.peakpoker.commands.game

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import hwr.oop.projects.peakpoker.core.game.Game
import hwr.oop.projects.peakpoker.core.player.Player

class GameNew : CliktCommand(name = "new") {
    override fun help(context: Context) = "Create a new Game."

    val players: List<String>? by option("--players")
        .convert { input -> input.split(":").map { it.trim() } }
        .help("Comma-separated list of player names")

    override fun run() {
        if (players.isNullOrEmpty()) {
            echo("No players provided. Use --players=<player1:player2:...>")
            return
        }

        val game = Game(
            id = 1,
            smallBlindAmount = 10,
            bigBlindAmount = 20,
            playersOnTable = players!!.map { Player(name = it, chips = 100) })

        // TODO: Save game to file

        echo("Game ID: ${game.id}")
        echo("New game created with players: ${game.playersOnTable.joinToString(", ") { it.name }}")
    }
}
