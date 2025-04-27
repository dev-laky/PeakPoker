package hwr.oop.projects.peakpoker.db.tables

import hwr.oop.projects.peakpoker.core.game.GameActions
import hwr.oop.projects.peakpoker.core.game.GameStates
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Bets : IntIdTable() {
    val gameId = reference("game_id", Games)
    val playerId = reference("player_id", Players)
    val amount = integer("amount")
    val action = enumeration("action", GameActions::class)
    val gameState = enumeration("game_state", GameStates::class)
    val ttl = timestamp("ttl")
}
