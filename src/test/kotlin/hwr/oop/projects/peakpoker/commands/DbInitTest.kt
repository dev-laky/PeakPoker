package hwr.oop.projects.peakpoker.commands

import com.github.ajalt.clikt.testing.test
import hwr.oop.projects.peakpoker.commands.db.DbInit
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class DbInitTest : AnnotationSpec() {
  @Test
  fun `test init command`() {
    // given
    val command = DbInit()

    // when
    val result = command.test()

    // then
    assertThat(command.commandName).isEqualTo("init")
    assertThat(result.output).contains("Creating Schema and connecting to database at:")
    assertThat(result.statusCode).isEqualTo(0)
  }

  @Test
  fun `test connect command help message`() {
    // given
    val command = DbInit()

    // when
    val result = command.test("--help")

    // then
    assertThat(result.output).contains("Initialize connection to the database.")
    assertThat(result.statusCode).isEqualTo(0)
  }
}
