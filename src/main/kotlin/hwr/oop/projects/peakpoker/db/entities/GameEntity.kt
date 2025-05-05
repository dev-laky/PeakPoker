package hwr.oop.projects.peakpoker.db.entities

import hwr.oop.projects.peakpoker.core.game.GameState
import hwr.oop.projects.peakpoker.db.config.DEFAULT_TTL_DURATION
import hwr.oop.projects.peakpoker.db.tables.Bets
import hwr.oop.projects.peakpoker.db.tables.GamePlayers
import hwr.oop.projects.peakpoker.db.tables.Games
import kotlinx.datetime.Clock.System.now
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration

class GameEntity(id: EntityID<Int>) : IntEntity(id) {
    var isActive by Games.isActive
    var currentPlayerTurnId by Games.currentPlayerTurnId
    var currentGameState by Games.currentGameState
    var deckId by Games.deckId
    var lastBet by Games.lastBet
    var lastAction by Games.lastAction
    var ttl by Games.ttl

    val deck by DeckEntity referencedOn Games.deckId
    val players by PlayerEntity via GamePlayers
    val bets by BetEntity referrersOn Bets.gameId

    companion object : IntEntityClass<GameEntity>(Games) {
        fun create(deckId: EntityID<Int>, ttlDuration: Duration? = DEFAULT_TTL_DURATION): GameEntity = transaction {
            new {
                this.isActive = true
                this.currentPlayerTurnId = null
                this.currentGameState = GameState.PRE_FLOP
                this.deckId = deckId
                this.lastBet = 0
                this.lastAction = null
                this.ttl = ttlDuration?.let { now().plus(it) }!!
            }
        }

        fun findActiveGames(): List<GameEntity> = transaction {
            find { Games.isActive eq true }.toList()
        }

        fun updateGameState(gameId: Int, newState: GameState) = transaction {
            findById(gameId)?.apply {
                currentGameState = newState
            }
        }
    }
}
