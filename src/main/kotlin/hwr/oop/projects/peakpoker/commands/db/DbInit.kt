package hwr.oop.projects.peakpoker.commands.db

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context

class DbInit : CliktCommand(name = "init") {
    override fun help(context: Context) = "Initialize connection to the database."
    override fun run() {

        // Postgres connection

        /*val connectionHandler = ConnectionHandler
        echo("Creating Schema and connecting to database at: ${connectionHandler.dbURL}")
        ConnectionHandler.init()*/

        echo("Creating Schema and connecting to database at: dbFile.absolutePath")
        // ConnectionHandler.init()
    }
}
