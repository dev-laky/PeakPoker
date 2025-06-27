package hwr.oop.projects.peakpoker.core.pot

import kotlinx.serialization.Serializable

@Serializable
data class PotInfo(
    val amount: Int,
    val eligiblePlayerNames: List<String>,
)