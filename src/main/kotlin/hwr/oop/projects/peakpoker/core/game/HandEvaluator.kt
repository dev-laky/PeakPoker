package hwr.oop.projects.peakpoker.core.hand

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.Rank

/**
 * Evaluates a poker hand and returns its ranking.
 */
object HandEvaluator {
    /**
     * Determines the ranking of the given list of five cards.
     * @param list of five cards representing the hand
     * @return the corresponding HandRanking
     */
    fun evaluate(cards: List<Card>): HandRanking {
        // Hand must contain exactly five cards and all must be unique
        require(cards.size == 5) { "Hand must contain exactly 5 cards" }
        require(cards.toSet().size == 5) { "Hand must contain 5 unique cards" }

        // Map card ranks to their ordinal values for easier comparison
        val ranks = cards.map { it.rank.ordinal }.sorted()
        val suits = cards.map { it.suit }

        // Count occurrences of each rank
        val rankCounts = ranks.groupingBy { it }.eachCount().values.sortedDescending()

        // Check for flush (boolean): all suits equal
        val isFlush = suits.distinct().size == 1

        // Check for straight (boolean): sequence of five values or wheel straight (A-2-3-4-5)
        val isStraight = ranks.zipWithNext().all { (a, b) -> b == a + 1 }
        || ranks == listOf(0, 1, 2, 3, Rank.ACE.ordinal)

        return when {
            isStraight && isFlush && ranks.maxOrNull() == Rank.ACE.ordinal   -> HandRanking.ROYAL_FLUSH
            isStraight && isFlush                                            -> HandRanking.STRAIGHT_FLUSH
            rankCounts.first() == 4                                          -> HandRanking.FOUR_OF_A_KIND
            rankCounts.first() == 3 && rankCounts[1] == 2                    -> HandRanking.FULL_HOUSE
            isFlush                                                          -> HandRanking.FLUSH
            isStraight                                                       -> HandRanking.STRAIGHT
            rankCounts.first() == 3                                          -> HandRanking.THREE_OF_A_KIND
            rankCounts.first() == 2 && rankCounts[1] == 2                    -> HandRanking.TWO_PAIR
            rankCounts.first() == 2                                          -> HandRanking.ONE_PAIR
            else                                                             -> HandRanking.HIGH_CARD
        }
    }
}



enum class HandRanking {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIR,
    THREE_OF_A_KIND,
    STRAIGHT,
    FLUSH,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    STRAIGHT_FLUSH,
    ROYAL_FLUSH
}
