package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer

interface GameActionable {
  fun raiseBetTo(playerName: String, chips: Int)
  fun call(playerName: String)
  fun check(playerName: String)
  fun fold(playerName: String)
  fun allIn(playerName: String)
}
