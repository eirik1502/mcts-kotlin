package mcts

import example_games.NimStateFuncs
import example_games.NimStateManager
import kotlin.test.Test

class PlayStatistics {

    @Test fun nimPlayStatistics() {
        val mctsPlayers = listOf(
                MctsPlayer(
                        moveSimulationCount = 1000,
                        printTreeEveryMove = false,
                        storeRoots = false
                ),
                MctsPlayer(
                        moveSimulationCount = 1000,
                        useNodeStates = true
                )
        )
        // 6, 8, 9 10
        val stateManagers = (3..6).map {
            NimStateManager(NimStateFuncs.createInitialState(
                    startPiecesCount = 12,
                    maxRemovePiecesCount = it,
                    startPlayer = 0,
                    playerCount = 2
            ))
        }


//    Play.playSingleGame(stateManager, mcts, verbose = true)
//    McNodeFuncs.printTree(mcts.roots.first(), maxDepth = -1)
        Play.playStatistics(stateManagers, mctsPlayers, 10)
    }
}