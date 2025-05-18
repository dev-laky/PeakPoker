package hwr.oop.projects.peakpoker.core.hand

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit

/** Help function to find the highest straight value in a set of ranks.
 * Returns the highest rank of a straight (5-14), or null if no straight is possible.
 * The wheel (A-2-3-4-5) returns 5, everything else returns its high card.
 */
private fun highestStraightValue(values: Set<Int>): Int? {
    // If there's an Ace (14), allow it to act as 1 for the wheel
    val ranks = if (14 in values) values + 1 else values

    // Try every possible high end from Ace (14) down to Five (5)
    for (high in 14 downTo 5) {
        // Check that high, high-1, … , high-4 are all present
        if ((high downTo high - 4).all { it in ranks }) {
            // Wheel (A-2-3-4-5) returns 5, everything else returns its high card
            return if (high == 5) 5 else high
        }
    }
    return null
}

/**
 * Compare two poker “high card” hands using kicker logic.
 *
 * This function takes each player’s two hole cards plus the five community cards,
 * extracts all seven card ranks, sorts them in descending order, and then compares
 * the two sorted lists element by element. The first time the ranks differ determines
 * the winner (the hand with the higher rank at that position wins). If all seven
 * ranks are identical in order, the hands are an exact tie.
 *
 * @param h1        The first player’s hole cards.
 * @param h2        The second player’s hole cards.
 * @param community The five shared community cards.
 * @return          A positive integer if h1 wins, a negative integer if h2 wins,
 *                  or zero if the hands are exactly tied.
 */
fun compareHighCard(
    h1: HoleCards,
    h2: HoleCards,
    community: CommunityCards
): Int {
    // 1) Gather all seven card ranks for each hand (2 hole cards + 5 community cards)
    //    and convert them to their integer values.
    val ranks1 = (h1.cards + community.cards)
        .map { it.rank.value }
        .sortedDescending()

    val ranks2 = (h2.cards + community.cards)
        .map { it.rank.value }
        .sortedDescending()

    // 2) Perform a lexicographical comparison of the two sorted rank lists:
    //    iterate through positions 0..6 and compare corresponding ranks.
    for (i in ranks1.indices) {
        val comparison = ranks1[i].compareTo(ranks2[i])
        if (comparison != 0) {
            // As soon as one rank is higher, we know which hand wins.
            return comparison
        }
    }

    // 3) If all seven ranks are equal in order, the hands are tied.
    return 0
}

/**
* Compare two poker “one pair” hands.
*
* This function evaluates each player’s best one-pair hand from their two hole cards
* plus the five community cards. It determines the winner by:
* 1) Identifying the highest-ranking pair in each seven-card set.
* 2) Comparing the pair ranks directly—higher pair wins.
* 3) If the pairs tie, extracting the three highest remaining cards (kickers) and
*    comparing them in order until a difference is found.
* 4) Declaring a tie only if both the pair and all three kickers are identical.
*
* @param h1        The first player’s hole cards.
* @param h2        The second player’s hole cards.
* @param community The five shared community cards.
* @return          A positive integer if h1 wins, a negative integer if h2 wins,
*                  or zero if the hands are exactly tied.
*/
fun compareOnePair(
    h1: HoleCards,
    h2: HoleCards,
    community: CommunityCards
): Int {
    // 1) Extract all seven card ranks for each hand.
    val v1 = (h1.cards + community.cards)
        .map { it.rank.value }
        .sortedDescending()
    val v2 = (h2.cards + community.cards)
        .map { it.rank.value }
        .sortedDescending()

    // 2) Count how many times each rank appears.
    val counts1 = v1.groupingBy { it }.eachCount()
    val counts2 = v2.groupingBy { it }.eachCount()

    // 3) Find the highest rank that appears at least twice (the best/highest pair).
    val pair1 = counts1
        .filter { it.value >= 2 }   // keep only entries where the count is 2 or more
        .keys                       // get the set of ranks that passed the filter
        .maxOrNull()!!              // pick the largest rank, non-null asserted

    val pair2 = counts2
        .filter { it.value >= 2 }
        .keys
        .maxOrNull()!!

    // 4) Compare the pair ranks.
    if (pair1 != pair2) {
        return pair1.compareTo(pair2)
    }

    // 5) With the same pair, remove those two cards and take the next three highest as kickers.
    val kickers1 = v1.filter { it != pair1 }.take(3)    // take the next three highest -> Hand has only 5 Cards
    val kickers2 = v2.filter { it != pair2 }.take(3)

    // 6) Compare the kickers lexicographically: first kicker, then second, then third.
    for (i in kickers1.indices) {
        val cmp = kickers1[i].compareTo(kickers2[i])
        if (cmp != 0) return cmp
    }

    // 7) No differences found → tie.
    return 0
}

/**
 * Compare two poker “two pair” hands.
 *
 * Each player’s best two-pair hand is determined by:
 * 1) Identifying the two distinct pair ranks, with the higher pair first.
 * 2) Finding the highest remaining card (the kicker).
 * 3) Comparing first on the high pair, then on the low pair, and finally on the kicker.
 * 4) Declaring a tie only if all three values are identical.
 *
 * @param h1        The first player’s hole cards.
 * @param h2        The second player’s hole cards.
 * @param community The five shared community cards.
 * @return          A positive integer if h1 wins, a negative integer if h2 wins,
 *                  or zero if the hands are exactly tied.
 */
fun compareTwoPair(
    h1: HoleCards,
    h2: HoleCards,
    community: CommunityCards
): Int {
    // 1) Extract (highPair, lowPair, kicker) for one hand
    fun extractTwoPairInfo(h: HoleCards): Triple<Int, Int, Int> {
        // Combine hole cards with community cards
        val allCards = h.cards + community.cards

        // 2) Count how many times each rank appears
        val rankCounts = allCards
            .groupingBy { it.rank.value }
            .eachCount()

        // 3) Keep only ranks that appear two or more times, sorted highest first
        val pairs = rankCounts
            .filter { it.value >= 2 }
            .keys
            .sortedDescending()

        // 4) The first two elements are the high and low pair ranks
        val highPair = pairs[0]
        val lowPair  = pairs[1]

        // 5) Exclude both pair ranks and pick the highest remaining card as kicker
        val usedPairs = setOf(highPair, lowPair)
        val kicker = allCards
            .map    { it.rank.value }
            .filter { it !in usedPairs }
            .maxOrNull()!!

        return Triple(highPair, lowPair, kicker)
    }

    // 6) Decompose each hand into (highPair, lowPair, kicker)
    val (hp1, lp1, k1) = extractTwoPairInfo(h1)
    val (hp2, lp2, k2) = extractTwoPairInfo(h2)

    // 7) Compare in order: high pair -> low pair-> kicker
    return compareValuesBy(
        Triple(hp1, lp1, k1),
        Triple(hp2, lp2, k2),
        { it.first },   // high pair
        { it.second },             // low pair
        { it.third }               // kicker
    )
}

/**
 * Compare two poker “three of a kind” hands.
 *
 * 1) Identify the rank of the three‐of‐a‐kind in each seven‐card set.
 * 2) Compare the trip ranks; the higher rank wins immediately.
 * 3) If the trip ranks tie, extract the two highest side cards (kickers).
 * 4) Compare the first kicker; if they differ, the higher wins.
 * 5) If the first kickers tie, compare the second kicker.
 * 6) Declare a tie only if both kickers are identical.
 *
 * @param h1        The first player’s hole cards.
 * @param h2        The second player’s hole cards.
 * @param community The five shared community cards.
 * @return          A positive integer if h1 wins, a negative integer if h2 wins,
 *                  or zero if the hands are exactly tied.
 */
fun compareThreeOfAKind(
    h1: HoleCards,
    h2: HoleCards,
    community: CommunityCards
): Int {
    data class TripsInfo(val tripRank: Int, val kickers: List<Int>)

    // Extracts the triplets rank and kickers for one hand
    fun extractTripsInfo(h: HoleCards): TripsInfo {
        // 1) Combine hole cards and community cards
        val allCards = h.cards + community.cards

        // 2) Count occurrences of each rank and find the rank with at least three cards
        val rankCounts = allCards.groupingBy { it.rank.value }.eachCount()
        val tripRank = rankCounts
            .filter { it.value >= 3 }
            .keys
            .first()

        // 3) Remove the trip cards and take the two highest remaining as kickers
        val kickers = allCards
            .map { it.rank.value }
            .filter { it != tripRank }
            .sortedDescending()
            .take(2)

        return TripsInfo(tripRank, kickers)
    }

    // 4) Extract info for both hands
    val (t1, k1) = extractTripsInfo(h1)
    val (t2, k2) = extractTripsInfo(h2)

    // 5) Compare the trip ranks
    if (t1 != t2) {
        return t1.compareTo(t2)
    }

    // 6) Compare kickers in order
    for (i in k1.indices) {
        if (k1[i] != k2[i]) {
            return k1[i].compareTo(k2[i])
        }
    }

    // 7) All values equal -> tie
    return 0
}

/**
 * Compare two poker “straight” hands.
 *
 * 1) Extract each player’s distinct rank set from their hole cards plus the community.
 * 2) Compute the highest straight present (using 5 for the wheel A-2-3-4-5), or 0 if none.
 * 3) A value of 0 loses to any straight (which is always ≥5).
 * 4) Return >0 if h1’s straight outranks h2’s, <0 if h2’s wins, or 0 if they tie.
 *
 * @param h1        The first player’s hole cards.
 * @param h2        The second player’s hole cards.
 * @param community The five shared community cards.
 * @return          Positive if h1 wins, negative if h2 wins, zero on tie.
 */
fun compareStraight(
    h1: HoleCards,
    h2: HoleCards,
    community: CommunityCards
): Int {
    // 1) Build the set of unique ranks for each hand (duplicates don’t matter for straights)
    val ranks1 = (h1.cards + community.cards)
        .map { it.rank.value }
        .toSet()
    val ranks2 = (h2.cards + community.cards)
        .map { it.rank.value }
        .toSet()

    // 2) Determine the highest straight value (5 for A-2-3-4-5), or null if no straight
    val straight1 = highestStraightValue(ranks1)  // returns 5 to 14 or null -> See helper function @top
    val straight2 = highestStraightValue(ranks2)

    // 3) Treat “no straight” as 0 so that any real straight (≥5) beats it
    val s1 = straight1 ?: 0
    val s2 = straight2 ?: 0

    // 4) Compare the two straight values directly
    return s1.compareTo(s2)
}

/**
 * Compare two poker “flush” hands.
 *
 * 1) Identify if each hand has a flush (five cards of the same suit).
 * 2) For that suit, take the five highest cards.
 * 3) Compare those five cards lexicographically.
 * 4) Return >0 if h1’s flush outranks h2’s, <0 if h2’s wins, or 0 if they tie.
 *
 * @param h1        The first player’s hole cards.
 * @param h2        The second player’s hole cards.
 * @param community The five shared community cards.
 * @return          Positive if h1 wins, negative if h2 wins, zero on tie.
 */
fun compareFlush(
    h1: HoleCards,
    h2: HoleCards,
    community: CommunityCards
): Int {
    // Extract the top five ranks of the flush suit for one hand
    fun top5FlushRanks(h: HoleCards): List<Int> {
        // 1) Combine hole + community cards and group them by suit
        val bySuit = (h.cards + community.cards).groupBy { it.suit }

        // 2) Find the suit that has at least five cards (the flush suit)
        val flushSuit = bySuit
            .filter { it.value.size >= 5 }
            .keys
            .first()

        // 3) From that suit’s cards, extract their rank values, sort descending, and take the top five
        return bySuit[flushSuit]!!       // 3.1) Get the List<Card> for the chosen flush suit (we know there are ≥5 cards), -> !! non-null assert
            .map { it.rank.value }       // 3.2) Transform each Card into its numeric rank (ex. KING -> 13)
            .sortedDescending()          // 3.3) Sort those rank integers from highest to lowest
            .take(5)                 // 3.4) Select the first five entries—that gives the five highest flush cards
    }

    // 4) Get the top-5 flush ranks for both players
    val f1 = top5FlushRanks(h1)
    val f2 = top5FlushRanks(h2)

    // 5) Compare the flush cards one by one
    for (i in 0 until 5) {
        val cmp = f1[i].compareTo(f2[i])
        if (cmp != 0) {
            return cmp
        }
    }

    // 6) If all five ranks are the same, it's a tie
    return 0
}

/**
 * Compare two poker “full house” hands.
 *
 * 1) Identify the rank of the three‐of‐a‐kind in the full house.
 * 2) Identify the rank of the highest remaining pair.
 * 3) Compare the trip ranks; the higher wins.
 * 4) If trip ranks tie, compare the pair ranks; the higher wins.
 * 5) If both match, it’s a tie.
 *
 * @param h1        The first player’s hole cards.
 * @param h2        The second player’s hole cards.
 * @param community The five shared community cards.
 * @return          Positive if h1 wins, negative if h2 wins, zero on tie.
 */
fun compareFullHouse(
    h1: HoleCards,
    h2: HoleCards,
    community: CommunityCards
): Int {
    // 1) Extract (tripRank, pairRank) for one hand
    fun extract(h: HoleCards): Pair<Int, Int> {
        // 1.1) Gather all seven ranks
        val vals = (h.cards + community.cards)
            .map { it.rank.value }

        // 1.2) Count occurrences of each rank
        val counts = vals
            .groupingBy { it }
            .eachCount()

        // 1.3) Find the highest rank with at least three cards (the trips)
        val trip = counts
            .filter { it.value >= 3 }
            .keys
            .maxOrNull()!!

        // 1.4) From the remaining ranks, find the highest rank with at least two cards (the pair)
        val pair = counts
            .filter { it.value >= 2 && it.key != trip }
            .keys
            .maxOrNull()!!

        // 1.5) Return both as a Pair(tripRank, pairRank)
        return trip to pair
    }

    // 2) Decompose both hands
    val (t1, p1) = extract(h1)
    val (t2, p2) = extract(h2)

    // 3) Compare the trip ranks
    if (t1 != t2) {
        return t1.compareTo(t2)
    }

    // 4) Trip ranks tied → compare the pair ranks
    return p1.compareTo(p2)
}

/**
 * Compare two poker “four of a kind” hands.
 *
 * 1) Identify the rank of the four‐of‐a‐kind in each seven‐card set.
 * 2) Identify the highest remaining card (the kicker).
 * 3) Compare the quad ranks; the higher wins.
 * 4) If quad ranks tie, compare the kicker.
 * 5) Declare a tie only if both values are identical.
 *
 * @param h1        The first player’s hole cards.
 * @param h2        The second player’s hole cards.
 * @param community The five shared community cards.
 * @return          Positive if h1 wins, negative if h2 wins, zero on tie.
 */
fun compareFourOfAKind(
    h1: HoleCards,
    h2: HoleCards,
    community: CommunityCards
): Int {
    data class QuadsInfo(val quadRank: Int, val kicker: Int)

    // Extracts the quad rank and kicker for one hand
    fun extract(h: HoleCards): QuadsInfo {
        // 1) Combine hole cards and community cards into a list of rank values
        val allRanks = (h.cards + community.cards)
            .map { it.rank.value }

        // 2) Count how many times each rank appears
        val counts = allRanks
            .groupingBy { it }
            .eachCount()

        // 3) Find the rank that appears at least four times (the quads)
        val quadRank = counts
            .filter { it.value >= 4 }
            .keys
            .first()

        // 4) Remove any cards of the quad rank and pick the highest remaining as kicker
        val kicker = allRanks
            .filter { it != quadRank }
            .maxOrNull()!!

        // 5) Return both as a simple pair
        return QuadsInfo(quadRank, kicker)
    }

    // 6) Extract quad+kick info for both players
    val (q1, k1) = extract(h1)
    val (q2, k2) = extract(h2)

    // 7) Compare the quad ranks
    if (q1 != q2) {
        return q1.compareTo(q2)
    }

    // 8) Quad ranks tied → compare the kicker
    return k1.compareTo(k2)
}

/**
 * Compare two players’ best straight‐flush hands.
 *
 * 1) Group each player’s seven cards by suit.
 * 2) For each suit with at least five cards, compute the highest straight (wheel allowed).
 * 3) From those suit‐specific results, pick the maximum high‐card (or 0 if none).
 * 4) Compare the two high‐cards: larger wins, equal means tie or neither has one.
 *
 * @param h1        The first player’s hole cards.
 * @param h2        The second player’s hole cards.
 * @param community The five shared community cards.
 * @return          Positive if h1 wins, negative if h2 wins, zero if tied or neither has a straight‐flush.
 */
fun compareStraightFlush(
    h1: HoleCards,
    h2: HoleCards,
    community: CommunityCards
): Int {
    // Extract the high‐card of the best straight‐flush for one hand
    fun sfHigh(h: HoleCards): Int {
        // 1) Group all seven cards by suit
        val bySuit: Map<Suit, List<Card>> = (h.cards + community.cards)
            .groupBy { it.suit }

        // 2) For each suit with ≥5 cards, compute that suit’s best straight
        val bestInEachSuit: List<Int> = bySuit.values
            .mapNotNull { cardsOfSuit ->
                // 2.1) Skip if fewer than 5 cards of this suit
                if (cardsOfSuit.size < 5) return@mapNotNull null

                // 2.2) Extract unique rank values for this suit
                val ranks = cardsOfSuit
                    .map { it.rank.value }
                    .toMutableSet()

                // 2.3) Allow Ace to act as 1 for a wheel straight (A-2-3-4-5)
                if (Rank.ACE.value in ranks) {
                    ranks.add(1)
                }

                // 2.4) Return the top straight‐run value, or null if none
                highestStraightValue(ranks)
            }

        // 3) From all suits’ results, pick the maximum, or 0 if no straight‐flush found
        return bestInEachSuit
            .maxOrNull()
            ?: 0
    }

    // 4) Compare the two players’ straight‐flush high‐cards
    return sfHigh(h1).compareTo(sfHigh(h2))
}
