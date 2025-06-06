package hwr.oop.projects.peakpoker.core.round

import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.deck.Deck
import hwr.oop.projects.peakpoker.core.exceptions.DuplicatePlayerException
import hwr.oop.projects.peakpoker.core.exceptions.InsufficientChipsException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidBetAmountException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidBlindConfigurationException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidCallException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidCheckException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidPlayerStateException
import hwr.oop.projects.peakpoker.core.exceptions.MinimumPlayersException
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
    if (smallBlindAmount <= 0) {
      throw InvalidBlindConfigurationException("Small blind amount must be positive")
    }
    if (bigBlindAmount <= 0) {
      throw InvalidBlindConfigurationException("Big blind amount must be positive")
    }
    if (bigBlindAmount != smallBlindAmount * 2) {
      throw InvalidBlindConfigurationException("Big blind amount must be exactly double the small blind amount")
    }
    if (players.size < 2) {
      throw MinimumPlayersException("Minimum number of players is 2")
    }
    if (players.distinctBy { it.name }.size != players.size) {
      throw DuplicatePlayerException("All players must be unique")
    }

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

  private fun requireLargerThanHighestBet(highestBet: Int, chips: Int) {
    if (highestBet >= chips) throw IllegalStateException("Bet must be higher than the current highest bet")
  }

  private fun requirePositiveChips(chips: Int) {
    if (chips < 0) throw IllegalArgumentException("Bet amount must be positive")
  }

  private fun requirePlayerTurn(
    currentPlayer: PokerPlayer,
    player: PokerPlayer,
  ) {
    if (currentPlayer != player) throw IllegalStateException("It's not your turn to bet")
  }

  private fun requirePlayerNotFolded(player: PokerPlayer) {
    if (player.isFolded) throw IllegalStateException("Cannot raise bet after folding")
  }

  private fun requirePlayerNotAllIn(player: PokerPlayer) {
    if (player.isAllIn) throw IllegalStateException("Cannot raise bet after going all-in")
  }

  private fun requireSufficientChipsToRaise(player: PokerPlayer, chips: Int) {
    if ((chips - player.getBet()) > player.getChips()) throw IllegalStateException(
      "Not enough chips to raise bet"
    )
  }

  private fun requireNotAtHighestBet(player: PokerPlayer, highestBet: Int) {
    if (player.getBet() == highestBet) throw IllegalStateException("You are already at the highest bet")
  }

  private fun requirePlayerNotFoldedForCall(player: PokerPlayer) {
    if (player.isFolded) throw IllegalStateException("You can not call after having folded")
  }

  private fun requirePlayerNotAllInForCall(player: PokerPlayer) {
    if (player.isAllIn) throw IllegalStateException("You can not call after having gone all-in")
  }

  private fun requireSufficientChipsToCall(
    player: PokerPlayer,
    highestBet: Int,
  ) {
    if (player.getChips() < (highestBet - player.getBet())) throw IllegalStateException(
      "You do not have enough chips to call."
    )
  }

  private fun requirePlayerAtHighestBet(player: PokerPlayer) {
    if (player.getBet() != getHighestBet()) throw IllegalStateException("You can not check if you are not at the highest bet")
  }

  /**
   * Sets the player's bet to the specified amount.
   *
   * This method validates that the bet is higher than the current highest bet
   * and that it's the player's turn before raising their bet.
   *
   * @param player The player who is raising their bet
   * @param chips The total amount to bet (not the additional amount)
   * @throws InvalidBetAmountException If the bet amount is negative
   * @throws InvalidBetAmountException If the bet is not higher than the current highest bet
   * @throws InvalidPlayerStateException If it is not the player's turn
   * @throws InvalidPlayerStateException If the player has already folded or gone all-in
   * @throws InsufficientChipsException If the player does not have enough chips
   */
  override fun raiseBetTo(player: PokerPlayer, chips: Int) {
    val currentPlayer = getCurrentPlayer()
    val highestBet = getHighestBet()

    requirePositiveChips(chips)
    requireLargerThanHighestBet(highestBet, chips)
    requirePlayerTurn(currentPlayer, player)
    requirePlayerNotFolded(player)
    requirePlayerNotAllIn(player)
    requireSufficientChipsToRaise(player, chips)
    
    pot += (chips - player.getBet())
    player.setBetAmount(chips)
    makeTurn()
  }

  /**
   * Allows a player to match the current highest bet.
   *
   * @param player The player who is calling
   * @throws InvalidPlayerStateException If it is not the player's turn
   * @throws InvalidCallException If the player is already at the highest bet
   * @throws InvalidPlayerStateException If the player has already folded or gone all-in
   * @throws InsufficientChipsException If the player does not have enough chips
   */
  override fun call(player: PokerPlayer) {
    val currentPlayer = getCurrentPlayer()
    val highestBet = getHighestBet()

    requirePlayerTurn(currentPlayer, player)
    requireNotAtHighestBet(player, highestBet)
    requirePlayerNotFoldedForCall(player)
    requirePlayerNotAllInForCall(player)
    requireSufficientChipsToCall(player, highestBet)

    pot += (highestBet - player.getBet())
    player.setBetAmount(highestBet)
    makeTurn()
  }

  /**
   * Allows a player to check (pass the action to the next player without betting).
   *
   * @param player The player who is checking
   * @throws InvalidPlayerStateException If it is not the player's turn
   * @throws InvalidPlayerStateException If the player has already folded or gone all-in
   * @throws InvalidCheckException If the player is not at the highest bet
   */
  override fun check(player: PokerPlayer) {
    val currentPlayer = getCurrentPlayer()

    requirePlayerTurn(currentPlayer, player)
    requirePlayerNotFolded(player)
    requirePlayerNotAllIn(player)
    requirePlayerAtHighestBet(player)

    player.check()
    makeTurn()
  }

  /**
   * Allows a player to fold (give up their hand and sit out the current round).
   *
   * @param player The player who is folding
   * @throws InvalidPlayerStateException If it is not the player's turn
   * @throws InvalidPlayerStateException If the player has already folded or gone all-in
   */
  override fun fold(player: PokerPlayer) {
    val currentPlayer = getCurrentPlayer()

    requirePlayerTurn(currentPlayer, player)
    requirePlayerNotFolded(player)
    requirePlayerNotAllIn(player)

    player.fold()
    makeTurn()
  }

  /**
   * Allows a player to bet all their remaining chips.
   *
   * @param player The player who is going all-in
   * @throws InvalidPlayerStateException If it is not the player's turn
   * @throws InvalidPlayerStateException If the player has already folded or gone all-in
   */
  override fun allIn(player: PokerPlayer) {
    val currentPlayer = getCurrentPlayer()

    requirePlayerTurn(currentPlayer, player)
    requirePlayerNotFolded(player)
    requirePlayerNotAllIn(player)

    pot += player.getChips()
    player.allIn()
    makeTurn()
  }
}