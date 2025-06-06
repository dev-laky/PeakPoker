package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer

interface GameActionable {
  fun raiseBetTo(player: PokerPlayer, chips: Int)
  fun call(player: PokerPlayer)
  fun check(player: PokerPlayer)
  fun fold(player: PokerPlayer)
  fun allIn(player: PokerPlayer)
}
