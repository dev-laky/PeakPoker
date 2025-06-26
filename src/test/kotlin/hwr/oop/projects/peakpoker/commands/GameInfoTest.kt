package hwr.oop.projects.peakpoker.commands

import com.github.ajalt.clikt.testing.test
import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.game.GameId
import hwr.oop.projects.peakpoker.core.game.PokerGame
import hwr.oop.projects.peakpoker.persistence.LoadGamePort
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import hwr.oop.projects.peakpoker.core.card.Suit
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.game.RoundInfo
import hwr.oop.projects.peakpoker.core.game.RoundPhase
import hwr.oop.projects.peakpoker.core.player.PlayerInfo
import hwr.oop.projects.peakpoker.core.pot.PotInfo
import hwr.oop.projects.peakpoker.core.game.GameInfo

class GameInfoTest : AnnotationSpec() {
  private lateinit var mockLoadGamePort: LoadGamePort

  private val testGameId = "test-game-info-123"
  private val gameIdObject = GameId(testGameId)

  @BeforeEach
  fun setup() {
    mockLoadGamePort = mock()
  }

  @Test
  fun `test game-info command with active game success`() {
    val player1Info =
      PlayerInfo("Alice", 900, 100, isFolded = false, isAllIn = false)
    val player2Info =
      PlayerInfo("Bob", 800, 20, isFolded = false, isAllIn = true)
    val player3Info =
      PlayerInfo("Charlie", 1000, 0, isFolded = true, isAllIn = false)
    val player4Info =
      PlayerInfo("David", 500, 0, isFolded = false, isAllIn = false)

    val communityCard1 = Card(Suit.HEARTS, Rank.TEN)
    val communityCard2 = Card(Suit.SPADES, Rank.ACE)
    val communityCard3 = Card(Suit.CLUBS, Rank.SEVEN)

    val mainPot = PotInfo(150, listOf("Alice", "Bob", "David"))
    val sidePot1 = PotInfo(50, listOf("Alice", "Bob"))

    val roundInfo = RoundInfo(
      roundPhase = RoundPhase.FLOP,
      smallBlindAmount = 10,
      smallBlindPlayerName = "Bob",
      bigBlindAmount = 20,
      currentPlayerName = "David",
      communityCards = listOf(communityCard1, communityCard2, communityCard3),
      pots = listOf(mainPot, sidePot1),
      players = listOf(player1Info, player2Info, player3Info, player4Info)
    )

    val gameInfo = GameInfo(
      gameId = testGameId,
      hasEnded = false,
      roundInfo = roundInfo
    )

    val mockGame: PokerGame = mock {
      on { id } doReturn gameIdObject
      on { getGameInfo() } doReturn gameInfo
    }
    whenever(mockLoadGamePort.loadGame(testGameId)).doReturn(mockGame)

    val command = GameInfo(mockLoadGamePort)

    val result = command.test(
      "--gameID=$testGameId"
    )

    assertThat(result.output).contains("=== GAME INFORMATION ===")
    assertThat(result.output).contains("Game ID: $testGameId")
    assertThat(result.output).contains("Game Status: ACTIVE")

    assertThat(result.output).contains("=== ROUND INFORMATION ===")
    assertThat(result.output).contains("Round Phase: FLOP")
    assertThat(result.output).contains("Small Blind Amount: 10 (Player: Bob)")
    assertThat(result.output).contains("Big Blind Amount: 20")
    assertThat(result.output).contains("Current Player: David")

    assertThat(result.output).contains("=== COMMUNITY CARDS ===")
    assertThat(result.output).contains("Cards: HEARTS, TEN <-> SPADES, ACE <-> CLUBS, SEVEN")

    assertThat(result.output).contains("=== POTS ===")
    assertThat(result.output).contains("Main Pot: 150 (Eligible: Alice, Bob, David)")
    assertThat(result.output).contains("Side Pot 1: 50 (Eligible: Alice, Bob)")

    assertThat(result.output).contains("=== PLAYERS ===")
    assertThat(result.output).contains("Alice: 900 chips, bet: 100")
    assertThat(result.output).contains("Bob: 800 chips, bet: 20 [ALL-IN]")
    assertThat(result.output).contains("Charlie: 1000 chips, bet: 0 [FOLDED]")
    assertThat(result.output).contains("David: 500 chips, bet: 0 [CURRENT TURN]")
    assertThat(result.statusCode).isEqualTo(0)
  }

  @Test
  fun `test game-info command with empty community cards`() {
    val player1Info =
      PlayerInfo("Alice", 900, 100, isFolded = false, isAllIn = false)

    val mainPot = PotInfo(150, listOf("Alice"))

    val roundInfo = RoundInfo(
      roundPhase = RoundPhase.PRE_FLOP, // Pre-flop usually has no community cards
      smallBlindAmount = 10,
      smallBlindPlayerName = "Alice",
      bigBlindAmount = 20,
      currentPlayerName = "Alice",
      communityCards = emptyList(), // Empty list for this test
      pots = listOf(mainPot),
      players = listOf(player1Info)
    )

    val gameInfo = GameInfo(
      gameId = testGameId,
      hasEnded = false,
      roundInfo = roundInfo
    )

    val mockGame: PokerGame = mock {
      on { id } doReturn gameIdObject
      on { getGameInfo() } doReturn gameInfo
    }
    whenever(mockLoadGamePort.loadGame(testGameId)).doReturn(mockGame)

    val command = GameInfo(mockLoadGamePort)

    val result = command.test(
      "--gameID=$testGameId"
    )

    assertThat(result.output).contains("=== COMMUNITY CARDS ===")
    assertThat(result.output).contains("No community cards dealt yet.")
    assertThat(result.statusCode).isEqualTo(0)
  }

  @Test
  fun `test game-info command with ended game success`() {
    val gameInfo = GameInfo(
      gameId = testGameId,
      hasEnded = true,
      roundInfo = null
    )

    val mockGame: PokerGame = mock {
      on { id } doReturn gameIdObject
      on { getGameInfo() } doReturn gameInfo
    }
    whenever(mockLoadGamePort.loadGame(testGameId)).doReturn(mockGame)

    val command = GameInfo(mockLoadGamePort)

    val result = command.test(
      "--gameID=$testGameId"
    )

    assertThat(result.output).contains("=== GAME INFORMATION ===")
    assertThat(result.output).contains("Game ID: $testGameId")
    assertThat(result.output).contains("Game Status: ENDED")
    assertThat(result.output).contains("No active round")
    assertThat(result.statusCode).isEqualTo(0)
  }

  @Test
  fun `test game-info command when game loading fails`() {
    val errorMessage = "Game not found: $testGameId"
    whenever(mockLoadGamePort.loadGame(testGameId)).doThrow(
      IllegalStateException(errorMessage)
    )

    val command = GameInfo(mockLoadGamePort)

    val result = command.test(
      "--gameID=$testGameId"
    )

    assertThat(result.output).contains("Error retrieving game information: Game not found: $testGameId")
  }

  @Test
  fun `test game-info command help message`() {
    val command = GameInfo(mockLoadGamePort)

    val result = command.test("--help")

    assertThat(result.output).contains("Retrieve information about whole game state.")
    assertThat(result.output).contains("--gameID=<text>  PokerGame ID")
    assertThat(result.statusCode).isEqualTo(0)
  }
}
