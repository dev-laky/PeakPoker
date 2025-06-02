package hwr.oop.projects.peakpoker.core.game

data class GameId(private val value: String) {
  companion object {
    fun generate(): GameId {
      return GameId(java.util.UUID.randomUUID().toString())
    }
  }

    override fun toString(): String {
        return value
    }
}