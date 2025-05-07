package hwr.oop.projects.peakpoker.core.cards

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.HoleCards
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
import hwr.oop.projects.peakpoker.core.player.PlayerInterface
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class HoleCardsTest : AnnotationSpec() {

    @Test
    fun `HoleCards should contain exactly two cards`() {
        val mockPlayer = object : PlayerInterface {
            override val name: String = "TestPlayer"
        }

        val cards = listOf(
            Card(Suit.DIAMONDS, Rank.FIVE),
            Card(Suit.HEARTS, Rank.ACE)
        )

        val holeCards = HoleCards(cards, mockPlayer)

        assertThat(holeCards.cards).hasSize(2)
    }

    @Test
    fun `HoleCards should throw exception when less than 2 cards are provided`() {
        val mockPlayer = object : PlayerInterface {
            override val name: String = "TestPlayer"
        }

        val cards = listOf(
            Card(Suit.DIAMONDS, Rank.FIVE)
        )

        assertThatThrownBy { HoleCards(cards, mockPlayer) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("exactly two cards")
    }

    @Test
    fun `HoleCards should throw exception when more than 2 cards are provided`() {
        val mockPlayer = object : PlayerInterface {
            override val name: String = "TestPlayer"
        }

        val cards = listOf(
            Card(Suit.DIAMONDS, Rank.FIVE),
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.SPADES, Rank.KING)
        )

        assertThatThrownBy { HoleCards(cards, mockPlayer) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("exactly two cards")
    }

    @Test
    fun `HoleCards should return the correct cards`() {
        val mockPlayer = object : PlayerInterface {
            override val name: String = "TestPlayer"
        }

        val card1 = Card(Suit.DIAMONDS, Rank.FIVE)
        val card2 = Card(Suit.HEARTS, Rank.ACE)

        val inputCards = listOf(card1, card2)
        val holeCards = HoleCards(inputCards, mockPlayer)

        assertThat(holeCards.cards).containsExactly(card1, card2)
    }

    @Test
    fun `HoleCards should not allow duplicate cards`() {
        val mockPlayer = object : PlayerInterface {
            override val name: String = "TestPlayer"
        }

        val duplicateCard = Card(Suit.DIAMONDS, Rank.FIVE)
        val cards = listOf(
            duplicateCard,
            duplicateCard
        )

        assertThatThrownBy { HoleCards(cards, mockPlayer) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("duplicates")
    }

    @Test
    fun `HoleCards should work with empty list initialization`() {
        val mockPlayer = object : PlayerInterface {
            override val name: String = "TestPlayer"
        }

        val holeCards = HoleCards(emptyList(), mockPlayer)

        assertThat(holeCards.cards).isEmpty()
    }

    @Test
    fun `HoleCards should implement Iterable interface correctly`() {
        val mockPlayer = object : PlayerInterface {
            override val name: String = "TestPlayer"
        }

        val cards = listOf(
            Card(Suit.DIAMONDS, Rank.FIVE),
            Card(Suit.HEARTS, Rank.ACE)
        )

        val holeCards = HoleCards(cards, mockPlayer)
        val iteratedCards = holeCards.toList()

        assertThat(iteratedCards).containsExactlyElementsOf(cards)
    }

}
