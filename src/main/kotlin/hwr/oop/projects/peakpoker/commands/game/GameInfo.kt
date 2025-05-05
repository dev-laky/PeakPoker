package hwr.oop.projects.peakpoker.commands.game

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import hwr.oop.projects.peakpoker.core.game.Game

class GameInfo : CliktCommand(name = "info") {
    override fun help(context: Context) = "Show information about the game."
    override fun run() {
        val testGame = Game(1002, 10, 20)
        val testGameSmallBlind = testGame.get

        echo()
    }
}