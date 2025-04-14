package hwr.oop

import hwr.oop.core.Player
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class PlayerTest : AnnotationSpec() {

    @Test
    fun `Player has name` () {
        val player = Player("Hans")
        assertThat(player.name).isEqualTo("Hans")
    }

    @Test
    fun `Player has money` () {
    }
}