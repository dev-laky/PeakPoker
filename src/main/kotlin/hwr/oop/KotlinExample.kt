package hwr.oop
import com.example.tui.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class KotlinExample {
  fun sayHello(): String {
    return "Hello World!"
  }
}

//fun main(args: Array<String>) {
////  val example = KotlinExample()
////  println(example.sayHello())
////  TerminalApp.run()
//  // Your code to run the application
////  com.example.tui.TerminalApp.run()
//
//}

//fun main(): Unit = runBlocking {
//  val job = launch {
//    TerminalApp.listenToInput()
//  }
//
//  job.invokeOnCompletion { println("\nExiting...") }
//}
fun setRawMode() {
  Runtime.getRuntime().exec(arrayOf("/bin/sh", "-c", "stty -echo -icanon < /dev/tty")).waitFor()
}

fun resetTerminal() {
  Runtime.getRuntime().exec(arrayOf("/bin/sh", "-c", "stty echo icanon < /dev/tty")).waitFor()
}

fun main(args: Array<String>) {
  setRawMode()
  try {
    println("Running TUI Application.")
    TerminalApp.run()
  } finally {
    resetTerminal() // Ensure terminal echo is reset in case of failure
  }
}
