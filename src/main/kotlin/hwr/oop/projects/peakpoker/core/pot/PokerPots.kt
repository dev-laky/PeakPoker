package hwr.oop.projects.peakpoker.core.pot

import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import kotlinx.serialization.Serializable

@Serializable
class PokerPots(
  private val players: List<PokerPlayer>,
  private val communityCards: CommunityCards,
  val pots: MutableList<Pot> =
    mutableListOf(
      Pot(
        eligiblePlayers = players.toSet(),
        communityCards = communityCards
      )
    ),
) : Iterable<Pot> by pots {
  private val mainPot: Pot get() = pots.first()

  fun addChipsToCurrentPot(chips: Int) {
    mainPot.addChips(chips)
  }

  /**
   * Creates a side pot when a player goes all-in.
   *
   * This method:
   * 1. Calculates excess chips from other players (amounts above what the all-in player could match)
   * 2. Reduces those players' bets to match the all-in amount
   * 3. Moves excess chips from the main pot to a new side pot
   * 4. Makes the all-in player ineligible for the side pot
   *
   * @param allInPlayer The player who has gone all-in
   */
  fun createSidePotIfNeeded(allInPlayer: PokerPlayer) {
    val excessAmount = calculateExcessAmount(allInPlayer)

    // If no excess chips, no need for a side pot
    if (excessAmount <= 0) return

    // If there are excess chips, move them to a side pot

    // Reduce the main pot by the excess amount
    mainPot.removeChips(excessAmount)

    // Create a side pot with eligible players (everyone but the all-in player)
    val eligiblePlayers =
      mainPot.eligiblePlayers.filter { it != allInPlayer }.toSet()
    pots.add(
      Pot(
        eligiblePlayers = eligiblePlayers,
        communityCards = communityCards,
        amount = excessAmount
      )
    )
  }

  private fun calculateExcessAmount(allInPlayer: PokerPlayer): Int {
    // Get all-in amount (player's bet and remaining chips)
    val allInTotal = allInPlayer.bet() + allInPlayer.chips()

    // Calculate excess chips from other players
    var excessAmount = 0
    players.forEach { player ->
      if (!player.isFolded() && player != allInPlayer && player.bet() > allInTotal) {
        val excess = player.bet() - allInTotal
        excessAmount += excess
        // Adjust player's bet to match all-in
        player.setBetAmount(allInTotal)
      }
    }

    return excessAmount
  }
}