package hwr.oop.projects.peakpoker.core.pot

import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.hand.HandEvaluator
import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Pot(
    val eligiblePlayers: Set<PokerPlayer>,
    @Transient val communityCards: CommunityCards = CommunityCards(),
    private var amount: Int = 0,
) {
    /**
     * Custom exception thrown when attempting to remove more chips than are in the pot.
     */
    class InsufficientPotAmountException(message: String) : IllegalStateException(message)

    @Transient
    private val handEvaluator: HandEvaluator = HandEvaluator(communityCards)

    /**
     * Adds the specified amount of chips to the pot.
     *
     *
     * @param chips The number of chips to add to the pot
     */
    fun addChips(chips: Int) {
        amount += chips
    }

    fun removeChips(chips: Int) {
        if (chips > amount) {
            throw InsufficientPotAmountException("Cannot remove more chips than are in the pot")
        }
        amount -= chips
    }

    fun amount(): Int {
        return amount
    }

    /**
     * Payouts the winnings from a pot to the appropriate players.
     *
     * It determines the eligible players (those who haven't folded), evaluates
     * their hands, and distributes the pot amount among the winners:
     *
     * - If there is a single winner, they receive the entire pot amount
     * - If there are multiple winners with equivalent hands, the pot is split evenly
     * - If the pot cannot be split evenly, the remainder is given to the first winner
     *
     * The method relies on the [HandEvaluator] to determine the highest hand(s) among the eligible players.
     */
    fun payoutWinnings() {
        val eligibleActivePlayers = eligiblePlayers.filter { !it.isFolded() }

        // Return if no eligible players (edge case)
        if (eligibleActivePlayers.isEmpty()) return

        // Get hole cards for each eligible player
        val holeCardsList = eligibleActivePlayers.map { it.hand() }

        // Skip if no valid hands (another edge case)
        if (holeCardsList.isEmpty()) return

        // Use HandEvaluator to determine winners
        val winningHoleCards = handEvaluator.determineHighestHand(holeCardsList)
        val winningPlayers = winningHoleCards.map { it.player }

        // Return if no winning players (edge case)
        if (winningPlayers.isEmpty()) return

        // Split the pot among winners
        val winAmount = amount / winningPlayers.size
        val remainder = amount % winningPlayers.size

        winningPlayers.forEachIndexed { index: Int, player: PokerPlayer? ->
            player?.addChips(winAmount)

            // Give the remainder to the first winner (convention in poker)
            if (index == 0 && remainder > 0) {
                player?.addChips(remainder)
            }
        }
    }
}