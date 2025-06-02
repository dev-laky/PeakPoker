package hwr.oop.projects.peakpoker.core.hand

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.HoleCards

object HandEvaluator {


    /**
     * Determines the player with the highest hand among a list of players.
     *
     * @param holeCardsList A list of `HoleCards` representing each player's cards.
     * @param community The `CommunityCards` shared by all players.
     * @return The `HoleCards` of the player with the highest hand.
     * @throws IllegalArgumentException If the list of players is empty.
     */
    fun determineHighestHand(holeCardsList: List<HoleCards>, community: CommunityCards): HoleCards {
        require(holeCardsList.isNotEmpty()) { "Must provide at least one player" }

        var bestPlayerHand = holeCardsList.first()
        var bestHand = getBestCombo(bestPlayerHand, community)

        for (player in holeCardsList.drop(1)) {
            val currentHand = getBestCombo(player, community)
            if (compareHands(currentHand, bestHand) > 0) {
                bestHand = currentHand
                bestPlayerHand = player
            }
        }
        return bestPlayerHand
    }

    /**
     * Evaluates the rank of a given hand of cards.
     *
     * @param cards A list of `Card` objects representing the hand to evaluate.
     * @return The `HandRank` of the evaluated hand.
     * @throws IllegalArgumentException If the hand does not contain exactly 5 unique cards.
     */
    private fun evaluate(cards: List<Card>): HandRank {
        require(cards.size == 5) { "Hand must contain exactly 5 cards" }
        require(cards.distinct().size == 5) { "Hand must contain 5 unique cards" }

        val ranks = cards.map { it.rank }
        val suits = cards.map { it.suit }
        val rankCounts =
            ranks.groupingBy { it }.eachCount().values.sortedDescending()
        val isFlush = suits.distinct().size == 1

        val values = ranks
            .sortedBy { it.value }
            .map { it.value }
        val isWheel = values == listOf(1, 2, 3, 4, 13)
        val isSequential = values.zipWithNext().all { (a, b) -> b == a + 1 }
        val isStraight = isWheel || isSequential
        val isRoyal = values == listOf(9, 10, 11, 12, 13)

        return when {
            isRoyal && isFlush                          -> HandRank.ROYAL_FLUSH
            isStraight && isFlush                       -> HandRank.STRAIGHT_FLUSH
            rankCounts[0] == 4                          -> HandRank.FOUR_OF_A_KIND
            rankCounts[0] == 3 && rankCounts[1] == 2    -> HandRank.FULL_HOUSE
            isFlush                                     -> HandRank.FLUSH
            isStraight                                  -> HandRank.STRAIGHT
            rankCounts[0] == 3                          -> HandRank.THREE_OF_A_KIND
            rankCounts[0] == 2 && rankCounts[1] == 2    -> HandRank.TWO_PAIR
            rankCounts[0] == 2                          -> HandRank.ONE_PAIR
            else                                        -> HandRank.HIGH_CARD
        }
    }

    /**
     * Compares two hands of cards to determine which is stronger.
     *
     * @param hand1 A list of `Card` objects representing the first hand.
     * @param hand2 A list of `Card` objects representing the second hand.
     * @return An integer: positive if `h1` is stronger, negative if `h2` is stronger, or zero if they are equal.
     */
   private fun compareHands(hand1: List<Card>, hand2: List<Card>): Int {
        val rank1 = evaluate(hand1)
        val rank2 = evaluate(hand2)

        if (rank1 != rank2) {
            return rank1.rank.compareTo(rank2.rank)
        }

        val v1 = hand1.map { it.rank.value }.sortedDescending()
        val v2 = hand2.map { it.rank.value }.sortedDescending()

        for (i in v1.indices) {
            val cmp = v1[i].compareTo(v2[i])
            if (cmp != 0) return cmp
        }

        return 0
    }


    /**
     * Finds the best combination of 5 cards from a player's hole cards and the community cards.
     *
     * @param hole The `HoleCards` of the player.
     * @param community The `CommunityCards` shared by all players.
     * @return A list of `Card` objects representing the best combination of 5 cards.
     * @throws IllegalArgumentException If the total number of cards is not 7.
     */
    private fun getBestCombo(hole: HoleCards, community: CommunityCards): List<Card> {
        val allCards = hole.cards + community.cards
        require(allCards.size == 7) { "Total cards must be 7 (2 hole + 5 community)" }

        var bestCombo: List<Card> = emptyList()
        var bestRank = HandRank.HIGH_CARD
        val cardCount = 7
        val totalMasks = 1 shl cardCount

        for (mask in 0 until totalMasks) {
            if (Integer.bitCount(mask) != 5) {
                continue
            }

            val combo = mutableListOf<Card>()
            for (i in 0 until cardCount) {
                if ((mask shr i) and 1 == 1) combo.add(allCards[i])
            }

            val handRank = evaluate(combo)

            if (handRank.rank > bestRank.rank) {
                bestRank = handRank
                bestCombo = combo
                continue
            }

            if (handRank.rank == bestRank.rank) {
                if (bestCombo.isEmpty() || compareHands(combo, bestCombo) > 0) {
                    bestCombo = combo
                    continue
                }
            }
        }

        return bestCombo
    }
}

