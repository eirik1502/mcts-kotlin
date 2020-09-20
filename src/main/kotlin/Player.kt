import mcts.Action
import mcts.State
import mcts.StateManager



interface Player {
    fun chooseAction(state: State, stateManager: StateManager): Action
}