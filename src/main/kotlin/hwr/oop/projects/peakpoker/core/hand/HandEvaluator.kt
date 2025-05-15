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

        val ranks = cards.map { it.rank }.sorted() //omitted it.rank.ordinal
        val suits = cards.map { it.suit }
        val rankCounts = ranks.groupingBy { it }.eachCount().values.sortedDescending()
        val isFlush = suits.distinct().size == 1
        val isStraight = when {
            ranks.zipWithNext().all { (a, b) -> b.value == a.value + 1 } -> true
            // After sorting, pairs of consecutive ranks (e.g., (1,2), (2,3), ...) are checked to confirm each card is a direct successor.
            // This determines a straight, with a special case for Ace (ordinal 14) as the lowest card.
            ranks == listOf(
                Rank.TWO.ordinal,
                Rank.THREE.ordinal,
                Rank.FOUR.ordinal,
                Rank.FIVE.ordinal,
                Rank.ACE.ordinal
            ) -> true
            else -> false
        }

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
                                                        //Bitmasking method
        var bestRank = HandRank.HIGH_CARD               //default
        val n = allCards.size                           //will be 7 anyway
        val totalMasks = 1 shl n                        // 128 = 2^7 (7 = number of cards) -> possible combinations to choose from and evaluate
        for (mask in 0 until totalMasks) {
            if (Integer.bitCount(mask) == 5) {      //whenever we are looking at a combination of 5 cards (when 5 bits are set to 1) ->(7 over 5 = 21 valid combinations)
                val combo = mutableListOf<Card>()
                for (i in 0 until n) {
                    if (mask shr i and 1 == 1) combo += allCards[i] //if the i-th bit is set to 1, add the i-th card to the combination
                }                                                   // shr looks at the bits of the number and shifts them to the right in this case 1 at a time
                val rank = evaluate(combo)                   // since we extracted it now, we can use our eval function again easily
                if (rank.ordinal > bestRank.ordinal) {              // checking the enum
                    bestRank = rank
                }
            }
        }
        return bestRank
    }

    /**
     * Determines the HoleCards with the strongest 5-card HandRank
     * by evaluating all possible combinations with the community cards.
     *
     * @param hands The list of HoleCards to evaluate.
     * @param community The CommunityCards shared by all players.
     * @return The HoleCards that form the highest-ranked hand.
     * @throws IllegalStateException if no hands are provided.
     */
    fun getHighestHandRank(hands: List<HoleCards>, community: CommunityCards): HoleCards {
        return hands.maxByOrNull { hand ->
            evaluateAll(hand, community).rank
        } ?: throw IllegalStateException("No hands to evaluate")
    }
}
