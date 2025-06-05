package hwr.oop.projects.peakpoker

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.extensions.system.captureStandardOut
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatNoException

class MainTest : AnnotationSpec() {

  @Test
  fun `test main function execution without errors`() {
    // Test execution - use `parse` for non-exiting invocation
    assertThatNoException().isThrownBy { main(arrayOf("--help")) }
    assertThatNoException().isThrownBy {
      val output =
        captureStandardOut { main(arrayOf("nonsense_command")) }.trim()
      assertThat(output).contains("poker")
    }
  }
}
