import mcts.Action
import mcts.State
import mcts.StateManager


class RandomPlayer : Player {

    override fun chooseAction(state: State, stateManager: StateManager): Action {
        val actions = stateManager.getPossibleActions(state)
        val performAction = actions.random()
        return performAction
    }

}