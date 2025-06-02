package hwr.oop.projects.peakpoker.core.exceptions

abstract class HandEvaluationException(message: String) : GameException(message)

class EmptyPlayerListException(message: String) : HandEvaluationException(message)