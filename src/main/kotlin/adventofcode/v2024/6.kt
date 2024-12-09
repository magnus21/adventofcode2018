package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.Point
import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day6 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val (map, startPosition) = parseInput(FileParser.getFileRows(2024, "6.txt"))

        printResult("part 1") { part1(map, startPosition) }
        printResult("part 2") { part2(map, startPosition) }
    }

    private fun part1(map: Map<Point, Char>, startPosition: Point): Int {
        return simulateGuardMovement(startPosition, map).map { it.first }.distinct().count()
    }

    private fun part2(map: Map<Point, Char>, startPosition: Point): Int {
        val guardMovement = simulateGuardMovement(startPosition, map).map { it.first }.distinct().minus(startPosition)
        return guardMovement.count { position ->
            simulateGuardMovement(startPosition, map, position).isEmpty()
        }
    }

    private fun simulateGuardMovement(
        startPosition: Point,
        map: Map<Point, Char>,
        extraObstacle: Point = Point(-100, -100)
    ): Set<Pair<Point, Point>> {
        var position = startPosition
        var direction = Point(0, -1)
        val visitedPositions = mutableSetOf(position to direction)
        while (true) {

            while (getPositionValue(extraObstacle, nextPosition(position, direction), map) == '#') {
                direction = rotate(direction)
            }
            position = nextPosition(position, direction)
            getPositionValue(extraObstacle, position, map) ?: break

            if (visitedPositions.contains(Pair(position, direction))) {
                return emptySet()
            }

            visitedPositions.add(Pair(position, direction))
        }

        return visitedPositions
    }

    private fun getPositionValue(
        extraObstacle: Point,
        position: Point,
        map: Map<Point, Char>
    ) = if (extraObstacle == position) '#' else map[position]

    private fun rotate(direction: Point) = Point(-direction.y, direction.x)

    private fun nextPosition(position: Point, direction: Point) =
        Point(position.x + direction.x, position.y + direction.y)

    private fun parseInput(input: List<String>): Pair<Map<Point, Char>, Point> {
        val map =
            input.flatMapIndexed { y, row -> row.toCharArray().mapIndexed { x, c -> Pair(Point(x, y), c) } }.toMap()
        return Pair(map, map.filter { it.value == '^' }.map { it.key }.first())
    }
}