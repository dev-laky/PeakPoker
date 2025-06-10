package hwr.oop.projects.peakpoker.core.cards

import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.CommunityCards
import hwr.oop.projects.peakpoker.core.card.Rank
import hwr.oop.projects.peakpoker.core.card.Suit
import hwr.oop.projects.peakpoker.core.exceptions.DuplicateCardException
import hwr.oop.projects.peakpoker.core.exceptions.InvalidCardConfigurationException
import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import hwr.oop.projects.peakpoker.core.round.PokerRound
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class CommunityCardsTest : AnnotationSpec() {
  private lateinit var player1: PokerPlayer
  private lateinit var player2: PokerPlayer
  private lateinit var player3: PokerPlayer
  private lateinit var testRound: PokerRound

  @BeforeEach
  fun setup() {
    player1 = PokerPlayer("Hans")
    player2 = PokerPlayer("Peter")
    player3 = PokerPlayer("Max")
    testRound = PokerRound(
      smallBlindAmount = 10, bigBlindAmount = 20,
      players = listOf(player1, player2, player3),
      smallBlindIndex = 0,
      onRoundComplete = {}
    )
  }

  @Test
  fun `CommunityCards should contain exactly five cards`() {
    val cards = listOf(
      Card(Suit.DIAMONDS, Rank.FIVE),
      Card(Suit.DIAMONDS, Rank.TEN),
      Card(Suit.SPADES, Rank.ACE),
      Card(Suit.CLUBS, Rank.KING),
      Card(Suit.HEARTS, Rank.QUEEN)
    )

    val communityCards = CommunityCards(cards, testRound)

    assertThat(communityCards.cards).hasSize(5)
  }

  @Test
  fun `CommunityCards should throw exception when less than 5 cards are provided`() {
    val cards = listOf(
      Card(Suit.DIAMONDS, Rank.FIVE),
      Card(Suit.DIAMONDS, Rank.TEN),
      Card(Suit.SPADES, Rank.ACE),
      Card(Suit.CLUBS, Rank.KING)
    )

    assertThatThrownBy { CommunityCards(cards, testRound) }
      .isExactlyInstanceOf(InvalidCardConfigurationException::class.java)
      .hasMessageContaining("exactly five cards")
  }

  @Test
  fun `CommunityCards should throw exception when more than 5 cards are provided`() {
    val cards = listOf(
      Card(Suit.DIAMONDS, Rank.FIVE),
      Card(Suit.DIAMONDS, Rank.TEN),
      Card(Suit.SPADES, Rank.ACE),
      Card(Suit.CLUBS, Rank.KING),
      Card(Suit.HEARTS, Rank.QUEEN),
      Card(Suit.HEARTS, Rank.JACK)
    )

    assertThatThrownBy { CommunityCards(cards, testRound) }
      .isExactlyInstanceOf(InvalidCardConfigurationException::class.java)
      .hasMessageContaining("exactly five cards")
  }

  @Test
  fun `CommunityCards should not allow duplicate cards`() {
    val duplicateCard = Card(Suit.DIAMONDS, Rank.FIVE)
    val cards = listOf(
      duplicateCard,
      Card(Suit.DIAMONDS, Rank.TEN),
      Card(Suit.SPADES, Rank.ACE),
      Card(Suit.CLUBS, Rank.KING),
      duplicateCard
    )

    assertThatThrownBy { CommunityCards(cards, testRound) }
      .isExactlyInstanceOf(DuplicateCardException::class.java)
      .hasMessageContaining("duplicates")
  }

  @Test
  fun `CommunityCards should work with empty list initialization`() {
    val communityCards = CommunityCards(emptyList(), testRound)

    assertThat(communityCards.cards).isEmpty()
  }

  @Test
  fun `CommunityCards should implement Iterable interface correctly`() {
    val cards = listOf(
      Card(Suit.DIAMONDS, Rank.FIVE),
      Card(Suit.DIAMONDS, Rank.TEN),
      Card(Suit.SPADES, Rank.ACE),
      Card(Suit.CLUBS, Rank.KING),
      Card(Suit.HEARTS, Rank.QUEEN)
    )

    val communityCards = CommunityCards(cards, testRound)
    val iteratedCards = communityCards.toList()

    assertThat(iteratedCards).containsExactlyElementsOf(cards)
  }
}
