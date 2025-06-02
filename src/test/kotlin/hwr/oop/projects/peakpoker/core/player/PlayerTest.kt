package hwr.oop.projects.peakpoker.core.player

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

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
        assertThat(player.getBet()).isEqualTo(10)
    }

    @Test
    fun `player initializes with isFolded and isAllIn as false`() {
        val player = PokerPlayer("Hans")
        assertThat(player.isFolded).isFalse()
        assertThat(player.isAllIn).isFalse()
    }

    @Test
    fun `getChips returns correct initial chip count`() {
        val initialChips = 500
        val player = PokerPlayer("Hans", initialChips)
        assertThat(player.getChips()).isEqualTo(initialChips)
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

        assertThat(player.getHand()).isEqualTo(holeCards)
    }

    @Test
    fun `betting reduces player chip count`() {
        val initialChips = 500
        val betAmount = 100
        val player = PokerPlayer("Hans", initialChips)

        player.setBetAmount(betAmount)

        assertThat(player.getChips()).isEqualTo(initialChips - betAmount)
        assertThat(player.getBet()).isEqualTo(betAmount)
    }

    @Test
    fun `multiple bets accumulate correctly`() {
        val initialChips = 500
        val player = PokerPlayer("Hans", initialChips)

        player.setBetAmount(100)
        player.setBetAmount(150)

        assertThat(player.getBet()).isEqualTo(150)
        assertThat(player.getChips()).isEqualTo(initialChips - 150)
    }

    @Test
    fun `bet validation throws exception for negative amounts`() {
        val player = PokerPlayer("Hans", 500)

        val exception = shouldThrow<IllegalArgumentException> {
            player.setBetAmount(-1)
        }

        assertThat(exception.message).isEqualTo("Chips amount must be greater than zero")
        assertThat(player.getBet()).isEqualTo(0)
        assertThat(player.getChips()).isEqualTo(500)
    }

    @Test
    fun `bet of zero amount is not accepted`() {
        val player = PokerPlayer("Hans", 500)

        val exception = shouldThrow<IllegalArgumentException> {
            player.setBetAmount(0)
        }

        assertThat(exception.message).isEqualTo("Chips amount must be greater than zero")
        assertThat(player.getBet()).isEqualTo(0)
        assertThat(player.getChips()).isEqualTo(500)
    }
}
