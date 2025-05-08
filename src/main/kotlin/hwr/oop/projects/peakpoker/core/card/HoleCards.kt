package hwr.oop.projects.peakpoker.core.card

import hwr.oop.projects.peakpoker.core.player.PlayerInterface

class HoleCards(
    val cards: List<Card>,
    val player: PlayerInterface
) : Iterable<Card> by cards {
    init {
        require(cards.isEmpty() || cards.size == 2) { "Hole cards must contain exactly two cards." }
        require(cards.distinct().size == cards.size) { "Hole cards must not contain duplicates." }
    }
}
