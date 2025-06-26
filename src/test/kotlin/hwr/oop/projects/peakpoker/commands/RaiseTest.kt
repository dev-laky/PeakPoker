package hwr.oop.projects.peakpoker.commands

import com.github.ajalt.clikt.testing.test
import hwr.oop.projects.peakpoker.core.game.GameId
import hwr.oop.projects.peakpoker.core.game.PokerGame
import hwr.oop.projects.peakpoker.persistence.LoadGamePort
import hwr.oop.projects.peakpoker.persistence.SaveGamePort
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RaiseTest : AnnotationSpec() {

  private lateinit var mockLoadGamePort: LoadGamePort
  private lateinit var mockSaveGamePort: SaveGamePort

  private val testGameId = "test-game-123"
  private val testPlayerName = "Alice"
  private val testAmount = 100
  private val gameIdObject = GameId(testGameId)

  @BeforeEach
  fun setup() {
    mockLoadGamePort = mock()
    mockSaveGamePort = mock()
  }

  @Test
  fun `test raise command with valid input success`() {
    val mockGame: PokerGame = mock {
      on { id } doReturn gameIdObject
    }
    whenever(mockLoadGamePort.loadGame(testGameId)).doReturn(mockGame)
    whenever(mockSaveGamePort.saveGame(mockGame)).doReturn(gameIdObject)

    val command = Raise(mockLoadGamePort, mockSaveGamePort)

    val result = command.test(
      "$testAmount",
      "--gameID", testGameId,
      "--player", testPlayerName
    )

    verify(mockLoadGamePort).loadGame(testGameId)
    verify(mockGame).raiseBetTo(testPlayerName, testAmount)
    verify(mockSaveGamePort).saveGame(mockGame)

    assertThat(result.output).contains("Player $testPlayerName raised bet to $testAmount chips in game $testGameId")
    assertThat(result.statusCode).isEqualTo(0)
  }

  @Test
  fun `test raise command with invalid amount argument`() {
    val command = Raise(mockLoadGamePort, mockSaveGamePort)
    val invalidAmount = "not-a-number"

    val result = command.test(
      invalidAmount,
      "--gameID", testGameId,
      "--player", testPlayerName
    )

    assertThat(result.output).contains("Error: invalid value for <amount>: Amount must be a number")
    assertThat(result.statusCode).isEqualTo(1)
  }

  @Test
  fun `test raise command when game loading fails`() {
    val errorMessage = "Game not found: $testGameId"
    whenever(mockLoadGamePort.loadGame(testGameId)).doThrow(
      IllegalStateException(errorMessage)
    )

    val command = Raise(mockLoadGamePort, mockSaveGamePort)

    val result = command.test(
      "$testAmount",
      "--gameID", testGameId,
      "--player", testPlayerName
    )

    assertThat(result.output).contains("Error raising bet: $errorMessage")
    verify(mockLoadGamePort).loadGame(testGameId)
  }

  @Test
  fun `test raise command when raiseBetTo fails`() {
    val errorMessage = "Amount must be a number"
    val mockGame: PokerGame = mock {
      on { id } doReturn gameIdObject
    }
    whenever(mockLoadGamePort.loadGame(testGameId)).doReturn(mockGame)
    whenever(mockGame.raiseBetTo(testPlayerName, testAmount)).doThrow(
      IllegalStateException(errorMessage)
    )

    val command = Raise(mockLoadGamePort, mockSaveGamePort)

    val result = command.test(
      "$testAmount",
      "--gameID", testGameId,
      "--player", testPlayerName
    )

    assertThat(result.output).contains("Error raising bet: $errorMessage")
    verify(mockLoadGamePort).loadGame(testGameId)
    verify(mockGame).raiseBetTo(testPlayerName, testAmount)
  }

  @Test
  fun `test raise command help message`() {
    val command = Raise(mockLoadGamePort, mockSaveGamePort)

    val result = command.test("--help")

    assertThat(result.output).contains("Raise bet to specified amount")
    assertThat(result.output).contains("--gameID=<text>  Game ID")
    assertThat(result.output).contains("--player=<text>  Player name")
    assertThat(result.statusCode).isEqualTo(0)
  }
}