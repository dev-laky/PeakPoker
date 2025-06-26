package hwr.oop.projects.peakpoker.persistence

import hwr.oop.projects.peakpoker.core.game.PokerGame
import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class GameStorageTest : AnnotationSpec() {
  @Test
  fun `GameStorage can be created with default empty games map`() {
    val storage = GameStorage()

    assertThat(storage.games).isEmpty()
  }

  @Test
  fun `GameStorage can be created with provided games map`() {
    val testGame = PokerGame(
      smallBlindAmount = 10,
      bigBlindAmount = 20,
      players = listOf(
        PokerPlayer("Alice", 1000),
        PokerPlayer("Bob", 1000)
      )
    )
    val gamesMap = mapOf(testGame.id.value to testGame)
    val storage = GameStorage(gamesMap)

    assertThat(storage.games).isEqualTo(gamesMap)
    assertThat(storage.games).hasSize(1)
  }

  @Test
  fun `GameStorage copy creates new instance with updated games`() {
    val originalStorage = GameStorage()
    val testGame = PokerGame(
      smallBlindAmount = 10,
      bigBlindAmount = 20,
      players = listOf(
        PokerPlayer("Alice", 1000),
        PokerPlayer("Bob", 1000)
      )
    )
    val newGames = mapOf(testGame.id.value to testGame)

    val updatedStorage = originalStorage.copy(games = newGames)

    assertThat(originalStorage.games).isEmpty()
    assertThat(updatedStorage.games).isEqualTo(newGames)
  }

  @Test
  fun `GameStorage equality works correctly`() {
    val storage1 = GameStorage()
    val storage2 = GameStorage()
    val storage3 = GameStorage(
      mapOf(
        "test" to PokerGame(
          smallBlindAmount = 10,
          bigBlindAmount = 20,
          players = listOf(
            PokerPlayer("Alice", 1000),
            PokerPlayer("Bob", 1000)
          )
        )
      )
    )

    assertThat(storage1).isEqualTo(storage2)
    assertThat(storage1).isNotEqualTo(storage3)
  }
}