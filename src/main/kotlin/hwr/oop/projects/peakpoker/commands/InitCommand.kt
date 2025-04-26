package hwr.oop.projects.peakpoker.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context

class InitCommand : CliktCommand(name = "init") {
    override fun help(context: Context) = "Initialize connection to the database."
    override fun run() {
        // TODO: Implement DB initialization logic
        echo("TODO: Initialized the database.")
    }
}
