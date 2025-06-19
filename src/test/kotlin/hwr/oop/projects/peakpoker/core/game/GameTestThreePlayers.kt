package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat


class GameTestThreePlayers : AnnotationSpec() {
  private lateinit var player1: PokerPlayer
  private lateinit var player2: PokerPlayer
  private lateinit var player3: PokerPlayer
  private lateinit var testGame: PokerGame

  @BeforeEach
  fun setup() {
    player1 = PokerPlayer("Hans")
    player2 = PokerPlayer("Peter")
    player3 = PokerPlayer("Max")
    testGame = PokerGame(
      10, 20,
      listOf(player1, player2, player3),
      GameId("testGame100")
    )
  }

  @Test
  fun `dealHoleCards correctly assigns 2 cards to each player during initialization`() {
    assertThat(player1.hand().cards).hasSize(2)
    assertThat(player2.hand().cards).hasSize(2)
    assertThat(player3.hand().cards).hasSize(2)
  }

  @Test
  fun `dealHoleCards assigns unique cards to each player`() {
    // then - collect all cards from players' hands and check for uniqueness
    val allCards =
      player1.hand().cards + player2.hand().cards + player3.hand().cards
    assertThat(allCards).hasSize(6) // 3 players * 2 cards
    assertThat(allCards.distinct()).hasSize(6) // All cards should be unique
  }

  @Test
  fun `test if GameId value returns correct value`() {
    // when
    val gameId = testGame.id.value

    // then
    assertThat(gameId).isEqualTo("testGame100")
  }
}