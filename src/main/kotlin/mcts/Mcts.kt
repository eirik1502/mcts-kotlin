package mcts

import Player
import mcts.tree.McNode
import mcts.tree.McNodeData
import mcts.tree.McNodeFuncs
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException


typealias DefaultPolicy = (stateManager: StateManager, state: State) -> State


object Mcts {

    fun createRootNode(state: State, stateManager: StateManager) = McNode(
            parent = null,
            nodeData = McNodeData(
                    state = state,
                    action = null,
                    nextPlayer = stateManager.getNextPlayer(state)
            )
    )

    fun performSimulations(
            simulationCount: Int,
            stateManager: StateManager,
            rootNode: McNode,
            treePolicy: TreePolicy,
            defaultPolicy: DefaultPolicy,
            storeState: Boolean = false
    ) {
        repeat(simulationCount) {
            performSimulation(
                    stateManager,
                    rootNode,
                    treePolicy,
                    defaultPolicy,
                    storeState
            )
        }
    }

    fun performSimulation(
            stateManager: StateManager,
            rootNode: McNode,
            treePolicy: TreePolicy,
            defaultPolicy: DefaultPolicy,
            storeState: Boolean = false
    ) {
        val nodePath = MctsCore.treeSearch(rootNode, treePolicy)
        val leafNode = nodePath.last()
        val leafNodeState = leafNode.nodeData.state ?: run {
            val initialState = nodePath.first().nodeData.state
                    ?: throw IllegalStateException("Root node must contain a state")
            // copyRootState is the identity function by default
            val initialCopiedState = stateManager.copyRootState(initialState)

            nodePath.drop(1).fold(initialCopiedState) { state, node ->
                val action = node.nodeData.action
                        ?: throw IllegalStateException("Non-root node must contain an action")
                stateManager.performAction(state, action)
            }
        }
        val leafIsTerminal = stateManager.isTerminalState(leafNodeState)
        val nodeToBeEvaluated =
                if (leafIsTerminal) leafNode
                else run {
                    MctsCore.expandNode(stateManager, leafNode, nodeState = leafNodeState, storeState = storeState)
                    MctsCore.chooseExpandedNodeChild(leafNode)
                }
        val rolloutState =
                if (leafIsTerminal)
                    leafNodeState
                else
                    nodeToBeEvaluated.nodeData.state
                            ?: stateManager.performAction(
                                    leafNodeState,
                                    nodeToBeEvaluated.nodeData.action
                                            ?: throw IllegalStateException("No action present in non-root node")
                            )

        val playersEvaluation = MctsCore.rolloutEvaluation(stateManager, rolloutState, defaultPolicy)
        MctsCore.backprop(nodeToBeEvaluated, playersEvaluation)
    }




}