package hwr.oop.projects.peakpoker.core.exceptions

/**
 * Base exception for all deck-related exceptions
 */
abstract class DeckException(message: String) : GameException(message)

/**
 * Thrown when trying to draw more cards than are available in the deck
 */
class InsufficientCardsException(message: String) : DeckException(message)