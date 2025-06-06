package hwr.oop.projects.peakpoker.commands.hand

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import hwr.oop.projects.peakpoker.core.player.PokerPlayer

class HandCommand : CliktCommand(name = "hand") {
  override fun help(context: Context) = "Create a new PokerGame."

  val playerName: String? by option("--player")
    .help("Colon-separated list of player names")
  val gameId: Int? by option("--gameID").int()
    .help("PokerGame ID")
    .check("PokerGame ID must be greater than 0") { it > 0 }

  override fun run() {
    if (gameId == null) {
      echo("You need to provide a game ID. Use --game=<id>")
      return
    }

    if (playerName == null) {
      echo("You need to provide a list of at least three players. Use --player=<name>")
      return
    }

    // TODO: Players needs to be loaded here with PokerPlayer by PokerGame ID...

    // Mock data for demonstration
    val player = PokerPlayer(name = playerName!!, chips = 100)
    val game = hwr.oop.projects.peakpoker.core.game.PokerGame(
      smallBlindAmount = 10,
      bigBlindAmount = 20,
      players = listOf(
        player,
        PokerPlayer(name = "Player2", chips = 100),
        PokerPlayer(name = "Player3", chips = 100)
      )
    )

    // TODO: HoleCards consists of game as well, but not needed here since player is loaded by game ID
    // TODO: We may want to remove the game from HoleCards in order to avoid any redundancy
    val hand = player.getHand()

    val cardDisplay = hand.getCards().joinToString(" <-> ") { card ->
      "${card.suit}, ${card.rank}"
    }

    echo("PokerGame ID: ${game.id}")
    echo("PokerPlayer: ${player.name}")
    echo("Hand: $cardDisplay")
  }
}
