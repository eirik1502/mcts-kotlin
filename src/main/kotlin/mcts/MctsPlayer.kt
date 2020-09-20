package mcts

import Player
import mcts.tree.McNode
import mcts.tree.McNodeData
import mcts.tree.McNodeFuncs
import java.lang.IllegalStateException


class MctsPlayer(
        val moveSimulationCount: Int,
        val treePolicy: TreePolicy = TreePolicies.uctTreePolicy(1f),
        val defaultPolicy: DefaultPolicy = DefaultPolicies.randomDefaultPolicy(),
        val useNodeStates: Boolean = false,
        val printTreeEveryMove: Boolean = false,
        val storeRoots: Boolean = false
) : Player {

    var roots = mutableListOf<McNode>()
        private set


    override fun chooseAction(state: State, stateManager: StateManager): Action {
        val root = Mcts.createRootNode(state, stateManager)

        Mcts.performSimulations(
                moveSimulationCount,
                stateManager,
                rootNode = root,
                treePolicy = treePolicy,
                defaultPolicy = defaultPolicy,
                storeState = useNodeStates
        )

        if (printTreeEveryMove) {
            McNodeFuncs.printTree(root)
        }
        if (storeRoots) {
            roots.add(root)
        }

        val rootChildren = root.children
        val action = rootChildren.maxBy { child -> child.edge.traversals }
                ?.nodeData?.action
                ?: throw IllegalStateException("No action available for the given state")
        return action
    }

    override fun toString(): String {
        return "MctsStateBasedPlayer(moveSimulationCount=$moveSimulationCount, useNodeStates=$useNodeStates)"
    }


}