package hwr.oop.projects.peakpoker.persistence

import hwr.oop.projects.peakpoker.core.game.GameId
import hwr.oop.projects.peakpoker.core.game.PokerGame
import kotlinx.serialization.json.Json
import java.io.File

class FileSystemPersistenceAdapter(private val file: File) :
  SaveGamePort,
  LoadGamePort {

  /**
   * Custom exception thrown when the file does not exist
   */
  class FileNotFoundException(message: String) : IllegalStateException(message)

  /**
   * Custom exception thrown when the game is not found in the storage
   */
  class GameNotFoundException(message: String) : IllegalStateException(message)

  /**
   * Custom exception thrown when there is an error loading the storage
   */
  class StorageLoadException(message: String, cause: Throwable) :
    IllegalStateException(message, cause)

  private val json = Json { prettyPrint = true; encodeDefaults = true }

  override fun loadGame(gameId: String): PokerGame {
    if (!file.exists()) {
      throw FileNotFoundException("Error loading storage: File does not exist")
    }

    val storage = loadStorage()
    return storage.games[gameId]
      ?: throw GameNotFoundException("Game not found: $gameId")
  }

  override fun saveGame(game: PokerGame): GameId {
    val storage = loadStorage()
    val updatedGames = storage.games + (game.id.value to game)
    val updatedStorage = storage.copy(games = updatedGames)

    file.writeText(json.encodeToString(updatedStorage))
    return game.id
  }

  private fun loadStorage(): GameStorage {
    if (!file.exists()) return GameStorage()

    return try {
      json.decodeFromString<GameStorage>(file.readText())
    } catch (e: Exception) {
      throw StorageLoadException("Error loading storage: ${e.message}", e)
    }
  }
}