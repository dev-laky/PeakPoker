package hwr.oop.projects.peakpoker

import com.github.ajalt.clikt.core.CliktError
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThatNoException
import io.kotest.extensions.system.captureStandardOut
import org.assertj.core.api.Assertions.assertThat

class MainTest : AnnotationSpec() {

    @Test
    fun `test main function execution without errors`() {
        // Test execution - use `parse` for non-exiting invocation
        assertThatNoException().isThrownBy { main(arrayOf("db")) }
        assertThatNoException().isThrownBy {
            val output = captureStandardOut {main(arrayOf("nonsense_command"))}.trim()
            assertThat(output).contains("Usage: poker")
        }
    }
}
