package com.example.tui

class Menu(private val options: List<String>) : View {
    private var selectedIndex = 0

    override fun render() {
        options.forEachIndexed { index, option ->
            if (index == selectedIndex) {
                println("> $option")
            } else {
                println("  $option")
            }
        }
    }

    override fun handleInput(input: Char) {
        when (input) {
            'w' -> selectedIndex = (selectedIndex - 1 + options.size) % options.size // Up arrow equivalent
            's' -> selectedIndex = (selectedIndex + 1) % options.size // Down arrow equivalent
        }
    }
}