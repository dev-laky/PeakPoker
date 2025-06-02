package hwr.oop.projects.peakpoker.core.round

import hwr.oop.projects.peakpoker.core.player.PokerPlayer

interface Round {
    /**
     * Sets the player's bet to the specified amount.
     *
     * This method validates that the bet is higher than the current highest bet
     * and that it's the player's turn before raising their bet.
     *
     * @param player The player who is raising their bet
     * @param chips The total amount to bet (not the additional amount)
     * @throws IllegalArgumentException If the bet amount is negative.
     * @throws IllegalStateException If any of the following conditions are true:
     *                               - The bet is not higher than the current highest bet.
     *                               - It is not the player's turn.
     *                               - The player has already folded.
     *                               - The player has already gone all-in.
     *                               - The player does not have enough chips.
     */
    fun raiseBetTo(player: PokerPlayer, chips: Int)

    /**
     * Matches the current highest bet for the player.
     *
     * @param player The player who is calling
     * @throws IllegalStateException If it's not the player's turn or the player has folded or gone all-in
     */
    fun call(player: PokerPlayer)

    /**
     * Maintains the player's current bet when no one has bet in the current round.
     *
     * @param player The player who is checking
     * @throws IllegalStateException If it's not the player's turn, the player has folded or gone all-in,
     *                              or if there's an active bet that must be called
     */
    fun check(player: PokerPlayer)

    /**
     * The player gives up their hand and any claim to the pot.
     *
     * @param player The player who is folding
     * @throws IllegalStateException If it's not the player's turn or the player has already folded or gone all-in
     */
    fun fold(player: PokerPlayer)

    /**
     * The player bets all their remaining chips.
     *
     * @param player The player who is going all-in
     * @throws IllegalStateException If it's not the player's turn or the player has already folded or gone all-in
     */
    fun allIn(player: PokerPlayer)
}