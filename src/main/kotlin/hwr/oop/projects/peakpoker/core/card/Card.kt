package hwr.oop.projects.peakpoker.core.card

import kotlinx.serialization.Serializable

@Serializable
data class Card(val suit: Suit, val rank: Rank)
