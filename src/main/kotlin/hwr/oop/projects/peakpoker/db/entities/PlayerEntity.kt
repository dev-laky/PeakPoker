package hwr.oop.projects.peakpoker.db.entities

import hwr.oop.projects.peakpoker.db.tables.GamePlayers
import hwr.oop.projects.peakpoker.db.tables.Players
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class PlayerEntity(id: EntityID<Int>) : IntEntity(id) {
    var username by Players.username
    var chips by Players.chips
    val games by GameEntity via GamePlayers

    companion object : IntEntityClass<PlayerEntity>(Players) {
        fun create(username: String, initialChips: Int = 1000): PlayerEntity = transaction {
            new {
                this.username = username
                this.chips = initialChips
            }
        }

        fun findByUsername(username: String): PlayerEntity? = transaction {
            find { Players.username eq username }.singleOrNull()
        }

        fun getAll(): List<PlayerEntity> = transaction {
            all().toList()
        }

        fun updateChips(playerId: Int, newChips: Int) = transaction {
            findById(playerId)?.apply {
                chips = newChips
            }
        }
    }
}
