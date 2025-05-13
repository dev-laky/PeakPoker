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
    fun `Player has name`() {
        val player = Player("Hans")
        val playerName: String = player.name
        assertThat(playerName).isEqualTo("Hans")
    }

    @Test
    fun `Player's bet can be raised`() {
        val player = Player("Hans")
        player.setBetAmount(10)
        assertThat(player.getBet()).isEqualTo(10)
    }

    @Test
    fun `Player initializes with isFolded and isAllIn as false`() {
        val player = Player("Hans")
        assertThat(player.isFolded).isFalse()
        assertThat(player.isAllIn).isFalse()
    }

    @Test
    fun `getChips returns correct initial chip count`() {
        val initialChips = 500
        val player = Player("Hans", initialChips)
        assertThat(player.getChips()).isEqualTo(initialChips)
    }

    @Test
    fun `assignHand correctly assigns hole cards to player`() {
        val player = Player("Hans")

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
    fun `betting reduces player's chip count`() {
        val initialChips = 500
        val betAmount = 100
        val player = Player("Hans", initialChips)

        player.setBetAmount(betAmount)

        assertThat(player.getChips()).isEqualTo(initialChips - betAmount)
        assertThat(player.getBet()).isEqualTo(betAmount)
    }

    @Test
    fun `multiple bets accumulate correctly`() {
        val initialChips = 500
        val player = Player("Hans", initialChips)

        player.setBetAmount(100)
        player.setBetAmount(150)

        assertThat(player.getBet()).isEqualTo(150)
        assertThat(player.getChips()).isEqualTo(initialChips - 150)
    }

    @Test
    fun `player can be marked as folded`() {
        val player = Player("Hans")

        assertThat(player.isFolded).isFalse()

        player.isFolded = true
        assertThat(player.isFolded).isTrue()
    }

    @Test
    fun `player can be marked as all-in`() {
        val player = Player("Hans")

        assertThat(player.isAllIn).isFalse()

        player.isAllIn = true
        assertThat(player.isAllIn).isTrue()
    }

    @Test
    fun `bet validation throws exception for negative amounts`() {
        val player = Player("Hans", 500)

        val exception = shouldThrow<IllegalArgumentException> {
            player.setBetAmount(-1)
        }

        assertThat(exception.message).isEqualTo("Chips amount must be greater than zero")
        // Verify player's state remains unchanged
        assertThat(player.getBet()).isEqualTo(0)
        assertThat(player.getChips()).isEqualTo(500)
    }

    @Test
    fun `bet of zero amount is not accepted`() {
        val player = Player("Hans", 500)

        val exception = shouldThrow<IllegalArgumentException> {
            player.setBetAmount(0)
        }

        assertThat(exception.message).isEqualTo("Chips amount must be greater than zero")
        // Verify player's state remains unchanged
        assertThat(player.getBet()).isEqualTo(0)
        assertThat(player.getChips()).isEqualTo(500)
    }

    @Test
    fun `player cannot have negative chips`() {
        val exception = shouldThrow<IllegalArgumentException> {
            Player("Hans", -100)
        }

        assertThat(exception.message).isEqualTo("Chips amount must be non-negative")
    }

    @Test
    fun `player cannot have blank name`() {
        val exception = shouldThrow<IllegalArgumentException> {
            Player("")
        }

        assertThat(exception.message).isEqualTo("Player name cannot be blank")
    }

    @Test
    fun `allIn sets bet to all remaining chips and chips to zero`() {
        val player = Player("Hans", 500)
        player.allIn(200)

        assertThat(player.getBet()).isEqualTo(200)
        assertThat(player.getChips()).isEqualTo(0)
        assertThat(player.isAllIn).isTrue()
    }

    @Test
    fun `allIn sets bet to all chips when allIn amount is greater than chips`() {
        val player = Player("Hans", 500)
        player.allIn(600)

        assertThat(player.getBet()).isEqualTo(500)
        assertThat(player.getChips()).isEqualTo(0)
        assertThat(player.isAllIn).isTrue()
    }

    @Test
    fun `setBetAmount only deducts the difference to previous bet`() {
        val player = Player("Hans", 500)

        player.setBetAmount(100)
        assertThat(player.getChips()).isEqualTo(400)

        player.setBetAmount(150)
        assertThat(player.getChips()).isEqualTo(350)
    }

    @Test
    fun `assignHand throws exception for invalid hand size`() {
        val player = Player("Hans")

        val exception = shouldThrow<IllegalArgumentException> {
            player.assignHand(
                HoleCards(
                    listOf(
                        Card(Suit.DIAMONDS, Rank.FIVE),
                        Card(Suit.DIAMONDS, Rank.SIX),
                        Card(Suit.DIAMONDS, Rank.SEVEN)
                    ),
                    player
                )
            )
        }

        assertThat(exception.message).isEqualTo("Hole cards must be empty or contain exactly two cards.")
    }
}
