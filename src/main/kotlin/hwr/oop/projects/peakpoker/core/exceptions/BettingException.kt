package hwr.oop.projects.peakpoker.core.exceptions

/**
 * Base exception for all betting-related exceptions
 */
abstract class BettingException(message: String) : GameException(message)

/**
 * Thrown when a player doesn't have enough chips for an action
 */
class InsufficientChipsException(message: String) : BettingException(message)

/**
 * Thrown when an invalid bet amount is provided
 */
class InvalidBetAmountException(message: String) : BettingException(message)

/**
 * Thrown when a player cannot check
 */
class InvalidCheckException(message: String) : BettingException(message)

/**
 * Thrown when a player cannot call
 */
class InvalidCallException(message: String) : BettingException(message)