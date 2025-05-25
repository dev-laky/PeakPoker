package hwr.oop.projects.peakpoker.commands

import com.github.ajalt.clikt.testing.test
import hwr.oop.projects.peakpoker.commands.db.DbCommand
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class DbCommandTest : AnnotationSpec() {
  @Test
  fun `test db command`() {
    // given
    val command = DbCommand()

    // when
    val result = command.test()

    // then
    assertThat(command.commandName).isEqualTo("db")
    assertThat(result.statusCode).isEqualTo(0)
  }
}
