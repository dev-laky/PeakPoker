package hwr.oop.projects.peakpoker.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import hwr.oop.projects.peakpoker.persistence.LoadGamePort

class HandInfo(private val loadGamePort: LoadGamePort) :
  CliktCommand(name = "hand") {
  override fun help(context: Context) = "Show Hole Cards for Player."

  val playerName by option("--player")
    .help("Colon-separated list of player names")
    .required()

  val gameId by option("--gameID")
    .help("PokerGame ID")
    .required()

  override fun run() {
    try {
      val game = loadGamePort.loadGame(gameId)

      val player = game.players.find { it.name == playerName } ?: run {
        echo("Player with name '$playerName' not found in game with ID '$gameId'.")
        return
      }

      val hand = player.hand()

      if (hand.cards.isEmpty()) return echo("Player '$playerName' has no cards in hand.")

      val cardDisplay = hand.cards.joinToString(" <-> ") { card ->
        "${card.suit}, ${card.rank}"
      }

      echo("PokerGame ID: ${game.id.value}")
      echo("PokerPlayer: ${player.name}")
      echo("Hand: $cardDisplay")

    } catch (e: Exception) {
      echo("Error retrieving hand information: ${e.message}")
    }
  }
}