package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.Player

interface GameActionable {
  fun raiseBetTo(player: Player, chips: Int)
  fun call(player: Player)
  fun check(player: Player)
  fun fold(player: Player)
  fun allIn(player: Player)
}
