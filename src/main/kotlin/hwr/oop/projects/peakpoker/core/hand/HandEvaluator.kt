package hwr.oop.projects.peakpoker.core.hand

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.HoleCards

object HandEvaluator {

    fun determineHighestHand(HoleCardsList: List<HoleCards>, community: CommunityCards): HoleCards {
        TODO("Implement")
    }

    fun evaluate(cards: List<Card>): HandRank {
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
            return rank1.rank.compareTo(rank2.rank)
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

