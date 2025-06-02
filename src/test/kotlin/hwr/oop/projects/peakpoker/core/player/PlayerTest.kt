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
    val pokerPlayer = PokerPlayer("Hans")
    val playerName: String = pokerPlayer.name
    assertThat(playerName).isEqualTo("Hans")
  }

  @Test
  fun `player bet can be raised`() {
    val pokerPlayer = PokerPlayer("Hans")
    pokerPlayer.setBetAmount(10)
    assertThat(pokerPlayer.getBet()).isEqualTo(10)
  }

  @Test
  fun `player initializes with isFolded and isAllIn as false`() {
    val pokerPlayer = PokerPlayer("Hans")
    assertThat(pokerPlayer.isFolded).isFalse()
    assertThat(pokerPlayer.isAllIn).isFalse()
  }

  @Test
  fun `getChips returns correct initial chip count`() {
    val initialChips = 500
    val pokerPlayer = PokerPlayer("Hans", initialChips)
    assertThat(pokerPlayer.getChips()).isEqualTo(initialChips)
  }

  @Test
  fun `assignHand correctly assigns hole cards to player`() {
    val pokerPlayer = PokerPlayer("Hans")

    val holeCards = HoleCards(
      listOf(
        Card(Suit.DIAMONDS, Rank.FIVE),
        Card(Suit.DIAMONDS, Rank.SIX)
      ),
      pokerPlayer
    )

    pokerPlayer.assignHand(holeCards)

    assertThat(pokerPlayer.getHand()).isEqualTo(holeCards)
  }

  @Test
  fun `betting reduces player chip count`() {
    val initialChips = 500
    val betAmount = 100
    val pokerPlayer = PokerPlayer("Hans", initialChips)

    pokerPlayer.setBetAmount(betAmount)

    assertThat(pokerPlayer.getChips()).isEqualTo(initialChips - betAmount)
    assertThat(pokerPlayer.getBet()).isEqualTo(betAmount)
  }

  @Test
  fun `multiple bets accumulate correctly`() {
    val initialChips = 500
    val pokerPlayer = PokerPlayer("Hans", initialChips)

    pokerPlayer.setBetAmount(100)
    pokerPlayer.setBetAmount(150)

    assertThat(pokerPlayer.getBet()).isEqualTo(150)
    assertThat(pokerPlayer.getChips()).isEqualTo(initialChips - 150)
  }

  @Test
  fun `bet validation throws exception for negative amounts`() {
    val pokerPlayer = PokerPlayer("Hans", 500)

    assertThatThrownBy { pokerPlayer.setBetAmount(-1) }
      .isExactlyInstanceOf(InvalidBetAmountException::class.java)
      .hasMessageContaining("Chips amount must be greater than zero")

    assertThat(pokerPlayer.getBet()).isEqualTo(0)
    assertThat(pokerPlayer.getChips()).isEqualTo(500)
  }

  @Test
  fun `bet of zero amount is not accepted`() {
    val pokerPlayer = PokerPlayer("Hans", 500)

    assertThatThrownBy { pokerPlayer.setBetAmount(0) }
      .isExactlyInstanceOf(InvalidBetAmountException::class.java)
      .hasMessageContaining("Chips amount must be greater than zero")

    assertThat(pokerPlayer.getBet()).isEqualTo(0)
    assertThat(pokerPlayer.getChips()).isEqualTo(500)
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
    val pokerPlayer = PokerPlayer("ZeroChipsPlayer", 0)
    assertThat(pokerPlayer.getChips()).isEqualTo(0)
  }

  @Test
  fun `player creation with positive chips is valid`() {
    val chips = 150
    val pokerPlayer = PokerPlayer("TestPlayer", chips)
    assertThat(pokerPlayer.getChips()).isEqualTo(chips)
  }

  @Test
  fun `player creation with blank name throws exception`() {
    assertThatThrownBy {
      PokerPlayer("", 100)
    }
      .isExactlyInstanceOf(InvalidPlayerStateException::class.java)
      .hasMessageContaining("Player name cannot be blank")
  }

  @Test
  fun `player creation with whitespace name throws exception`() {
    assertThatThrownBy {
      PokerPlayer("   ", 100)
    }
      .isExactlyInstanceOf(InvalidPlayerStateException::class.java)
      .hasMessageContaining("Player name cannot be blank")
  }
}
