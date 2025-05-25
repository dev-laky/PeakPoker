package hwr.oop.projects.peakpoker.core.hand

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.HoleCards

object HandEvaluator {
    /**
     * Evaluates the best HandRank from exactly five cards.
     */
    fun evaluate(cards: List<Card>): HandRank {
        require(cards.size == 5) { "Hand must contain exactly 5 cards" }
        require(cards.distinct().size == 5) { "Hand must contain 5 unique cards" }

        val ranks = cards.map { it.rank }
        val suits = cards.map { it.suit }
        val rankCounts = ranks.groupingBy { it }.eachCount().values.sortedDescending()
        val isFlush = suits.distinct().size == 1

        val values = ranks.map { it.value }.sorted()
        val isWheel = values == listOf(1, 2, 3, 4, 13)
        val isSequential = values.zipWithNext().all { (a, b) -> b == a + 1 }
        val isStraight = isWheel || isSequential
        val isRoyal = values == listOf(9, 10, 11, 12, 13)

        return when {
            isRoyal && isFlush -> HandRank.ROYAL_FLUSH
            isStraight && isFlush -> HandRank.STRAIGHT_FLUSH
            rankCounts[0] == 4 -> HandRank.FOUR_OF_A_KIND
            rankCounts[0] == 3 && rankCounts[1] == 2 -> HandRank.FULL_HOUSE
            isFlush -> HandRank.FLUSH
            isStraight -> HandRank.STRAIGHT
            rankCounts[0] == 3 -> HandRank.THREE_OF_A_KIND
            rankCounts[0] == 2 && rankCounts[1] == 2 -> HandRank.TWO_PAIR
            rankCounts[0] == 2 -> HandRank.ONE_PAIR
            else -> HandRank.HIGH_CARD
        }
    }

    fun evaluateAll(hole: HoleCards, community: CommunityCards): HandRank {
        return evaluate(getBestCombo(hole, community))
    }

    fun compareHands(h1: List<Card>, h2: List<Card>): Int {
        val rank1 = evaluate(h1)
        val rank2 = evaluate(h2)

        if (rank1 != rank2) {
            return rank1.ordinal.compareTo(rank2.ordinal)
        }

        val v1 = h1.map { it.rank.value }.sortedDescending()
        val v2 = h2.map { it.rank.value }.sortedDescending()

        for (i in v1.indices) {
            val cmp = v1[i].compareTo(v2[i])
            if (cmp != 0) return cmp
        }

        return 0
    }

    fun getBestCombo(hole: HoleCards, community: CommunityCards): List<Card> {
        val allCards = hole.cards + community.cards
        require(allCards.size == 7) { "Total cards must be 7 (2 hole + 5 community)" }

        var bestCombo: List<Card>? = null
        var bestRank = HandRank.HIGH_CARD
        val n = allCards.size
        val totalMasks = 1 shl n

        for (mask in 0 until totalMasks) {
            if (Integer.bitCount(mask) == 5) {
                val combo = mutableListOf<Card>()
                for (i in 0 until n) {
                    if ((mask shr i) and 1 == 1) combo += allCards[i]
                }
                val rank = evaluate(combo)
                if (rank.ordinal > bestRank.ordinal) {
                    bestRank = rank
                    bestCombo = combo
                } else if (rank.ordinal == bestRank.ordinal) {
                    if (bestCombo == null || compareHands(combo, bestCombo) > 0) {
                        bestCombo = combo
                    }
                }
            }
        }
        return bestCombo ?: emptyList()
    }

    fun getHighestHandRank(hands: List<HoleCards>, community: CommunityCards): HoleCards {
        return hands.maxByOrNull { hand ->
            evaluateAll(hand, community).ordinal
        } ?: throw IllegalStateException("No hands to evaluate")
    }

}

