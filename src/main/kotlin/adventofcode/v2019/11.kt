package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.v2019.Day11.Color.*
import adventofcode.v2019.Day11.Direction.*
import adventofcode.v2019.shared.IntCodeComputer
import kotlin.system.measureTimeMillis

object Day11 {

    private val directions = listOf(LEFT, UP, RIGHT, DOWN)

    @JvmStatic
    fun main(args: Array<String>) {

        val inputProgram = FileParser.getCommaSeparatedValuesAsList(2019, "11.txt").map { it.toLong() }

        // Part 1
        val time1 = measureTimeMillis {
            val hull = mutableMapOf<Coordinate, Color>()
            runProgram(inputProgram.toMutableList(), 0L, hull)
            println(hull.size)
        }
        println("Time part 1: ($time1 milliseconds)")

        // Part 2
        val time2 = measureTimeMillis {
            val hull = mutableMapOf<Coordinate, Color>()
            runProgram(inputProgram.toMutableList(), 1L, hull)
            printField(hull)
        }
        println("Time part 2: ($time2 milliseconds)")

    }

    private fun runProgram(
        inputProgram: List<Long>,
        startInput: Long,
        hull: MutableMap<Coordinate, Color>
    ) {
        var input = startInput
        val robot = Robot(Coordinate(0, 0), UP)
        val program = inputProgram.toMutableList()

        val intCodeComputer = IntCodeComputer(program.toMutableList())

        while (true) {
            val result = intCodeComputer.runWithInput(listOf(input))
            if (result.second == IntCodeComputer.DONE) {
                break
            }

            val resultForThisRound = result.first.takeLast(2)

            paintHull(hull, robot, Color.from(resultForThisRound[0]))
            updateDirection(robot, resultForThisRound[1])
            moveRobot(robot)

            input = getNextInput(hull, robot.position)
        }
    }

    private fun paintHull(
        hull: MutableMap<Coordinate, Color>,
        robot: Robot,
        color: Color
    ) {
        hull[Coordinate(robot.position.x, robot.position.y)] = color
    }

    private fun updateDirection(robot: Robot, directionCode: Long) {
        val index = directions.indexOf(robot.direction) + (if (directionCode == 0L) -1 else 1)
        val newIndex = if (index < 0) directions.size - 1 else if (index >= directions.size) 0 else index
        robot.direction = directions[newIndex]
    }

    private fun moveRobot(robot: Robot) {
        robot.position = Coordinate(robot.position.x + robot.direction.x, robot.position.y + robot.direction.y)
    }

    private fun getNextInput(hull: MutableMap<Coordinate, Color>, position: Coordinate): Long {
        return hull.getOrDefault(position, BLACK).colorCode.toLong();
    }

    private fun printField(hull: MutableMap<Coordinate, Color>) {
        val xSpan = Pair(hull.entries.map { it.key.x }.min()!!, hull.entries.map { it.key.x }.max()!!)
        val ySpan = Pair(hull.entries.map { it.key.y }.min()!!, hull.entries.map { it.key.y }.max()!!)
        for (y in ySpan.first..ySpan.second) {
            for (x in xSpan.first..xSpan.second) {
                val color = hull[Coordinate(x, y)]
                // White background -> invert colors.
                print(if (color != null && color == WHITE) "#" else " ")
            }
            println()
        }
    }

    private data class Coordinate(val x: Int, val y: Int)
    private enum class Direction(val x: Int, val y: Int) {
        LEFT(-1, 0), RIGHT(1, 0), UP(0, -1), DOWN(0, 1)
    }

    private enum class Color(val colorCode: Int) {
        BLACK(0), WHITE(1);

        companion object {
            fun from(colorCode: Long) = if (colorCode.toInt() == BLACK.colorCode) BLACK else WHITE
        }
    }

    private data class Robot(var position: Coordinate, var direction: Direction)
}