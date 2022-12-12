package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil.Point
import adventofcode.util.AdventOfCodeUtil.getPerpendicularNeighbours2d
import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import adventofcode.util.Queue
import kotlin.time.ExperimentalTime

object Day12 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val (start, end, heightMap) = parseInput(FileParser.getFileRows(2022, "12.txt"))

        printResult("part 1") { part1(start, end, heightMap) }
        printResult("part 2") { part2(end, heightMap) }
    }

    private fun part1(start: Point, end: Point, heightMap: Map<Point, Int>): Int {
        return findShortestPath(start, end, heightMap)!!.trail.size - 1
    }

    private fun part2(end: Point, heightMap: Map<Point, Int>): Int? {
        return heightMap
            .filter { it.value == 'a'.code }
            .filter { it.key.x == 0 } // Input map only has a's with corresponding b's in first column.
            .map { findShortestPath(it.key, end, heightMap)!!.trail.size - 1 }
            .minOrNull()!!
    }


    private fun findShortestPath(startPosition: Point, endPosition: Point, heightMap: Map<Point, Int>): Path? {
        val startPath = Path(mutableListOf(startPosition))

        val queue = Queue<Path>()
        queue.enqueue(startPath)

        val shortestPathAtPosition = mutableMapOf(Pair(startPosition, 1))

        val maxValue = Integer.MAX_VALUE
        while (queue.isNotEmpty()) {
            val trail = queue.dequeue()!!.trail
            val position = trail.last()
            val positionHeight = heightMap[position]!!

            val newPaths = getPerpendicularNeighbours2d(position.x, position.y)
                .map { Point(it.first, it.second) }
                .filter {
                    val height = heightMap[it]
                    height != null && height <= positionHeight + 1
                }
                .filter { trail.size + 1 < shortestPathAtPosition.getOrDefault(it, maxValue) }
                .map { Path(trail.plus(it)) }

            val end = newPaths.find { it.trail.last() == endPosition }
            if (end != null) {
                return end
            }

            newPaths.forEach {
                shortestPathAtPosition[it.trail.last()] = it.trail.size
                queue.enqueue(it)
            }
        }
        return null
    }

    private fun parseInput(rows: List<String>): Triple<Point, Point, Map<Point, Int>> {
        var start: Point? = null
        var end: Point? = null
        val heightMap = rows.flatMapIndexed { y, row ->
            row.mapIndexed { x, c ->
                when (c) {
                    'S' -> start = Point(x, y)
                    'E' -> end = Point(x, y)
                }
                val height = if (c == 'S') 'a' else if (c == 'E') 'z' else c
                Point(x, y, height.code)
            }
        }.associate { Pair(Point(it.x, it.y), it.z) }

        return Triple(start!!, end!!, heightMap)
    }

    data class Path(val trail: List<Point> = listOf())
}