package hwr.oop.projects.peakpoker.core.hand

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.hand.HandRank


object HandEvaluator {

    /**
     * Evaluates the best HandRank from exactly five cards.
     */
    internal fun evaluate(cards: List<Card>): HandRank {
        require(cards.size == 5) { "Hand must contain exactly 5 cards" }
        require(cards.distinct().size == 5) { "Hand must contain 5 unique cards" }

        val ranks = cards.map { it.rank }.sorted() // omitted it.rank.ordinal
        val suits = cards.map { it.suit }
        val rankCounts = ranks.groupingBy { it }.eachCount().values.sortedDescending()
        val isFlush = suits.distinct().size == 1

        // Straight detection (including wheel A-2-3-4-5)
        val sortedValues = ranks.map { it.value }.sorted()
        val isSequential = sortedValues.zipWithNext().all { (a, b) -> b == a + 1 }
        // special case: 2,3,4,5,A (where A has value==13)
        val isWheel = sortedValues == listOf(
            Rank.TWO.value,
            Rank.THREE.value,
            Rank.FOUR.value,
            Rank.FIVE.value,
            Rank.ACE.value
        )
        val isStraight = isSequential || isWheel

        return when {
            isStraight && isFlush && ranks.maxOrNull() == Rank.ACE         -> HandRank.ROYAL_FLUSH
            isStraight && isFlush                                          -> HandRank.STRAIGHT_FLUSH
            rankCounts[0] == 4                                             -> HandRank.FOUR_OF_A_KIND
            rankCounts[0] == 3 && rankCounts[1] == 2                       -> HandRank.FULL_HOUSE
            isFlush                                                        -> HandRank.FLUSH
            isStraight                                                     -> HandRank.STRAIGHT
            rankCounts[0] == 3                                             -> HandRank.THREE_OF_A_KIND
            rankCounts[0] == 2 && rankCounts[1] == 2                       -> HandRank.TWO_PAIR
            rankCounts[0] == 2                                             -> HandRank.ONE_PAIR
            else                                                           -> HandRank.HIGH_CARD
        }
    }

    /**
     * Combines hole cards and community cards, evaluates all 5-card combinations,
     * and returns the highest HandRank.
     */
    fun evaluateAll(hole: HoleCards, community: CommunityCards): HandRank {
        val allCards = hole.cards + community.cards
        require(allCards.size == 7) { "Total cards must be 7 (2 hole + 5 community)" }

        var bestRank = HandRank.HIGH_CARD               // default
        val n = allCards.size                           // will be 7 anyway
        val totalMasks = 1 shl n                        // 128 = 2^7 -> possible subsets
        for (mask in 0 until totalMasks) {
            if (Integer.bitCount(mask) == 5) {          // look at every 5-card combo
                val combo = mutableListOf<Card>()
                for (i in 0 until n) {
                    if (mask shr i and 1 == 1) combo += allCards[i]
                }
                val rank = evaluate(combo)
                if (rank.ordinal > bestRank.ordinal) {
                    bestRank = rank
                }
            }
        }
        return bestRank
    }

    /**
     * Determines the HoleCards with the strongest 5-card HandRank
     * by evaluating all possible combinations with the community cards.
     */
    fun getHighestHandRank(hands: List<HoleCards>, community: CommunityCards): HoleCards {
        return hands.maxByOrNull { hand ->
            evaluateAll(hand, community).ordinal
        } ?: throw IllegalStateException("No hands to evaluate")
    }
}
