package hwr.oop.projects.peakpoker.persistence

import hwr.oop.projects.peakpoker.core.game.GameId
import hwr.oop.projects.peakpoker.core.game.PokerGame
import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import java.io.File

class FileSystemPersistenceAdapterTest : AnnotationSpec() {
  private lateinit var tempFile: File
  private lateinit var adapter: FileSystemPersistenceAdapter
  private lateinit var testGame: PokerGame

  @BeforeEach
  fun setup() {
    tempFile = File("poker_data_test.json")
    adapter = FileSystemPersistenceAdapter(tempFile)

    val players = listOf(
      PokerPlayer("Alice", 1000),
      PokerPlayer("Bob", 1000)
    )
    testGame = PokerGame(
      smallBlindAmount = 10,
      bigBlindAmount = 20,
      players = players
    )
  }

  @AfterEach
  fun cleanup() {
    if (tempFile.exists()) {
      tempFile.delete()
    }
  }

  @Test
  fun `saveGame creates file when it does not exist`() {
    tempFile.delete()
    assertThat(tempFile.exists()).isFalse()

    adapter.saveGame(testGame)

    assertThat(tempFile.exists()).isTrue()
  }

  @Test
  fun `saveGame returns game id`() {
    val returnedId = adapter.saveGame(testGame)

    assertThat(returnedId).isEqualTo(testGame.id)
  }

  @Test
  fun `saveGame stores game in json format`() {
    adapter.saveGame(testGame)

    val content = tempFile.readText()
    assertThat(content).contains("\"games\"")
    assertThat(content).contains(testGame.id.value)
  }

  @Test
  fun `loadGame loads existing game by id`() {
    adapter.saveGame(testGame)

    val loadedGame = adapter.loadGame(testGame.id.value)

    assertThat(loadedGame.id).isEqualTo(testGame.id)
    assertThat(loadedGame.players).hasSize(2)
  }

  @Test
  fun `loadGame throws exception when file does not exist`() {
    tempFile.delete()

    assertThatThrownBy { adapter.loadGame(testGame.id.value) }
      .isExactlyInstanceOf(IllegalStateException::class.java)
      .hasMessageContaining("Error loading storage")
  }

  @Test
  fun `loadGame throws exception when game id not found`() {
    adapter.saveGame(testGame)
    val nonExistentId = GameId.generate()

    assertThatThrownBy { adapter.loadGame(nonExistentId.value) }
      .isExactlyInstanceOf(IllegalStateException::class.java)
      .hasMessageContaining("Game not found: ${nonExistentId.value}")
  }

  @Test
  fun `saveGame preserves existing games when adding new game`() {
    val firstGame = testGame
    adapter.saveGame(firstGame)

    val secondGame = PokerGame(
      smallBlindAmount = 5,
      bigBlindAmount = 10,
      players = listOf(
        PokerPlayer("Charlie", 500),
        PokerPlayer("David", 500)
      )
    )
    adapter.saveGame(secondGame)

    val loadedFirst = adapter.loadGame(firstGame.id.value)
    val loadedSecond = adapter.loadGame(secondGame.id.value)

    assertThat(loadedFirst.id).isEqualTo(firstGame.id)
    assertThat(loadedSecond.id).isEqualTo(secondGame.id)
  }

  @Test
  fun `saveGame overwrites existing game with same id`() {
    adapter.saveGame(testGame)

    // Modify the game state (simulate game progress)
    testGame.call("Alice")
    adapter.saveGame(testGame)

    val loadedGame = adapter.loadGame(testGame.id.value)
    assertThat(loadedGame.id).isEqualTo(testGame.id)
  }

  @Test
  fun `loadStorage returns empty storage when file does not exist`() {
    tempFile.delete()

    // Access private method via reflection for testing
    val method =
      FileSystemPersistenceAdapter::class.java.getDeclaredMethod("loadStorage")
    method.isAccessible = true
    val storage = method.invoke(adapter) as GameStorage

    assertThat(storage.games).isEmpty()
  }

  @Test
  fun `loadStorage throws exception when json is corrupted`() {
    tempFile.writeText("invalid json content")

    assertThatThrownBy { adapter.loadGame(testGame.id.value) }
      .isExactlyInstanceOf(IllegalStateException::class.java)
      .hasMessageContaining("Error loading storage")
  }

  @Test
  fun `json output is pretty printed for readability`() {
    adapter.saveGame(testGame)

    val content = tempFile.readText()
    assertThat(content).contains("\n")
    assertThat(content).contains("  ")
  }

  @Test
  fun `multiple games can be stored and loaded independently`() {
    val games = (1..5).map { index ->
      PokerGame(
        smallBlindAmount = 10,
        bigBlindAmount = 20,
        players = listOf(
          PokerPlayer("Player${index}A", 1000),
          PokerPlayer("Player${index}B", 1000)
        )
      )
    }

    games.forEach { adapter.saveGame(it) }

    games.forEach { originalGame ->
      val loadedGame = adapter.loadGame(originalGame.id.value)
      assertThat(loadedGame.id).isEqualTo(originalGame.id)
    }
  }

  @Test
  fun `empty file is handled correctly`() {
    tempFile.writeText("")

    assertThatThrownBy { adapter.loadGame(testGame.id.value) }
      .isExactlyInstanceOf(IllegalStateException::class.java)
      .hasMessageContaining("Error loading storage")
  }

  @Test
  fun `file with only whitespace is handled correctly`() {
    tempFile.writeText("   \n  \t  ")

    assertThatThrownBy { adapter.loadGame(testGame.id.value) }
      .isExactlyInstanceOf(IllegalStateException::class.java)
      .hasMessageContaining("Error loading storage")
  }

  @Test
  fun `saveGame outputs pretty printed json with proper formatting`() {
    adapter.saveGame(testGame)

    val content = tempFile.readText()
    assertThat(content).contains("{\n")
    assertThat(content).contains("  \"games\"")
    assertThat(content).contains("\n}")
  }

  @Test
  fun `saveGame includes default values in json output`() {
    val gameWithDefaults = PokerGame(
      smallBlindAmount = 10,
      bigBlindAmount = 20,
      players = listOf(
        PokerPlayer("Alice", 1000),
        PokerPlayer("Bob", 1000)
      )
    )

    adapter.saveGame(gameWithDefaults)

    val content = tempFile.readText()
    val hasRequiredFields = content.contains("\"smallBlindAmount\"") &&
        content.contains("\"bigBlindAmount\"") &&
        content.contains("\"players\"")
    assertThat(hasRequiredFields).isTrue()
  }
}