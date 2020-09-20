package example_games

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NimGameTest {

    @Test
    fun testActions() {

        val state = NimStateFuncs.createInitialState(
                3,
                3,
                0
        )

        assertEquals(NimState(
                startPiecesCount = 3,
                maxRemovePiecesCount = 3,
                piecesCount = 3,
                prevPlayer = 1,
                isInitialState = true
        ), state, "Initial state not correctly created")

        val actions = NimStateFuncs.getPossibleActions(state)

        assertEquals(setOf(
                NimAction(1),
                NimAction(2),
                NimAction(3)
        ), actions.toSet())

        val successorStates = actions.map { NimStateFuncs.performAction(state, it) }

        assertEquals(setOf(
                state.copy(prevPlayer = 0, isInitialState = false, piecesCount = 2),
                state.copy(prevPlayer = 0, isInitialState = false, piecesCount = 1),
                state.copy(prevPlayer = 0, isInitialState = false, piecesCount = 0)
        ), successorStates.toSet(), "successor states equal")

        assertEquals(
                1, successorStates.filter { NimStateFuncs.isTerminalState(it) }.count(),
                "Exactly one state should be terminal"
        )
    }

    @Test
    fun testNoInvalidActions() {

        val state = NimStateFuncs.createInitialState(
                2,
                3,
                0
        )

        assertEquals(NimState(
                startPiecesCount = 2,
                maxRemovePiecesCount = 3,
                piecesCount = 2,
                prevPlayer = 1,
                isInitialState = true
        ), state, "Initial state not correctly created")

        val actions = NimStateFuncs.getPossibleActions(state)

        assertEquals(setOf(NimAction(1), NimAction(2)), actions.toSet())

        val successorStates = actions.map { NimStateFuncs.performAction(state, it) }

        assertEquals(setOf(
                state.copy(prevPlayer = 0, isInitialState = false, piecesCount = 1),
                state.copy(prevPlayer = 0, isInitialState = false, piecesCount = 0)
        ), successorStates.toSet(), "successor states equal")
    }
}