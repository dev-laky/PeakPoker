package hwr.oop.projects.peakpoker.core.round

import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.deck.Deck
import hwr.oop.projects.peakpoker.core.exceptions.InsufficientChipsException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidBetAmountException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidCallException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidCheckException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidPlayerStateException
import hwr.oop.projects.peakpoker.core.game.GameActionable
import hwr.oop.projects.peakpoker.core.player.PokerPlayer

class PokerRound(
  private val players: List<PokerPlayer>,
  private val smallBlindAmount: Int,
  private val bigBlindAmount: Int,
  private val smallBlindIndex: Int,
  private val onRoundComplete: () -> Unit,  // Callback function to notify when the round is complete
) : GameActionable {

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
    if (players.all { it.isFolded() || it.isAllIn() }) return true

    // Check if all players have called the highest bet
    val highestBet = getHighestBet()

    // Check if all players checked or gone all-in or folded
    if (highestBet == 0) {
      return players.all { it.hasChecked() }
    }

    return players.all { it.bet() == highestBet }
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
    if (players.all { it.isFolded() || it.isAllIn() }) {
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
    if (nextPlayer.isFolded() || nextPlayer.isAllIn()) {
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
    return players.maxOf { it.bet() }
  }

  private fun getCurrentPlayer(): PokerPlayer {
    return players[currentPlayerIndex]
  }

  private fun getNextPlayer(): PokerPlayer {
    return players[(currentPlayerIndex + 1) % players.size]
  }

  private fun getPlayerByName(name: String): PokerPlayer {
    players.forEach { player ->
      if (player.name == name) return player
    }
    throw IllegalStateException("Player with name $name not found")
  }

  private fun setBlinds() {
    raiseBetTo(getCurrentPlayer().name, smallBlindAmount)

    // Check for the same blind amounts --> call
    if (bigBlindAmount == smallBlindAmount) {
      call(getCurrentPlayer().name)
      return
    }

    raiseBetTo(getCurrentPlayer().name, bigBlindAmount)
  }

  private fun requireLargerThanHighestBet(highestBet: Int, chips: Int) {
    if (highestBet >= chips) throw InvalidBetAmountException("Bet must be higher than the current highest bet")
  }

  private fun requirePositiveChips(chips: Int) {
    if (chips < 0) throw InvalidBetAmountException("Bet amount must be positive")
  }

  private fun requirePlayerTurn(
    currentPlayer: PokerPlayer,
    player: PokerPlayer,
  ) {
    if (currentPlayer != player) throw InvalidPlayerStateException("It's not your turn to bet")
  }

  private fun requirePlayerNotFolded(player: PokerPlayer) {
    if (player.isFolded()) throw InvalidPlayerStateException("Cannot raise bet after folding")
  }

  private fun requirePlayerNotAllIn(player: PokerPlayer) {
    if (player.isAllIn()) throw InvalidPlayerStateException("Cannot raise bet after going all-in")
  }

  private fun requireSufficientChipsToRaise(player: PokerPlayer, chips: Int) {
    if ((chips - player.bet()) > player.chips()) throw InsufficientChipsException(
      "Not enough chips to raise bet"
    )
  }

  private fun requireNotAtHighestBet(player: PokerPlayer, highestBet: Int) {
    if (player.bet() == highestBet) throw InvalidCallException("You are already at the highest bet")
  }

  private fun requirePlayerNotFoldedForCall(player: PokerPlayer) {
    if (player.isFolded()) throw InvalidPlayerStateException("You can not call after having folded")
  }

  private fun requirePlayerNotAllInForCall(player: PokerPlayer) {
    if (player.isAllIn()) throw InvalidPlayerStateException("You can not call after having gone all-in")
  }

  private fun requireSufficientChipsToCall(
    player: PokerPlayer,
    highestBet: Int,
  ) {
    if (player.chips() < (highestBet - player.bet())) throw InsufficientChipsException(
      "You do not have enough chips to call."
    )
  }

  private fun requirePlayerAtHighestBet(player: PokerPlayer) {
    if (player.bet() != getHighestBet()) throw InvalidCheckException("You can not check if you are not at the highest bet")
  }

  /**
   * Sets the player's bet to the specified amount.
   *
   * This method validates that the bet is higher than the current highest bet
   * and that it's the player's turn before raising their bet.
   *
   * @param playerName The player who is raising their bet
   * @param chips The total amount to bet (not the additional amount)
   * @throws InvalidBetAmountException If the bet amount is negative
   * @throws InvalidBetAmountException If the bet is not higher than the current highest bet
   * @throws InvalidPlayerStateException If it is not the player's turn
   * @throws InvalidPlayerStateException If the player has already folded or gone all-in
   * @throws InsufficientChipsException If the player does not have enough chips
   */
  override fun raiseBetTo(playerName: String, chips: Int) {
    val player = getPlayerByName(playerName)
    val currentPlayer = getCurrentPlayer()
    val highestBet = getHighestBet()

    requirePositiveChips(chips)
    requireLargerThanHighestBet(highestBet, chips)
    requirePlayerTurn(currentPlayer, player)
    requirePlayerNotFolded(player)
    requirePlayerNotAllIn(player)
    requireSufficientChipsToRaise(player, chips)

    pot += (chips - player.bet())
    player.setBetAmount(chips)
    makeTurn()
  }

  /**
   * Allows a player to match the current highest bet.
   *
   * @param playerName The player who is calling
   * @throws InvalidPlayerStateException If it is not the player's turn
   * @throws InvalidCallException If the player is already at the highest bet
   * @throws InvalidPlayerStateException If the player has already folded or gone all-in
   * @throws InsufficientChipsException If the player does not have enough chips
   */
  override fun call(playerName: String) {
    val player = getPlayerByName(playerName)
    val currentPlayer = getCurrentPlayer()
    val highestBet = getHighestBet()

    requirePlayerTurn(currentPlayer, player)
    requireNotAtHighestBet(player, highestBet)
    requirePlayerNotFoldedForCall(player)
    requirePlayerNotAllInForCall(player)
    requireSufficientChipsToCall(player, highestBet)

    pot += (highestBet - player.bet())
    player.setBetAmount(highestBet)
    makeTurn()
  }

  /**
   * Allows a player to check (pass the action to the next player without betting).
   *
   * @param playerName The player who is checking
   * @throws InvalidPlayerStateException If it is not the player's turn
   * @throws InvalidPlayerStateException If the player has already folded or gone all-in
   * @throws InvalidCheckException If the player is not at the highest bet
   */
  override fun check(playerName: String) {
    val player = getPlayerByName(playerName)
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
   * @param playerName The player who is folding
   * @throws InvalidPlayerStateException If it is not the player's turn
   * @throws InvalidPlayerStateException If the player has already folded or gone all-in
   */
  override fun fold(playerName: String) {
    val player = getPlayerByName(playerName)
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
   * @param playerName The player who is going all-in
   * @throws InvalidPlayerStateException If it is not the player's turn
   * @throws InvalidPlayerStateException If the player has already folded or gone all-in
   */
  override fun allIn(playerName: String) {
    val player = getPlayerByName(playerName)
    val currentPlayer = getCurrentPlayer()

    requirePlayerTurn(currentPlayer, player)
    requirePlayerNotFolded(player)
    requirePlayerNotAllIn(player)

    pot += player.chips()
    player.allIn()
    makeTurn()
  }
}