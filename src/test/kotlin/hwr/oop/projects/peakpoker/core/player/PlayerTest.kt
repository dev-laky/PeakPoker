package hwr.oop.projects.peakpoker.core.player

import hwr.oop.projects.peakpoker.core.card.HoleCards
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
        player.raiseBet(10)
        assertThat(player.getBet()).isEqualTo(10)
    }


    @Test
    fun `Right exception thrown on negative bet`() {
        val player = Player("Hans")
        val exception = shouldThrow<IllegalArgumentException> {
            player.raiseBet(-10)
        }
        assertThat(exception.message).isEqualTo("Bet amount must be positive")
    }

    @Test
    fun `Right exception on raising bet on a folded player`() {
        val player = Player("Hans")
        player.isFolded = true
        val exception = shouldThrow<IllegalStateException> {
            player.raiseBet(100)
        }
        assertThat(exception.message).isEqualTo("Cannot raise bet after folding")
    }

    @Test
    fun `Right exception on raising bet on an all-in player`() {
        val player = Player("Hans")
        player.isAllIn = true
        val exception = shouldThrow<IllegalStateException> {
            player.raiseBet(100)
        }
        assertThat(exception.message).isEqualTo("Cannot raise bet after going all-in")
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
        val holeCards = HoleCards(emptyList(), player)
        player.assignHand(holeCards)
        assertThat(player.getHand()).isEqualTo(holeCards)
    }

    @Test
    fun `betting reduces player's chip count`() {
        val initialChips = 500
        val betAmount = 100
        val player = Player("Hans", initialChips)

        player.raiseBet(betAmount)

        assertThat(player.getChips()).isEqualTo(initialChips - betAmount)
        assertThat(player.getBet()).isEqualTo(betAmount)
    }

    @Test
    fun `multiple bets accumulate correctly`() {
        val initialChips = 500
        val player = Player("Hans", initialChips)

        player.raiseBet(100)
        player.raiseBet(150)

        assertThat(player.getBet()).isEqualTo(250)
        assertThat(player.getChips()).isEqualTo(initialChips - 250)
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
            player.raiseBet(-1)
        }

        assertThat(exception.message).isEqualTo("Bet amount must be positive")
        // Verify player's state remains unchanged
        assertThat(player.getBet()).isEqualTo(0)
        assertThat(player.getChips()).isEqualTo(500)
    }

    @Test
    fun `bet of zero amount is accepted`() {
        val player = Player("Hans", 500)

        player.raiseBet(0)

        // Verify player's state - bet increased by 0, chips remain unchanged
        assertThat(player.getBet()).isEqualTo(0)
        assertThat(player.getChips()).isEqualTo(500)
    }

}
