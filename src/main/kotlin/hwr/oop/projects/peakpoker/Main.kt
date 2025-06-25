package hwr.oop.projects.peakpoker

import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.core.parse
import com.github.ajalt.clikt.core.subcommands
import hwr.oop.projects.peakpoker.commands.PokerCommand
import java.io.File

fun main(args: Array<String>) {
  val persistenceAdapter =
    hwr.oop.projects.peakpoker.persistence.FileSystemPersistenceAdapter(
      File("poker_data.json")
    )

  val command = PokerCommand().subcommands()

  try {
    command.parse(args)
  } catch (_: CliktError) {
    println(command.getFormattedHelp())
  }
}
