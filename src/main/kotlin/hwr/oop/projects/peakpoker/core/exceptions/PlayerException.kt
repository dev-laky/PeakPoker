package hwr.oop.projects.peakpoker.core.exceptions

/**
 * Base exception for all player-related exceptions
 */
abstract class PlayerException(message: String) : GameException(message)

/**
 * Thrown when a player is not found in the game
 */
class PlayerNotFoundException(message: String) : PlayerException(message)

/**
 * Thrown when attempting to add a player that already exists
 */
class DuplicatePlayerException(message: String) : PlayerException(message)

/**
 * Thrown when a player attempts to act out of turn
 */
class NotPlayerTurnException(message: String) : PlayerException(message)

/**
 * Thrown when an action is attempted on a player in an invalid state
 */
class InvalidPlayerStateException(message: String) : PlayerException(message)