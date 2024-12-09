package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.Point
import adventofcode.util.AdventOfCodeUtil.getPerpendicularNeighbours2d
import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day10 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val map = parseInput(FileParser.getFileRows(2024, "10.txt"))

        printResult("part 1") { part1(map) }
        printResult("part 2") { part2(map) }
    }

    private fun part1(map: Map<Point, Int>): Int {
        return map.filter { it.value == 0 }.map { Pair(it.key, it.value) }
            .sumOf { getTrailHeadPaths(it, map).distinct().size }
    }

    private fun part2(map: Map<Point, Int>): Int {
        return map.filter { it.value == 0 }.map { Pair(it.key, it.value) }
            .sumOf { getTrailHeadPaths(it, map).size }
    }

    private fun getTrailHeadPaths(currentPosition: Pair<Point, Int>, map: Map<Point, Int>): List<Pair<Point, Int>> {
        if (currentPosition.second == 9) {
            return listOf(currentPosition)
        }

        return getPerpendicularNeighbours2d(currentPosition.first.x, currentPosition.first.y)
            .filter {
                val neighbour = map[Point(it.first, it.second)]
                neighbour != null && neighbour == currentPosition.second + 1
            }.flatMap {
                getTrailHeadPaths(Pair(Point(it.first, it.second), currentPosition.second + 1), map)
            }
    }

    private fun parseInput(input: List<String>) =
        input.flatMapIndexed { y, row -> row.mapIndexed { x, n -> Pair(Point(x, y), n.toString().toInt()) } }.toMap()
}