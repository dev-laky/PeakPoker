package hwr.oop.projects.peakpoker.core.exceptions

/**
 * Base exception for all card-related exceptions
 */
abstract class CardException(message: String) : GameException(message)

/**
 * Thrown when a card configuration is invalid
 */
class InvalidCardConfigurationException(message: String) :
  CardException(message)

/**
 * Thrown when duplicate cards are detected
 */
class DuplicateCardException(message: String) : CardException(message)