package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.Player
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class GameTestThreePlayers : AnnotationSpec() {
  private lateinit var player1: Player
  private lateinit var player2: Player
  private lateinit var player3: Player
  private lateinit var testGame: Game

  @BeforeEach
  fun setup() {
    player1 = Player("Hans")
    player2 = Player("Peter")
    player3 = Player("Max")
    testGame = Game(
      10, 20,
      listOf(player1, player2, player3),
      GameId("testGame100")
    )
  }

  @Test
  fun `check if get current player works correctly`() {
    // when
    val currentPlayer = testGame.getCurrentPlayer()

    // then
    assertThat(currentPlayer.name).isEqualTo("Max")
  }

  @Test
  fun `makeTurn advances currentPlayerIndex to next player`() {
    val initialPlayer = testGame.getCurrentPlayer()
    testGame.call(player3)
    val nextPlayer = testGame.getCurrentPlayer()

    assertThat(nextPlayer.name).isNotEqualTo(initialPlayer.name)
    assertThat(testGame.currentPlayerIndex).isEqualTo(0)
  }

  @Test
  fun `checkPlayerValidity returns false for existing players and true for new players`() {
    val existingPlayer = Player("Hans")
    val newPlayer = Player("Sara")

    assertThat(testGame.checkPlayerValidity(existingPlayer)).isFalse()
    assertThat(testGame.checkPlayerValidity(newPlayer)).isTrue()
  }

  @Test
  fun `getHighestBet is equal to big blind amount on init`() {
    assertThat(testGame.getHighestBet()).isEqualTo(20)
  }

  @Test
  fun `getHighestBet returns updated value when player raises`() {
    // when
    testGame.call(player3)
    testGame.raiseBetTo(player1, 50)

    // then
    assertThat(testGame.getHighestBet()).isEqualTo(50)
  }

  @Test
  fun `getHighestBet returns highest bet when multiple players have different bets`() {
    // when
    testGame.call(player3)
    testGame.raiseBetTo(player1, 40)
    testGame.call(player2)

    // then
    assertThat(testGame.getHighestBet()).isEqualTo(40)
    assertThat(player1.getBet()).isEqualTo(40)
    assertThat(player2.getBet()).isEqualTo(40)
    assertThat(player3.getBet()).isEqualTo(20)
  }

  @Test
  fun `getHighestBet still counts folded player bets`() {
    // when
    testGame.call(player3)
    testGame.raiseBetTo(player1, 50)
    testGame.fold(player2)

    // then
    assertThat(testGame.getHighestBet()).isEqualTo(50)
    assertThat(player2.isFolded).isTrue()
    assertThat(player2.getBet()).isEqualTo(20) // Player keeps their bet even when folded
  }

  @Test
  fun `big blind amount must be twice the smallBlind amount`() {
    assertThat(testGame.getBigBlind()).isEqualTo(testGame.getSmallBlind() * 2)
  }

  @Test
  fun `getSmallBlind returns correct small blind amount`() {
    assertThat(testGame.getSmallBlind()).isEqualTo(10)
  }

  @Test
  fun `getBigBlind returns correct big blind amount`() {
    assertThat(testGame.getBigBlind()).isEqualTo(20)
  }

  @Test
  fun `makeTurn cycles to first player at the end of table`() {
    testGame.call(player3)

    assertThat(testGame.getCurrentPlayer()).isEqualTo(player1)
  }

  @Test
  fun `makeTurn correctly sets currentPlayerIndex`() {
    testGame.call(player3)
    testGame.raiseBetTo(player1, 100)

    assertThat(testGame.currentPlayerIndex).isEqualTo(1)
  }

  @Test
  fun `dealHoleCards correctly assigns 2 cards to each player during initialization`() {
    assertThat(player1.getHand().getCards()).hasSize(2)
    assertThat(player2.getHand().getCards()).hasSize(2)
    assertThat(player3.getHand().getCards()).hasSize(2)
  }

  @Test
  fun `dealHoleCards removes correct number of cards from the deck`() {
    val playerCount = testGame.playersOnTable.size

    assertThat(testGame.getDeck().show()).hasSize(52 - (2 * playerCount))
  }

  @Test
  fun `dealHoleCards assigns unique cards to each player`() {
    // then - collect all cards from players' hands and check for uniqueness
    val allCards = player1.getHand().getCards() + player2.getHand().getCards() + player3.getHand().getCards()
    assertThat(allCards).hasSize(6) // 3 players * 2 cards
    assertThat(allCards.distinct()).hasSize(6) // All cards should be unique
  }

  // POT TESTS
  @Test
  fun `initial pot equals sum of blinds`() {
    // then
    assertThat(testGame.getPot()).isEqualTo(30) // Small blind(10) + Big blind(20)
    assertThat(testGame.getPot()).isEqualTo(player1.getBet() + player2.getBet() + player3.getBet())
  }

  @Test
  fun `pot increases when players bet or raise`() {
    // when
    testGame.call(player3) // Player3 calls big blind (20)
    testGame.raiseBetTo(player1, 50) // Player1 raises to 50

    // then
    assertThat(testGame.getPot()).isEqualTo(90) // 20 + 20 + 50
  }

  @Test
  fun `pot includes bets from folded players`() {
    // when
    testGame.call(player3) // Player3 calls to 20
    testGame.raiseBetTo(player1, 50) // Player1 raises to 50
    testGame.fold(player2) // Player2 folds (still has 20 in pot)

    // then
    assertThat(testGame.getPot()).isEqualTo(90) // 50 + 20 + 20
    assertThat(player2.isFolded).isTrue()
    assertThat(testGame.getPot()).isEqualTo(player1.getBet() + player2.getBet() + player3.getBet())
  }
}