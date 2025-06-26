package hwr.oop.projects.peakpoker.core.player

import kotlinx.serialization.Serializable

@Serializable
data class PlayerInfo(
  val name: String,
  val chips: Int,
  val bet: Int,
  val isFolded: Boolean,
  val isAllIn: Boolean,
)