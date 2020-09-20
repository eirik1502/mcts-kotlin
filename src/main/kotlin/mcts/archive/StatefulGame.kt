package mcts.archive

import mcts.Action

interface StatefulGame {
    fun getPlayerCount(): Int
    fun getPossibleActions(): Collection<Action>
    fun performAction(action: Action)
    fun getNextPlayer(): Int
    fun isTerminalState(): Boolean
    fun getPlayersEvaluation(): List<Float>

    fun resetToInitialState()

    fun actionString(): String = "No action string available"
}