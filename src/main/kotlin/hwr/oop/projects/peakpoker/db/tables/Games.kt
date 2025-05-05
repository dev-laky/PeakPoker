package hwr.oop.projects.peakpoker.db.tables

import hwr.oop.projects.peakpoker.core.game.GameAction
import hwr.oop.projects.peakpoker.core.game.GameState
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Games : IntIdTable() {
    val isActive = bool("is_active")
    val currentPlayerTurnId = integer("current_player_turn_id").nullable()
    val currentGameState = enumeration("current_game_state", GameState::class)
    val deckId = reference("deck_id", Decks)
    val lastBet = integer("last_bet").nullable()
    val lastAction = enumeration("last_action", GameAction::class).nullable()
    val ttl = timestamp("ttl")
}
