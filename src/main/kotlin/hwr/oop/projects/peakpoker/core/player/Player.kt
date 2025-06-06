package hwr.oop.projects.peakpoker.core.player

import hwr.oop.projects.peakpoker.core.card.HoleCards

// This Interface is being used to mock the PokerPlayer class in the tests.
interface Player {
  val name: String

  fun isFolded(): Boolean
  fun isAllIn(): Boolean
  fun hasChecked(): Boolean

  fun chips(): Int
  fun hand(): HoleCards
  fun bet(): Int

  fun resetRoundState()
  fun resetBet()

  fun assignHand(cards: HoleCards)
  fun setBetAmount(chips: Int)

  fun check()
  fun fold()
  fun allIn()
}
