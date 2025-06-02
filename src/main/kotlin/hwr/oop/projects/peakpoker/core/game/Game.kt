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
import hwr.oop.projects.peakpoker.core.player.Player

class Game(
  private val smallBlindAmount: Int,
  private val bigBlindAmount: Int,
  val playersOnTable: List<Player> = listOf(),
  override val id: GameId = GameId.generate(),
) : GameInterface {

  // Variable to track the index of the small blind player within PlayersOnTable
  private var smallBlindIndex: Int = 0
  private val deck: Deck = Deck()


  // Will be = 2 after "blind" init
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

  fun getDeck(): Deck {
    return deck
  }

  fun getSmallBlind(): Int {
    return smallBlindAmount
  }

  fun getBigBlind(): Int {
    return bigBlindAmount
  }

  fun getCurrentPlayer(): Player {
    return playersOnTable[currentPlayerIndex]
  }

  private fun getNextPlayer(): Player {
    return playersOnTable[(currentPlayerIndex + 1) % playersOnTable.size]
  }

  fun getHighestBet(): Int {
    return playersOnTable.maxOf { it.getBet() }
  }

  fun getSmallBlindIndex(): Int {
    return smallBlindIndex
  }

  private fun calculatePot(): Int {
    return playersOnTable.sumOf { it.getBet() }
  }

  fun checkPlayerValidity(player: Player): Boolean {
    return playersOnTable.none { it.name == player.name }
  }

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
   * @param player The player who is raising their bet
   * @param chips The total amount to bet (not the additional amount)
   * @throws InvalidBetAmountException If the bet amount is negative
   * @throws InvalidBetAmountException If the bet is not higher than the current highest bet
   * @throws InvalidPlayerStateException If it is not the player's turn
   * @throws InvalidPlayerStateException If the player has already folded or gone all-in
   * @throws InsufficientChipsException If the player does not have enough chips
   */
  fun raiseBetTo(player: Player, chips: Int) {
    val currentPlayer = getCurrentPlayer()
    val highestBet = getHighestBet()
    when {
      chips < 0 -> throw InvalidBetAmountException("Bet amount must be positive")
      highestBet >= chips -> throw InvalidBetAmountException("Bet must be higher than the current highest bet")
      currentPlayer != player -> throw InvalidPlayerStateException("It's not your turn to bet")
      player.isFolded -> throw InvalidPlayerStateException("Cannot raise bet after folding")
      player.isAllIn -> throw InvalidPlayerStateException("Cannot raise bet after going all-in")
      // The player needs to go all-in or fold
      (chips - player.getBet()) > player.getChips() -> throw InsufficientChipsException(
        "Not enough chips to raise bet"
      )
    }
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
  fun call(player: Player) {
    val currentPlayer = getCurrentPlayer()
    val highestBet = getHighestBet()
    when {
      currentPlayer != player -> throw InvalidPlayerStateException("It's not your turn to call")
      player.getBet() == highestBet -> throw InvalidCallException("You are already at the highest bet")
      player.isFolded -> throw InvalidPlayerStateException("You can not call after having folded")
      player.isAllIn -> throw InvalidPlayerStateException("You can not call after having gone all-in")
      // The player needs to go all-in or fold
      player.getChips() < (highestBet - player.getBet()) -> throw InsufficientChipsException(
        "You do not have enough chips to call."
      )
    }
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
  fun check(player: Player) {
    val currentPlayer = getCurrentPlayer()
    when {
      currentPlayer != player -> throw InvalidPlayerStateException("It's not your turn to check")
      player.isFolded -> throw InvalidPlayerStateException("You can not check after having folded")
      player.isAllIn -> throw InvalidPlayerStateException("You can not check after having gone all-in")
      player.getBet() != getHighestBet() -> throw InvalidCheckException("You can not check if you are not at the highest bet")
    }
    makeTurn()
  }

  /**
   * Allows a player to fold (give up their hand and sit out the current round).
   *
   * @param player The player who is folding
   * @throws InvalidPlayerStateException If it is not the player's turn
   * @throws InvalidPlayerStateException If the player has already folded or gone all-in
   */
  fun fold(player: Player) {
    val currentPlayer = getCurrentPlayer()
    when {
      currentPlayer != player -> throw InvalidPlayerStateException("It's not your turn to fold")
      player.isFolded -> throw InvalidPlayerStateException("You have already folded")
      player.isAllIn -> throw InvalidPlayerStateException("You can not fold after having gone all-in")
    }
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
  fun allIn(player: Player) {
    val currentPlayer = getCurrentPlayer()

    when {
      currentPlayer != player -> throw InvalidPlayerStateException("It's not your turn to all in")
      player.isFolded -> throw InvalidPlayerStateException("You can not go all-in after having folded")
      player.isAllIn -> throw InvalidPlayerStateException("You have already gone all-in")
    }
    player.allIn()
    makeTurn()
  }

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
