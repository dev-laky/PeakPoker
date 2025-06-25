package hwr.oop.projects.peakpoker.core.game

import kotlinx.serialization.Serializable

@Serializable
data class GameId(val value: String) {
  companion object {
    fun generate(): GameId {
      return GameId(java.util.UUID.randomUUID().toString())
    }
  }
}