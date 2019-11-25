package adventofcode

import java.io.File


fun main(args: Array<String>) {

    val rawInput = File("src/main/resources/game.txt").readLines()
    val pinPositions = parsePinPoints(rawInput)

    printPinPositions(pinPositions)

    NickarpGame.simulate(pinPositions)
}

object NickarpGame {
    fun simulate(pinPositions: MutableMap<PinPosition, PinPositionState>) {

        val winningMovesList = mutableListOf<GameState>()
        findWinningMoves(GameState(pinPositions, mutableListOf()), winningMovesList)

        winningMovesList.sortBy { it.moves.size }

        val winner = winningMovesList[0]

        printMoves(winner)
        printPinPositions(winner.field)

    }

    private fun printMoves(winner: GameState) {
        winner.moves.forEach {
            val dy = it.second.y - it.first.y
            val dys = when {
                dy == 0 -> ""
                dy < 0 -> "upp "
                else -> "ner "
            }

            val dxs = if(it.second.x - it.first.x > 0) "höger" else "vänster"

            println(String.format("Rad,kolumn: %d,%d -> %s%s", it.first.y, it.first.x, dys, dxs))
        }
    }

    private fun findWinningMoves(
        gameState: GameState,
        winningMovesList: MutableList<GameState>
    ) {

        if (fieldIsCleared(gameState)) {
            winningMovesList.add(gameState)
        } else {
            val possibleMoves = getPossibleMoves(gameState)

            if (!possibleMoves.isEmpty()) {
                possibleMoves.map {
                    if(winningMovesList.isEmpty()) {
                        val newFieldState = gameState.field.toMutableMap()
                        newFieldState[it.first] = PinPositionState.EMPTY
                        newFieldState[it.second] = PinPositionState.PIN
                        newFieldState[it.third] = PinPositionState.EMPTY

                        val newMoves = gameState.moves.toMutableList()
                        newMoves.add(it)
                        findWinningMoves(GameState(newFieldState, newMoves), winningMovesList)
                    }
                }
            }
        }
    }

    private fun fieldIsCleared(gameState: GameState): Boolean {
        return gameState.field.filter { it.value == PinPositionState.PIN }.size == 1
    }

    private fun getPossibleMoves(gameState: GameState): List<Triple<PinPosition, PinPosition, PinPosition>> {
        val deltas =
            listOf(Move(-2, 0, 2), Move(2, 0, 2), Move(-1, -1, 2), Move(-1, 1, 2), Move(1, -1, 2), Move(1, 1, 2))
        return gameState.field
            .filter { it.value == PinPositionState.PIN }
            .flatMap { pos ->
                deltas
                    .filter {
                        gameState.field[PinPosition(
                            pos.key.x + it.dx * it.step,
                            pos.key.y + it.dy * it.step
                        )] == PinPositionState.EMPTY
                                && gameState.field[PinPosition(
                            pos.key.x + it.dx,
                            pos.key.y + it.dy
                        )] == PinPositionState.PIN
                    }.map {
                        Triple(
                            pos.key,
                            PinPosition(pos.key.x + it.dx * it.step, pos.key.y + it.dy * it.step),
                            PinPosition(pos.key.x + it.dx, pos.key.y + it.dy)
                        )
                    }

            }
    }
}

data class GameState(
    var field: MutableMap<PinPosition, PinPositionState>,
    var moves: List<Triple<PinPosition, PinPosition, PinPosition>>
)

data class PinPosition(var x: Int, var y: Int)
data class Move(val dx: Int, val dy: Int, val step: Int)
enum class PinPositionState(val code: Char) {
    EMPTY('O'), PIN('X')
}

private fun parsePinPoints(rawInput: List<String>): MutableMap<PinPosition, PinPositionState> {
    val pinPositions = mutableMapOf<PinPosition, PinPositionState>()

    var y = 0
    rawInput.forEach { line ->
        var x = 0
        line.forEach {
            when (it) {
                PinPositionState.EMPTY.code -> pinPositions[PinPosition(x, y)] = PinPositionState.EMPTY
                PinPositionState.PIN.code -> pinPositions[PinPosition(x, y)] = PinPositionState.PIN
            }
            x++
        }
        y++
    }

    return pinPositions
}

private fun printPinPositions(
    pinPositions: MutableMap<PinPosition, PinPositionState>
) {
    println()
    print("  ")
    for (x in 0..8 step 2) {
        print(x.toString() + " ")
    }
    println()
    for (y in 0..4) {
        print(y.toString() + " ")
        for (x in 0..9) {
            print(pinPositions[PinPosition(x, y)]?.code ?: ' ')
        }
        println()
    }
    print("   ")
    for (x in 1..7 step 2) {
        print(x.toString() + " ")
    }
    println()
    println()
}