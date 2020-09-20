package mcts


typealias State = Any
typealias Action = Any

interface StateManager {
    fun getPlayerCount(): Int
    fun getInitialState(): State
    fun getPossibleActions(state: State): Collection<Action>
    fun performAction(state: State, action: Action): State
    fun isTerminalState(state: State): Boolean
    fun getNextPlayer(state: State): Int
    fun getPlayersEvaluation(state: State): List<Float>  // a score for each player
    fun actionString(state: State, action: Action?): String = "No action string available"

    /**
     * Override this method if a mutable state is used and not storing states in nodes
     * Tree search will call this on the root node state each time it is performed.
     */
    fun copyRootState(state: State) = state
}