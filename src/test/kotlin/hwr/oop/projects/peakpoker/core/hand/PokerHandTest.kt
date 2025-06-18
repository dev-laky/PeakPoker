package hwr.oop.projects.peakpoker.core.hand

import io.kotest.core.spec.style.AnnotationSpec
import hwr.oop.projects.peakpoker.core.card.Card
import hwr.oop.projects.peakpoker.core.card.Suit.CLUBS
import hwr.oop.projects.peakpoker.core.card.Suit.HEARTS
import hwr.oop.projects.peakpoker.core.card.Suit.DIAMONDS
import hwr.oop.projects.peakpoker.core.card.Suit.SPADES
import hwr.oop.projects.peakpoker.core.card.Rank.TWO
import hwr.oop.projects.peakpoker.core.card.Rank.THREE
import hwr.oop.projects.peakpoker.core.card.Rank.FOUR
import hwr.oop.projects.peakpoker.core.card.Rank.FIVE
import hwr.oop.projects.peakpoker.core.card.Rank.SIX
import hwr.oop.projects.peakpoker.core.card.Rank.SEVEN
import hwr.oop.projects.peakpoker.core.card.Rank.EIGHT
import hwr.oop.projects.peakpoker.core.card.Rank.NINE
import hwr.oop.projects.peakpoker.core.card.Rank.TEN
import hwr.oop.projects.peakpoker.core.card.Rank.JACK
import hwr.oop.projects.peakpoker.core.card.Rank.QUEEN
import hwr.oop.projects.peakpoker.core.card.Rank.KING
import hwr.oop.projects.peakpoker.core.card.Rank.ACE
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class PokerHandTest : AnnotationSpec() {

  // Constructor tests
  @Test
  fun `creates valid poker hand with 5 cards`() {
    val hand = PokerHand(
      listOf(
        Card(HEARTS, ACE),
        Card(SPADES, KING),
        Card(DIAMONDS, QUEEN),
        Card(CLUBS, JACK),
        Card(HEARTS, TEN)
      )
    )
    assertThat(hand).isNotNull
  }

  @Test
  fun `throws exception for less than 5 cards`() {
    assertThatThrownBy {
      PokerHand(
        listOf(
          Card(HEARTS, ACE),
          Card(SPADES, KING),
          Card(DIAMONDS, QUEEN),
          Card(CLUBS, JACK)
        )
      )
    }.isInstanceOf(IllegalArgumentException::class.java)
  }

  @Test
  fun `throws exception for more than 5 cards`() {
    assertThatThrownBy {
      PokerHand(
        listOf(
          Card(HEARTS, ACE),
          Card(SPADES, KING),
          Card(DIAMONDS, QUEEN),
          Card(CLUBS, JACK),
          Card(HEARTS, TEN),
          Card(SPADES, NINE)
        )
      )
    }.isInstanceOf(IllegalArgumentException::class.java)
  }

  @Test
  fun `throws exception for duplicate cards`() {
    assertThatThrownBy {
      PokerHand(
        listOf(
          Card(HEARTS, ACE),
          Card(HEARTS, ACE), // Duplicate card
          Card(DIAMONDS, QUEEN),
          Card(CLUBS, JACK),
          Card(HEARTS, TEN)
        )
      )
    }.isInstanceOf(IllegalArgumentException::class.java)
  }

  // Hand rank tests
  @Test
  fun `correctly identifies royal flush`() {
    val royalFlush = PokerHand(
      listOf(
        Card(HEARTS, ACE),
        Card(HEARTS, KING),
        Card(HEARTS, QUEEN),
        Card(HEARTS, JACK),
        Card(HEARTS, TEN)
      )
    )
    assertThat(royalFlush.rank).isEqualTo(HandRank.ROYAL_FLUSH)
  }

  @Test
  fun `correctly identifies straight flush`() {
    val straightFlush = PokerHand(
      listOf(
        Card(CLUBS, NINE),
        Card(CLUBS, EIGHT),
        Card(CLUBS, SEVEN),
        Card(CLUBS, SIX),
        Card(CLUBS, FIVE)
      )
    )
    assertThat(straightFlush.rank).isEqualTo(HandRank.STRAIGHT_FLUSH)
  }

  @Test
  fun `correctly identifies four of a kind`() {
    val fourOfAKind = PokerHand(
      listOf(
        Card(HEARTS, JACK),
        Card(DIAMONDS, JACK),
        Card(CLUBS, JACK),
        Card(SPADES, JACK),
        Card(HEARTS, KING)
      )
    )
    assertThat(fourOfAKind.rank).isEqualTo(HandRank.FOUR_OF_A_KIND)
  }

  @Test
  fun `correctly identifies full house`() {
    val fullHouse = PokerHand(
      listOf(
        Card(HEARTS, QUEEN),
        Card(DIAMONDS, QUEEN),
        Card(CLUBS, QUEEN),
        Card(SPADES, TWO),
        Card(HEARTS, TWO)
      )
    )
    assertThat(fullHouse.rank).isEqualTo(HandRank.FULL_HOUSE)
  }

  @Test
  fun `correctly identifies flush`() {
    val flush = PokerHand(
      listOf(
        Card(DIAMONDS, ACE),
        Card(DIAMONDS, JACK),
        Card(DIAMONDS, NINE),
        Card(DIAMONDS, SEVEN),
        Card(DIAMONDS, THREE)
      )
    )
    assertThat(flush.rank).isEqualTo(HandRank.FLUSH)
  }

  @Test
  fun `correctly identifies straight`() {
    val straight = PokerHand(
      listOf(
        Card(HEARTS, EIGHT),
        Card(DIAMONDS, SEVEN),
        Card(CLUBS, SIX),
        Card(SPADES, FIVE),
        Card(HEARTS, FOUR)
      )
    )
    assertThat(straight.rank).isEqualTo(HandRank.STRAIGHT)
  }

  @Test
  fun `correctly identifies wheel straight`() {
    val wheelStraight = PokerHand(
      listOf(
        Card(HEARTS, ACE),
        Card(DIAMONDS, TWO),
        Card(CLUBS, THREE),
        Card(SPADES, FOUR),
        Card(HEARTS, FIVE)
      )
    )
    assertThat(wheelStraight.rank).isEqualTo(HandRank.STRAIGHT)
  }

  @Test
  fun `correctly identifies three of a kind`() {
    val threeOfAKind = PokerHand(
      listOf(
        Card(HEARTS, FOUR),
        Card(DIAMONDS, FOUR),
        Card(CLUBS, FOUR),
        Card(SPADES, JACK),
        Card(HEARTS, ACE)
      )
    )
    assertThat(threeOfAKind.rank).isEqualTo(HandRank.THREE_OF_A_KIND)
  }

  @Test
  fun `correctly identifies two pair`() {
    val twoPair = PokerHand(
      listOf(
        Card(HEARTS, NINE),
        Card(DIAMONDS, NINE),
        Card(CLUBS, SEVEN),
        Card(SPADES, SEVEN),
        Card(HEARTS, ACE)
      )
    )
    assertThat(twoPair.rank).isEqualTo(HandRank.TWO_PAIR)
  }

  @Test
  fun `correctly identifies one pair`() {
    val onePair = PokerHand(
      listOf(
        Card(HEARTS, TEN),
        Card(DIAMONDS, TEN),
        Card(CLUBS, KING),
        Card(SPADES, SEVEN),
        Card(HEARTS, THREE)
      )
    )
    assertThat(onePair.rank).isEqualTo(HandRank.ONE_PAIR)
  }

  @Test
  fun `correctly identifies high card`() {
    val highCard = PokerHand(
      listOf(
        Card(HEARTS, ACE),
        Card(DIAMONDS, KING),
        Card(CLUBS, JACK),
        Card(SPADES, SEVEN),
        Card(HEARTS, THREE)
      )
    )
    assertThat(highCard.rank).isEqualTo(HandRank.HIGH_CARD)
  }

  // Comparison tests
  @Test
  fun `compares different ranked hands correctly`() {
    val flush = PokerHand(
      listOf(
        Card(DIAMONDS, ACE),
        Card(DIAMONDS, JACK),
        Card(DIAMONDS, NINE),
        Card(DIAMONDS, SEVEN),
        Card(DIAMONDS, THREE)
      )
    )

    val straight = PokerHand(
      listOf(
        Card(HEARTS, EIGHT),
        Card(DIAMONDS, SEVEN),
        Card(CLUBS, SIX),
        Card(SPADES, FIVE),
        Card(HEARTS, FOUR)
      )
    )

    assertThat(flush.compareTo(straight)).isGreaterThan(0)
    assertThat(straight.compareTo(flush)).isLessThan(0)
  }

  @Test
  fun `compares same ranked hands with different high cards`() {
    val pairAce = PokerHand(
      listOf(
        Card(HEARTS, ACE),
        Card(DIAMONDS, ACE),
        Card(CLUBS, KING),
        Card(SPADES, QUEEN),
        Card(HEARTS, JACK)
      )
    )

    val pairKing = PokerHand(
      listOf(
        Card(HEARTS, KING),
        Card(DIAMONDS, KING),
        Card(CLUBS, QUEEN),
        Card(SPADES, JACK),
        Card(HEARTS, TEN)
      )
    )

    assertThat(pairAce.compareTo(pairKing)).isGreaterThan(0)
    assertThat(pairKing.compareTo(pairAce)).isLessThan(0)
  }

  @Test
  fun `compares same ranked hands with same high card but different kickers`() {
    val pairAceKingHigh = PokerHand(
      listOf(
        Card(HEARTS, ACE),
        Card(DIAMONDS, ACE),
        Card(CLUBS, KING),
        Card(SPADES, QUEEN),
        Card(HEARTS, JACK)
      )
    )

    val pairAceQueenHigh = PokerHand(
      listOf(
        Card(HEARTS, ACE),
        Card(DIAMONDS, ACE),
        Card(CLUBS, QUEEN),
        Card(SPADES, JACK),
        Card(HEARTS, TEN)
      )
    )

    assertThat(pairAceKingHigh.compareTo(pairAceQueenHigh)).isGreaterThan(0)
    assertThat(pairAceQueenHigh.compareTo(pairAceKingHigh)).isLessThan(0)
  }

  @Test
  fun `compares identical hands as equal`() {
    val hand1 = PokerHand(
      listOf(
        Card(HEARTS, ACE),
        Card(DIAMONDS, KING),
        Card(CLUBS, QUEEN),
        Card(SPADES, JACK),
        Card(HEARTS, TEN)
      )
    )

    val hand2 = PokerHand(
      listOf(
        Card(SPADES, ACE),
        Card(CLUBS, KING),
        Card(HEARTS, QUEEN),
        Card(DIAMONDS, JACK),
        Card(SPADES, TEN)
      )
    )

    assertThat(hand1.compareTo(hand2)).isEqualTo(0)
  }
}