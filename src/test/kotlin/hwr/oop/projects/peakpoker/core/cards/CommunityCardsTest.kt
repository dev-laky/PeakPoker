package hwr.oop.projects.peakpoker.core.cards

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
import hwr.oop.projects.peakpoker.core.deck.Deck
import hwr.oop.projects.peakpoker.core.exceptions.DuplicateCardException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidCardConfigurationException
import hwr.oop.projects.peakpoker.core.game.RoundPhase
import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class CommunityCardsTest : AnnotationSpec() {
  private lateinit var player1: PokerPlayer
  private lateinit var player2: PokerPlayer
  private lateinit var player3: PokerPlayer

  @BeforeEach
  fun setup() {
    player1 = PokerPlayer("Hans")
    player2 = PokerPlayer("Peter")
    player3 = PokerPlayer("Max")
  }

  @Test
  fun `CommunityCards should contain exactly five cards`() {
    val cards = mutableListOf(
      Card(Suit.DIAMONDS, Rank.FIVE),
      Card(Suit.DIAMONDS, Rank.TEN),
      Card(Suit.SPADES, Rank.ACE),
      Card(Suit.CLUBS, Rank.KING),
      Card(Suit.HEARTS, Rank.QUEEN)
    )

    val communityCards = CommunityCards(cards)

    assertThat(communityCards.cards()).hasSize(5)
  }

  @Test
  fun `CommunityCards should throw exception when one card is given`() {
    val cards = mutableListOf(
      Card(Suit.DIAMONDS, Rank.FIVE)
    )

    assertThatThrownBy { CommunityCards(cards) }
      .isExactlyInstanceOf(InvalidCardConfigurationException::class.java)
      .hasMessageContaining("contain exactly 3, 4, or 5 cards")
  }

  @Test
  fun `CommunityCards should throw exception when two cards are given`() {
    val cards = mutableListOf(
      Card(Suit.DIAMONDS, Rank.FIVE),
      Card(Suit.DIAMONDS, Rank.TEN)
    )

    assertThatThrownBy { CommunityCards(cards) }
      .isExactlyInstanceOf(InvalidCardConfigurationException::class.java)
      .hasMessageContaining("contain exactly 3, 4, or 5 cards")
  }

  @Test
  fun `CommunityCards should throw exception when more than 5 cards are provided`() {
    val cards = mutableListOf(
      Card(Suit.DIAMONDS, Rank.FIVE),
      Card(Suit.DIAMONDS, Rank.TEN),
      Card(Suit.SPADES, Rank.ACE),
      Card(Suit.CLUBS, Rank.KING),
      Card(Suit.HEARTS, Rank.QUEEN),
      Card(Suit.HEARTS, Rank.JACK)
    )

    assertThatThrownBy { CommunityCards(cards) }
      .isExactlyInstanceOf(InvalidCardConfigurationException::class.java)
      .hasMessageContaining("contain exactly 3, 4, or 5 cards")
  }

  @Test
  fun `CommunityCards should not allow duplicate cards`() {
    val duplicateCard = Card(Suit.DIAMONDS, Rank.FIVE)
    val cards = mutableListOf(
      duplicateCard,
      Card(Suit.DIAMONDS, Rank.TEN),
      Card(Suit.SPADES, Rank.ACE),
      Card(Suit.CLUBS, Rank.KING),
      duplicateCard
    )

    assertThatThrownBy { CommunityCards(cards) }
      .isExactlyInstanceOf(DuplicateCardException::class.java)
      .hasMessageContaining("duplicates")
  }

  @Test
  fun `CommunityCards should work with empty list initialization`() {
    val communityCards = CommunityCards(mutableListOf())

    assertThat(communityCards.cards()).isEmpty()
  }

  @Test
  fun `CommunityCards should implement Iterable interface correctly`() {
    val cards = mutableListOf(
      Card(Suit.DIAMONDS, Rank.FIVE),
      Card(Suit.DIAMONDS, Rank.TEN),
      Card(Suit.SPADES, Rank.ACE),
      Card(Suit.CLUBS, Rank.KING),
      Card(Suit.HEARTS, Rank.QUEEN)
    )

    val communityCards = CommunityCards(cards)
    val iteratedCards = communityCards.toList()

    assertThat(iteratedCards).containsExactlyElementsOf(cards)
  }

  @Test
  fun `dealCommunityCards should throw exception when called with PRE_FLOP phase`() {
    val communityCards = CommunityCards()
    val deck = Deck()

    assertThatThrownBy {
      communityCards.dealCommunityCards(
        RoundPhase.PRE_FLOP,
        deck
      )
    }
      .isExactlyInstanceOf(IllegalStateException::class.java)
      .hasMessageContaining("Cannot deal community cards before the flop")
  }

  @Test
  fun `dealCommunityCards should throw exception when called with SHOWDOWN phase`() {
    val communityCards = CommunityCards()
    val deck = Deck()

    assertThatThrownBy {
      communityCards.dealCommunityCards(
        RoundPhase.SHOWDOWN,
        deck
      )
    }
      .isExactlyInstanceOf(IllegalStateException::class.java)
      .hasMessageContaining("Cannot deal community cards after the showdown")
  }
}
