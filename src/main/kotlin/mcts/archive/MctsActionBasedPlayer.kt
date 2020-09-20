package mcts.archive

import mcts.Action
import mcts.TreePolicies
import mcts.TreePolicy
import mcts.tree.McNode
import mcts.tree.McNodeData
import mcts.tree.McNodeFuncs
import java.lang.IllegalStateException

open class MctsActionBasedPlayer(
        val moveSimulationCount: Int,
        val maxDepth: Int? = null,
        val treePolicy: TreePolicy = TreePolicies.uctTreePolicy(1f),
        val printTreeEveryMove: Boolean = false,
        val storeRoots: Boolean = false
) {

    fun chooseAction(game: StatefulGame): Action {
        val root = McNode(
                parent = null,
                nodeData = McNodeData(
                        action = null,
                        nextPlayer = game.getNextPlayer()
                )
        )
        (0 until moveSimulationCount).forEach {
            MctsActionBased.performSimulation(
                    game,
                    rootNode = root,
                    maxDepth = maxDepth,
                    treePolicy = treePolicy
            )
            game.resetToInitialState()
        }

        if (printTreeEveryMove) {
            McNodeFuncs.printTree(root)
        }

        val rootChildren = root.children

        val maxChildTraversals = rootChildren
                .map { it.edge.traversals }
                .max()
        val bestChildren = rootChildren.filter { it.edge.traversals == maxChildTraversals }
        val chosenChild = bestChildren.random()
        println("Chosen child qVal: ${chosenChild.edge.qValue}")
        val action = chosenChild.nodeData.action
                ?: throw IllegalStateException("No action available for the given state")
        return action
    }

    override fun toString(): String {
        return "MctsPlayer(moveSimulationCount=$moveSimulationCount)"
    }
}