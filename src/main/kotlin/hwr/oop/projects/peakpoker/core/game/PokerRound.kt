package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.deck.Deck
import hwr.oop.projects.peakpoker.core.player.PlayerInfo
import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import hwr.oop.projects.peakpoker.core.pot.PokerPots
import hwr.oop.projects.peakpoker.core.pot.PotInfo
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class PokerRound(
  private val players: List<PokerPlayer>,
  private val smallBlindAmount: Int,
  private val bigBlindAmount: Int,
  private val smallBlindIndex: Int,
) : GameActionable {
  /**
   * Exception thrown when a player attempts a betting action without sufficient chips
   */
  class InsufficientChipsForBettingException(message: String) : IllegalStateException(message)

  /**
   * Thrown when an action is attempted on a player in an invalid state
   */
  class InvalidPlayerStateException(message: String) : IllegalStateException(message)

  /**
   * Exception thrown when a bet amount violates poker betting rules
   */
  class InvalidBetAmountException(message: String) : IllegalStateException(message)

  /**
   * Exception thrown when a player attempts an invalid call action
   */
  class InvalidCallActionException(message: String) : IllegalStateException(message)

  /**
   * Exception thrown when a player attempts an invalid check action
   */
  class InvalidCheckActionException(message: String) : IllegalStateException(message)

  /**
   * Exception thrown when a player with a specified name is not found in the game
   */
  class PlayerNotFoundException(message: String) : IllegalStateException(message)

  /**
   * Exception thrown when an invalid operation is attempted for the current round phase
   */
  class IllegalRoundPhaseException(message: String) : IllegalStateException(message)

  @Transient
  private var onRoundComplete: () -> Unit =
    {} // Callback function to notify when the round is complete

  private var roundPhase = RoundPhase.PRE_FLOP

  // Will be = 2 after "blind" init
  private var currentPlayerIndex: Int = smallBlindIndex

  private val deck: Deck = Deck()

  private val communityCards: CommunityCards =
    CommunityCards(mutableListOf())

  private val pots: PokerPots = PokerPots(players, communityCards)

  // Track if the round has been initialized (for deserialization)
  private var isInitialized: Boolean = false

  init {
    initializeRound()
  }

  /**
   * Restores the callback function after deserialization
   */
  fun restoreCallback(callback: () -> Unit) {
    this.onRoundComplete = callback
  }

  private fun initializeRound() {
    if (isInitialized) return

    // Set the blinds for the players at the table
    setBlinds()

    // Deal hole cards to players
    players.forEach { player ->
      val cards = deck.draw(2)
      player.assignHand(HoleCards(cards, player))
    }

    isInitialized = true
  }

  fun getRoundInfo(): RoundInfo {
    return RoundInfo(
      smallBlindAmount = smallBlindAmount,
      bigBlindAmount = bigBlindAmount,
      players = players.map { player ->
        PlayerInfo(
          name = player.name,
          chips = player.chips(),
          bet = player.bet(),
          isFolded = player.isFolded(),
          isAllIn = player.isAllIn()
        )
      },
      smallBlindPlayerName = players[smallBlindIndex].name,
      roundPhase = roundPhase,
      communityCards = communityCards.cards(),
      pots = pots.pots.map { pot ->
        PotInfo(
          amount = pot.amount(),
          eligiblePlayerNames = pot.eligiblePlayers.map { it.name },
        )
      },
      currentPlayerName = getCurrentPlayer().name,
    )
  }

  /**
   * Handles the showdown phase when players reveal their cards to determine the winner.
   *
   * This method executes the following steps in sequence:
   * 1. Distributes winnings from all pots to winning players
   * 2. Resets player round states (bets and statuses)
   * 3. Notifies the game about round completion via callback
   *
   * This is the final phase of a poker round, after which a new round can begin.
   */
  private fun showdown() {
    // 1. Distribute winnings across all pots
    // Process each pot (starting from side pots, then main pot)
    pots.reversed().forEach { it.payoutWinnings() }

    // 2. Reset player round states
    players.forEach { player ->
      player.resetBet()
      player.resetRoundState()
    }

    // 3. Notify the game about the round completion
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

    return players.all { it.isFolded() || it.isAllIn() || it.bet() == highestBet }
  }

  private fun initNextGamePhase() {
    roundPhase = when (roundPhase) {
      RoundPhase.PRE_FLOP -> RoundPhase.FLOP
      RoundPhase.FLOP -> RoundPhase.TURN
      RoundPhase.TURN -> RoundPhase.RIVER
      RoundPhase.RIVER -> RoundPhase.SHOWDOWN
      RoundPhase.SHOWDOWN -> throw IllegalRoundPhaseException("PokerGame is already in the SHOWDOWN phase")
    }

    // Check for the Showdown phase
    if (roundPhase == RoundPhase.SHOWDOWN) {
      showdown()
      return
    }

    resetBets()

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

    // Skip any folded / all-in players / eliminated players
    if (nextPlayer.isFolded() || nextPlayer.isAllIn() || nextPlayer.chips() < 1) {
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
    throw PlayerNotFoundException("Player with name $name not found")
  }

  private fun setBlinds() {
    // Skip setting blinds if they're already set
    val currentBets = players.sumOf { it.bet() }
    if (currentBets > 0) {
      return
    }

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
    if ((chips - player.bet()) > player.chips()) throw InsufficientChipsForBettingException(
      "Not enough chips to raise bet"
    )
  }

  private fun requireNotAtHighestBet(player: PokerPlayer, highestBet: Int) {
    if (player.bet() == highestBet) throw InvalidCallActionException("You are already at the highest bet")
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
    if (player.chips() < (highestBet - player.bet())) throw InsufficientChipsForBettingException(
      "You do not have enough chips to call."
    )
  }

  private fun requirePlayerAtHighestBet(player: PokerPlayer) {
    if (player.bet() != getHighestBet()) throw InvalidCheckActionException("You can not check if you are not at the highest bet")
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
   * @throws InsufficientChipsForBettingException If the player does not have enough chips
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

    pots.addChipsToCurrentPot(chips - player.bet())
    player.setBetAmount(chips)
    makeTurn()
  }

  /**
   * Allows a player to match the current highest bet.
   *
   * @param playerName The player who is calling
   * @throws InvalidPlayerStateException If it is not the player's turn
   * @throws InvalidCallActionException If the player is already at the highest bet
   * @throws InvalidPlayerStateException If the player has already folded or gone all-in
   * @throws InsufficientChipsForBettingException If the player does not have enough chips
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

    pots.addChipsToCurrentPot(highestBet - player.bet())
    player.setBetAmount(highestBet)
    makeTurn()
  }

  /**
   * Allows a player to check (pass the action to the next player without betting).
   *
   * @param playerName The player who is checking
   * @throws InvalidPlayerStateException If it is not the player's turn
   * @throws InvalidPlayerStateException If the player has already folded or gone all-in
   * @throws InvalidCheckActionException If the player is not at the highest bet
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

    pots.addChipsToCurrentPot(player.chips())
    pots.createSidePotIfNeeded(player)
    player.allIn()
    makeTurn()
  }
}