package hwr.oop.projects.peakpoker.persistence

import hwr.oop.projects.peakpoker.core.game.PokerGame
import kotlinx.serialization.Serializable

@Serializable
data class GameStorage(
  val games: Map<String, PokerGame> = emptyMap(),
)