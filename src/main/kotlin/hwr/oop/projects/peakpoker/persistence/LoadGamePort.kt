package hwr.oop.projects.peakpoker.persistence

import hwr.oop.projects.peakpoker.core.game.PokerGame

interface LoadGamePort {
  fun loadGame(gameId: String): PokerGame
}