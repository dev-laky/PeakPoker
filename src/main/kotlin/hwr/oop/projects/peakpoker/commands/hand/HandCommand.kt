package hwr.oop.projects.peakpoker.commands.hand

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import hwr.oop.projects.peakpoker.core.player.Player

class HandCommand : CliktCommand(name = "hand") {
    override fun help(context: Context) = "Create a new Game."

    val playerName: String? by option("--player")
        .help("Comma-separated list of player names")
    val gameId: Int? by option("--gameID").int()
        .help("Game ID")
        .check("Game ID must be greater than 0") { it > 0 }

    override fun run() {
        if (gameId == null) {
            echo("No game ID provided. Use --game=<id>")
            return
        }

        if (playerName == null) {
            echo("No player provided. Use --player=<name>")
            return
        }

        // TODO: Players needs to be loaded here with Player by Game ID...

        // Mock data for demonstration
        val player = Player(name = playerName!!, chips = 100)
        val game = hwr.oop.projects.peakpoker.core.game.Game(
            id = gameId!!,
            smallBlindAmount = 10,
            bigBlindAmount = 20,
            playersOnTable = listOf(
                player,
                Player(name = "Player2", chips = 100),
                Player(name = "Player3", chips = 100)
            )
        )

        // TODO: HoleCards consists of game as well, but not needed here since player is loaded by game ID
        // TODO: We may want to remove the game from HoleCards in order to avoid any redundancy
        val hand = player.getHand()

        val cardDisplay = hand.cards.joinToString(" <-> ") { card ->
            "${card.suit}, ${card.rank}"
        }

        echo("Game ID: ${game.id}")
        echo("Player: ${player.name}")
        echo("Hand: $cardDisplay")
    }
}
