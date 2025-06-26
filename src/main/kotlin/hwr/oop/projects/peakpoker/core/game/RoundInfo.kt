package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.player.PlayerInfo
import hwr.oop.projects.peakpoker.core.pot.PotInfo
import kotlinx.serialization.Serializable

@Serializable
data class RoundInfo(
  val smallBlindAmount: Int,
  val bigBlindAmount: Int,
  val players: List<PlayerInfo>,
  val smallBlindPlayerName: String,
  val roundPhase: RoundPhase,
  val communityCards: List<Card>,
  val pots: List<PotInfo>,
  val currentPlayerName: String,
)