package hwr.oop.projects.peakpoker.db.tables

import hwr.oop.projects.peakpoker.core.card.Card
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Decks : IntIdTable() {
    val dealtCards: Column<List<Card>> = json(
        name = "dealt_cards",
        serialize = { Json.encodeToString(it) },
        deserialize = { Json.decodeFromString(it) }
    )
    val communityCards: Column<List<Card>> = json(
        name = "community_cards",
        serialize = { Json.encodeToString(it) },
        deserialize = { Json.decodeFromString(it) }
    )
    val ttl = timestamp("ttl")
}
