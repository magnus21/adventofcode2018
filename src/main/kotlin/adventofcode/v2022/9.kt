package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil
import adventofcode.util.AdventOfCodeUtil.Point
import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day9 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2022, "9.txt")

        val steps = parseInput(input)

        printResult("part 1") {
            part1(steps)
        }
        printResult("part 2") {
            part2(steps)
        }
    }

    private fun part1(steps: List<Point>): Int {

        var head = Point(0, 0)
        var tail = Point(0, 0)
        val visited = mutableSetOf(tail)

        steps.forEach { move ->
            (1..move.z).forEach { _ ->
                head = Point(head.x + move.x, head.y + move.y)
                val distance = AdventOfCodeUtil.manhattanDistance(head, tail)
                if (distance > 1 && (head.x == tail.x || head.y == tail.y)) {
                    tail = moveTail(head, tail)
                } else if (distance >= 3) {
                    tail = moveTailDiagonally(head, tail)
                }
                visited.add(tail)
            }
        }
        return visited.count()
    }

    private fun part2(steps: List<Point>): Int {

        var knots = (0..9).map { Point(0, 0) }
        val visited = mutableSetOf(Point(0, 0))

        steps.forEach { move ->
            (1..move.z).forEach { _ ->
                val newKnots = mutableListOf<Point>()
                knots.forEachIndexed { i, knot ->
                    if (i == 0) {
                        newKnots.add(Point(knot.x + move.x, knot.y + move.y))
                    } else {
                        val head = newKnots[i - 1]
                        val distance = AdventOfCodeUtil.manhattanDistance(head, knot)
                        if (distance > 1 && (head.x == knot.x || head.y == knot.y)) {
                            newKnots.add(moveTail(head, knot))
                        } else if (distance >= 3) {
                            newKnots.add(moveTailDiagonally(head, knot))
                        } else {
                            newKnots.add(knot)
                        }
                        if (i == knots.size - 1) {
                            visited.add(newKnots.last())
                        }
                    }
                }
                knots = newKnots
            }
        }
        return visited.count()
    }

    private fun moveTailDiagonally(head: Point, tail: Point): Point {
        val xd = head.x - tail.x
        val yd = head.y - tail.y
        return Point(tail.x + xd / kotlin.math.abs(xd), tail.y + yd / kotlin.math.abs(yd))
    }

    private fun moveTail(head: Point, tail: Point): Point {
        val xd = head.x - tail.x
        val yd = head.y - tail.y

        return when (Pair(xd, yd)) {
            Pair(2, 0) -> Point(tail.x + 1, tail.y)
            Pair(-2, 0) -> Point(tail.x - 1, tail.y)
            Pair(0, 2) -> Point(tail.x, tail.y + 1)
            else -> Point(tail.x, tail.y - 1)
        }
    }

    private fun parseInput(input: List<String>) =
        input.map {
            val parts = it.split(" ")
            val (x, y) = when (parts[0]) {
                "R" -> Pair(1, 0)
                "L" -> Pair(-1, 0)
                "U" -> Pair(0, 1)
                else -> Pair(0, -1)
            }
            Point(x, y, parts[1].toInt())
        }
}