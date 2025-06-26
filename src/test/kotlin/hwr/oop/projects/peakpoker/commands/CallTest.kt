package hwr.oop.projects.peakpoker.commands

import com.github.ajalt.clikt.testing.test
import hwr.oop.projects.peakpoker.core.game.GameId
import hwr.oop.projects.peakpoker.core.game.PokerGame
import hwr.oop.projects.peakpoker.persistence.LoadGamePort
import hwr.oop.projects.peakpoker.persistence.SaveGamePort
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CallTest : AnnotationSpec() {

  private lateinit var mockLoadGamePort: LoadGamePort
  private lateinit var mockSaveGamePort: SaveGamePort

  private val testGameId = "test-game-ABC"
  private val testPlayerName = "David"
  private val gameIdObject = GameId(testGameId)

  @BeforeEach
  fun setup() {
    mockLoadGamePort = mock()
    mockSaveGamePort = mock()
  }

  @Test
  fun `test call command with valid input success`() {
    val mockGame: PokerGame = mock {
      on { id } doReturn gameIdObject
    }
    whenever(mockLoadGamePort.loadGame(testGameId)).doReturn(mockGame)
    whenever(mockSaveGamePort.saveGame(mockGame)).doReturn(gameIdObject)

    val command = Call(mockLoadGamePort, mockSaveGamePort)

    val result = command.test(
      "--gameID", testGameId,
      "--player", testPlayerName
    )

    verify(mockLoadGamePort).loadGame(testGameId)
    verify(mockGame).call(testPlayerName)
    verify(mockSaveGamePort).saveGame(mockGame)

    assertThat(result.output).contains("Player $testPlayerName called the bet in game $testGameId")
    assertThat(result.statusCode).isEqualTo(0)
  }

  @Test
  fun `test call command when game loading fails`() {
    val errorMessage = "Game not found for calling: $testGameId"
    whenever(mockLoadGamePort.loadGame(testGameId)).doThrow(
      IllegalStateException(errorMessage)
    )

    val command = Call(mockLoadGamePort, mockSaveGamePort)

    val result = command.test(
      "--gameID", testGameId,
      "--player", testPlayerName
    )

    assertThat(result.output).contains("Error calling bet: $errorMessage")
    verify(mockLoadGamePort).loadGame(testGameId)
    verify(mockSaveGamePort, never()).saveGame(any())
  }

  @Test
  fun `test call command when call operation fails`() {
    val errorMessage = "Player $testPlayerName cannot call right now"
    val mockGame: PokerGame = mock {
      on { id } doReturn gameIdObject
    }
    whenever(mockLoadGamePort.loadGame(testGameId)).doReturn(mockGame)
    whenever(mockGame.call(testPlayerName)).doThrow(
      IllegalStateException(errorMessage)
    )

    val command = Call(mockLoadGamePort, mockSaveGamePort)

    val result = command.test(
      "--gameID", testGameId,
      "--player", testPlayerName
    )

    assertThat(result.output).contains("Error calling bet: $errorMessage")
    verify(mockLoadGamePort).loadGame(testGameId)
    verify(mockGame).call(testPlayerName)
    verify(mockSaveGamePort, never()).saveGame(any())
  }

  @Test
  fun `test call command help message`() {
    val command = Call(mockLoadGamePort, mockSaveGamePort)

    val result = command.test("--help")

    assertThat(result.output).contains("Call the current bet in the game")
    assertThat(result.output).contains("--gameID=<text>  Game ID")
    assertThat(result.output).contains("--player=<text>  Player name")
    assertThat(result.statusCode).isEqualTo(0)
  }
}
