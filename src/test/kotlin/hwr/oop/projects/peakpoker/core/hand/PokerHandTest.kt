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
    }.isExactlyInstanceOf(PokerHand.InvalidHandSizeException::class.java)
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
    }.isExactlyInstanceOf(PokerHand.InvalidHandSizeException::class.java)
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
    }.isExactlyInstanceOf(PokerHand.DuplicateCardException::class.java)
  }

  // Hand rank tests - Instead of checking the private rank property,
  // we'll use comparison to verify the relative strength of hands
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

    val straightFlush = PokerHand(
      listOf(
        Card(CLUBS, NINE),
        Card(CLUBS, EIGHT),
        Card(CLUBS, SEVEN),
        Card(CLUBS, SIX),
        Card(CLUBS, FIVE)
      )
    )

    // Royal flush is better than a straight flush
    assertThat(royalFlush.compareTo(straightFlush)).isGreaterThan(0)
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

    val fourOfAKind = PokerHand(
      listOf(
        Card(HEARTS, JACK),
        Card(DIAMONDS, JACK),
        Card(CLUBS, JACK),
        Card(SPADES, JACK),
        Card(HEARTS, KING)
      )
    )

    // Straight flush is better than four of a kind
    assertThat(straightFlush.compareTo(fourOfAKind)).isGreaterThan(0)
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

    val fullHouse = PokerHand(
      listOf(
        Card(HEARTS, QUEEN),
        Card(DIAMONDS, QUEEN),
        Card(CLUBS, QUEEN),
        Card(SPADES, TWO),
        Card(HEARTS, TWO)
      )
    )

    // Four of a kind is better than a full house
    assertThat(fourOfAKind.compareTo(fullHouse)).isGreaterThan(0)
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

    val flush = PokerHand(
      listOf(
        Card(DIAMONDS, ACE),
        Card(DIAMONDS, JACK),
        Card(DIAMONDS, NINE),
        Card(DIAMONDS, SEVEN),
        Card(DIAMONDS, THREE)
      )
    )

    // A full house is better than a flush
    assertThat(fullHouse.compareTo(flush)).isGreaterThan(0)
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

    val straight = PokerHand(
      listOf(
        Card(HEARTS, EIGHT),
        Card(DIAMONDS, SEVEN),
        Card(CLUBS, SIX),
        Card(SPADES, FIVE),
        Card(HEARTS, FOUR)
      )
    )

    // Flush is better than straight
    assertThat(flush.compareTo(straight)).isGreaterThan(0)
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

    val threeOfAKind = PokerHand(
      listOf(
        Card(HEARTS, FOUR),
        Card(DIAMONDS, FOUR),
        Card(CLUBS, FOUR),
        Card(SPADES, JACK),
        Card(HEARTS, ACE)
      )
    )

    // Straight is better than three of a kind
    assertThat(straight.compareTo(threeOfAKind)).isGreaterThan(0)
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

    val threeOfAKind = PokerHand(
      listOf(
        Card(HEARTS, FOUR),
        Card(DIAMONDS, FOUR),
        Card(CLUBS, FOUR),
        Card(SPADES, JACK),
        Card(HEARTS, KING)
      )
    )

    // Wheel straight is better than three of a kind
    assertThat(wheelStraight.compareTo(threeOfAKind)).isGreaterThan(0)
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

    val twoPair = PokerHand(
      listOf(
        Card(HEARTS, NINE),
        Card(DIAMONDS, NINE),
        Card(CLUBS, SEVEN),
        Card(SPADES, SEVEN),
        Card(HEARTS, ACE)
      )
    )

    // Three of a kind is better than two pairs
    assertThat(threeOfAKind.compareTo(twoPair)).isGreaterThan(0)
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

    val onePair = PokerHand(
      listOf(
        Card(HEARTS, TEN),
        Card(DIAMONDS, TEN),
        Card(CLUBS, KING),
        Card(SPADES, SEVEN),
        Card(HEARTS, THREE)
      )
    )

    // Two pairs are better than one pair
    assertThat(twoPair.compareTo(onePair)).isGreaterThan(0)
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

    val highCard = PokerHand(
      listOf(
        Card(HEARTS, ACE),
        Card(DIAMONDS, KING),
        Card(CLUBS, JACK),
        Card(SPADES, SEVEN),
        Card(HEARTS, THREE)
      )
    )

    // One pair is better than high card
    assertThat(onePair.compareTo(highCard)).isGreaterThan(0)
  }

  @Test
  fun `correctly identifies high card`() {
    val highCardAce = PokerHand(
      listOf(
        Card(HEARTS, ACE),
        Card(DIAMONDS, KING),
        Card(CLUBS, JACK),
        Card(SPADES, SEVEN),
        Card(HEARTS, THREE)
      )
    )

    val highCardKing = PokerHand(
      listOf(
        Card(HEARTS, KING),
        Card(DIAMONDS, QUEEN),
        Card(CLUBS, JACK),
        Card(SPADES, SEVEN),
        Card(HEARTS, THREE)
      )
    )

    // Ace high is better than king-high
    assertThat(highCardAce.compareTo(highCardKing)).isGreaterThan(0)
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

  @Test
  fun `royal flush with isFlush condition negated should fail`() {
    val notFlushRoyal = PokerHand(
      listOf(
        Card(HEARTS, ACE),
        Card(SPADES, KING),
        Card(HEARTS, QUEEN),
        Card(HEARTS, JACK),
        Card(HEARTS, TEN)
      )
    )

    val fourOfAKind = PokerHand(
      listOf(
        Card(HEARTS, JACK),
        Card(DIAMONDS, JACK),
        Card(CLUBS, JACK),
        Card(SPADES, JACK),
        Card(HEARTS, KING)
      )
    )

    assertThat(fourOfAKind.compareTo(notFlushRoyal)).isGreaterThan(0)
  }

  @Test
  fun `logical AND replaced with OR should fail`() {
    val regularFlush = PokerHand(
      listOf(
        Card(DIAMONDS, ACE),
        Card(DIAMONDS, NINE),
        Card(DIAMONDS, SEVEN),
        Card(DIAMONDS, FIVE),
        Card(DIAMONDS, THREE)
      )
    )

    val straightFlush = PokerHand(
      listOf(
        Card(CLUBS, NINE),
        Card(CLUBS, EIGHT),
        Card(CLUBS, SEVEN),
        Card(CLUBS, SIX),
        Card(CLUBS, FIVE)
      )
    )

    assertThat(straightFlush.compareTo(regularFlush)).isGreaterThan(0)
  }

  @Test
  fun `condition order swapped should still work`() {
    val royalFlush = PokerHand(
      listOf(
        Card(SPADES, ACE),
        Card(SPADES, KING),
        Card(SPADES, QUEEN),
        Card(SPADES, JACK),
        Card(SPADES, TEN)
      )
    )

    val straightFlush = PokerHand(
      listOf(
        Card(HEARTS, NINE),
        Card(HEARTS, EIGHT),
        Card(HEARTS, SEVEN),
        Card(HEARTS, SIX),
        Card(HEARTS, FIVE)
      )
    )

    assertThat(royalFlush.compareTo(straightFlush)).isGreaterThan(0)
  }

  @Test
  fun `both conditions false should not be royal flush`() {
    val highCard = PokerHand(
      listOf(
        Card(HEARTS, ACE),
        Card(SPADES, KING),
        Card(DIAMONDS, QUEEN),
        Card(CLUBS, JACK),
        Card(HEARTS, NINE)
      )
    )

    val onePair = PokerHand(
      listOf(
        Card(HEARTS, TWO),
        Card(DIAMONDS, TWO),
        Card(CLUBS, THREE),
        Card(SPADES, FOUR),
        Card(HEARTS, FIVE)
      )
    )

    assertThat(onePair.compareTo(highCard)).isGreaterThan(0)
  }

  @Test
  fun `royal condition boundary case`() {
    val almostRoyal = PokerHand(
      listOf(
        Card(HEARTS, ACE),
        Card(HEARTS, KING),
        Card(HEARTS, QUEEN),
        Card(HEARTS, JACK),
        Card(HEARTS, NINE)
      )
    )

    val fourOfAKind = PokerHand(
      listOf(
        Card(HEARTS, EIGHT),
        Card(DIAMONDS, EIGHT),
        Card(CLUBS, EIGHT),
        Card(SPADES, EIGHT),
        Card(HEARTS, KING)
      )
    )

    assertThat(fourOfAKind.compareTo(almostRoyal)).isGreaterThan(0)
  }
}