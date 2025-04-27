package hwr.oop.projects.peakpoker.db.entities

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.db.config.DEFAULT_TTL_DURATION
import hwr.oop.projects.peakpoker.db.tables.Decks
import hwr.oop.projects.peakpoker.db.tables.Games
import kotlinx.datetime.Clock.System.now
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration

class DeckEntity(id: EntityID<Int>) : IntEntity(id) {
    var dealtCards by Decks.dealtCards
    var communityCards by Decks.communityCards
    var ttl by Decks.ttl

    val games by GameEntity referrersOn Games.deckId

    companion object : IntEntityClass<DeckEntity>(Decks) {
        fun create(ttlDuration: Duration? = DEFAULT_TTL_DURATION): DeckEntity = transaction {
            new {
                this.dealtCards = emptyList()
                this.communityCards = emptyList()
                this.ttl = ttlDuration?.let { now().plus(it) }!!
            }
        }

        fun addCommunityCard(deckId: Int, card: Card) = transaction {
            findById(deckId)?.apply {
                communityCards = communityCards + card
            }
        }

        fun dealCards(deckId: Int, cards: List<Card>) = transaction {
            findById(deckId)?.apply {
                dealtCards = dealtCards + cards
            }
        }
    }
}
