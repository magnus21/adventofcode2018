package adventofcode.v2021

import adventofcode.util.AdventOfCodeUtil.Point
import adventofcode.util.AdventOfCodeUtil.manhattanDistance
import adventofcode.util.FileParser
import adventofcode.util.Queue
import kotlin.math.abs
import kotlin.system.measureTimeMillis

object Day23 {

    @JvmStatic
    fun main(args: Array<String>) {

        val time1 = measureTimeMillis {
            val (hallwayLength, amphipods) = parseInput(FileParser.getFileRows(2021, "23.txt"))
            val leastEnergyPaths = mutableListOf<State>()
            findPathWithLowestRisk(hallwayLength, amphipods, leastEnergyPaths, 2)

            val answer = leastEnergyPaths.minByOrNull { it.cost }!!.cost
            println("answer part 1: $answer")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val (hallwayLength, amphipods) = parseInput(FileParser.getFileRows(2021, "23.2.txt"))
            val leastEnergyPaths = mutableListOf<State>()
            findPathWithLowestRisk(hallwayLength, amphipods, leastEnergyPaths, 4)

            val answer = leastEnergyPaths.minByOrNull { it.cost }!!.cost
            println("answer part 2: $answer")
        }
        println("Time: $time2 ms")

    }

    private fun findPathWithLowestRisk(
        hallwayLength: Int,
        startAmphipods: List<Amphipod>,
        endStates: MutableList<State>,
        depth: Int
    ) {
        val queue = Queue<State>()
        queue.enqueue(State(startAmphipods, 0, emptyList()))

        var minCost = Int.MAX_VALUE
        while (queue.isNotEmpty()) {
            val (amphipods, cost, moveHistory) = queue.dequeue()!!
            if (amphiodsInCorrectRooms(amphipods, depth)) {
                val costOfEndState = amphipods.sumOf { it.cost }
                endStates.add(State(amphipods, costOfEndState, moveHistory))
                minCost = costOfEndState
            } else {
                val positionsForAmphipods = amphipods.associateBy { it.position }

                val possibleMovesMap =
                    amphipods.associateWith { a ->
                        getPossibleMoves(a, hallwayLength, positionsForAmphipods, depth).sortedBy { it.third }
                    }

                val firstToNestMove =
                    possibleMovesMap.map { (amph, moves) -> Pair(amph, moves.filter { it.second }) }
                        .firstOrNull { it.second.isNotEmpty() }

                if (firstToNestMove?.second?.isNotEmpty() == true) {
                    val amphToMove = firstToNestMove.first
                    val costOfMove = firstToNestMove.second.first().third * amphipodMap[amphToMove.name]!!.second

                    val newAmph = Amphipod(
                        amphToMove.name,
                        firstToNestMove.second.first().first,
                        amphToMove.cost + costOfMove
                    )
                    val newState = amphipods.minus(amphToMove).plus(newAmph)

                    if (cost + amphToMove.cost < minCost) {
                        queue.enqueue(
                            State(
                                newState,
                                cost + costOfMove,
                                moveHistory.plus(Move(newAmph.name, amphToMove.position, newAmph.position, costOfMove))
                            )
                        )
                    }
                } else {
                    possibleMovesMap.forEach { (amphipod, moves) ->
                        moves.forEach { move ->
                            val costOfMove = move.third * amphipodMap[amphipod.name]!!.second
                            val movedAmph = Amphipod(amphipod.name, move.first, amphipod.cost + costOfMove)

                            val amphs = amphipods
                                .filter { it != amphipod }
                                .map { Amphipod(it.name, it.position, it.cost) }
                                .plus(movedAmph)

                            val costOfNewState = amphs.sumOf { it.cost }
                            if (costOfNewState < minCost) {
                                queue.enqueue(
                                    State(
                                        amphs,
                                        costOfNewState,
                                        moveHistory.plus(
                                            Move(movedAmph.name, amphipod.position, movedAmph.position, costOfMove)
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getPossibleMoves(
        amphipod: Amphipod,
        hallwayLength: Int,
        amphipodPositions: Map<Point, Amphipod>,
        depth: Int
    ): List<Triple<Point, Boolean, Int>> {
        // Don't move if already in nest (with same amphipods below).
        val nestXPos = amphipodMap[amphipod.name]!!.first
        if (nestXPos == amphipod.position.x) {
            if ((amphipod.position.y..depth).all { amphipodPositions[Point(nestXPos, it)]!!.name == amphipod.name }) {
                return emptyList()
            }
        }

        val nestPath = getPathToNest(amphipod, amphipodPositions, depth)
        if (nestPath != null) {
            return listOf(nestPath)
        }

        // Moves into hallway
        val availablePositions = mutableListOf<Triple<Point, Boolean, Int>>()
        val xPos = amphipod.position.x
        val yPos = amphipod.position.y
        //
        if (yPos == 1 || (yPos > 1 && wayOutOpen(amphipod, amphipodPositions, xPos))) {
            var xl = xPos - 1
            while (!amphipodPositions.containsKey(Point(xl, 0)) && xl >= 0) {
                if (xl != 2 && xl != 4 && xl != 6 && xl != 8) {
                    availablePositions.add(
                        Triple(Point(xl, 0), false, manhattanDistance(amphipod.position, Point(xl, 0)))
                    )
                }
                xl--
            }

            var xr = xPos + 1
            while (!amphipodPositions.containsKey(Point(xr, 0)) && xr < hallwayLength) {
                if (xr != 2 && xr != 4 && xr != 6 && xr != 8) {
                    availablePositions.add(
                        Triple(Point(xr, 0), false, manhattanDistance(amphipod.position, Point(xr, 0)))
                    )
                }
                xr++
            }
        }

        return availablePositions
    }

    private fun getPathToNest(
        amphipod: Amphipod,
        positions: Map<Point, Amphipod>,
        depth: Int
    ): Triple<Point, Boolean, Int>? {
        val nestX = amphipodMap[amphipod.name]!!.first
        val corridorOpen = if (nestX > amphipod.position.x)
            (amphipod.position.x + 1 until nestX).none { positions.containsKey(Point(it, 0)) }
        else
            (nestX + 1 until amphipod.position.x).none { positions.containsKey(Point(it, 0)) }

        val amphX = amphipod.position.x
        val amphY = amphipod.position.y
        val nestOpen = nestOpen(amphipod, positions, nestX, depth)

        if (corridorOpen && nestOpen && (amphY < 2 || wayOutOpen(amphipod, positions, amphX))) {
            val nestY = getPosInNest(positions, nestX, depth)
            return Triple(Point(nestX, nestY), true, abs(amphipod.position.x - nestX) + amphY + nestY)
        }
        return null
    }

    private fun wayOutOpen(amphipod: Amphipod, positions: Map<Point, Amphipod>, x: Int) =
        (1 until amphipod.position.y).none { positions.containsKey(Point(x, it)) }

    private fun getPosInNest(positions: Map<Point, Amphipod>, x: Int, depth: Int) =
        (1..depth).last { !positions.containsKey(Point(x, it)) }

    private fun nestOpen(amphipod: Amphipod, positions: Map<Point, Amphipod>, x: Int, depth: Int) =
        (1..depth).all { positions[Point(x, it)] == null || positions[Point(x, it)]!!.name == amphipod.name }

    private fun amphiodsInCorrectRooms(amphipods: List<Amphipod>, depth: Int): Boolean {
        return amphipods.all { amphipodMap[it.name]!!.first == it.position.x && it.position.y in 1..depth }
    }

    private val amphipodMap = mapOf(
        Pair('A', Pair(2, 1)),
        Pair('B', Pair(4, 10)),
        Pair('C', Pair(6, 100)),
        Pair('D', Pair(8, 1000))
    )

    private fun parseInput(rows: List<String>): Pair<Int, List<Amphipod>> {
        val hallwayLength = rows.first().length - 2

        val amphipods = rows.drop(2).take(rows.size - 3).flatMapIndexed { i, row ->
            row.filter { it.isLetter() }.mapIndexed { j, it -> Amphipod(it, Point(2 + j * 2, i + 1)) }
        }

        return Pair(hallwayLength, amphipods)
    }

    data class Amphipod(val name: Char, val position: Point, var cost: Int = 0)
    data class Move(val name: Char, val from: Point, val to: Point, val cost: Int)
    data class State(val amphipods: List<Amphipod>, val cost: Int, val moves: List<Move>)
}