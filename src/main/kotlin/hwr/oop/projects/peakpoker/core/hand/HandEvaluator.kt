package hwr.oop.projects.peakpoker.core.hand

import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.HoleCards

object HandEvaluator {
    internal fun evaluate(hand: HoleCards, communityCards: CommunityCards): HandRank {
        TODO("Combine hole cards and community cards, evaluate the hand, and return the HandRank")
    }

    fun getHighestHandRank(hands: List<HoleCards>, communityCards: CommunityCards): HoleCards {
        return hands.maxByOrNull { hand ->
            evaluate(hand, communityCards).ordinal
        } ?: throw IllegalStateException("No hands to evaluate")
    }
}
