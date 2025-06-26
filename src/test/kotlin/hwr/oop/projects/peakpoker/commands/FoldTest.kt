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

class FoldTest : AnnotationSpec() {

  private lateinit var mockLoadGamePort: LoadGamePort
  private lateinit var mockSaveGamePort: SaveGamePort

  private val testGameId = "test-game-456"
  private val testPlayerName = "Bob"
  private val gameIdObject = GameId(testGameId)

  @BeforeEach
  fun setup() {
    mockLoadGamePort = mock()
    mockSaveGamePort = mock()
  }

  @Test
  fun `test fold command with valid input success`() {
    val mockGame: PokerGame = mock {
      on { id } doReturn gameIdObject
    }
    whenever(mockLoadGamePort.loadGame(testGameId)).doReturn(mockGame)
    whenever(mockSaveGamePort.saveGame(mockGame)).doReturn(gameIdObject)

    val command = Fold(mockLoadGamePort, mockSaveGamePort)

    val result = command.test(
      "--gameID", testGameId,
      "--player", testPlayerName
    )

    verify(mockLoadGamePort).loadGame(testGameId)
    verify(mockGame).fold(testPlayerName)
    verify(mockSaveGamePort).saveGame(mockGame)

    assertThat(result.output).contains("Player $testPlayerName folded their hand in game $testGameId")
    assertThat(result.statusCode).isEqualTo(0)
  }

  @Test
  fun `test fold command when game loading fails`() {
    val errorMessage = "Game not found for folding: $testGameId"
    whenever(mockLoadGamePort.loadGame(testGameId)).doThrow(
      IllegalStateException(errorMessage)
    )

    val command = Fold(mockLoadGamePort, mockSaveGamePort)

    val result = command.test(
      "--gameID", testGameId,
      "--player", testPlayerName
    )

    assertThat(result.output).contains("Error folding hand: $errorMessage")
    verify(mockLoadGamePort).loadGame(testGameId)
    verify(mockSaveGamePort, never()).saveGame(any())
  }

  @Test
  fun `test fold command when fold operation fails`() {
    val errorMessage = "Player $testPlayerName cannot fold right now"
    val mockGame: PokerGame = mock {
      on { id } doReturn gameIdObject
    }
    whenever(mockLoadGamePort.loadGame(testGameId)).doReturn(mockGame)
    whenever(mockGame.fold(testPlayerName)).doThrow(
      IllegalStateException(errorMessage)
    )

    val command = Fold(mockLoadGamePort, mockSaveGamePort)

    val result = command.test(
      "--gameID", testGameId,
      "--player", testPlayerName
    )

    assertThat(result.output).contains("Error folding hand: $errorMessage")
    verify(mockLoadGamePort).loadGame(testGameId)
    verify(mockGame).fold(testPlayerName)
    verify(mockSaveGamePort, never()).saveGame(any())
  }

  @Test
  fun `test fold command help message`() {
    val command = Fold(mockLoadGamePort, mockSaveGamePort)

    val result = command.test("--help")

    assertThat(result.output).contains("Fold your hand in the game")
    assertThat(result.output).contains("--gameID=<text>  Game ID")
    assertThat(result.output).contains("--player=<text>  Player name")
    assertThat(result.statusCode).isEqualTo(0)
  }
}
