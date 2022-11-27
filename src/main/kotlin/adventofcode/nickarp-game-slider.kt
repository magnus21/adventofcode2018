package adventofcode

import adventofcode.util.Queue
import java.io.File


fun main() {

    val rawInput = File("src/main/resources/game-slider.txt").readLines()
    val brickPositions = parseInput(rawInput)

    printBrickPositions(brickPositions)

    NickarpGameSlider.simulate(brickPositions)
}

object NickarpGameSlider {
    fun simulate(brickPositions: MutableMap<BrickPosition, Brick>) {

        val winningMovesList = mutableListOf<SliderGameState>()
        findWinningMoves(SliderGameState(brickPositions, mutableListOf()), winningMovesList)

        winningMovesList.sortBy { it.moves.size }

        val winner = winningMovesList[0]

        printMoves(winner)
        printBrickPositions(winner.field)
    }

    private fun printMoves(winner: SliderGameState) {
        winner.moves.forEach {
            println("Bricka $it")
        }
    }

    private fun findWinningMoves(
        gameState: SliderGameState,
        winningMovesList: MutableList<SliderGameState>
    ) {

        val queue = Queue<SliderGameState>()
        queue.enqueue(gameState)

        val distinctStates = mutableSetOf<MutableMap<BrickPosition, Brick>>()
        while (queue.isNotEmpty()) {
            if (fieldIsOrdered(gameState)) {
                winningMovesList.add(gameState)
                break
            }
            explorePath(distinctStates, queue)
        }
    }

    private fun explorePath(
        distinctStates: MutableSet<MutableMap<BrickPosition, Brick>>,
        queue: Queue<SliderGameState>
    ) {
        val gameState = queue.dequeue()!!
        val empty = gameState.field.filter { it.value.number == 0 }.entries.first()

        val possibleMoves = getPossibleMoves(gameState)

        possibleMoves.forEach { move ->
            val brickPosition = move.key
            val newFieldState = gameState.field.toMutableMap()
            newFieldState[brickPosition] = empty.value
            newFieldState[empty.key] = move.value

            if (!distinctStates.contains(newFieldState)) {
                distinctStates.add(newFieldState)
                val newMoves = gameState.moves.toMutableList()
                newMoves.add(Pair(move.value, empty.key))
                queue.enqueue(SliderGameState(newFieldState, newMoves))
            }
        }
        queue.sortQueue(compareBy<SliderGameState> { correctBricksStreakCount(it) }.reversed())
    }

    private fun correctBricksStreakCount(gameState: SliderGameState): Int {
        var c = 0;
        for (entry in gameState.field) {
            if (brickInCorrectPlace(gameState, entry)) {
                c++
            } else {
                break
            }
        }
        return c
    }

    private fun correctBricksCount(gameState: SliderGameState): Int {
        return gameState.field
            .filter { it.value.number > 0 }
            .count { it.key.x + it.key.y * 4 + 1 == it.value.number }
    }

    private fun fieldIsOrdered(gameState: SliderGameState): Boolean {
        return correctBricksCount(gameState) == gameState.field.size - 1
    }

    private fun getPossibleMoves(gameState: SliderGameState): Map<BrickPosition, Brick> {
        return gameState.field
            .filter { it.value.number > 0 }
            .filter { brickShouldBeMoved(gameState, it) }
            .filter { canMove(it, gameState) == true }
    }

    private fun brickShouldBeMoved(gameState: SliderGameState, brick: Map.Entry<BrickPosition, Brick>): Boolean {
        return when {
            brick.value.number in 1..8 && previousBricksInPlace(gameState, brick) && !brickInCorrectPlace(
                gameState,
                brick
            ) -> false
            else -> true
        }
    }

    private fun previousBricksInPlace(gameState: SliderGameState, brick: Map.Entry<BrickPosition, Brick>): Boolean {
        return gameState.field
            .filter { it.value.number < brick.value.number }
            .all { it.key.x + it.key.y * 4 + 1 == it.value.number }
    }

    private fun brickInCorrectPlace(gameState: SliderGameState, brick: Map.Entry<BrickPosition, Brick>): Boolean {
        return gameState.field
            .filter { it.value.number == brick.value.number }
            .all { it.key.x + it.key.y * 4 + 1 == brick.value.number }
    }

    private val steps = listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
    private fun canMove(brick: Map.Entry<BrickPosition, Brick>, gameState: SliderGameState): Boolean? {
        return steps.map {
            val bp = BrickPosition(brick.key.x + it.first, brick.key.y - it.second)
            gameState.field[bp]?.number == 0
        }.firstOrNull { it }
    }
}

data class SliderGameState(
    var field: MutableMap<BrickPosition, Brick>,
    var moves: List<Pair<Brick, BrickPosition>>


) {
    override fun toString(): String {
        return field.map { it.value.number }.joinToString(" ")
    }
}

data class BrickPosition(var x: Int, var y: Int)
data class Brick(val number: Int)

private fun parseInput(rawInput: List<String>): MutableMap<BrickPosition, Brick> {
    val brickPositions = mutableMapOf<BrickPosition, Brick>()

    var y = 0
    rawInput.forEach { line ->
        var x = 0
        line.split(" ").forEach {
            when {
                it.all { c -> c.isDigit() } -> brickPositions[BrickPosition(x, y)] = Brick(it.toInt())
                else -> brickPositions[BrickPosition(x, y)] = Brick(0)
            }
            x++
        }
        y++
    }

    return brickPositions
}

private fun printBrickPositions(positions: MutableMap<BrickPosition, Brick>) {
    println()
    for (y in 0..3) {
        for (x in 0..3) {
            print("${positions[BrickPosition(x, y)]?.number} ")
        }
        println()
    }
    println()
}