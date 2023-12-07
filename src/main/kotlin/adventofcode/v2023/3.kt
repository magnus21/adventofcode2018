package adventofcode.v2023

import adventofcode.util.AdventOfCodeUtil
import adventofcode.util.AdventOfCodeUtil.Point
import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day3 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val (numbers, schematic) = parseSchematic(FileParser.getFileRows(2023, "3.txt"))

        printResult("part 1") { part1(numbers, schematic) }
        printResult("part 2") { part2(numbers, schematic) }
    }

    private fun part1(numbers: List<Number>, schematic: Map<Point, Char>): Int {
        return numbers.filter { n ->
            val y = n.startPosition.y
            (n.startPosition.x until n.startPosition.x + n.value.toString().length).any { x ->
                val neighbours = AdventOfCodeUtil.getNeighbours2d(x, y)
                neighbours.any {
                    val c = schematic[Point(it.first, it.second)]
                    c != null && !c.isDigit() && c != '.'
                }
            }
        }.sumOf { it.value }
    }

    private fun part2(numbers: List<Number>, schematic: Map<Point, Char>): Int {
        val possibleGearsMap = mutableMapOf<Point, List<Number>>()
        numbers.forEach { n ->
            val y = n.startPosition.y
            val possibleGears = (n.startPosition.x until n.startPosition.x + n.value.toString().length).flatMap { x ->
                val neighbours = AdventOfCodeUtil.getNeighbours2d(x, y)
                neighbours.filter {
                    val c = schematic[Point(it.first, it.second)]
                    c != null && c == '*'
                }.map { Point(it.first, it.second) }
            }.toSet()

            possibleGears.forEach {
                if (!possibleGearsMap.containsKey(it)) {
                    possibleGearsMap[it] = listOf()
                }
                possibleGearsMap[it] = possibleGearsMap[it]!!.plus(n)
            }
        }

        return possibleGearsMap
            .filter { it.value.size == 2 }
            .map { e -> e.value.map { it.value }.reduce(Int::times) }
            .sum()
    }

    private fun parseSchematic(fileRows: List<String>): Pair<List<Number>, Map<Point, Char>> {
        val numbers = mutableListOf<Number>()
        val map = mutableMapOf<Point, Char>()
        fileRows.mapIndexed { i, row ->
            map.putAll(row.mapIndexed { j, c -> Pair(Point(j, i), c) })

            var startIndex = -1
            var isDigit = false
            row.forEachIndexed { j, c ->
                if (isDigit && !c.isDigit()) {
                    numbers.add(Number(Point(startIndex, i), row.substring(startIndex, j).toInt()))
                } else if (!isDigit && c.isDigit()) {
                    startIndex = j
                }
                isDigit = c.isDigit()
                if (isDigit && j == row.length - 1) {
                    numbers.add(Number(Point(startIndex, i), row.substring(startIndex, j + 1).toInt()))
                }
            }
        }

        return Pair(numbers, map)
    }

    data class Number(val startPosition: Point, val value: Int)

}