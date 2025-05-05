package hwr.oop.projects.peakpoker.db.entities

import hwr.oop.projects.peakpoker.core.game.GameAction
import hwr.oop.projects.peakpoker.core.game.GameState
import hwr.oop.projects.peakpoker.db.config.DEFAULT_TTL_DURATION
import hwr.oop.projects.peakpoker.db.tables.Bets
import hwr.oop.projects.peakpoker.db.tables.Games
import hwr.oop.projects.peakpoker.db.tables.Players
import kotlinx.datetime.Clock.System.now
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration

class BetEntity(id: EntityID<Int>) : IntEntity(id) {
    var gameId by Bets.gameId
    var playerId by Bets.playerId
    var amount by Bets.amount
    var action by Bets.action
    var gameState by Bets.gameState
    var ttl by Bets.ttl

    val game by GameEntity referencedOn Bets.gameId
    val player by PlayerEntity referencedOn Bets.playerId

    companion object : IntEntityClass<BetEntity>(Bets) {
        fun create(
            gameId: EntityID<Int>,
            playerId: EntityID<Int>,
            amount: Int,
            action: GameAction,
            gameState: GameState,
            ttlDuration: Duration? = DEFAULT_TTL_DURATION
        ): BetEntity = transaction {
            new {
                this.gameId = gameId
                this.playerId = playerId
                this.amount = amount
                this.action = action
                this.gameState = gameState
                this.ttl = ttlDuration?.let { now().plus(it) }!!
            }
        }

        fun findBetsByGame(gameId: Int): List<BetEntity> = transaction {
            find { Bets.gameId eq EntityID(gameId, Games) }.toList()
        }

        fun findBetsByPlayer(playerId: Int): List<BetEntity> = transaction {
            find { Bets.playerId eq EntityID(playerId, Players) }.toList()
        }

        fun findLatestBetByGame(gameId: Int): BetEntity? = transaction {
            find { Bets.gameId eq EntityID(gameId, Games) }
                .orderBy(Bets.ttl to SortOrder.DESC)
                .limit(1)
                .singleOrNull()
        }
    }
}
