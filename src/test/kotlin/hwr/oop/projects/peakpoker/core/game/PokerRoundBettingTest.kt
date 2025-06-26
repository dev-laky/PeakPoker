package hwr.oop.projects.peakpoker.core.game

import hwr.oop.projects.peakpoker.core.player.PokerPlayer
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy

class PokerRoundBettingTest : AnnotationSpec() {
  @Test
  fun `requireLargerThanHighestBet throws exception when bet equals highest bet`() {
    val players = listOf(PokerPlayer("Alice", 100), PokerPlayer("Bob", 100))
    val round = PokerRound(players, 10, 20, 0)

    val requireMethod = round.javaClass.getDeclaredMethod(
      "requireLargerThanHighestBet",
      Int::class.java,
      Int::class.java
    )
    requireMethod.isAccessible = true

    assertThatThrownBy { requireMethod.invoke(round, 50, 50) }
      .cause()
      .isInstanceOf(PokerRound.InvalidBetAmountException::class.java)
      .hasMessage("Bet must be higher than the current highest bet")
  }

  @Test
  fun `requireLargerThanHighestBet throws exception when bet is less than highest bet`() {
    val players = listOf(PokerPlayer("Alice", 100), PokerPlayer("Bob", 100))
    val round = PokerRound(players, 10, 20, 0)

    val requireMethod = round.javaClass.getDeclaredMethod(
      "requireLargerThanHighestBet",
      Int::class.java,
      Int::class.java
    )
    requireMethod.isAccessible = true

    assertThatThrownBy { requireMethod.invoke(round, 60, 50) }
      .cause()
      .isInstanceOf(PokerRound.InvalidBetAmountException::class.java)
      .hasMessage("Bet must be higher than the current highest bet")
  }

  @Test
  fun `requirePositiveChips throws exception when chips is negative`() {
    val players = listOf(PokerPlayer("Alice", 100), PokerPlayer("Bob", 100))
    val round = PokerRound(players, 10, 20, 0)

    val requireMethod = round.javaClass.getDeclaredMethod(
      "requirePositiveChips",
      Int::class.java
    )
    requireMethod.isAccessible = true

    assertThatThrownBy { requireMethod.invoke(round, -10) }
      .cause()
      .isInstanceOf(PokerRound.InvalidBetAmountException::class.java)
      .hasMessage("Bet amount must be positive")
  }

  @Test
  fun `requireSufficientChipsToRaise throws exception when not enough chips`() {
    val players = listOf(PokerPlayer("Alice", 100), PokerPlayer("Bob", 100))
    val round = PokerRound(players, 10, 20, 0)

    val player = players[0]
    player.setBetAmount(30) // Player already bet 30 chips, has 70 remaining

    val requireMethod = round.javaClass.getDeclaredMethod(
      "requireSufficientChipsToRaise",
      PokerPlayer::class.java,
      Int::class.java
    )
    requireMethod.isAccessible = true

    // Try to rise to 110, requiring 80 more chips (110-30) which exceeds the available 70
    assertThatThrownBy { requireMethod.invoke(round, player, 110) }
      .cause()
      .isInstanceOf(PokerRound.InsufficientChipsForBettingException::class.java)
      .hasMessage("Not enough chips to raise bet")
  }

  @Test
  fun `requireSufficientChipsToCall throws exception when not enough chips to call`() {
    val players = listOf(PokerPlayer("Alice", 100), PokerPlayer("Bob", 100))
    val round = PokerRound(players, 10, 20, 0)

    val player = players[0]
    player.setBetAmount(20)

    // Set player's chips manually to a small amount
    val chipsField = player.javaClass.getDeclaredField("chips")
    chipsField.isAccessible = true
    chipsField.setInt(player, 10)

    val requireMethod = round.javaClass.getDeclaredMethod(
      "requireSufficientChipsToCall",
      PokerPlayer::class.java,
      Int::class.java
    )
    requireMethod.isAccessible = true

    // Try to call a bet of 50 (need 30 more chips but only have 10)
    assertThatThrownBy { requireMethod.invoke(round, player, 50) }
      .cause()
      .isInstanceOf(PokerRound.InsufficientChipsForBettingException::class.java)
      .hasMessage("You do not have enough chips to call.")
  }

  @Test
  fun `requirePlayerAtHighestBet throws exception when player bet is lower than highest bet`() {
    val players = listOf(PokerPlayer("Alice", 100), PokerPlayer("Bob", 100))
    val round = PokerRound(players, 10, 20, 0)

    // Setup player with a bet lower than the highest bet
    val player = players[0]
    player.setBetAmount(20)

    // Set up another player with a higher bet
    players[1].setBetAmount(40)

    val requireMethod = round.javaClass.getDeclaredMethod(
      "requirePlayerAtHighestBet",
      PokerPlayer::class.java
    )
    requireMethod.isAccessible = true

    assertThatThrownBy { requireMethod.invoke(round, player) }
      .cause()
      .isInstanceOf(PokerRound.InvalidCheckActionException::class.java)
      .hasMessage("You can not check if you are not at the highest bet")
  }

  @Test
  fun `raiseBetTo throws InvalidBetAmountException when chips are negative`() {
    val players = listOf(PokerPlayer("Alice", 100), PokerPlayer("Bob", 100))
    val round = PokerRound(players, 10, 20, 0)

    assertThatThrownBy { round.raiseBetTo("Alice", -10) }
      .isInstanceOf(PokerRound.InvalidBetAmountException::class.java)
      .hasMessage("Bet amount must be positive")
  }

  @Test
  fun `raiseBetTo throws InvalidBetAmountException when bet equals highest bet`() {
    val players = listOf(PokerPlayer("Alice", 100), PokerPlayer("Bob", 100))
    val round = PokerRound(players, 10, 20, 0)

    // Bob is at the current highest bet (20)
    assertThatThrownBy { round.raiseBetTo("Alice", 20) }
      .isInstanceOf(PokerRound.InvalidBetAmountException::class.java)
      .hasMessage("Bet must be higher than the current highest bet")
  }

  @Test
  fun `raiseBetTo throws InvalidBetAmountException when player doesn't have enough chips`() {
    val players = listOf(PokerPlayer("Alice", 100), PokerPlayer("Bob", 100))
    val round = PokerRound(players, 10, 20, 0)

    // Alice only has 90 chips remaining (100-10), trying to bet the total 150
    assertThatThrownBy { round.raiseBetTo("Alice", 150) }
      .isInstanceOf(PokerRound.InsufficientChipsForBettingException::class.java)
      .hasMessage("Not enough chips to raise bet")
  }

  @Test
  fun `check throws InvalidCheckException when player is not at highest bet`() {
    val players = listOf(PokerPlayer("Alice", 100), PokerPlayer("Bob", 100))
    val round = PokerRound(players, 10, 20, 0)

    // Alice has bet 10, the highest bet is 20
    assertThatThrownBy { round.check("Alice") }
      .isInstanceOf(PokerRound.InvalidCheckActionException::class.java)
      .hasMessage("You can not check if you are not at the highest bet")
  }

  @Test
  fun `raiseBetTo should subtract current bet from chips amount correctly`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    players[0].setBetAmount(15)
    val originalChips = players[0].chips()

    round.raiseBetTo("Alice", 30)

    val expectedChipsAfter = originalChips - (30 - 15)
    assertThat(players[0].chips()).isEqualTo(expectedChipsAfter)
  }

  @Test
  fun `raiseBetTo should add correct amount to pot using subtraction`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    players[0].setBetAmount(15)

    val potsField = round.javaClass.getDeclaredField("pots")
    potsField.isAccessible = true
    val pots = potsField.get(round)

    val mainPotMethod = pots.javaClass.getDeclaredMethod("getMainPot")
    mainPotMethod.isAccessible = true
    val mainPot = mainPotMethod.invoke(pots)

    val initialAmount =
      mainPot.javaClass.getDeclaredMethod("amount").invoke(mainPot) as Int

    round.raiseBetTo("Alice", 40)

    val finalAmount =
      mainPot.javaClass.getDeclaredMethod("amount").invoke(mainPot) as Int
    val addedAmount = finalAmount - initialAmount

    assertThat(addedAmount).isEqualTo(40 - 15)
  }

  @Test
  fun `call should subtract current bet from highest bet correctly`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 20, 1)

    round.raiseBetTo("Alice", 50)

    val charlieOriginalChips = players[2].chips()
    val charlieCurrentBet = players[2].bet()

    round.call("Bob")

    val expectedChipsAfter = charlieOriginalChips - (50 - charlieCurrentBet)

    round.call("Charlie")
    assertThat(players[2].chips()).isEqualTo(expectedChipsAfter)
  }

  @Test
  fun `call should add correct amount to pot using subtraction`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 20, 1)

    round.raiseBetTo("Alice", 40)

    val potsField = round.javaClass.getDeclaredField("pots")
    potsField.isAccessible = true
    val pots = potsField.get(round)

    val mainPotMethod = pots.javaClass.getDeclaredMethod("getMainPot")
    mainPotMethod.isAccessible = true
    val mainPot = mainPotMethod.invoke(pots)

    val initialAmount =
      mainPot.javaClass.getDeclaredMethod("amount").invoke(mainPot) as Int
    val bobCurrentBet = players[1].bet()

    round.call("Bob")

    val finalAmount =
      mainPot.javaClass.getDeclaredMethod("amount").invoke(mainPot) as Int
    val addedAmount = finalAmount - initialAmount

    assertThat(addedAmount).isEqualTo(40 - bobCurrentBet)
  }

  @Test
  fun `allIn should add player chips to pot not remaining chips`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 75)
    )
    val round = PokerRound(players, 10, 20, 1)

    val bobChips = players[1].chips()

    val potsField = round.javaClass.getDeclaredField("pots")
    potsField.isAccessible = true
    val pots = potsField.get(round)

    val mainPotMethod = pots.javaClass.getDeclaredMethod("getMainPot")
    mainPotMethod.isAccessible = true
    val mainPot = mainPotMethod.invoke(pots)

    val initialAmount =
      mainPot.javaClass.getDeclaredMethod("amount").invoke(mainPot) as Int

    round.allIn("Bob")

    val finalAmount =
      mainPot.javaClass.getDeclaredMethod("amount").invoke(mainPot) as Int
    val addedAmount = finalAmount - initialAmount

    assertThat(addedAmount).isEqualTo(bobChips)
  }

  @Test
  fun `requireLargerThanHighestBet should use greater than not greater than or equal`() {
    val players = listOf(PokerPlayer("Alice", 100), PokerPlayer("Bob", 100))
    val round = PokerRound(players, 10, 20, 0)

    val requireMethod = round.javaClass.getDeclaredMethod(
      "requireLargerThanHighestBet",
      Int::class.java,
      Int::class.java
    )
    requireMethod.isAccessible = true

    assertThatThrownBy { requireMethod.invoke(round, 50, 50) }
      .cause()
      .isInstanceOf(PokerRound.InvalidBetAmountException::class.java)
  }

  @Test
  fun `requirePositiveChips should reject zero accept it`() {
    val players = listOf(PokerPlayer("Alice", 100), PokerPlayer("Bob", 100))
    val round = PokerRound(players, 10, 20, 0)

    val requireMethod = round.javaClass.getDeclaredMethod(
      "requirePositiveChips",
      Int::class.java
    )
    requireMethod.isAccessible = true

    requireMethod.invoke(round, 0)

    assertThatCode { requireMethod.invoke(round, 0) }
      .doesNotThrowAnyException()
  }

  @Test
  fun `requireSufficientChipsToRaise should use greater than not greater than or equal`() {
    val players = listOf(PokerPlayer("Alice", 100), PokerPlayer("Bob", 100))
    val round = PokerRound(players, 10, 20, 0)

    val player = players[0]
    player.setBetAmount(30)

    // Set player's chips manually to exactly the amount needed for the raise
    val chipsField = player.javaClass.getDeclaredField("chips")
    chipsField.isAccessible = true
    chipsField.setInt(
      player,
      70
    ) // player has exactly 70 chips left after betting 30

    val requireMethod = round.javaClass.getDeclaredMethod(
      "requireSufficientChipsToRaise",
      PokerPlayer::class.java,
      Int::class.java
    )
    requireMethod.isAccessible = true

    // With 70 chips and a bet of 30, player should be able to raise to 100 (100-30=70)
    assertThatCode { requireMethod.invoke(round, player, 100) }
      .doesNotThrowAnyException()

    // But should throw exception when trying to raise to 101 (101-30=71 > 70)
    assertThatThrownBy { requireMethod.invoke(round, player, 101) }
      .cause()
      .isInstanceOf(PokerRound.InsufficientChipsForBettingException::class.java)
  }

  @Test
  fun `requirePositiveChips should allow zero chips`() {
    val players = listOf(PokerPlayer("Alice", 100), PokerPlayer("Bob", 100))
    val round = PokerRound(players, 10, 20, 0)

    val requireMethod = round.javaClass.getDeclaredMethod(
      "requirePositiveChips",
      Int::class.java
    )
    requireMethod.isAccessible = true

    assertThatCode { requireMethod.invoke(round, 0) }
      .doesNotThrowAnyException()
  }

  @Test
  fun `requireNotAtHighestBet should use not equal not equal`() {
    val players = listOf(PokerPlayer("Alice", 100), PokerPlayer("Bob", 100))
    val round = PokerRound(players, 10, 20, 0)

    val player = players[1]

    val requireMethod = round.javaClass.getDeclaredMethod(
      "requireNotAtHighestBet",
      PokerPlayer::class.java,
      Int::class.java
    )
    requireMethod.isAccessible = true

    assertThatThrownBy { requireMethod.invoke(round, player, 20) }
      .cause()
      .isInstanceOf(PokerRound.InvalidCallActionException::class.java)
  }

  @Test
  fun `requirePlayerAtHighestBet should use equal not not equal`() {
    val players = listOf(PokerPlayer("Alice", 100), PokerPlayer("Bob", 100))
    val round = PokerRound(players, 10, 20, 0)

    val player = players[0]

    val requireMethod = round.javaClass.getDeclaredMethod(
      "requirePlayerAtHighestBet",
      PokerPlayer::class.java
    )
    requireMethod.isAccessible = true

    assertThatThrownBy { requireMethod.invoke(round, player) }
      .cause()
      .isInstanceOf(PokerRound.InvalidCheckActionException::class.java)
  }

  @Test
  fun `getNextPlayer should use modulo arithmetic correctly`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    val currentPlayerIndexField =
      round.javaClass.getDeclaredField("currentPlayerIndex")
    currentPlayerIndexField.isAccessible = true
    currentPlayerIndexField.set(round, 2)

    val getNextPlayerMethod = round.javaClass.getDeclaredMethod("getNextPlayer")
    getNextPlayerMethod.isAccessible = true
    val nextPlayer = getNextPlayerMethod.invoke(round) as PokerPlayer

    assertThat(nextPlayer.name).isEqualTo("Alice")
  }

  @Test
  fun `makeTurn should increment currentPlayerIndex correctly`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 20, 2)

    val currentPlayerIndexField =
      round.javaClass.getDeclaredField("currentPlayerIndex")
    currentPlayerIndexField.isAccessible = true
    val initialIndex = currentPlayerIndexField.get(round) as Int

    round.call("Bob")

    val newIndex = currentPlayerIndexField.get(round) as Int
    assertThat(newIndex).isNotEqualTo(initialIndex)
  }

  @Test
  fun `checkForNextGamePhase should use logical AND not OR for all conditions`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    players[0].fold()

    val checkMethod = round.javaClass.getDeclaredMethod("checkForNextGamePhase")
    checkMethod.isAccessible = true
    val result = checkMethod.invoke(round) as Boolean

    assertThat(result).isTrue()
  }

  @Test
  fun `setBlinds should check sum is greater than zero not greater than or equal`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )

    val aliceBetField = players[0].javaClass.getDeclaredField("bet")
    aliceBetField.isAccessible = true
    aliceBetField.setInt(players[0], 0)

    val bobBetField = players[1].javaClass.getDeclaredField("bet")
    bobBetField.isAccessible = true
    bobBetField.setInt(players[1], 0)

    val round = PokerRound(players, 20, 40, 0)

    val setBlindsMethod = round.javaClass.getDeclaredMethod("setBlinds")
    setBlindsMethod.isAccessible = true
    setBlindsMethod.invoke(round)

    assertThat(players[0].bet()).isNotEqualTo(0)
    assertThat(players[1].bet()).isNotEqualTo(0)
  }

  @Test
  fun `raiseBetTo should validate player equality correctly`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    assertThatThrownBy { round.raiseBetTo("Bob", 30) }
      .isInstanceOf(PokerRound.InvalidPlayerStateException::class.java)
      .hasMessage("It's not your turn to bet")
  }

  @Test
  fun `call should validate player equality correctly`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    assertThatThrownBy { round.call("Bob") }
      .isInstanceOf(PokerRound.InvalidPlayerStateException::class.java)
      .hasMessage("It's not your turn to bet")
  }

  @Test
  fun `check should validate player equality correctly`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    assertThatThrownBy { round.check("Bob") }
      .isInstanceOf(PokerRound.InvalidPlayerStateException::class.java)
      .hasMessage("It's not your turn to bet")
  }

  @Test
  fun `fold should validate player equality correctly`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    assertThatThrownBy { round.fold("Bob") }
      .isInstanceOf(PokerRound.InvalidPlayerStateException::class.java)
      .hasMessage("It's not your turn to bet")
  }

  @Test
  fun `allIn should validate player equality correctly`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100),
      PokerPlayer("Charlie", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    assertThatThrownBy { round.allIn("Bob") }
      .isInstanceOf(PokerRound.InvalidPlayerStateException::class.java)
      .hasMessage("It's not your turn to bet")
  }

  @Test
  fun `raiseBetTo should use correct boolean logic for folded check`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    players[0].fold()

    val currentPlayerIndexField =
      round.javaClass.getDeclaredField("currentPlayerIndex")
    currentPlayerIndexField.isAccessible = true
    currentPlayerIndexField.set(round, 0)

    assertThatThrownBy { round.raiseBetTo("Alice", 30) }
      .isInstanceOf(PokerRound.InvalidPlayerStateException::class.java)
      .hasMessage("Cannot raise bet after folding")
  }

  @Test
  fun `call should use correct boolean logic for folded check`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    players[0].fold()

    val currentPlayerIndexField =
      round.javaClass.getDeclaredField("currentPlayerIndex")
    currentPlayerIndexField.isAccessible = true
    currentPlayerIndexField.set(round, 0)

    assertThatThrownBy { round.call("Alice") }
      .isInstanceOf(PokerRound.InvalidPlayerStateException::class.java)
      .hasMessage("You can not call after having folded")
  }

  @Test
  fun `check should use correct boolean logic for folded check`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    players[0].fold()

    val currentPlayerIndexField =
      round.javaClass.getDeclaredField("currentPlayerIndex")
    currentPlayerIndexField.isAccessible = true
    currentPlayerIndexField.set(round, 0)

    assertThatThrownBy { round.check("Alice") }
      .isInstanceOf(PokerRound.InvalidPlayerStateException::class.java)
      .hasMessage("Cannot raise bet after folding")
  }

  @Test
  fun `fold should use correct boolean logic for folded check`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    players[0].fold()

    val currentPlayerIndexField =
      round.javaClass.getDeclaredField("currentPlayerIndex")
    currentPlayerIndexField.isAccessible = true
    currentPlayerIndexField.set(round, 0)

    assertThatThrownBy { round.fold("Alice") }
      .isInstanceOf(PokerRound.InvalidPlayerStateException::class.java)
      .hasMessage("Cannot raise bet after folding")
  }

  @Test
  fun `allIn should use correct boolean logic for folded check`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    players[0].fold()

    val currentPlayerIndexField =
      round.javaClass.getDeclaredField("currentPlayerIndex")
    currentPlayerIndexField.isAccessible = true
    currentPlayerIndexField.set(round, 0)

    assertThatThrownBy { round.allIn("Alice") }
      .isInstanceOf(PokerRound.InvalidPlayerStateException::class.java)
      .hasMessage("Cannot raise bet after folding")
  }

  @Test
  fun `raiseBetTo should use correct boolean logic for allIn check`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    val isAllInField = players[0].javaClass.getDeclaredField("isAllIn")
    isAllInField.isAccessible = true
    isAllInField.setBoolean(players[0], true)

    assertThatThrownBy { round.raiseBetTo("Alice", 30) }
      .isInstanceOf(PokerRound.InvalidPlayerStateException::class.java)
      .hasMessage("Cannot raise bet after going all-in")
  }

  @Test
  fun `call should use correct boolean logic for allIn check`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    val isAllInField = players[0].javaClass.getDeclaredField("isAllIn")
    isAllInField.isAccessible = true
    isAllInField.setBoolean(players[0], true)

    assertThatThrownBy { round.call("Alice") }
      .isInstanceOf(PokerRound.InvalidPlayerStateException::class.java)
      .hasMessage("You can not call after having gone all-in")
  }

  @Test
  fun `check should use correct boolean logic for allIn check`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    val isAllInField = players[0].javaClass.getDeclaredField("isAllIn")
    isAllInField.isAccessible = true
    isAllInField.setBoolean(players[0], true)

    assertThatThrownBy { round.check("Alice") }
      .isInstanceOf(PokerRound.InvalidPlayerStateException::class.java)
      .hasMessage("Cannot raise bet after going all-in")
  }

  @Test
  fun `fold should use correct boolean logic for allIn check`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    val isAllInField = players[0].javaClass.getDeclaredField("isAllIn")
    isAllInField.isAccessible = true
    isAllInField.setBoolean(players[0], true)

    assertThatThrownBy { round.fold("Alice") }
      .isInstanceOf(PokerRound.InvalidPlayerStateException::class.java)
      .hasMessage("Cannot raise bet after going all-in")
  }

  @Test
  fun `allIn should use correct boolean logic for allIn check`() {
    val players = listOf(
      PokerPlayer("Alice", 100),
      PokerPlayer("Bob", 100)
    )
    val round = PokerRound(players, 10, 20, 0)

    val isAllInField = players[0].javaClass.getDeclaredField("isAllIn")
    isAllInField.isAccessible = true
    isAllInField.setBoolean(players[0], true)

    assertThatThrownBy { round.allIn("Alice") }
      .isInstanceOf(PokerRound.InvalidPlayerStateException::class.java)
      .hasMessage("Cannot raise bet after going all-in")
  }
}