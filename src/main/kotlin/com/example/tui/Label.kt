package com.example.tui

class Label(private val text: String) : View {
    override fun render() {
        println(text)
    }

    override fun handleInput(input: Char) {
        // Labels do not handle input
    }
}