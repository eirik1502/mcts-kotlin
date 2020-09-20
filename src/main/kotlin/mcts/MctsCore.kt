package mcts

import mcts.tree.McNode
import mcts.tree.McNodeData
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException


typealias TreePolicy = (node: McNode) -> McNode


object MctsCore {
    fun treeSearch(rootNode: McNode, treePolicy: TreePolicy): List<McNode> {
        var node = rootNode
        val nodePath = mutableListOf(rootNode)
        while (node.children.size > 0) {
            node = treePolicy(node)
            nodePath.add(node)
        }
        return nodePath
    }

    fun expandNode(
            stateManager: StateManager,
            node: McNode,
            nodeState: State? = null,
            storeState: Boolean = false
    ): McNode {
        val state = nodeState ?: node.nodeData.state
        ?: throw IllegalStateException("node does not contain a state and nodeState is not provided")
        stateManager.getPossibleActions(state)
                .forEach { action ->
                    val nextState = stateManager.performAction(state, action)
                    McNode(
                            parent = node,  // assigns this node as a child of the parent
                            nodeData = McNodeData(
                                    state = if (storeState) nextState else null,
                                    action = action,
                                    nextPlayer = stateManager.getNextPlayer(nextState)
                            )
                    )
                }
        return node
    }

    /**
     * Returns a list with the score obtained by each player
     */
    fun rolloutEvaluation(
            stateManager: StateManager,
            fromState: State,
            defaultPolicy: DefaultPolicy
    ): List<Float> {
        val terminalState = rollout(stateManager, fromState, defaultPolicy)
        val playersEvaluation = stateManager.getPlayersEvaluation(terminalState)
        return playersEvaluation
    }

    fun rollout(stateManager: StateManager, fromState: State, defaultPolicy: DefaultPolicy): State {
        var state = fromState
        while (!stateManager.isTerminalState(state)) {
            state = defaultPolicy(stateManager, state)
        }
        return state
    }

    fun chooseExpandedNodeChild(node: McNode): McNode {
        return node.children.random()
    }

    fun backprop(fromNode: McNode, playersValues: List<Float>) {
        var currNode: McNode? = fromNode.parent
                ?: throw IllegalArgumentException("Cannot backprop from a root node")
        var prevNode = fromNode
        fromNode.nodeData.visits += 1

        while (currNode != null) {
            currNode.nodeData.visits += 1

            val nextPlayer = currNode.nodeData.nextPlayer
            val edge = prevNode.edge
            edge.traversals += 1
            val nextPlayerValue = playersValues[nextPlayer]
            edge.eval += nextPlayerValue
            edge.qValue = edge.eval / edge.traversals

            prevNode = currNode
            currNode = currNode.parent
        }

    }
}