package adventofcode.v2019

import adventofcode.util.FileParser
import kotlin.math.abs
import kotlin.system.measureTimeMillis

object Day3 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2019, "3.txt").map { s -> s.split(",") }

        val time = measureTimeMillis {
            val grid1 = getGridCoordinates(input[0])
            val grid2 = getGridCoordinates(input[1])

            val intersections = grid1.intersect(grid2)

            // Part 1
            println(intersections.map { abs(it.x) + abs(it.y) }.minOrNull())

            // Part 2
            println(intersections
                .map { i -> getSteps(grid1, i)!!.steps + getSteps(grid2, i)!!.steps }
                .minOrNull())
        }
        println("Time: ($time milliseconds)")
    }

    private fun getSteps(grid: Set<CoordinateSteps>, coord: CoordinateSteps) =
        grid.find { it.x == coord.x && it.y == coord.y }

    private fun getGridCoordinates(list: List<String>): Set<CoordinateSteps> {
        val coordinates = mutableSetOf<CoordinateSteps>()
        list.forEach { addCoords(coordinates, it) }
        return coordinates
    }

    private fun addCoords(coordinates: MutableSet<CoordinateSteps>, directionSteps: String) {
        val direction = when (directionSteps[0]) {
            'R' -> Pair(1, 0)
            'L' -> Pair(-1, 0)
            'U' -> Pair(0, -1)
            else -> Pair(0, 1)
        }
        val steps = directionSteps.substring(1).toInt()
        val startCoordinate = if (coordinates.isEmpty()) CoordinateSteps(0, 0, 0) else coordinates.last()
        for (step in 1..steps) {
            coordinates.add(
                CoordinateSteps(
                    startCoordinate.x + step * direction.first,
                    startCoordinate.y + step * direction.second,
                    coordinates.size + 1
                )
            )
        }
    }

    private data class CoordinateSteps(val x: Int, val y: Int, val steps: Int) {
        override fun equals(other: Any?): Boolean =
            if (other is CoordinateSteps) x == other.x && y == other.y else false

        override fun hashCode(): Int {
            var result = x
            result = 31 * result + y
            return result
        }
    }
}
