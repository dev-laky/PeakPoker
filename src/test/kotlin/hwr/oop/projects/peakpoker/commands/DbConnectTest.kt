package hwr.oop.projects.peakpoker.commands

import com.github.ajalt.clikt.testing.test
import hwr.oop.projects.peakpoker.commands.db.DbConnect
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class DbConnectTest : AnnotationSpec() {
    @Test
    fun `test connect command`() {
        // given
        val command = DbConnect()

        // when
        val result = command.test()

        // then
        assertThat(command.commandName).isEqualTo("connect")
        assertThat(result.output).contains("Connecting to database at:")
        assertThat(result.statusCode).isEqualTo(0)
    }

    @Test
    fun `test connect command help message`() {
        // given
        val command = DbConnect()

        // when
        val result = command.test("--help")

        // then
        assertThat(result.output).contains("Connect to the database.")
        assertThat(result.statusCode).isEqualTo(0)
    }
}
