package hwr.oop.projects.peakpoker.commands

import com.github.ajalt.clikt.testing.test
import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
import hwr.oop.projects.peakpoker.core.game.GameId
import hwr.oop.projects.peakpoker.core.game.PokerGame
import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import hwr.oop.projects.peakpoker.persistence.LoadGamePort
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class HandInfoTest : AnnotationSpec() {
  private lateinit var mockLoadGamePort: LoadGamePort

  private val testGameId = "test-game-QWE"
  private val testPlayerName = "Frank"
  private val gameIdObject = GameId(testGameId)

  @BeforeEach
  fun setup() {
    mockLoadGamePort = mock()
  }

  @Test
  fun `test hand command with valid input success`() {
    val card1 = Card(Suit.CLUBS, Rank.ACE)
    val card2 = Card(Suit.DIAMONDS, Rank.KING)
    val hand = HoleCards(listOf(card1, card2))

    val mockPlayer: PokerPlayer = mock {
      on { name } doReturn testPlayerName
      on { hand() } doReturn hand
    }

    val mockGame: PokerGame = mock {
      on { id } doReturn gameIdObject
      on { players } doReturn listOf(mockPlayer)
    }
    whenever(mockLoadGamePort.loadGame(testGameId)).doReturn(mockGame)

    val command = HandInfo(mockLoadGamePort)

    val result = command.test(
      "--gameID", testGameId,
      "--player", testPlayerName
    )

    assertThat(result.output).contains("PokerGame ID: $testGameId")
    assertThat(result.output).contains("PokerPlayer: $testPlayerName")
    assertThat(result.output).contains("Hand: CLUBS, ACE <-> DIAMONDS, KING")
    assertThat(result.statusCode).isEqualTo(0)
  }

  @Test
  fun `test hand command when game loading fails`() {
    val errorMessage = "Game not found for hand info: $testGameId"
    whenever(mockLoadGamePort.loadGame(testGameId)).doThrow(
      IllegalStateException(errorMessage)
    )

    val command = HandInfo(mockLoadGamePort)

    val result = command.test(
      "--gameID", testGameId,
      "--player", testPlayerName
    )

    assertThat(result.output).contains("Error retrieving hand information: $errorMessage")
  }

  @Test
  fun `test hand command when player not found`() {
    val mockGame: PokerGame = mock {
      on { id } doReturn gameIdObject
      on { players } doReturn emptyList()
    }
    whenever(mockLoadGamePort.loadGame(testGameId)).doReturn(mockGame)

    val command = HandInfo(mockLoadGamePort)

    val result = command.test(
      "--gameID", testGameId,
      "--player", testPlayerName
    )

    assertThat(result.output).contains("Player with name '$testPlayerName' not found in game with ID '$testGameId'.")
    assertThat(result.statusCode).isEqualTo(0)
  }

  @Test
  fun `test hand command when player has no cards`() {
    val emptyHand = HoleCards(emptyList())

    val mockPlayer: PokerPlayer = mock {
      on { name } doReturn testPlayerName
      on { hand() } doReturn emptyHand
    }

    val mockGame: PokerGame = mock {
      on { id } doReturn gameIdObject
      on { players } doReturn listOf(mockPlayer)
    }
    whenever(mockLoadGamePort.loadGame(testGameId)).doReturn(mockGame)

    val command = HandInfo(mockLoadGamePort)

    val result = command.test(
      "--gameID", testGameId,
      "--player", testPlayerName
    )

    assertThat(result.output).contains("Player '$testPlayerName' has no cards in hand.")
    assertThat(result.statusCode).isEqualTo(0)
  }

  @Test
  fun `test hand command help message`() {
    val command = HandInfo(mockLoadGamePort)

    val result = command.test("--help")

    assertThat(result.output).contains("Show Hole Cards for Player.")
    assertThat(result.output).contains("--player=<text>  Colon-separated list of player names")
    assertThat(result.output).contains("--gameID=<text>  PokerGame ID")
    assertThat(result.statusCode).isEqualTo(0)
  }
}
