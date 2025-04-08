package com.example.tui

class Screen(private val views: List<CustomView>) {
    private fun clearScreen() {
        // ANSI escape code to clear the terminal
        print("\u001b[H\u001b[2J")
        System.out.flush()
    }

    fun update(input: String) {
        views.forEach { it.handleInput(input) }
    }

    fun render() {
        clearScreen() // Clear screen before rendering
        views.forEach { it.render() }
    }
}