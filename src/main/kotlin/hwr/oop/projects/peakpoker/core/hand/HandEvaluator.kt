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
     * Determines the player with the highest hand among a list of players.
     *
     * @param holeCardsList A list of [HoleCards] representing each player's cards
     * @param community The [CommunityCards] shared by all players
     * @return The [HoleCards] of the player with the highest hand
     * @throws IllegalArgumentException If the list of players is empty
     */
    fun determineHighestHand(holeCardsList: List<HoleCards>, community: CommunityCards): HoleCards {
        require(holeCardsList.isNotEmpty()) { "Must provide at least one player" }

        var bestPlayerHand = holeCardsList.first()
        var bestHand = getBestCombo(bestPlayerHand, community)

        holeCardsList.drop(1).forEach { player ->
            val currentHand = getBestCombo(player, community)
            if (currentHand.compareTo(bestHand) > 0) {
                bestHand = currentHand
                bestPlayerHand = player
            }
        }
        return bestPlayerHand
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
    private fun getBestCombo(hole: HoleCards, community: CommunityCards): PokerHand {
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

