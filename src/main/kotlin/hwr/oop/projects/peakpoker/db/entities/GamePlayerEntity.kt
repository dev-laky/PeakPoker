package hwr.oop.projects.peakpoker.db.entities

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.player.PlayerRole
import hwr.oop.projects.peakpoker.db.config.DEFAULT_TTL_DURATION
import hwr.oop.projects.peakpoker.db.tables.GamePlayers
import kotlinx.datetime.Clock.System.now
import org.jetbrains.exposed.dao.CompositeEntity
import org.jetbrains.exposed.dao.CompositeEntityClass
import org.jetbrains.exposed.dao.id.CompositeID
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration

class GamePlayerEntity(id: EntityID<CompositeID>) : CompositeEntity(id) {
    var gameId by GamePlayers.gameId
    var playerId by GamePlayers.playerId
    var playerGameRole by GamePlayers.playerGameRole
    var hasFolded by GamePlayers.hasFolded
    var hand by GamePlayers.hand
    var ttl by GamePlayers.ttl

    var game by GameEntity referencedOn GamePlayers.gameId
    var player by PlayerEntity referencedOn GamePlayers.playerId

    companion object : CompositeEntityClass<GamePlayerEntity>(GamePlayers) {
        fun create(
            gameId: EntityID<Int>,
            playerId: EntityID<Int>,
            role: PlayerRole,
            ttlDuration: Duration? = DEFAULT_TTL_DURATION
        ): GamePlayerEntity = transaction {

            val id = CompositeID {
                it[GamePlayers.gameId] = gameId
                it[GamePlayers.playerId] = playerId
            }

            new(id) {
                this.playerGameRole = role
                this.hasFolded = false
                this.hand = emptyList()
                this.ttl = ttlDuration?.let { now().plus(it) }!!
            }
        }

        fun findPlayersInGame(gameId: EntityID<Int>): SizedIterable<GamePlayerEntity> = transaction {
            find { GamePlayers.gameId eq gameId }
        }

        fun updatePlayerHand(gameId: EntityID<Int>, playerId: EntityID<Int>, hand: List<Card>) = transaction {
            find {
                (GamePlayers.gameId eq gameId) and
                        (GamePlayers.playerId eq playerId)
            }.firstOrNull()?.apply {
                this.hand = hand
            }
        }

        fun foldPlayer(gameId: EntityID<Int>, playerId: EntityID<Int>) = transaction {
            find {
                (GamePlayers.gameId eq gameId) and
                        (GamePlayers.playerId eq playerId)
            }.firstOrNull()?.apply {
                this.hasFolded = true
            }
        }
    }
}
