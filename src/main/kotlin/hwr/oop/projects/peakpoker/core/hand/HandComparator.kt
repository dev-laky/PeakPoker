package hwr.oop.projects.peakpoker.core.hand

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
import hwr.oop.projects.peakpoker.core.hand.HandEvaluator
import kotlin.comparisons.compareBy



// Hilfsfunktion für Straights (inkl. Wheel A-2-3-4-5)
private fun highestStraightValue(values: Set<Int>): Int? {
    // If there's an Ace (14), allow it to act as 1 for the wheel
    val ranks = if (14 in values) values + 1 else values

    // Try every possible high end from Ace (14) down to Five (5)
    for (high in 14 downTo 5) {
        // Check that high, high-1, …, high-4 are all present
        if ((high downTo high - 4).all { it in ranks }) {
            // Wheel (A-2-3-4-5) returns 5, everything else returns its high card
            return if (high == 5) 5 else high
        }
    }
    return null
}


/**
 * Vergleicht zwei High-Card-Hände per Kicker-Logik:
 * Wer an der ersten Stelle, an der sie sich unterscheiden,
 * den höheren Kartenwert hat, gewinnt.
 *
 * @return >0 wenn h1 gewinnt, <0 wenn h2 gewinnt, 0 bei exaktem Tie
 */
fun compareHighCard(
    h1: HoleCards,
    h2: HoleCards,
    community: CommunityCards
): Int {
    // 1) Alle 7 Karten pro Handwerte extrahieren und absteigend sortieren
    val k1 = (h1.cards + community.cards)
        .map { it.rank.value }
        .sortedDescending()
    val k2 = (h2.cards + community.cards)
        .map { it.rank.value }
        .sortedDescending()

    // 2) Lexikographisch vergleichen: am ersten ungleichen Wert entscheiden
    for (i in k1.indices) {
        val cmp = k1[i].compareTo(k2[i])
        if (cmp != 0) return cmp
    }
    // 3) Alles gleich → Unentschieden
    return 0
}

fun compareOnePair(
    h1: HoleCards,
    h2: HoleCards,
    community: CommunityCards
): Int {
    // 1) Alle 7 Karten pro Hand extrahieren und nach Wert absteigend sortieren
    val v1 = (h1.cards + community.cards)
        .map { it.rank.value }
        .sortedDescending()
    val v2 = (h2.cards + community.cards)
        .map { it.rank.value }
        .sortedDescending()

    // 2) Paar-Rang ermitteln (höchstes Paar, das mindestens zwei Karten hat)
    val counts1 = v1.groupingBy { it }.eachCount()
    val counts2 = v2.groupingBy { it }.eachCount()
    val pair1 = counts1.filter { it.value >= 2 }.keys.maxOrNull()!!
    val pair2 = counts2.filter { it.value >= 2 }.keys.maxOrNull()!!

    // 3) Erst nach Paar-Rang entscheiden
    if (pair1 != pair2) {
        return pair1.compareTo(pair2)
    }

    // 4) Bei gleichem Paar: die drei höchsten übrigen Karten als Kickers
    val kickers1 = v1.filter { it != pair1 }.take(3)
    val kickers2 = v2.filter { it != pair2 }.take(3)

    // 5) Lexikographischer Vergleich der Kickers
    for (i in kickers1.indices) {
        val cmp = kickers1[i].compareTo(kickers2[i])
        if (cmp != 0) return cmp
    }

    // 6) Alles gleich → Unentschieden
    return 0
}

// Two Pair: erst das höchste Paar, dann das zweite Paar, dann den Kicker
fun compareTwoPair(
    h1: HoleCards,
    h2: HoleCards,
    community: CommunityCards
): Int {
    // For a given hand, extract the two pair values (high, low) and the kicker
    fun extractTwoPairInfo(h: HoleCards): Triple<Int, Int, Int> {
        // Combine hole + community cards
        val allCards = h.cards + community.cards

        // Count how many of each rank we have
        val rankCounts = allCards.groupingBy { it.rank.value }.eachCount()

        // Find all ranks that appear at least twice, sorted descending
        val pairs = rankCounts
            .filter { it.value >= 2 }
            .keys
            .sortedDescending()

        // The top two entries are our two pair ranks
        val highPair = pairs[0]
        val lowPair  = pairs[1]

        // The kicker is the highest card *not* part of the two pairs
        val used = setOf(highPair, lowPair)
        val kicker = allCards
            .map    { it.rank.value }
            .filter { it !in used }
            .maxOrNull()!!

        return Triple(highPair, lowPair, kicker)
    }

    val (hp1, lp1, k1) = extractTwoPairInfo(h1)
    val (hp2, lp2, k2) = extractTwoPairInfo(h2)

    // Lexicographical compare: highPair, then lowPair, then kicker
    return compareValuesBy(
        Triple(hp1, lp1, k1),
        Triple(hp2, lp2, k2),
        { it.first },  // compare high pair
        { it.second }, // then low pair
        { it.third }   // then kicker
    )
}


/**
 * Compare three‐of‐a‐kind hands:
 *  1) by the rank of the trips
 *  2) if tied, by the highest remaining kicker
 *  3) if still tied, by the next kicker
 */
fun compareThreeOfAKind(
    h1: HoleCards,
    h2: HoleCards,
    community: CommunityCards
): Int {
    data class TripsInfo(val tripRank: Int, val kickers: List<Int>)

    fun extractTripsInfo(h: HoleCards): TripsInfo {
        val allCards = h.cards + community.cards
        val rankCounts = allCards.groupingBy { it.rank.value }.eachCount()
        // Find the three‐of‐a‐kind rank
        val tripRank = rankCounts.filter { it.value >= 3 }
            .keys
            .first()
        // Collect remaining cards as potential kickers
        val kickers = allCards
            .map { it.rank.value }
            .filter { it != tripRank }
            .sortedDescending()
            .take(2)
        return TripsInfo(tripRank, kickers)
    }

    val (t1, k1) = extractTripsInfo(h1)
    val (t2, k2) = extractTripsInfo(h2)

    // Compare trip ranks first
    if (t1 != t2) return t1.compareTo(t2)
    // Compare kickers lexicographically
    for (i in k1.indices) {
        if (k1[i] != k2[i]) return k1[i].compareTo(k2[i])
    }
    return 0
}

// Straight: höchster Kartenwert (5 für Wheel), or 0 if no straight
fun compareStraight(
    h1: HoleCards,
    h2: HoleCards,
    community: CommunityCards
): Int {
    // Combine hole + board, extract unique rank values
    val v1 = (h1.cards + community.cards).map { it.rank.value }.toSet()
    val v2 = (h2.cards + community.cards).map { it.rank.value }.toSet()

    // Compute highest straight (or null if none) and fall back to 0
    val s1 = highestStraightValue(v1) ?: 0
    val s2 = highestStraightValue(v2) ?: 0

    // Compare: 0 (no straight) loses to any straight ≥5
    return s1.compareTo(s2)
}

// Flush: die fünf höchsten Karten einer Farbe lex-vergleichen
fun compareFlush(
    h1: HoleCards, h2: HoleCards, community: CommunityCards
): Int {
    fun top5(h: HoleCards): List<Int> {
        val bySuit = (h.cards + community.cards).groupBy { it.suit }
        val suit   = bySuit.filter { it.value.size >= 5 }.keys.first()
        return bySuit[suit]!!.map { it.rank.value }
            .sortedDescending().take(5)
    }
    val f1 = top5(h1)
    val f2 = top5(h2)
    for (i in 0 until 5) {
        val cmp = f1[i].compareTo(f2[i])
        if (cmp != 0) return cmp
    }
    return 0
}

// Full House: erst Trips-Rang, dann höchstes Paar (aus den verbleibenden)
fun compareFullHouse(
    h1: HoleCards, h2: HoleCards, community: CommunityCards
): Int {
    fun extract(h: HoleCards): Pair<Int, Int> {
        val vals   = (h.cards + community.cards).map { it.rank.value }
        val counts = vals.groupingBy { it }.eachCount()
        val trip   = counts.filter { it.value >= 3 }.keys.maxOrNull()!!
        val pair   = counts.filter { it.value >= 2 && it.key != trip }
            .keys.maxOrNull()!!
        return trip to pair
    }
    val (t1, p1) = extract(h1)
    val (t2, p2) = extract(h2)
    if (t1 != t2) return t1.compareTo(t2)
    return p1.compareTo(p2)
}

// Four of a Kind: Vierling-Rang, dann höchster verbleibender Kicker
fun compareFourOfAKind(
    h1: HoleCards, h2: HoleCards, community: CommunityCards
): Int {
    fun extract(h: HoleCards): Pair<Int, Int> {
        val vals   = (h.cards + community.cards).map { it.rank.value }
        val counts = vals.groupingBy { it }.eachCount()
        val quad   = counts.filter { it.value >= 4 }.keys.first()
        val kick   = vals.filter { it != quad }.maxOrNull()!!
        return quad to kick
    }
    val (q1, k1) = extract(h1)
    val (q2, k2) = extract(h2)
    if (q1 != q2) return q1.compareTo(q2)
    return k1.compareTo(k2)
}

// HandComparator.kt

/**
 * Compare two players’ best straight‐flushes (including wheel and royal).
 * Returns >0 if h1 wins, <0 if h2 wins, 0 if tied or neither has one.
 */
fun compareStraightFlush(
    h1: HoleCards,
    h2: HoleCards,
    community: CommunityCards
): Int {
    // Compute the “high card” of the best straight-flush in h’s 7 cards.
    fun sfHigh(h: HoleCards): Int {
        // 1) Collect all 7 cards and group them by suit.
        val bySuit: Map<Suit, List<Card>> = (h.cards + community.cards)
            .groupBy { it.suit }

        // 2) For each suit that has at least 5 cards, try to find its best straight.
        val bestInEachSuit: List<Int> = bySuit.values
            .mapNotNull { cardsOfSuit ->
                if (cardsOfSuit.size < 5) {
                    // fewer than 5 in this suit → can't be a straight-flush
                    null
                } else {
                    // pull out the unique numeric ranks
                    val ranks = cardsOfSuit
                        .map { it.rank.value }
                        .toMutableSet()

                    // allow Ace to count low (wheel A-2-3-4-5)
                    if (Rank.ACE.value in ranks) {
                        ranks.add(0)
                    }

                    // highestStraightValue returns the top rank of any 5-card run, or null
                    highestStraightValue(ranks)
                }
            }

        // 3) Of all suits’ results, take the maximum, or 0 if none found
        return bestInEachSuit
            .maxOrNull()
            ?: 0
    }

    // Finally compare the two high-cards
    return sfHigh(h1).compareTo(sfHigh(h2))
}