package hwr.oop.projects.peakpoker.core.hand

import hwr.oop.projects.peakpoker.core.card.Card

/**
 * Represents a poker hand consisting of exactly 5 cards.
 * Provides functionality to evaluate and compare poker hands.
 *
 * @property cards The list of cards that make up the hand
 */
class PokerHand(private val cards: List<Card>) : Iterable<Card> by cards {
  /**
   * Exception thrown when a poker hand does not contain exactly 5 cards
   */
  class InvalidHandSizeException(message: String) : Exception(message)

  /**
   * Exception thrown when a poker hand contains duplicate cards
   */
  class DuplicateCardException(message: String) : Exception(message)

  private val rank: HandRank

  init {
    if (cards.size != 5) {
      throw InvalidHandSizeException("Hand must contain exactly 5 cards")
    }

    if (cards.distinct().size != 5) {
      throw DuplicateCardException("Hand must contain 5 unique cards")
    }

    // Evaluate the rank of the hand upon initialization
    rank = evaluate()
  }

  /**
   * Evaluates the rank of this poker hand based on standard poker hand rankings.
   *
   * Determines the poker hand rank by analyzing card patterns such as:
   * Royal Flush, Straight Flush, Four of a Kind, Full House, etc.
   *
   * @return The [HandRank] of the evaluated hand
   */
  private fun evaluate(): HandRank {

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

  /**
   * Compares this poker hand to another poker hand to determine which is stronger.
   *
   * @param otherHand The other poker hand to compare against
   * @return A positive integer if this hand is stronger, negative if the other hand is stronger,
   *         or zero if they are equal
   */
  fun compareTo(otherHand: PokerHand): Int {
    if (rank != otherHand.rank) {
      return rank.rank.compareTo(otherHand.rank.rank)
    }

    val v1 = cards.map { it.rank.value }.sortedDescending()
    val v2 = otherHand.cards.map { it.rank.value }.sortedDescending()

    for (i in v1.indices) {
      val cmp = v1[i].compareTo(v2[i])
      if (cmp != 0) return cmp
    }

    return 0
  }
}