package hwr.oop.projects.peakpoker.core.game

data class GameId(private val value: String) {
  companion object {
    fun generate(): GameId {
      return GameId(java.util.UUID.randomUUID().toString())
    }
  }

    /**
     * Returns a string representation of the GameId.
     *
     * @return The string value of this game identifier
     */
    override fun toString(): String {
        return value
    }
}