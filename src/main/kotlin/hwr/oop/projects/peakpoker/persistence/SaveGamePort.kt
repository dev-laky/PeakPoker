package hwr.oop.projects.peakpoker.persistence

import hwr.oop.projects.peakpoker.core.game.GameId
import hwr.oop.projects.peakpoker.core.game.PokerGame

fun interface SaveGamePort {
  fun saveGame(game: PokerGame): GameId
}