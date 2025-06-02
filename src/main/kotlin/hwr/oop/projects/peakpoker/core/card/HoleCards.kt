package hwr.oop.projects.peakpoker.core.card

import hwr.oop.projects.peakpoker.core.player.Player

class HoleCards(
    val cards: List<Card>,
    val player: Player, // TODO: Evalate if player is needed (@timwidmoser might use this)
) : Iterable<Card> by cards {
  init {
    require(cards.isEmpty() || cards.size == 2) { "Hole cards must be empty or contain exactly two cards." }
    require(cards.distinct().size == cards.size) { "Hole cards must not contain duplicates." }
  }
}
