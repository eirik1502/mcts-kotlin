package mcts

import example_games.NimStateFuncs
import example_games.NimStateManager
import mcts.tree.McNode
import mcts.tree.McNodeData
import mcts.tree.McNodeFuncs
import kotlin.test.Test

class MctsTest {


    @Test
    fun testPerformSimulation() {
        val stateManager = NimStateManager(NimStateFuncs.createInitialState(10, 3, 0))
        val initialState = stateManager.getInitialState()
        val root = McNode(
                null,
                McNodeData(
                        initialState,
                        null,
                        stateManager.getNextPlayer(initialState)
                )
        )
        McNodeFuncs.printTree(root, includeUnvisitedNodes = true)
        (0 until 10).forEach {
            println("Simulation $it")
            Mcts.performSimulation(
                    stateManager,
                    root,
                    TreePolicies.uctTreePolicy(1f),
                    DefaultPolicies.randomDefaultPolicy()
            )
            McNodeFuncs.printTree(root, includeUnvisitedNodes = true)
        }

    }
}