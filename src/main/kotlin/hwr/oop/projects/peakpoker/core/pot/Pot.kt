package hwr.oop.projects.peakpoker.core.pot

import hwr.oop.projects.peakpoker.core.player.PokerPlayer

data class Pot(
  val amount: Int = 0,
  val eligiblePlayers: Set<PokerPlayer> = setOf(),
)