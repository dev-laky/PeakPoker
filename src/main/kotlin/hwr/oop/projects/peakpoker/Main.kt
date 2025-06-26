package hwr.oop.projects.peakpoker

import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.core.parse
import com.github.ajalt.clikt.core.subcommands
import hwr.oop.projects.peakpoker.commands.AllIn
import hwr.oop.projects.peakpoker.commands.Call
import hwr.oop.projects.peakpoker.commands.Poker
import hwr.oop.projects.peakpoker.commands.CreateNewGame
import hwr.oop.projects.peakpoker.commands.Fold
import hwr.oop.projects.peakpoker.commands.HandInfo
import hwr.oop.projects.peakpoker.commands.Raise
import hwr.oop.projects.peakpoker.commands.Check
import hwr.oop.projects.peakpoker.commands.GameInfo
import java.io.File

fun main(args: Array<String>) {
  val persistenceAdapter =
    hwr.oop.projects.peakpoker.persistence.FileSystemPersistenceAdapter(
      File("poker_data.json")
    )

  val command = Poker().subcommands(
    CreateNewGame(persistenceAdapter),
    HandInfo(persistenceAdapter),
    GameInfo(persistenceAdapter),
    Raise(persistenceAdapter, persistenceAdapter),
    Call(persistenceAdapter, persistenceAdapter),
    Check(
      persistenceAdapter,
      persistenceAdapter
    ),
    Fold(persistenceAdapter, persistenceAdapter),
    AllIn(persistenceAdapter, persistenceAdapter)
  )

  try {
    command.parse(args)
  } catch (_: CliktError) {
    println(command.getFormattedHelp())
  }
}
