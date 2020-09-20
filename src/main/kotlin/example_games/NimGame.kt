package example_games

import mcts.Action
import mcts.State
import mcts.StateManager


data class NimState(
        val startPiecesCount: Int,
        val maxRemovePiecesCount: Int,
        val playerCount: Int = 2,
        val piecesCount: Int = -1,
        val prevPlayer: Int = -1,  // should be the non-starting player when in initial_state
        val isInitialState: Boolean = false
)

data class NimAction(
        val removePiecesCount: Int
)

data class NimStateFromAction(
        val state: NimState,
        val action: NimAction?
)

object NimStateFuncs {
    fun nextPlayer(player: Int, playerCount: Int): Int = (player + 1) % playerCount
    fun prevPlayer(player: Int, playerCount: Int): Int = (player - 1) % playerCount

    fun createInitialState(
            startPiecesCount: Int,
            maxRemovePiecesCount: Int,
            startPlayer: Int,
            playerCount: Int = 2
    ) = NimState(
            startPiecesCount,
            maxRemovePiecesCount,
            playerCount = playerCount,
            piecesCount = startPiecesCount,
            prevPlayer = prevPlayer(startPlayer, playerCount),
            isInitialState = true
    )

    fun getPossibleActions(state: NimState): List<NimAction> {
        return (1..state.maxRemovePiecesCount)
                .filter { state.piecesCount - it >= 0 }
                .map { NimAction(removePiecesCount = it) }
    }

    fun performAction(state: NimState, action: NimAction): NimState = state.copy(
            piecesCount = state.piecesCount - action.removePiecesCount,
            prevPlayer = nextPlayer(state.prevPlayer, state.playerCount),
            isInitialState = false
    )

    fun isTerminalState(state: NimState): Boolean = state.piecesCount <= 0

    fun playerWon(state: NimState): Int {
        if (!isTerminalState(state)) {
            throw IllegalStateException("evaluating playerWon of a non-terminal state")
        }

        return state.prevPlayer
    }

}

class NimStateManager(
        val initialState: NimState
) : StateManager {

    override fun getPlayerCount(): Int {
        return initialState.playerCount
    }

    override fun getInitialState(): State = initialState

    override fun getPossibleActions(state: State): Collection<Action> =
            NimStateFuncs.getPossibleActions(state as NimState)

    override fun performAction(state: State, action: Action): State {
        return NimStateFuncs.performAction(state as NimState, action as NimAction)
    }


    override fun isTerminalState(state: State): Boolean {
        return NimStateFuncs.isTerminalState(state as NimState)
    }

    override fun getNextPlayer(state: State): Int {
        val nimState = state as NimState
        return NimStateFuncs.nextPlayer(nimState.prevPlayer, nimState.playerCount)
    }

    override fun getPlayersEvaluation(state: State): List<Float> {
        val playerWon = NimStateFuncs.playerWon(state as NimState)
        return (0 until state.playerCount).map { if (it == playerWon) 1f else 0f }
    }

    override fun actionString(state: State, action: Action?): String {
        val nimState = state as NimState
        return if (nimState.isInitialState) {
            "Start Pile: ${state.piecesCount} stones"
        }
        else {
            action ?: throw IllegalArgumentException("Got no action and not in initial state")
            val nimAction = action as NimAction
            val actionStr = "Player ${state.prevPlayer} selects ${nimAction.removePiecesCount}:" +
                    " Remaining stones = ${nimState.piecesCount}"
            if (!isTerminalState(nimState)) actionStr else actionStr +
                    "\nPlayer ${getPlayersEvaluation(nimState).withIndex().maxBy { (_, eval) -> eval }?.index ?: -1} wins"
        }

    }

    override fun toString(): String {
        return "NimStateManager(initialState=$initialState)"
    }


}