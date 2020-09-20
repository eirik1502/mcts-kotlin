import mcts.StateManager
import utils.Timing

typealias ResultTime = Pair<List<Float>, Float>

object Play {

    fun playSingleGame(
            stateManager: StateManager,
            player: Player,
            verbose: Boolean = false
    ): ResultTime {
        val (state, timeNanos) = Timing.measureNanoTime {
            var state = stateManager.getInitialState()
            if (verbose) {
                println(stateManager.actionString(state, null))
            }
            while (!stateManager.isTerminalState(state)) {
                val action = player.chooseAction(state, stateManager)
                state = stateManager.performAction(state, action)

                if (verbose) {
                    println(stateManager.actionString(state, action))
                }
            }
            state
        }
        val timeMillis = (timeNanos.toDouble() * 0.000001).toFloat()
        return stateManager.getPlayersEvaluation(state) to timeMillis
    }

    fun playMultipleGames(
            stateManager: StateManager,
            player: Player,
            playCount: Int = 100,
            progressBar: Boolean = true
    ): List<ResultTime> {
        val results = (0 until playCount)
                .map {
                    if (progressBar) {
                        print(if (it % 10 == 0) it else ".")
                        if (it % 100 == 99) println()
                    }

                    playSingleGame(stateManager, player, false)
                }
        if (progressBar) println()
        return results
    }

    fun playStatistics(stateManager: StateManager, player: Player, playCount: Int = 100) {
        val results = playMultipleGames(stateManager, player, playCount)
        playResultStatistics(results, player.toString())
    }

    fun playStatistics(stateManagers: List<StateManager>, players: List<Player>, playCount: Int = 100) {
        val resultsByPlayerStateManager = players.flatMap { player ->
            stateManagers.map { stateManager ->
                val results = playMultipleGames(stateManager, player, playCount)
                Triple(player, stateManager, results)
            }
        }
        resultsByPlayerStateManager
                .forEach { playResultStatistics(it.third, "${it.first}, ${it.second}") }
    }

    fun playResultStatistics(resultsTime: List<ResultTime>, tag: String = "") {
        val results = resultsTime.map { it.first }
        val timings = resultsTime.map { it.second }
        val playsCount = results.size
        val playersCount = results[0].size
        val winnerPlayers = results.map {
            val maxEval = it.max()!!
            it.map { eval -> eval >= maxEval }
        }
        val winsByPlayer = winnerPlayers.fold(winnerPlayers[0].indices.map { 0 }) { acc, winners ->
            acc.zip(winners).map { (a, b) -> a + if (b) 1 else 0 }
        }

        println("""
            tag: $tag
            games played: $playsCount
            avr time: ${timings.sum() / timings.size} ms
            wins by player: $winsByPlayer
        """.trimIndent())
    }

}