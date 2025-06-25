package hwr.oop.projects.peakpoker.core.game

import kotlinx.serialization.Serializable

@Serializable
enum class RoundPhase {
  PRE_FLOP, FLOP, TURN, RIVER, SHOWDOWN
}