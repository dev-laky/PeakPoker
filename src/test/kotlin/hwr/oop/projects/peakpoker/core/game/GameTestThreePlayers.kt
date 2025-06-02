package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class GameTestThreePlayers : AnnotationSpec() {
  lateinit var pokerPlayer1: PokerPlayer
  lateinit var pokerPlayer2: PokerPlayer
  lateinit var pokerPlayer3: PokerPlayer
  lateinit var testPokerGame: PokerGame

  @BeforeEach
  fun setup() {
    pokerPlayer1 = PokerPlayer("Hans")
    pokerPlayer2 = PokerPlayer("Peter")
    pokerPlayer3 = PokerPlayer("Max")
    testPokerGame = PokerGame(
      10, 20,
      listOf(pokerPlayer1, pokerPlayer2, pokerPlayer3),
      GameId("testGame100")
    )
  }

  @Test
  fun `check if get current player works correctly`() {
    // when
    val currentPlayer = testPokerGame.getCurrentPlayer()

    // then
    assertThat(currentPlayer.name).isEqualTo("Max")
  }

  @Test
  fun `makeTurn advances currentPlayerIndex to next player`() {
    val initialPlayer = testPokerGame.getCurrentPlayer()
    testPokerGame.call(pokerPlayer3)
    val nextPlayer = testPokerGame.getCurrentPlayer()

    assertThat(nextPlayer.name).isNotEqualTo(initialPlayer.name)
    assertThat(testPokerGame.currentPlayerIndex).isEqualTo(0)
  }

  @Test
  fun `checkPlayerValidity returns false for existing players and true for new players`() {
    val existingPokerPlayer = PokerPlayer("Hans")
    val newPokerPlayer = PokerPlayer("Sara")

    assertThat(testPokerGame.checkPlayerValidity(existingPokerPlayer)).isFalse()
    assertThat(testPokerGame.checkPlayerValidity(newPokerPlayer)).isTrue()
  }

  @Test
  fun `getHighestBet is equal to big blind amount on init`() {
    assertThat(testPokerGame.getHighestBet()).isEqualTo(20)
  }

  @Test
  fun `getHighestBet returns updated value when player raises`() {
    // when
    testPokerGame.call(pokerPlayer3)
    testPokerGame.raiseBetTo(pokerPlayer1, 50)

    // then
    assertThat(testPokerGame.getHighestBet()).isEqualTo(50)
  }

  @Test
  fun `getHighestBet returns highest bet when multiple players have different bets`() {
    // when
    testPokerGame.call(pokerPlayer3)
    testPokerGame.raiseBetTo(pokerPlayer1, 40)
    testPokerGame.call(pokerPlayer2)

    // then
    assertThat(testPokerGame.getHighestBet()).isEqualTo(40)
    assertThat(pokerPlayer1.getBet()).isEqualTo(40)
    assertThat(pokerPlayer2.getBet()).isEqualTo(40)
    assertThat(pokerPlayer3.getBet()).isEqualTo(20)
  }

  @Test
  fun `getHighestBet still counts folded player bets`() {
    // when
    testPokerGame.call(pokerPlayer3)
    testPokerGame.raiseBetTo(pokerPlayer1, 50)
    testPokerGame.fold(pokerPlayer2)

    // then
    assertThat(testPokerGame.getHighestBet()).isEqualTo(50)
    assertThat(pokerPlayer2.isFolded).isTrue()
    assertThat(pokerPlayer2.getBet()).isEqualTo(20) // Player keeps their bet even when folded
  }

  @Test
  fun `big blind amount must be twice the smallBlind amount`() {
    assertThat(testPokerGame.bigBlindAmount).isEqualTo(testPokerGame.smallBlindAmount * 2)
  }

  @Test
  fun `getSmallBlind returns correct small blind amount`() {
    assertThat(testPokerGame.getSmallBlind()).isEqualTo(10)
  }

  @Test
  fun `getBigBlind returns correct big blind amount`() {
    assertThat(testPokerGame.getBigBlind()).isEqualTo(20)
  }

  @Test
  fun `makeTurn cycles to first player at the end of table`() {
    testPokerGame.call(pokerPlayer3)

    assertThat(testPokerGame.getCurrentPlayer()).isEqualTo(pokerPlayer1)
  }

  @Test
  fun `makeTurn correctly sets currentPlayerIndex`() {
    testPokerGame.call(pokerPlayer3)
    testPokerGame.raiseBetTo(pokerPlayer1, 100)

    assertThat(testPokerGame.currentPlayerIndex).isEqualTo(1)
  }

  @Test
  fun `dealHoleCards correctly assigns 2 cards to each player during initialization`() {
    assertThat(pokerPlayer1.getHand().cards).hasSize(2)
    assertThat(pokerPlayer2.getHand().cards).hasSize(2)
    assertThat(pokerPlayer3.getHand().cards).hasSize(2)
  }

  @Test
  fun `dealHoleCards removes correct number of cards from the deck`() {
    val playerCount = testPokerGame.playersOnTable.size

    assertThat(testPokerGame.deck.show()).hasSize(52 - (2 * playerCount))
  }

  @Test
  fun `dealHoleCards assigns unique cards to each player`() {
    // then - collect all cards from players' hands and check for uniqueness
    val allCards = pokerPlayer1.getHand().cards + pokerPlayer2.getHand().cards + pokerPlayer3.getHand().cards
    assertThat(allCards).hasSize(6) // 3 players * 2 cards
    assertThat(allCards.distinct()).hasSize(6) // All cards should be unique
  }

  @Test
  fun `initial pot equals sum of blinds`() {
    // then
    assertThat(testPokerGame.calculatePot()).isEqualTo(30) // Small blind(10) + Big blind(20)
    assertThat(testPokerGame.calculatePot()).isEqualTo(pokerPlayer1.getBet() + pokerPlayer2.getBet() + pokerPlayer3.getBet())
  }

  @Test
  fun `pot increases when players bet or raise`() {
    // when
    testPokerGame.call(pokerPlayer3) // Player3 calls big blind (20)
    testPokerGame.raiseBetTo(pokerPlayer1, 50) // Player1 raises to 50

    // then
    assertThat(testPokerGame.calculatePot()).isEqualTo(90) // 20 + 20 + 50
  }

  @Test
  fun `pot includes bets from folded players`() {
    // when
    testPokerGame.call(pokerPlayer3) // Player3 calls to 20
    testPokerGame.raiseBetTo(pokerPlayer1, 50) // Player1 raises to 50
    testPokerGame.fold(pokerPlayer2) // Player2 folds (still has 20 in pot)

    // then
    assertThat(testPokerGame.calculatePot()).isEqualTo(90) // 50 + 20 + 20
    assertThat(pokerPlayer2.isFolded).isTrue()
    assertThat(testPokerGame.calculatePot()).isEqualTo(pokerPlayer1.getBet() + pokerPlayer2.getBet() + pokerPlayer3.getBet())
  }
}