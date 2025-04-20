package hwr.oop

import hwr.oop.projects.peakpoker.core.Player
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class PlayerTest : AnnotationSpec() {

    @Test
    fun `Player has name` () {
        val player = Player("Hans")
        val playerName: String = player.name
        assertThat(player.name).isEqualTo("Hans")
    }

    @Test
    fun `Player's bet can be raised` () {
        val player = Player("Hans")
        player.raise_bet(10)
        assertThat(player.get_bet()).isEqualTo(10)
    }

    @Test
    fun `Right exception thrown on negative bet` () {
        val player = Player("Hans")
        val exception = shouldThrow <IllegalArgumentException> {
            player.raise_bet(-10)
        }
        assertThat(exception.message).isEqualTo("Bet amount must be positive")
    }

    @Test
    fun `Right exception on raising bet on a folded player` () {
        val player = Player("Hans")
        player.fold()
        val exception = shouldThrow <IllegalStateException> {
            player.raise_bet(100)
        }
        assertThat(exception.message).isEqualTo("Cannot raise bet after folding")
    }

    @Test
    fun `Right exception on raising bet on an all-in player` () {
        val player = Player("Hans")
        player.all_in()
        val exception = shouldThrow <IllegalStateException> {
            player.raise_bet(100)
        }
        assertThat(exception.message).isEqualTo("Cannot raise bet after going all-in")
    }

    @Test
    fun `Player's bet can be folded` () {
        val player = Player("Hans")
        player.fold()
        assertThat(player.isFolded()).isTrue
    }

    @Test
    fun `Player's bet can be all-in` () {
        val player = Player("Hans")
        player.all_in()
        assertThat(player.isAllIn()).isTrue
    }

}