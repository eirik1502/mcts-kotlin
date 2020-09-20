package mcts

object DefaultPolicies {

    fun randomDefaultPolicy(): DefaultPolicy = { stateManager, state ->
        val actions = stateManager.getPossibleActions(state)
        val performAction = actions.random()
        stateManager.performAction(state, performAction)
    }
}