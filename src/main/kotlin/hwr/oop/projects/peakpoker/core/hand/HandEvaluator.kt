package hwr.oop.projects.peakpoker.core.hand

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.HoleCards

/**
 * Evaluates poker hands and determines winning players.
 */
class HandEvaluator(
  private val communityCards: CommunityCards,
) {
  /**
   * Exception thrown when an empty player list is provided
   */
  class EmptyPlayerListEvaluationException(message: String) : IllegalArgumentException(message)

  /**
   * Exception thrown when an invalid number of cards is found
   */
  class InvalidCardCountException(message: String) : IllegalArgumentException(message)

  /**
   * Exception thrown when no valid hand can be found
   */
  class NoValidHandFoundException(message: String) : IllegalStateException(message)

  /**
   * Determines all players with the highest hand among a list of players.
   * Returns all players who are tied for the best hand to properly support split pots.
   *
   * @return List of [HoleCards] of all players tied for the highest hand
   * @throws EmptyPlayerListEvaluationException If the list of players is empty
   */
  fun determineHighestHand(holeCardsList: List<HoleCards>): List<HoleCards> {
    if (holeCardsList.isEmpty()) {
      throw EmptyPlayerListEvaluationException("Must provide at least one player")
    }
    // If only one player, they win by default
    if (holeCardsList.size == 1) return listOf(holeCardsList.first())

    // Find the best hand value among all players
    val bestHandValue: PokerHand = holeCardsList
      .map { getBestCombo(it) }
      .reduce { bestHand, hand -> if (hand.compareTo(bestHand) > 0) hand else bestHand }

    // Collect all players whose hands match the best hand value (ties)
    val tiedWinners = mutableListOf<HoleCards>()
    holeCardsList.forEach { holeCards ->
      val currentHand = getBestCombo(holeCards)
      if (currentHand.compareTo(bestHandValue) == 0) {
        tiedWinners.add(holeCards)
      }
    }

    return tiedWinners
  }

  /**
   * Finds the best 5-card poker hand from a player's hole cards and the community cards.
   *
   * @param hole The [HoleCards] of the player
   * @return The best [PokerHand] combination
   * @throws InvalidCardCountException If the total number of cards is not 7
   * @throws NoValidHandFoundException If no valid hand could be found
   */
  private fun getBestCombo(hole: HoleCards): PokerHand {
    val allCards = hole.cards + communityCards.cards()

    if (allCards.size != 7) {
      throw InvalidCardCountException("Total cards must be 7 (2 hole + 5 community)")
    }

    var bestCombo: PokerHand? = null
    val cardCount = 7
    val totalMasks = 1 shl cardCount

    for (mask in 0 until totalMasks) {
      if (Integer.bitCount(mask) != 5) continue

      val combo = mutableListOf<Card>()
      for (i in 0 until cardCount) {
        if ((mask shr i) and 1 == 1) combo.add(allCards[i])
      }

      val hand = PokerHand(combo)
      if (bestCombo == null || hand.compareTo(bestCombo) > 0) {
        bestCombo = hand
      }
    }

    return bestCombo ?: throw NoValidHandFoundException("No valid hand found")
  }
}

