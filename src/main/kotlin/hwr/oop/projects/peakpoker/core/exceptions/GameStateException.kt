package hwr.oop.projects.peakpoker.core.exceptions

/**
 * Base exception for all game state-related exceptions
 */
abstract class GameStateException(message: String) : GameException(message)

/**
 * Thrown when the game configuration is invalid
 */
class InvalidGameConfigurationException(message: String) :
  GameStateException(message)

/**
 * Thrown when there aren't enough players to start the game
 */
class MinimumPlayersException(message: String) : GameStateException(message)

/**
 * Thrown when the blind configuration is invalid
 */
class InvalidBlindConfigurationException(message: String) :
  GameStateException(message)