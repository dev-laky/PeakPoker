import kotlinx.coroutines.*
import com.example.tui.Screen
import com.example.tui.Menu
import com.example.tui.Label

object TerminalApp {
    private val screen = Screen(
        listOf(
            Label("Welcome to the TUI App"),
            Menu(listOf("Option 1", "Option 2", "Quit"))
        )
    )

    // Main application loop
    fun run() = runBlocking {
        while (true) {
            screen.render()  // Render the TUI
            val input = readKey()  // Capture user input
            if (input == "q") break  // Exit condition
            screen.update(input)  // Update the screen based on input
            delay(100)  // Delay for responsiveness, can be adjusted
        }
    }

    // Function to read a single character from standard input
    fun readKey(): String {
        return try {
//            System.`in`.read().toChar()
            val console = System.console() ?: throw RuntimeException("Console is not available")
            val reader = console.reader()
            var first = reader.read()
            return when (first) {
                27 -> {
                    // Escape sequence for arrow keys
                    val second = reader.read()
                    val third = reader.read()
                    when (second) { // Rebinding of keys
                        91 -> when (third) {
                            65 -> "w"  // Up arrow
                            66 -> "s"  // Down arrow
                            else -> ""  // Fallback to quitting on unknown escape sequence
                        }
                        else -> ""  // Fallback to quitting on unknown escape sequence
                    }
                }
                else -> when (first.toChar().toString()) {
                    "j" -> "s"
                    "k" -> "w"
                    "h" -> "a"
                    "l" -> "d"
                    else -> first.toChar().toString()  // Regular character input
                } // Regular character input
            }
        } catch (e: Exception) {
            "q"  // Fallback to quitting on exception
        }
    }
}