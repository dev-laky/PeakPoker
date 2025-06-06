package hwr.oop.projects.peakpoker.core.player

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
import hwr.oop.projects.peakpoker.core.exceptions.InvalidBetAmountException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidPlayerStateException
import hwr.oop.projects.peakpoker.core.exceptions.InsufficientChipsException
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class PlayerTest : AnnotationSpec() {
  @Test
  fun `player has name`() {
    val player = PokerPlayer("Hans")
    val playerName: String = player.name
    assertThat(playerName).isEqualTo("Hans")
  }

  @Test
  fun `player bet can be raised`() {
    val player = PokerPlayer("Hans")
    player.setBetAmount(10)
    assertThat(player.bet()).isEqualTo(10)
  }

  @Test
  fun `player initializes with isFolded and isAllIn as false`() {
    val player = PokerPlayer("Hans")
    assertThat(player.isFolded()).isFalse()
    assertThat(player.isAllIn()).isFalse()
  }

  @Test
  fun `getChips returns correct initial chip count`() {
    val initialChips = 500
    val player = PokerPlayer("Hans", initialChips)
    assertThat(player.chips()).isEqualTo(initialChips)
  }

  @Test
  fun `assignHand correctly assigns hole cards to player`() {
    val player = PokerPlayer("Hans")

    val holeCards = HoleCards(
      listOf(
        Card(Suit.DIAMONDS, Rank.FIVE),
        Card(Suit.DIAMONDS, Rank.SIX)
      ),
      player
    )

    player.assignHand(holeCards)

    assertThat(player.hand()).isEqualTo(holeCards)
  }

  @Test
  fun `betting reduces player chip count`() {
    val initialChips = 500
    val betAmount = 100
    val player = PokerPlayer("Hans", initialChips)

    player.setBetAmount(betAmount)

    assertThat(player.chips()).isEqualTo(initialChips - betAmount)
    assertThat(player.bet()).isEqualTo(betAmount)
  }

  @Test
  fun `multiple bets accumulate correctly`() {
    val initialChips = 500
    val player = PokerPlayer("Hans", initialChips)

    player.setBetAmount(100)
    player.setBetAmount(150)

    assertThat(player.bet()).isEqualTo(150)
    assertThat(player.chips()).isEqualTo(initialChips - 150)
  }

  @Test
  fun `bet validation throws exception for negative amounts`() {
    val player = PokerPlayer("Hans", 500)

    assertThatThrownBy { player.setBetAmount(-1) }
      .isExactlyInstanceOf(InvalidBetAmountException::class.java)
      .hasMessageContaining("Chips amount must be greater than zero")

    assertThat(player.bet()).isEqualTo(0)
    assertThat(player.chips()).isEqualTo(500)
  }

  @Test
  fun `bet of zero amount is not accepted`() {
    val player = PokerPlayer("Hans", 500)

    assertThatThrownBy { player.setBetAmount(0) }
      .isExactlyInstanceOf(InvalidBetAmountException::class.java)
      .hasMessageContaining("Chips amount must be greater than zero")

    assertThat(player.bet()).isEqualTo(0)
    assertThat(player.chips()).isEqualTo(500)
  }

  @Test
  fun `player creation with negative chips throws exception`() {
    assertThatThrownBy {
      PokerPlayer("TestPlayer", -100)
    }
      .isExactlyInstanceOf(InsufficientChipsException::class.java)
      .hasMessageContaining("Chips amount must be non-negative")
  }

  @Test
  fun `player creation with zero chips is valid`() {
    val player = PokerPlayer("ZeroChipsPlayer", 0)
    assertThat(player.chips()).isEqualTo(0)
  }

  @Test
  fun `player creation with positive chips is valid`() {
    val chips = 150
    val player = PokerPlayer("TestPlayer", chips)
    assertThat(player.chips()).isEqualTo(chips)
  }

  @Test
  fun `player creation with blank name throws exception`() {
    assertThatThrownBy {
      PokerPlayer("", 100)
    }
      .isExactlyInstanceOf(InvalidPlayerStateException::class.java)
      .hasMessageContaining("PokerPlayer name cannot be blank")
  }

  @Test
  fun `player creation with whitespace name throws exception`() {
    assertThatThrownBy {
      PokerPlayer("   ", 100)
    }
      .isExactlyInstanceOf(InvalidPlayerStateException::class.java)
      .hasMessageContaining("PokerPlayer name cannot be blank")
  }
}
