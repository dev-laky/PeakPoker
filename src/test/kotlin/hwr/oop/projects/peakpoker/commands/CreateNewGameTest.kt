package hwr.oop.projects.peakpoker.commands

import com.github.ajalt.clikt.testing.test
import hwr.oop.projects.peakpoker.core.game.GameId
import hwr.oop.projects.peakpoker.core.game.PokerGame
import hwr.oop.projects.peakpoker.persistence.SaveGamePort
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mockito.never
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CreateNewGameTest : AnnotationSpec() {
  private lateinit var mockSaveGamePort: SaveGamePort

  private val testGameId = "new-game-id-123"

  @BeforeEach
  fun setup() {
    mockSaveGamePort = mock()
  }

  @Test
  fun `test new-game command when no players provided`() {
    val command = CreateNewGame(mockSaveGamePort)

    val result = command.test("")

    assertThat(result.output).contains("Error: missing option --players")
    assertThat(result.statusCode).isEqualTo(1)
  }

  @Test
  fun `test new-game command when players option is empty`() {
    val command = CreateNewGame(mockSaveGamePort)

    val result = command.test("--players", "")

    assertThat(result.output).contains("Error: option --players requires a value")
    assertThat(result.statusCode).isEqualTo(1)
    verify(mockSaveGamePort, never()).saveGame(any())
  }

  @Test
  fun `test new-game command when PokerGame creation fails due to invalid player count`() {
    val playerNames = "SoloPlayer"

    val command = CreateNewGame(mockSaveGamePort)

    val result = command.test(
      "--players", playerNames
    )

    assertThat(result.output).contains("Error: option --players requires a value")
    assertThat(result.statusCode).isEqualTo(1)
    verify(mockSaveGamePort, never()).saveGame(any<PokerGame>())
  }

  @Test
  fun `test new-game command help message`() {
    val command = CreateNewGame(mockSaveGamePort)

    val result = command.test("--help")

    assertThat(result.output).contains("Create a new Game.")
    assertThat(result.output).contains("--players=<value>  Colon-separated list of player names")
    assertThat(result.statusCode).isEqualTo(0)
  }

  @Test
  fun `test new-game command with valid players success`() {
    val gameId = GameId(testGameId)

    whenever(mockSaveGamePort.saveGame(any<PokerGame>())).thenReturn(gameId)

    val command = CreateNewGame(mockSaveGamePort)

    val result = command.test("--players=John:Jane:Mike")

    verify(mockSaveGamePort).saveGame(any<PokerGame>())

    assertThat(result.output).contains("Game ID: $testGameId")
    assertThat(result.output).contains("New game created with players: John, Jane, Mike")
    assertThat(result.statusCode).isEqualTo(0)
  }

  @Test
  fun `test new-game command when players list is null after conversion`() {
    val command = CreateNewGame(mockSaveGamePort)

    val result = command.test("--players=")

    assertThat(result.output).contains("PokerPlayer name cannot be blank")
    verify(mockSaveGamePort, never()).saveGame(any())
  }

  @Test
  fun `test new-game command when players list is empty after split`() {
    val command = CreateNewGame(mockSaveGamePort)

    val result = command.test("--players=   :   :   ")

    assertThat(result.output).contains("Error: got unexpected extra arguments (: :)")
    verify(mockSaveGamePort, never()).saveGame(any())
  }

  @Test
  fun `test new-game command when GameException is thrown during game creation`() {
    val command = CreateNewGame(mockSaveGamePort)

    val result = command.test("--players=OnlyOnePlayer")

    assertThat(result.output).contains("Error creating game:")
    assertThat(result.statusCode).isEqualTo(0)
    verify(mockSaveGamePort, never()).saveGame(any())
  }

  @Test
  fun `test new-game command successful execution with trimmed player names`() {
    val command = CreateNewGame(mockSaveGamePort)

    val result = command.test("--players= John : Jane : Mike ")

    assertThat(result.output).contains("Error: got unexpected extra arguments (John : Jane ...)")
    verify(mockSaveGamePort, never()).saveGame(any())
  }
}
