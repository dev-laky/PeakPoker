package hwr.oop.projects.peakpoker.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import hwr.oop.projects.peakpoker.persistence.LoadGamePort
import kotlin.collections.joinToString

class GameInfo(private val loadGamePort: LoadGamePort) :
  CliktCommand(name = "game-info") {
  override fun help(context: Context) =
    "Retrieve information about whole game state."

  val gameId by option("--gameID")
    .help("PokerGame ID")
    .required()

  override fun run() {
    try {
      val game = loadGamePort.loadGame(gameId)
      val gameInfo = game.getGameInfo()

      echo("=== GAME INFORMATION ===")
      echo("Game ID: ${gameInfo.gameId}")
      echo("Game Status: ${if (gameInfo.hasEnded) "ENDED" else "ACTIVE"}")

      gameInfo.roundInfo?.let { round ->
        echo("\n=== ROUND INFORMATION ===")
        echo("Round Phase: ${round.roundPhase}")
        echo("Small Blind Amount: ${round.smallBlindAmount} (Player: ${round.smallBlindPlayerName})")
        echo("Big Blind Amount: ${round.bigBlindAmount}")
        echo("Current Player: ${round.currentPlayerName}")

        echo("\n=== COMMUNITY CARDS ===")
        if (round.communityCards.isEmpty()) {
          echo("No community cards dealt yet.")
        } else {
          echo(
            "Cards: ${
              round.communityCards.joinToString(" <-> ") { card ->
                "${card.suit}, ${card.rank}"
              }
            }"
          )
        }

        echo("\n=== POTS ===")
        round.pots.forEachIndexed { index, pot ->
          val potType = if (index == 0) "Main Pot" else "Side Pot $index"
          echo(
            "$potType: ${pot.amount} (Eligible: ${
              pot.eligiblePlayerNames.joinToString(
                ", "
              )
            })"
          )
        }

        echo("\n=== PLAYERS ===")
        round.players.forEach { player ->
          val status = mutableListOf<String>()
          if (player.isFolded) status.add("FOLDED")
          if (player.isAllIn) status.add("ALL-IN")
          if (player.name == round.currentPlayerName) status.add("CURRENT TURN")

          val statusText =
            if (status.isNotEmpty()) " [${status.joinToString(", ")}]" else ""
          echo("${player.name}: ${player.chips} chips, bet: ${player.bet}${statusText}")
        }
      } ?: echo("\nNo active round")

    } catch (e: Exception) {
      echo("Error retrieving game information: ${e.message}")
    }
  }
}