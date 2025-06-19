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
import hwr.oop.projects.peakpoker.core.pot.Pot
import hwr.oop.projects.peakpoker.core.hand.HandEvaluator

class PokerRound(
  private val players: List<PokerPlayer>,
  private val smallBlindAmount: Int,
  private val bigBlindAmount: Int,
  private val smallBlindIndex: Int,
  private val onRoundComplete: () -> Unit,  // Callback function to notify when the round is complete
  // TODO: Do we need the HandEvaluator as a dependency here?
  private val handEvaluator: HandEvaluator = HandEvaluator(),
) : GameActionable {

  private val deck: Deck = Deck()
  private val communityCards: CommunityCards =
    CommunityCards(mutableListOf(), this)
  private var roundPhase = RoundPhase.PRE_FLOP

  private val pots: MutableList<Pot> = mutableListOf(Pot(0, players.toSet()))
  private val mainPot: Pot get() = pots.first()
  val totalPotAmount: Int get() = pots.sumOf { it.amount }

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
//    // 1. Ensure all community cards are dealt
//    // In case we reached showdown without all cards being revealed (e.g., all players but one folded)
//    while (communityCards.cards.size < 5) {
//      communityCards.dealCommunityCards(roundPhase, deck)
//    }

    // 2. Distribute winnings across all pots
    distributeWinnings()

    // 3. Reset player round states
    players.forEach { player ->
      player.resetBet()
      player.resetRoundState()
    }

    // 4. Clear community cards and pots
    communityCards.reset()
    pots.clear()
    pots.add(Pot(0, players.toSet()))

    // 5. Notify the game about the round completion
    onRoundComplete()
    TODO("Implement showdown logic to determine the winner based on the players' hands and community cards")
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

    addToCurrentPot(player, chips - player.bet())
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

    addToCurrentPot(player, highestBet - player.bet())
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

    addToCurrentPot(player, player.chips())
    createSidePot(player)
    player.allIn()
    makeTurn()
  }

  // New changes: Side-pots
  // Seems to work just fine, but needs more testing
  private fun addToCurrentPot(player: PokerPlayer, amount: Int) {
    // Add to main pot by creating an updated version (as Pot is immutable)
    val updatedMainPot = mainPot.copy(amount = mainPot.amount + amount)
    pots[0] = updatedMainPot
  }

  private fun createSidePot(allInPlayer: PokerPlayer) {
    // Get all-in amount (player's bet + remaining chips)
    val allInTotal = allInPlayer.bet() + allInPlayer.chips()

    // Calculate excess chips from other players
    var excessAmount = 0
    for (player in players) {
      // Find all players who are not folded && not the all-in player && and who have bet more than the all-in total
      if (!player.isFolded() && player != allInPlayer && player.bet() > allInTotal) {
        val excess = player.bet() - allInTotal
        excessAmount += excess
        // Adjust player's bet to match all-in
        player.setBetAmount(allInTotal)
      }
    }

    // If there are excess chips, move them to a side pot
    if (excessAmount > 0) {
      // Reduce main pot by the excess amount
      val updatedMainPot = mainPot.copy(amount = mainPot.amount - excessAmount)
      pots[0] = updatedMainPot

      // Create side pot with eligible players (everyone but the all-in player)
      val eligiblePlayers =
        mainPot.eligiblePlayers.filter { it != allInPlayer }.toSet()
      pots.add(Pot(excessAmount, eligiblePlayers))
    }
  }

  private fun distributeWinnings() {
    // Process each pot (starting from side pots, then main pot)
    for (pot in pots.reversed()) {
      val eligibleActivePlayers = pot.eligiblePlayers.filter { !it.isFolded() }

      // Skip if no eligible players (edge case)
      if (eligibleActivePlayers.isEmpty()) continue

      // Determine winners for this pot
      val winners = determineWinners(eligibleActivePlayers)

      // Split the pot among winners
      if (winners.isNotEmpty()) {
        val winAmount = pot.amount / winners.size
        val remainder = pot.amount % winners.size

        winners.forEachIndexed { index, player ->
          player.addChips(winAmount)

          // Give the remainder to the first winner (convention in poker)
          if (index == 0 && remainder > 0) {
            player.addChips(remainder)
          }
        }
      }
    }
  }

  private fun determineWinners2(eligiblePlayers: Collection<PokerPlayer>): List<PokerPlayer> {
    if (eligiblePlayers.isEmpty()) return emptyList()

    // Get hole cards for each eligible player
    val holeCardsList = eligiblePlayers.map { it.hand() }

    // If only one player remains, they win by default
    if (holeCardsList.size == 1) return listOf(holeCardsList.first().player)

    // Since getBestCombo is private, we'll use determineHighestHand to find the best hand type
    // But then we'll loop through all players to find ties
    val bestPlayerHand =
      handEvaluator.determineHighestHand(holeCardsList, communityCards)

    val winners = mutableListOf<PokerPlayer>()

    // Compare each player's hand with others
    for (holeCards in holeCardsList) {
      // Skip already processed hands
      if (winners.contains(holeCards.player)) continue

      val playerToCompare = holeCards.player
      val otherPlayers = holeCardsList.filter { it.player != playerToCompare }

      // If this is the player with the best hand we found earlier, add them
      if (holeCards.player == bestPlayerHand.player) {
        winners.add(holeCards.player)

        // Find all ties by comparing each remaining player with the best player
        for (otherHoleCards in otherPlayers) {
          val result = handEvaluator.determineHighestHand(
            listOf(bestPlayerHand, otherHoleCards),
            communityCards
          )

          // If comparing these two players doesn't yield a clear winner, they're tied
          if (otherHoleCards.player == result.player) {
            winners.add(otherHoleCards.player)
          }
        }

        // Break after processing the best player
        break
      }
    }

    return winners
  }

  /**
   * Determines the winners by comparing each player's hand directly against the best hand.
   *
   * @param eligiblePlayers A collection of eligible players
   * @return A list of [PokerPlayer]s who are tied for the best hand
   */
  private fun determineWinners(eligiblePlayers: Collection<PokerPlayer>): List<PokerPlayer> {
    if (eligiblePlayers.isEmpty()) return emptyList()

    // Get hole cards for each eligible player
    val holeCardsList = eligiblePlayers.map { it.hand() }

    // If only one player remains, they win by default
    if (holeCardsList.size == 1) return listOf(holeCardsList.first().player)

    // Find the player with the best hand
    val bestPlayerHand = handEvaluator.determineHighestHand(holeCardsList, communityCards)
    val winners = mutableListOf(bestPlayerHand.player)

    // Find all ties by comparing each remaining player with the best player
    for (holeCards in holeCardsList) {
      // Skip the best player we already added
      if (holeCards.player == bestPlayerHand.player) continue

      // Key insight: In a tie, determineHighestHand always returns the first player
      // So if we reverse the order and the second player wins, they must be tied
      val result = handEvaluator.determineHighestHand(
        listOf(holeCards, bestPlayerHand),
        communityCards
      )

      // If the current player wins when listed first, they must be tied with the best player
      if (result.player == holeCards.player) {
        winners.add(holeCards.player)
      }
    }

    return winners
  }
}