package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class GameBettingTest : AnnotationSpec() {
  private fun setPrivateField(obj: Any, fieldName: String, value: Any?) {
    val field = obj.javaClass.getDeclaredField(fieldName)
    field.isAccessible = true
    field.set(obj, value)
  }

  @Test
  fun `raiseBetTo actually calls underlying round raiseBetTo method`() {
    val players = listOf(
      PokerPlayer("Alice", 200),
      PokerPlayer("Bob", 200)
    )

    val game = PokerGame(10, 20, players)

    setPrivateField(game, "hasEnded", false)

    val initialAliceChips = players[0].chips()
    val raiseAmount = 50

    game.raiseBetTo("Alice", raiseAmount)

    val finalAliceChips = players[0].chips()

    assertThat(finalAliceChips).isNotEqualTo(initialAliceChips)
    assertThat(finalAliceChips).isLessThan(initialAliceChips)
  }

  @Test
  fun `raiseBetTo produces different game state than no action`() {
    val players1 = listOf(
      PokerPlayer("Alice", 200),
      PokerPlayer("Bob", 200)
    )

    val players2 = listOf(
      PokerPlayer("Alice", 200),
      PokerPlayer("Bob", 200)
    )

    val game1 = PokerGame(10, 20, players1)
    val game2 = PokerGame(10, 20, players2)

    setPrivateField(game1, "hasEnded", false)
    setPrivateField(game2, "hasEnded", false)

    game1.raiseBetTo("Alice", 60)

    assertThat(players1[0].chips()).isNotEqualTo(players2[0].chips())
  }

  @Test
  fun `raiseBetTo changes player chip count verifying delegation`() {
    val players = listOf(
      PokerPlayer("Alice", 250),
      PokerPlayer("Bob", 250)
    )

    val game = PokerGame(10, 20, players)

    setPrivateField(game, "hasEnded", false)

    val aliceInitialChips = players[0].chips()
    val bobInitialChips = players[1].chips()

    game.raiseBetTo("Alice", 75)

    assertThat(players[0].chips()).isLessThan(aliceInitialChips)
    assertThat(players[1].chips()).isEqualTo(bobInitialChips)
  }

  @Test
  fun `fold produces different game state than no action`() {
    val players1 = listOf(
      PokerPlayer("Alice", 200),
      PokerPlayer("Bob", 200)
    )

    val players2 = listOf(
      PokerPlayer("Alice", 200),
      PokerPlayer("Bob", 200)
    )

    val game1 = PokerGame(10, 20, players1)
    val game2 = PokerGame(10, 20, players2)

    setPrivateField(game1, "hasEnded", false)
    setPrivateField(game2, "hasEnded", false)

    game1.fold("Alice")

    // Games should be in different states after one has a fold action
    assertThat(game1.toString()).isNotEqualTo(game2.toString())
  }

  @Test
  fun `fold does not change player chip count verifying delegation`() {
    val players = listOf(
      PokerPlayer("Alice", 250),
      PokerPlayer("Bob", 250)
    )

    val game = PokerGame(10, 20, players)

    setPrivateField(game, "hasEnded", false)

    val aliceInitialChips = players[0].chips()
    val bobInitialChips = players[1].chips()

    game.fold("Alice")

    // Fold should not change chip counts directly (chips are only moved at end of round)
    assertThat(players[0].chips()).isEqualTo(aliceInitialChips)
    assertThat(players[1].chips()).isEqualTo(bobInitialChips)
  }

  @Test
  fun `check actually calls underlying round check method`() {
    val players = listOf(
      PokerPlayer("Alice", 200),
      PokerPlayer("Bob", 200),
      PokerPlayer("Charlie", 200)
    )

    val game = PokerGame(10, 20, players)

    setPrivateField(game, "hasEnded", false)

    val initialRoundInfo = game.getGameInfo().roundInfo

    game.call("Charlie")
    game.call("Alice")
    game.check("Alice")

    val finalRoundInfo = game.getGameInfo().roundInfo

    assertThat(finalRoundInfo).isNotEqualTo(initialRoundInfo)
  }

  @Test
  fun `check produces different game state than no action`() {
    val players1 = listOf(
      PokerPlayer("Alice", 200),
      PokerPlayer("Bob", 200),
      PokerPlayer("Charlie", 200)
    )

    val players2 = listOf(
      PokerPlayer("Alice", 200),
      PokerPlayer("Bob", 200),
      PokerPlayer("Charlie", 200)
    )

    val game1 = PokerGame(10, 20, players1)
    val game2 = PokerGame(10, 20, players2)

    setPrivateField(game1, "hasEnded", false)
    setPrivateField(game2, "hasEnded", false)

    game1.call("Charlie")
    game1.call("Alice")
    game2.call("Charlie")
    game2.call("Alice")

    game1.check("Alice")

    val game1FinalInfo = game1.getGameInfo().roundInfo
    val game2FinalInfo = game2.getGameInfo().roundInfo

    assertThat(game1FinalInfo).isNotEqualTo(game2FinalInfo)
  }

  @Test
  fun `check does not change player chip count verifying delegation`() {
    val players = listOf(
      PokerPlayer("Alice", 250),
      PokerPlayer("Bob", 250),
      PokerPlayer("Charlie", 250)
    )

    val game = PokerGame(10, 20, players)

    setPrivateField(game, "hasEnded", false)

    game.call("Charlie")
    game.call("Alice")

    val aliceChipsAfterCall = players[0].chips()
    val bobChipsAfterCall = players[1].chips()
    val charlieChipsAfterCall = players[2].chips()

    game.check("Alice")

    assertThat(players[0].chips()).isEqualTo(aliceChipsAfterCall)
    assertThat(players[1].chips()).isEqualTo(bobChipsAfterCall)
    assertThat(players[2].chips()).isEqualTo(charlieChipsAfterCall)
  }
}