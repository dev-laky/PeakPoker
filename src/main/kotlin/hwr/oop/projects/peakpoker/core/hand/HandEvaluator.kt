package hwr.oop.projects.peakpoker.core.hand

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.HoleCards

/**
 * Evaluates poker hands and determines winning players.
 * Replaces the singleton implementation with a class-based approach.
 */
class HandEvaluator {
  /**
   * Determines all players with the highest hand among a list of players.
   * Returns all players who are tied for the best hand to properly support split pots.
   *
   * @param holeCardsList A list of [HoleCards] representing each player's cards
   * @param community The [CommunityCards] shared by all players
   * @return List of [HoleCards] of all players tied for the highest hand
   * @throws IllegalArgumentException If the list of players is empty
   */
  fun determineHighestHand(
    holeCardsList: List<HoleCards>,
    community: CommunityCards,
  ): List<HoleCards> {
    require(holeCardsList.isNotEmpty()) { "Must provide at least one player" }

    // If only one player, they win by default
    if (holeCardsList.size == 1) return listOf(holeCardsList.first())

    // Find the best hand value among all players
    var bestHandValue: PokerHand? = null
    holeCardsList.forEach { holeCards ->
      val currentHand = getBestCombo(holeCards, community)
      if (bestHandValue == null || currentHand.compareTo(bestHandValue) > 0) {
        bestHandValue = currentHand
      }
    }

    // Collect all players whose hands match the best hand value (ties)
    val tiedWinners = mutableListOf<HoleCards>()
    holeCardsList.forEach { holeCards ->
      val currentHand = getBestCombo(holeCards, community)
      if (currentHand.compareTo(bestHandValue!!) == 0) {
        tiedWinners.add(holeCards)
      }
    }

    return tiedWinners
  }

  /**
   * Finds the best 5-card poker hand from a player's hole cards and the community cards.
   *
   * @param hole The [HoleCards] of the player
   * @param community The [CommunityCards] shared by all players
   * @return The best [PokerHand] combination
   * @throws IllegalArgumentException If the total number of cards is not 7
   * @throws IllegalStateException If no valid hand could be found
   */
  private fun getBestCombo(
    hole: HoleCards,
    community: CommunityCards,
  ): PokerHand {
    val allCards = hole.cards + community.cards

    require(allCards.size == 7) { "Total cards must be 7 (2 hole + 5 community)" }

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

    return bestCombo ?: throw IllegalStateException("No valid hand found")
  }
}

