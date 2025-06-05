package hwr.oop.projects.peakpoker.core.round

import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.deck.Deck
import hwr.oop.projects.peakpoker.core.player.PokerPlayer

class PokerRound(
  private val players: List<PokerPlayer>,
  private val smallBlindAmount: Int,
  private val bigBlindAmount: Int,
  private val smallBlindIndex: Int,
  private val onRoundComplete: () -> Unit,  // Callback function to notify when the round is complete
) : Round {

  private val deck: Deck = Deck()
  private val communityCards: CommunityCards =
    CommunityCards(mutableListOf(), this)
  private var roundPhase = RoundPhase.PRE_FLOP

  private var pot = 0

  // Will be = 2 after "blind" init
  private var currentPlayerIndex: Int = smallBlindIndex

  init {
    // Set the blinds for the players at the table
    setBlinds()

    // Deal hole cards to players
    players.forEach { player ->
      val cards = deck.draw(2)
      player.assignHand(HoleCards(cards, player))
    }
  }

  private fun showdown() {
    TODO("Implement showdown logic to determine the winner based on the players' hands and community cards")

    // Notify the game about the round completion
    onRoundComplete()
  }

  private fun checkForNextGamePhase(): Boolean {
    // Check if all players have folded or gone all-in
    if (players.all { it.isFolded || it.isAllIn }) return true

    // Check if all players have called the highest bet
    val highestBet = getHighestBet()

    // Check if all players checked or gone all-in or folded
    if (highestBet == 0) {
      return players.all { it.hasChecked }
    }

    return players.all { it.getBet() == highestBet }
  }

  private fun initNextGamePhase() {
    roundPhase = when (roundPhase) {
      RoundPhase.PRE_FLOP -> RoundPhase.FLOP
      RoundPhase.FLOP -> RoundPhase.TURN
      RoundPhase.TURN -> RoundPhase.RIVER
      RoundPhase.RIVER -> RoundPhase.SHOWDOWN
      RoundPhase.SHOWDOWN -> throw IllegalStateException("PokerGame is already in the SHOWDOWN phase")
    }

    resetBets()

    // Check for the Showdown phase
    if (roundPhase == RoundPhase.SHOWDOWN) {
      showdown()
      return
    }

    communityCards.dealCommunityCards(roundPhase, deck)

    // Check if all players have folded or gone all-in
    if (players.all { it.isFolded || it.isAllIn }) {
      initNextGamePhase()
      return
    }

    // Start at the small blind player for the next phase
    currentPlayerIndex = smallBlindIndex
  }

  private fun makeTurn() {
    // Check for the next game phase
    val isNextPhase = checkForNextGamePhase()
    if (isNextPhase) {
      initNextGamePhase()
      return
    }

    val nextPlayer = getNextPlayer()

    // Skip any folded / all-in players
    if (nextPlayer.isFolded || nextPlayer.isAllIn) {
      currentPlayerIndex = (players.indexOf(nextPlayer))
      makeTurn()
      return
    }

    currentPlayerIndex = (players.indexOf(nextPlayer))
  }

  private fun resetBets() {
    players.forEach { player ->
      player.resetBet()
    }
  }

  private fun getHighestBet(): Int {
    return players.maxOf { it.getBet() }
  }

  private fun getCurrentPlayer(): PokerPlayer {
    return players[currentPlayerIndex]
  }

  private fun getNextPlayer(): PokerPlayer {
    return players[(currentPlayerIndex + 1) % players.size]
  }

  private fun setBlinds() {
    raiseBetTo(getCurrentPlayer(), smallBlindAmount)

    // Check for the same blind amounts --> call
    if (bigBlindAmount == smallBlindAmount) {
      call(getCurrentPlayer())
      return
    }

    raiseBetTo(getCurrentPlayer(), bigBlindAmount)
  }

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
  override fun raiseBetTo(player: PokerPlayer, chips: Int) {
    val currentPlayer = getCurrentPlayer()
    val highestBet = getHighestBet()
    when {
      chips < 0 -> throw IllegalArgumentException("Bet amount must be positive")
      highestBet >= chips -> throw IllegalStateException("Bet must be higher than the current highest bet")
      currentPlayer != player -> throw IllegalStateException("It's not your turn to bet")
      player.isFolded -> throw IllegalStateException("Cannot raise bet after folding")
      player.isAllIn -> throw IllegalStateException("Cannot raise bet after going all-in")

      // The player needs to go all-in or fold
      (chips - player.getBet()) > player.getChips() -> throw IllegalStateException(
        "Not enough chips to raise bet"
      )
    }
    pot += (chips - player.getBet())
    player.setBetAmount(chips)
    makeTurn()
  }

  override fun call(player: PokerPlayer) {
    val currentPlayer = getCurrentPlayer()
    val highestBet = getHighestBet()
    when {
      currentPlayer != player -> throw IllegalStateException("It's not your turn to call")
      player.getBet() == highestBet -> throw IllegalStateException("You are already at the highest bet")
      player.isFolded -> throw IllegalStateException("You can not call after having folded")
      player.isAllIn -> throw IllegalStateException("You can not call after having gone all-in")

      // The player needs to go all-in or fold
      player.getChips() <= (highestBet - player.getBet()) -> throw IllegalStateException(
        "You do not have enough chips to call."
      )
    }
    pot += (highestBet - player.getBet())
    player.setBetAmount(highestBet)
    makeTurn()
  }

  override fun check(player: PokerPlayer) {
    val currentPlayer = getCurrentPlayer()
    when {
      currentPlayer != player -> throw IllegalStateException("It's not your turn to check")
      player.isFolded -> throw IllegalStateException("You can not check after having folded")
      player.isAllIn -> throw IllegalStateException("You can not check after having gone all-in")
      player.getBet() != getHighestBet() -> throw IllegalStateException("You can not check if you are not at the highest bet")
    }
    player.check()
    makeTurn()
  }

  override fun fold(player: PokerPlayer) {
    val currentPlayer = getCurrentPlayer()
    when {
      currentPlayer != player -> throw IllegalStateException("It's not your turn to fold")
      player.isFolded -> throw IllegalStateException("You have already folded")
      player.isAllIn -> throw IllegalStateException("You can not fold after having gone all-in")
    }
    player.fold()
    makeTurn()
  }

  override fun allIn(player: PokerPlayer) {
    val currentPlayer = getCurrentPlayer()
    when {
      currentPlayer != player -> throw IllegalStateException("It's not your turn to all in")
      player.isFolded -> throw IllegalStateException("You can not go all-in after having folded")
      player.isAllIn -> throw IllegalStateException("You have already gone all-in")
    }
    pot += player.getChips()
    player.allIn()
    makeTurn()
  }
}