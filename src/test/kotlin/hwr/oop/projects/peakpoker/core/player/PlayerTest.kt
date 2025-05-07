package hwr.oop.projects.peakpoker.core.player

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class PlayerTest : AnnotationSpec() {

    @Test
    fun `Player has name` () {
        val player = Player("Hans")
        val playerName: String = player.name
        assertThat(playerName).isEqualTo("Hans")
    }

    @Test
    fun `Player's bet can be raised`() {
        val player = Player("Hans")
        player.raiseBet(10)
        assertThat(player.getBet).isEqualTo(10)
    }

    // TODO: Ensure that chip amount is not negative when initializing a player
    // TODO: Ensure that List of Cards as players hand can not be longer than 2 element (card objects)

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
        player.fold()
        val exception = shouldThrow<IllegalStateException> {
            player.raiseBet(100)
        }
        assertThat(exception.message).isEqualTo("Cannot raise bet after folding")
    }

    @Test
    fun `Right exception on raising bet on an all-in player`() {
        val player = Player("Hans")
        player.allIn()
        val exception = shouldThrow<IllegalStateException> {
            player.raiseBet(100)
        }
        assertThat(exception.message).isEqualTo("Cannot raise bet after going all-in")
    }

    @Test
    fun `Player's bet can be folded`() {
        val player = Player("Hans")
        player.fold()
        assertThat(player.isFolded()).isTrue
    }

    @Test
    fun `Player's bet can be all-in`() {
        val player = Player("Hans")
        player.allIn()
        assertThat(player.isAllIn()).isTrue
    }

}
