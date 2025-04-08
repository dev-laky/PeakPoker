package com.example.tui

class Label(private val text: String) : CustomView {
    override fun render() {
        println(text)
    }

    override fun handleInput(input: String) {
        // Labels do not handle input
    }
}