package hwr.oop

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
    fun `Player has money` () {
    }
}