package hwr.oop.projects.peakpoker.core.player

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class PlayerTest : AnnotationSpec() {
    // Basic initialization tests
    @Test
    fun `Player has name`() {
        val player = Player("Hans")
        assertThat(player.name).isEqualTo("Hans")
    }

    @Test
    fun `Player initializes with default chip count`() {
        val player = Player("Hans")
        assertThat(player.getChips()).isEqualTo(100) // Default is 100
    }

    @Test
    fun `Player initializes with specified chip count`() {
        val initialChips = 500
        val player = Player("Hans", initialChips)
        assertThat(player.getChips()).isEqualTo(initialChips)
    }

    @Test
    fun `Player initializes with zero chips`() {
        val player = Player("Hans", 0)
        assertThat(player.getChips()).isEqualTo(0)
    }

    @Test
    fun `Player initializes with isFolded and isAllIn as false`() {
        val player = Player("Hans")
        assertThat(player.isFolded).isFalse()
        assertThat(player.isAllIn).isFalse()
    }

    @Test
    fun `Player cannot have negative chips`() {
        val exception = shouldThrow<IllegalArgumentException> {
            Player("Hans", -100)
        }
        assertThat(exception.message).isEqualTo("Chips amount must be non-negative")
    }

    @Test
    fun `Player cannot have blank name`() {
        val exception = shouldThrow<IllegalArgumentException> {
            Player("")
        }
        assertThat(exception.message).isEqualTo("Player name cannot be blank")
    }

    // Hand assignment tests
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
    fun `assignHand throws exception for hand with wrong number of cards`() {
        val player = Player("Hans")

        // Test with 1 card
        val nonCardHand = HoleCards(
            emptyList(),
            player
        )
        var exception = shouldThrow<IllegalArgumentException> {
            player.assignHand(nonCardHand)
        }
        assertThat(exception.message).isEqualTo("A player must have exactly 2 hole cards")
    }

    // Core betting functionality tests
    @Test
    fun `betting correctly updates player's bet and chip count`() {
        val initialChips = 500
        val betAmount = 100
        val player = Player("Hans", initialChips)

        player.setBetAmount(betAmount)

        assertThat(player.getChips()).isEqualTo(initialChips - betAmount)
        assertThat(player.getBet()).isEqualTo(betAmount)
    }

    @Test
    fun `setBetAmount only deducts the difference to previous bet`() {
        val player = Player("Hans", 500)

        player.setBetAmount(100)
        assertThat(player.getBet()).isEqualTo(100)
        assertThat(player.getChips()).isEqualTo(400)

        player.setBetAmount(150)
        assertThat(player.getBet()).isEqualTo(150)
        assertThat(player.getChips()).isEqualTo(350)
    }

    @Test
    fun `setBetAmount throws exception for zero amount`() {
        val player = Player("Hans", 500)

        val exception = shouldThrow<IllegalArgumentException> {
            player.setBetAmount(0)
        }
        assertThat(exception.message).isEqualTo("Chips amount must be greater than zero")
        assertThat(player.getBet()).isEqualTo(0)
        assertThat(player.getChips()).isEqualTo(500)
    }

    @Test
    fun `setBetAmount throws exception for negative amount`() {
        val player = Player("Hans", 500)

        val exception = shouldThrow<IllegalArgumentException> {
            player.setBetAmount(-10)
        }
        assertThat(exception.message).isEqualTo("Chips amount must be greater than zero")
        assertThat(player.getBet()).isEqualTo(0)
        assertThat(player.getChips()).isEqualTo(500)
    }

    // All-in and fold tests
    @Test
    fun `allIn sets bet to all remaining chips and chips to zero`() {
        val player = Player("Hans", 500)
        player.allIn()

        assertThat(player.getBet()).isEqualTo(500)
        assertThat(player.getChips()).isEqualTo(0)
        assertThat(player.isAllIn).isTrue()
    }

    @Test
    fun `allIn with zero chips doesnt work`() {
        val player = Player("Hans", 0)

        val exception = shouldThrow<IllegalArgumentException> {
            player.allIn()
        }

        assertThat(exception.message).isEqualTo("Chips amount must be greater than zero")
        assertThat(player.getBet()).isEqualTo(0)
        assertThat(player.getChips()).isEqualTo(0)
        assertThat(player.isAllIn).isFalse()
    }

    @Test
    fun `fold sets isFolded to true`() {
        val player = Player("Hans")
        player.fold()

        assertThat(player.isFolded).isTrue()
    }

    @Test
    fun `fold does not affect chips or bet`() {
        val initialChips = 500
        val betAmount = 100
        val player = Player("Hans", initialChips)

        player.setBetAmount(betAmount)
        player.fold()

        assertThat(player.getBet()).isEqualTo(betAmount)
        assertThat(player.getChips()).isEqualTo(initialChips - betAmount)
    }

    @Test
    fun `fold sets isFolded to true`() {
        val player = Player("Hans")

        player.fold()

        assertThat(player.isFolded).isTrue()
    }

    @Test
    fun `allIn sets bet to full chips when amount equals chips`() {
        val player = Player("Hans", 500)

        player.allIn(500)

        assertThat(player.getBet()).isEqualTo(500)
        assertThat(player.getChips()).isEqualTo(0)
        assertThat(player.isAllIn).isTrue()
    }
}
