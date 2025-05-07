package hwr.oop.projects.peakpoker

import com.github.ajalt.clikt.core.parse
import com.github.ajalt.clikt.core.subcommands
import hwr.oop.projects.peakpoker.commands.PokerCommand
import hwr.oop.projects.peakpoker.commands.db.DbCommand
import hwr.oop.projects.peakpoker.commands.db.DbConnect
import hwr.oop.projects.peakpoker.commands.db.DbInit

fun main(args: Array<String>) =
    PokerCommand().subcommands(DbCommand().subcommands(DbInit(), DbConnect())).parse(args)
