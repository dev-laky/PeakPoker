package hwr.oop.projects.peakpoker.db

import hwr.oop.projects.peakpoker.db.tables.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object ConnectionHandler {
    val dbFile: File
        get() = createPath()

    fun connect() {
        Database.connect(
            "jdbc:sqlite:${dbFile.absolutePath}", "org.sqlite.JDBC"
        )
    }

    fun init() {
        Database.connect(
            "jdbc:sqlite:${dbFile.absolutePath}", "org.sqlite.JDBC"
        )

        transaction {
            SchemaUtils.create(
                Players, Games, GamePlayers, Decks, Bets
            )
        }
    }

    private fun createPath(): File {
        val projectRoot = File(System.getProperty("user.dir"))
        val dbFile = File(projectRoot, "assets/data/data.db")

        if (!dbFile.exists()) {
            dbFile.parentFile.mkdirs()
            dbFile.createNewFile()
        }
        return dbFile
    }
}

// The following code for PostgresSQL integration is commented out as it is not used in the current implementation.
// json table definitions must be replaced by jsonb when using PostgresSQL

/*object ConnectionHandler {
    private const val URL = "jdbc:postgresql://localhost:5432/peakpoker"
    private const val DRIVER = "org.postgresql.Driver"
    private const val USER = "user"
    private const val PASSWORD = "password"

    val dbURL: String get() = URL

    fun init() {
        Database.connect(URL, DRIVER, USER, PASSWORD)

        transaction {
            SchemaUtils.create(
                Players,
                Games,
                GamePlayers,
                Decks,
                Bets
            )
        }
    }
}*/
