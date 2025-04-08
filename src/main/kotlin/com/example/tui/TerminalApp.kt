import kotlinx.coroutines.*
import com.example.tui.TerminalApp

fun main() = runBlocking {
    val job = launch {
        TerminalApp.listenToInput()
    }

    job.invokeOnCompletion { println("\nExiting...") }
}

object TerminalApp {
    private val screen = Screen(listOf(
        Label("Welcome to the TUI App"),
        Menu(listOf("Option 1", "Option 2", "Quit"))
    ))

    fun listenToInput() = runBlocking {
        while (true) {
            screen.render()
            val input = readKey()
            if (input == 'q') break
            screen.update(input)
            delay(100) // Adjust delay as needed for responsiveness
        }
    }
}

// Simple function to read a single character from standard input
fun readKey(): Char {
    return try {
        System.`in`.read().toChar()
    } catch (e: IOException) {
        'q' // Fall back by quitting on exception
    }
}