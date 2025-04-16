package hwr.oop.projects.peakpoker

import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import hwr.oop.projects.peakpoker.commands.InitCommand
import hwr.oop.projects.peakpoker.commands.PokerCommand

fun main(args: Array<String>) = PokerCommand()
    .subcommands(
        InitCommand(),
    )
    .main(args)
