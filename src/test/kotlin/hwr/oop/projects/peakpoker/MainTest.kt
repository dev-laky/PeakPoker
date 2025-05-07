package hwr.oop.projects.peakpoker

import com.github.ajalt.clikt.core.CliktError
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.assertThrows

class MainTest : AnnotationSpec() {

    @Test
    fun `test main function execution without errors`() {
        // Test execution - use `parse` for non-exiting invocation
        assertThatNoException().isThrownBy { main(arrayOf("--help")) }
        assertThrows<CliktError> { main(arrayOf("nonsense_command")) }
    }
}
