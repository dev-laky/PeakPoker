package hwr.oop.projects.peakpoker.core.game

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

/**
 * Represents a poker game with players, cards, and game mechanics.
 *
 * This class implements the core poker game logic including betting rounds,
 * player turns, and hand management. It enforces poker rules such as
 * betting order, blind requirements, and valid player actions.
 *
 * @property smallBlindAmount The amount required for the small blind
 * @property bigBlindAmount The amount required for the big blind
 * @property playersOnTable List of players participating in this game
 * @property id Unique identifier for this game instance
 * @throws InvalidBlindConfigurationException If blind amounts are invalid
 * @throws MinimumPlayersException If there are fewer than 2 players
 * @throws DuplicatePlayerException If there are duplicate players
 */
class PokerGame(
  val smallBlindAmount: Int,
  val bigBlindAmount: Int,
  val playersOnTable: List<PokerPlayer> = listOf(),
  override val id: GameId = GameId.generate(),
) : Game {

  /**
   * Index of the small blind player within playersOnTable.
   */
  private var smallBlindIndex: Int = 0

  /**
   * The card deck used for this game.
   */
  val deck: Deck = Deck()

  /**
   * The community cards shared by all players.
   */
  val communityCards: CommunityCards = CommunityCards(emptyList(), this)

  /**
   * The current state of the game (pre-flop, flop, turn, river).
   */
  val gameState = GameState.PRE_FLOP

  /**
   * Index of the current active player within playersOnTable.
   * Will be = 2 after "blind" init.
   */
  var currentPlayerIndex: Int = 0

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
    if (playersOnTable.size < 2) {
      throw MinimumPlayersException("Minimum number of players is 2")
    }
    if (playersOnTable.distinctBy { it.name }.size != playersOnTable.size) {
      throw DuplicatePlayerException("All players must be unique")
    }

    // Set the blinds for the players at the table
    setBlinds()

    // Deal hole cards to players
    dealHoleCards()
  }

  /**
   * Returns the small blind amount for this game.
   *
   * @return The small blind amount in chips
   */
  fun getSmallBlind(): Int {
    return smallBlindAmount
  }

  /**
   * Returns the big blind amount for this game.
   *
   * @return The big blind amount in chips
   */
  fun getBigBlind(): Int {
    return bigBlindAmount
  }

  /**
   * Returns the player whose turn it currently is.
   *
   * @return The currently active player
   */
  fun getCurrentPlayer(): PokerPlayer {
    return playersOnTable[currentPlayerIndex]
  }

  /**
   * Returns the player who will act next after the current player.
   *
   * @return The next player in the turn order
   */
  fun getNextPlayer(): PokerPlayer {
    return playersOnTable[(currentPlayerIndex + 1) % playersOnTable.size]
  }

  /**
   * Returns the highest bet amount currently on the table.
   *
   * @return The highest bet amount among all players
   */
  fun getHighestBet(): Int {
    return playersOnTable.maxOf { it.getBet() }
  }

  /**
   * Returns the index of the small blind player.
   *
   * @return The index position of the small blind player
   */
  fun getSmallBlindIndex(): Int {
    return smallBlindIndex
  }

  /**
   * Calculates the total amount of chips in the pot.
   *
   * @return The sum of all player bets
   */
  fun calculatePot(): Int {
    return playersOnTable.sumOf { it.getBet() }
  }

  /**
   * Checks if a player with the same name exists in the game.
   *
   * @param pokerPlayer The player to check
   * @return true if no player with the same name exists, false otherwise
   */
  fun checkPlayerValidity(pokerPlayer: PokerPlayer): Boolean {
    return playersOnTable.none { it.name == pokerPlayer.name }
  }

  /**
   * Checks if the provided player is a participant in this game instance.
   *
   * This method validates whether a player is actually registered in the current game
   * by checking if the player object exists in the playersOnTable list. This is an identity check,
   * not just a name comparison, so two different player objects with the same name will be
   * treated as different players.
   *
   * @param player The PokerPlayer to check
   * @return true if the player is a participant in this game, false otherwise
   */
  private fun isPlayerInThisGame(player: PokerPlayer): Boolean {
    return playersOnTable.contains(player)
  }

  /**
   * Advances the turn to the next active player.
   *
   * This method skips players who have folded or gone all-in.
   */
  private fun makeTurn() {
    val nextPlayer = getNextPlayer()

    // Skip any folded / all-in players
    if (nextPlayer.isFolded || nextPlayer.isAllIn) {
      currentPlayerIndex = (playersOnTable.indexOf(nextPlayer))
      makeTurn()
      return
    }

    currentPlayerIndex = (playersOnTable.indexOf(nextPlayer))
  }

  /**
   * Deals two hole cards to each player from the deck.
   */
  private fun dealHoleCards() {
    playersOnTable.forEach { player ->
      val cards = deck.draw(2)
      player.assignHand(HoleCards(cards, player))
    }
  }

  /**
   * Sets the player's bet to the specified amount.
   *
   * This method validates that the bet is higher than the current highest bet
   * and that it's the player's turn before raising their bet.
   *
   * @param pokerPlayer The player who is raising their bet
   * @param chips The total amount to bet (not the additional amount)
   * @throws InvalidBetAmountException If the bet amount is negative
   * @throws InvalidBetAmountException If the bet is not higher than the current highest bet
   * @throws InvalidPlayerStateException If it is not the player's turn
   * @throws InvalidPlayerStateException If the player has already folded or gone all-in
   * @throws InsufficientChipsException If the player does not have enough chips
   */
  fun raiseBetTo(pokerPlayer: PokerPlayer, chips: Int) {
    if (!isPlayerInThisGame(pokerPlayer)) {
      throw InvalidPlayerStateException("Player is not part of this game")
    }

    val currentPlayer = getCurrentPlayer()
    val highestBet = getHighestBet()
    when {
      chips < 0 -> throw InvalidBetAmountException("Bet amount must be positive")
      highestBet >= chips -> throw InvalidBetAmountException("Bet must be higher than the current highest bet")
      currentPlayer != pokerPlayer -> throw InvalidPlayerStateException("It's not your turn to bet")
      pokerPlayer.isFolded -> throw InvalidPlayerStateException("Cannot raise bet after folding")
      pokerPlayer.isAllIn -> throw InvalidPlayerStateException("Cannot raise bet after going all-in")
      // The player needs to go all-in or fold
      (chips - pokerPlayer.getBet()) > pokerPlayer.getChips() -> throw InsufficientChipsException(
        "Not enough chips to raise bet"
      )
    }
    pokerPlayer.setBetAmount(chips)
    makeTurn()
  }

  /**
   * Allows a player to match the current highest bet.
   *
   * @param pokerPlayer The player who is calling
   * @throws InvalidPlayerStateException If it is not the player's turn
   * @throws InvalidCallException If the player is already at the highest bet
   * @throws InvalidPlayerStateException If the player has already folded or gone all-in
   * @throws InsufficientChipsException If the player does not have enough chips
   */
  fun call(pokerPlayer: PokerPlayer) {
    val currentPlayer = getCurrentPlayer()
    val highestBet = getHighestBet()
    when {
      currentPlayer != pokerPlayer -> throw InvalidPlayerStateException("It's not your turn to call")
      pokerPlayer.getBet() == highestBet -> throw InvalidCallException("You are already at the highest bet")
      pokerPlayer.isFolded -> throw InvalidPlayerStateException("You can not call after having folded")
      pokerPlayer.isAllIn -> throw InvalidPlayerStateException("You can not call after having gone all-in")
      // The player needs to go all-in or fold
      pokerPlayer.getChips() < (highestBet - pokerPlayer.getBet()) -> throw InsufficientChipsException(
        "You do not have enough chips to call."
      )
    }
    pokerPlayer.setBetAmount(highestBet)
    makeTurn()
  }

  /**
   * Allows a player to check (pass the action to the next player without betting).
   *
   * @param pokerPlayer The player who is checking
   * @throws InvalidPlayerStateException If it is not the player's turn
   * @throws InvalidPlayerStateException If the player has already folded or gone all-in
   * @throws InvalidCheckException If the player is not at the highest bet
   */
  fun check(pokerPlayer: PokerPlayer) {
    val currentPlayer = getCurrentPlayer()
    when {
      currentPlayer != pokerPlayer -> throw InvalidPlayerStateException("It's not your turn to check")
      pokerPlayer.isFolded -> throw InvalidPlayerStateException("You can not check after having folded")
      pokerPlayer.isAllIn -> throw InvalidPlayerStateException("You can not check after having gone all-in")
      pokerPlayer.getBet() != getHighestBet() -> throw InvalidCheckException("You can not check if you are not at the highest bet")
    }
    makeTurn()
  }

  /**
   * Allows a player to fold (give up their hand and sit out the current round).
   *
   * @param pokerPlayer The player who is folding
   * @throws InvalidPlayerStateException If it is not the player's turn
   * @throws InvalidPlayerStateException If the player has already folded or gone all-in
   */
  fun fold(pokerPlayer: PokerPlayer) {
    val currentPlayer = getCurrentPlayer()
    when {
      currentPlayer != pokerPlayer -> throw InvalidPlayerStateException("It's not your turn to fold")
      pokerPlayer.isFolded -> throw InvalidPlayerStateException("You have already folded")
      pokerPlayer.isAllIn -> throw InvalidPlayerStateException("You can not fold after having gone all-in")
    }
    pokerPlayer.fold()
    makeTurn()
  }

  /**
   * Allows a player to bet all their remaining chips.
   *
   * @param pokerPlayer The player who is going all-in
   * @throws InvalidPlayerStateException If it is not the player's turn
   * @throws InvalidPlayerStateException If the player has already folded or gone all-in
   */
  fun allIn(pokerPlayer: PokerPlayer) {
    val currentPlayer = getCurrentPlayer()

    when {
      currentPlayer != pokerPlayer -> throw InvalidPlayerStateException("It's not your turn to all in")
      pokerPlayer.isFolded -> throw InvalidPlayerStateException("You can not go all-in after having folded")
      pokerPlayer.isAllIn -> throw InvalidPlayerStateException("You have already gone all-in")
    }
    pokerPlayer.allIn()
    makeTurn()
  }

  /**
   * Sets the blind bets for the first two players.
   *
   * This method places the initial small and big blind bets and adjusts
   * the small blind index for the next round.
   */
  private fun setBlinds() {
    raiseBetTo(getCurrentPlayer(), smallBlindAmount)

    // Check for same blind amounts --> call
    if (bigBlindAmount == smallBlindAmount) {
      call(getCurrentPlayer())
      return
    }

    raiseBetTo(getCurrentPlayer(), bigBlindAmount)
    smallBlindIndex = (smallBlindIndex + 1) % playersOnTable.size
  }
}
