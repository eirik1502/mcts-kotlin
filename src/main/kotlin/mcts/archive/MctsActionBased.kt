package mcts.archive

import mcts.MctsCore
import mcts.TreePolicy
import mcts.tree.McNode
import mcts.tree.McNodeData


object MctsActionBased {
    fun performSimulation(
            game: StatefulGame,
            rootNode: McNode,
            maxDepth: Int? = null,
            treePolicy: TreePolicy
    ) {
        val leafNode = treeSearch(rootNode, game, treePolicy)
        val leafIsTerminal = game.isTerminalState()
        val nodeToBeEvaluated =
                if (leafIsTerminal) leafNode
                else MctsCore.chooseExpandedNodeChild(expandNode(leafNode, game))

        if (!leafIsTerminal) {
            game.performAction(nodeToBeEvaluated.nodeData.action!!)
            rollout(game, maxDepth = maxDepth)
        }

        val playersEvaluation = game.getPlayersEvaluation()
        MctsCore.backprop(nodeToBeEvaluated, playersEvaluation)
    }

    fun treeSearch(rootNode: McNode, game: StatefulGame, treePolicy: TreePolicy): McNode {
        var node: McNode = rootNode
        while (node.children.isNotEmpty()) {
            val child = treePolicy(node)
            val action = child.nodeData.action!!
            game.performAction(action)
            node = child
        }
        return node
    }

    fun expandNode(node: McNode, game: StatefulGame): McNode {
        game.getPossibleActions()
                .map { action ->
                    McNode(
                            parent = node,
                            nodeData = McNodeData(
                                    action = action,
                                    nextPlayer = game.getNextPlayer()
                            )
                    )
                }
        return node
    }

    fun rollout(game: StatefulGame, maxDepth: Int? = null) {
        var depth = 0
        while (!game.isTerminalState() && depth != maxDepth) {
            game.performAction(game.getPossibleActions().random())
            depth++
        }
    }
}
