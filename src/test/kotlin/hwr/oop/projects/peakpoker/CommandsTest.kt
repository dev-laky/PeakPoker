package hwr.oop.projects.peakpoker

import com.github.ajalt.clikt.testing.test
import hwr.oop.projects.peakpoker.commands.PokerCommand
import hwr.oop.projects.peakpoker.commands.db.DbCommand
import hwr.oop.projects.peakpoker.commands.db.DbConnect
import hwr.oop.projects.peakpoker.commands.db.DbInit
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class CommandsTest : AnnotationSpec() {
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

    @Test
    fun `test init command`() {
        // given
        val command = DbInit()

        // when
        val result = command.test()

        // then
        assertThat(command.commandName).isEqualTo("init")
        assertThat(result.statusCode).isEqualTo(0)
    }

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
}
