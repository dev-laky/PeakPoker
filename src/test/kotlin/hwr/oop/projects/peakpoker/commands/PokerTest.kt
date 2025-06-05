package hwr.oop.projects.peakpoker.commands

import com.github.ajalt.clikt.testing.test
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class PokerTest : AnnotationSpec() {
  @Test
  fun `test poker command`() {
    // given
    val command = PokerCommand()

    // when
    val result = command.test()

    // then
    assertThat(command.commandName).isEqualTo("poker")
    assertThat(result.statusCode).isEqualTo(0)
  }
}
