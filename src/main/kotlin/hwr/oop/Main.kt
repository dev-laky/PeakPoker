package hwr.oop

import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import hwr.oop.commands.InitCommand
import hwr.oop.commands.PokerCommand

fun main(args: Array<String>) = PokerCommand()
    .subcommands(
        InitCommand(),
    )
    .main(args)
