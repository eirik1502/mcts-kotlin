import example_games.NimStateFuncs
import example_games.NimStateManager
import mcts.MctsPlayer

fun main() {
    val mcts = MctsPlayer(
            moveSimulationCount = 200,
            printTreeEveryMove = false,
            storeRoots = false
    )
    // 6, 8, 9 10
    val stateManagers = listOf(9).map {
        NimStateManager(NimStateFuncs.createInitialState(
                startPiecesCount = 50,
                maxRemovePiecesCount = it,
                startPlayer = 0,
                playerCount = 3
        ))
    }


//    Play.playSingleGame(stateManager, mcts, verbose = true)
//    McNodeFuncs.printTree(mcts.roots.first(), maxDepth = -1)
    Play.playStatistics(stateManagers, listOf(mcts), 3000)
}


fun compareRandomMcts() {
    val stateManagers = (0..1)
            .map {
                NimStateFuncs.createInitialState(
                        startPiecesCount = 12,
                        maxRemovePiecesCount = 3,
                        startPlayer = it
                )
            }
            .map { NimStateManager(it) }

    val players = listOf(
            MctsPlayer(
                    moveSimulationCount = 10
            ),
            RandomPlayer()
    )

    Play.playStatistics(stateManagers, players)
}