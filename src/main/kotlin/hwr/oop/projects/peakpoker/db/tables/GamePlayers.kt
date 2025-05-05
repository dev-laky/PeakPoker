package hwr.oop.projects.peakpoker.db.tables

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.player.PlayerRole
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

// m:m relationship between Games and Players
object GamePlayers : CompositeIdTable("GamePlayers") {
    val gameId = reference("game_id", Games).entityId()
    val playerId = reference("player_id", Players).entityId()
    val playerGameRole = enumeration("player_game_role", PlayerRole::class)
    val hasFolded = bool("has_folded")
    val hand: Column<List<Card>> = json(
        name = "hand",
        serialize = { Json.encodeToString(it) },
        deserialize = { Json.decodeFromString(it) }
    )
    val ttl = timestamp("ttl")

    override val primaryKey = PrimaryKey(gameId, playerId)
}
