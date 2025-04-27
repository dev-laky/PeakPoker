package hwr.oop.projects.peakpoker.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Players : IntIdTable() {
    val username = varchar("username", 50)
    val chips = integer("chips")
}
