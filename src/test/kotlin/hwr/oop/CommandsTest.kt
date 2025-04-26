package hwr.oop

import com.github.ajalt.clikt.testing.test
import hwr.oop.commands.InitCommand
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class CommandsTest : AnnotationSpec() {
    @Test
    fun `test init command`() {
        val command = InitCommand()
        val result = command.test()
        assertThat(result.output).isEqualTo("TODO: Initialized the database.\n")
        assertThat(result.statusCode).isEqualTo(0)
        assertThat(command.commandName).isEqualTo("init")
    }
}
