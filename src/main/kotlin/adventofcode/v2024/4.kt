package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.Point
import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day4 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val data = parseInput(FileParser.getFileRows(2024, "4.txt"))

        printResult("part 1") { part1(data) }
        printResult("part 2") { part2(data) }
    }

    private fun parseInput(fileRows: List<String>): Map<Point, Char> {
        return fileRows.flatMapIndexed { i, row ->
            row.toCharArray().mapIndexed { j, c -> Pair(Point(j, i), c) }
        }.toMap()
    }

    private fun part1(data: Map<Point, Char>): Int {
        val directions = getDirections()
        val wordCount = data.entries
            .filter { e -> e.value == 'X' }
            .flatMap { (point, _) -> directions.mapNotNull { findWord(it, point, data) } }
            .count()

        return wordCount
    }

    private fun part2(data: Map<Point, Char>): Int {
        val directions = getDirections()
        val wordCount = data.entries
            .filter { e -> e.value == 'A' }
            .count { (point, _) -> findXPattern(point, data, directions) }

        return wordCount
    }

    private fun findXPattern(p: Point, data: Map<Point, Char>, directions: Set<Pair<Int, Int>>): Boolean {
        return getXPairs().all {
            val valueOne = data[Point(p.x + it.first.first, p.y + it.first.second)]
            val valueTwo = data[Point(p.x + it.second.first, p.y + it.second.second)]

            setOf(valueOne, valueTwo) == setOf('M', 'S')
        }
    }

    private fun findWord(d: Pair<Int, Int>, p: Point, data: Map<Point, Char>): String? {
        val word = "XMAS"
        val result = word.foldIndexed("") { i, acc, c ->
            acc.plus(data[Point(p.x + d.first * i, p.y + d.second * i)] ?: " ")
        }
        return if (result == word) result else null
    }

    private fun getDirections(): Set<Pair<Int, Int>> {
        return setOf(-1, 0, 1).flatMap { yy ->
            setOf(-1, 0, 1).map { xx ->
                if (yy == 0 && xx == 0) null else Pair(xx, yy)
            }
        }.filterNotNull().toSet()
    }

    private fun getXPairs(): List<Pair<Pair<Int, Int>, Pair<Int, Int>>> {
        return listOf(
            Pair(Pair(-1, -1), Pair(1, 1)),
            Pair(Pair(1, -1), Pair(-1, 1)),
        )
    }
}