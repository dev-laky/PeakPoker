package hwr.oop.projects.peakpoker.core.game

import kotlinx.serialization.Serializable

@Serializable
data class GameInfo(
  val gameId: String,
  val hasEnded: Boolean,
  val roundInfo: RoundInfo?,
)