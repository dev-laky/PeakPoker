package hwr.oop.projects.peakpoker.commands.db

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import hwr.oop.projects.peakpoker.db.ConnectionHandler
import hwr.oop.projects.peakpoker.db.ConnectionHandler.dbFile

class DbConnect : CliktCommand(name = "connect") {
    override fun help(context: Context) = "Connect to the database."
    override fun run() {

        // Postgres connection

        /*val connectionHandler = ConnectionHandler
        echo("Connecting to database at: ${connectionHandler.dbURL}")
        ConnectionHandler.connect()*/

        echo("Connecting to database at: ${dbFile.absolutePath}")
        ConnectionHandler.connect()
    }
}
