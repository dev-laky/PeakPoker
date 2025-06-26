package hwr.oop.projects.peakpoker.core.player

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
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
      .isExactlyInstanceOf(PokerPlayer.InvalidBetOperationException::class.java)
      .hasMessageContaining("Chips amount must be greater than zero")

    assertThat(player.bet()).isEqualTo(0)
    assertThat(player.chips()).isEqualTo(500)
  }

  @Test
  fun `bet of zero amount is not accepted`() {
    val player = PokerPlayer("Hans", 500)

    assertThatThrownBy { player.setBetAmount(0) }
      .isExactlyInstanceOf(PokerPlayer.InvalidBetOperationException::class.java)
      .hasMessageContaining("Chips amount must be greater than zero")

    assertThat(player.bet()).isEqualTo(0)
    assertThat(player.chips()).isEqualTo(500)
  }

  @Test
  fun `player creation with negative chips throws exception`() {
    assertThatThrownBy {
      PokerPlayer("TestPlayer", -100)
    }
      .isExactlyInstanceOf(PokerPlayer.InvalidInitialChipsBalanceException::class.java)
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
      .isExactlyInstanceOf(PokerPlayer.InvalidPlayerNameException::class.java)
      .hasMessageContaining("PokerPlayer name cannot be blank")
  }

  @Test
  fun `player creation with whitespace name throws exception`() {
    assertThatThrownBy {
      PokerPlayer("   ", 100)
    }
      .isExactlyInstanceOf(PokerPlayer.InvalidPlayerNameException::class.java)
      .hasMessageContaining("PokerPlayer name cannot be blank")
  }

  @Test
  fun `check sets hasChecked to true`() {
    val player = PokerPlayer("Hans")
    assertThat(player.hasChecked()).isFalse()

    player.check()
    assertThat(player.hasChecked()).isTrue()
  }

  @Test
  fun `fold sets isFolded to true`() {
    val player = PokerPlayer("Hans")
    assertThat(player.isFolded()).isFalse()

    player.fold()
    assertThat(player.isFolded()).isTrue()
  }

  @Test
  fun `allIn sets isAllIn to true and bets all chips`() {
    val player = PokerPlayer("Hans", 100)
    assertThat(player.isAllIn()).isFalse()

    player.allIn()

    assertThat(player.isAllIn()).isTrue()
    assertThat(player.bet()).isEqualTo(100)
    assertThat(player.chips()).isEqualTo(0)
  }

  @Test
  fun `allIn with existing bet adds remaining chips`() {
    val player = PokerPlayer("Hans", 100)
    player.setBetAmount(30)

    player.allIn()

    assertThat(player.isAllIn()).isTrue()
    assertThat(player.bet()).isEqualTo(100)
    assertThat(player.chips()).isEqualTo(0)
  }

  @Test
  fun `resetRoundState resets isFolded and isAllIn to false`() {
    val player = PokerPlayer("Hans")
    player.fold()
    player.allIn()

    assertThat(player.isFolded()).isTrue()
    assertThat(player.isAllIn()).isTrue()

    player.resetRoundState()

    assertThat(player.isFolded()).isFalse()
    assertThat(player.isAllIn()).isFalse()
  }

  @Test
  fun `resetBet resets bet to zero and hasChecked to false`() {
    val player = PokerPlayer("Hans", 100)
    player.setBetAmount(50)
    player.check()

    assertThat(player.bet()).isEqualTo(50)
    assertThat(player.hasChecked()).isTrue()

    player.resetBet()

    assertThat(player.bet()).isEqualTo(0)
    assertThat(player.hasChecked()).isFalse()
  }

  @Test
  fun `addChips increases chip count`() {
    val player = PokerPlayer("Hans", 100)

    player.addChips(50)

    assertThat(player.chips()).isEqualTo(150)
  }

  @Test
  fun `addChips with zero amount does not change chips`() {
    val player = PokerPlayer("Hans", 100)

    player.addChips(0)

    assertThat(player.chips()).isEqualTo(100)
  }

  @Test
  fun `addChips with negative amount throws exception`() {
    val player = PokerPlayer("Hans", 100)

    assertThatThrownBy { player.addChips(-10) }
      .isExactlyInstanceOf(PokerPlayer.InvalidBetOperationException::class.java)
      .hasMessageContaining("Cannot add negative amount of chips")

    assertThat(player.chips()).isEqualTo(100)
  }

  @Test
  fun `default constructor initializes with 100 chips`() {
    val player = PokerPlayer("Hans")
    assertThat(player.chips()).isEqualTo(100)
  }

  @Test
  fun `hand initializes with empty hole cards`() {
    val player = PokerPlayer("Hans")
    val hand = player.hand()

    assertThat(hand.cards).isEmpty()
    assertThat(hand.player).isEqualTo(player)
  }

  @Test
  fun `bet initializes to zero`() {
    val player = PokerPlayer("Hans")
    assertThat(player.bet()).isEqualTo(0)
  }

  @Test
  fun `setBetAmount with same amount as current bet does not change chips`() {
    val player = PokerPlayer("Hans", 100)
    player.setBetAmount(50)
    val chipsAfterFirstBet = player.chips()

    player.setBetAmount(50)

    assertThat(player.chips()).isEqualTo(chipsAfterFirstBet)
    assertThat(player.bet()).isEqualTo(50)
  }

  @Test
  fun `setBetAmount with lower amount than current bet increases chips`() {
    val player = PokerPlayer("Hans", 100)
    player.setBetAmount(50)

    player.setBetAmount(30)

    assertThat(player.chips()).isEqualTo(70)
    assertThat(player.bet()).isEqualTo(30)
  }
}
