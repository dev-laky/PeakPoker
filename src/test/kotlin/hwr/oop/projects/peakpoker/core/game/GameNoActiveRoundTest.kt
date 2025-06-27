package hwr.oop.projects.peakpoker.core.game

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class GameNoActiveRoundTest : AnnotationSpec() {
  @Test
  fun `exception message is correctly passed to parent constructor`() {
    val testMessage = "Test error message"
    val exception = PokerGame.NoActiveRoundException(testMessage)

    assertThat(exception.message).isEqualTo(testMessage)
  }

  @Test
  fun `exception message can be empty string`() {
    val exception = PokerGame.NoActiveRoundException("")

    assertThat(exception.message).isEqualTo("")
  }

  @Test
  fun `exception is instance of IllegalStateException`() {
    val exception = PokerGame.NoActiveRoundException("test")

    assertThat(exception).isInstanceOf(IllegalStateException::class.java)
  }

  @Test
  fun `exception is instance of RuntimeException`() {
    val exception = PokerGame.NoActiveRoundException("test")

    assertThat(exception).isInstanceOf(RuntimeException::class.java)
  }

  @Test
  fun `exception can be thrown and caught`() {
    val testMessage = "No active round available"

    assertThatThrownBy {
      throw PokerGame.NoActiveRoundException(testMessage)
    }
      .isExactlyInstanceOf(PokerGame.NoActiveRoundException::class.java)
      .hasMessage(testMessage)
  }

  @Test
  fun `exception preserves stack trace when thrown`() {
    val testMessage = "Stack trace test"
    var caughtException: PokerGame.NoActiveRoundException?

    try {
      throw PokerGame.NoActiveRoundException(testMessage)
    } catch (e: PokerGame.NoActiveRoundException) {
      caughtException = e
    }

    assertThat(caughtException).isNotNull
    assertThat(caughtException.stackTrace).isNotEmpty
  }
}