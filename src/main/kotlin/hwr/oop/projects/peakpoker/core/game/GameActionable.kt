package hwr.oop.projects.peakpoker.core.game

import kotlinx.serialization.Serializable

@Serializable
sealed interface GameActionable {
  fun raiseBetTo(playerName: String, chips: Int)
  fun call(playerName: String)
  fun check(playerName: String)
  fun fold(playerName: String)
  fun allIn(playerName: String)
}
