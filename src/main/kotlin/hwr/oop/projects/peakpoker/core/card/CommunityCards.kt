package hwr.oop.projects.peakpoker.core.card

import hwr.oop.projects.peakpoker.core.game.GameInterface

class CommunityCards(
    val cards: List<Card>,
    val game: GameInterface
) : Iterable<Card> by cards {
    init {
        require(cards.isEmpty() || cards.size == 5) { "Community cards must be empty or contain exactly five cards." }
        require(cards.distinct().size == cards.size) { "Community cards must not contain duplicates." }
    }
}
