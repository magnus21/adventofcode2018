package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.Point
import adventofcode.util.AdventOfCodeUtil.getPerpendicularNeighbours2d
import adventofcode.util.AdventOfCodeUtil.manhattanDistance2D
import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import adventofcode.util.Queue
import kotlin.time.ExperimentalTime

object Day20 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {

        val map = parseMap(FileParser.getFileRows(2024, "20.txt"))

        printResult("part 1") { solve(map) }
        printResult("part 2") { solve(map, true) }
    }

    private fun solve(map: Map<Point, Char>, part2: Boolean = false): Int {
        val start = map.filter { it.value == 'S' }.map { it.key }.first()
        val end = map.filter { it.value == 'E' }.map { it.key }.first()
        val originalPath = findOriginalPath(map, start, end)
        val cheats = findCheats(map, originalPath!!, part2)
        return cheats.filter { it.third >= 100 }.size
    }


    private fun findCheats(
        map: Map<Point, Char>,
        originalPath: List<Point>,
        part2: Boolean
    ): List<Triple<Point, Point, Int>> {
        return originalPath.dropLast(1).flatMap { findCheatsForPosition(it, map, originalPath, part2) }
    }

    private fun findCheatsForPosition(
        pos: Point,
        map: Map<Point, Char>,
        originalPath: List<Point>,
        part2: Boolean
    ): List<Triple<Point, Point, Int>> {
        if (part2) {
            return map
                .asSequence()
                .filter { it.value != '#' }
                .filter { it.key != pos }
                .filter { manhattanDistance2D(pos, it.key) <= 20 }
                .map { Triple(pos, it.key, getSave(originalPath, pos, it.key, manhattanDistance2D(pos, it.key))) }
                .filter { it.third > 0 }
                .toList()
        } else {
            return getPerpendicularNeighbours2d(pos)
                .map { Pair(it, getNextCheatPoint(pos, it)) }
                .filter { map[it.first] == '#' && (map[it.second] == '.' || map[it.second] == 'E') }
                .map { Triple(it.first, it.second, getSave(originalPath, pos, it.second,2)) }
                .filter { it.third > 0 }
        }
    }

    private fun getSave(originalPath: List<Point>, from: Point, to: Point, cheatLength: Int) =
        originalPath.indexOf(to) - originalPath.indexOf(from) - cheatLength

    private fun getNextCheatPoint(point: Point, neighbour: Point) =
        Point(neighbour.x + neighbour.x - point.x, neighbour.y + neighbour.y - point.y)

    private fun findOriginalPath(map: Map<Point, Char>, start: Point, end: Point): List<Point>? {
        val queue = Queue<CPUPath>()
        queue.enqueue(CPUPath(listOf(start)))

        while (queue.isNotEmpty()) {
            val path = queue.dequeue()!!
            if (path.steps.last() == end) {
                return path.steps
            } else {
                explorePath(map, path, queue)
            }
        }
        return null
    }

    private fun explorePath(
        map: Map<Point, Char>,
        path: CPUPath,
        queue: Queue<CPUPath>
    ) {
        val pos = path.steps.last()
        getPerpendicularNeighbours2d(pos)
            .filter { (map[it] == '.' || map[it] == 'E') && !path.steps.contains(it) }
            .map { CPUPath(path.steps.plus(it)) }
            .forEach(queue::enqueue)

        queue.sortQueue(compareBy { it.steps.size })
    }

    data class CPUPath(
        val steps: List<Point> = emptyList()
    )

    private fun getPath(map: Map<Point, Char>, path: List<Point>, end: Point): List<Point> {
        if (path.last() == end) {
            return path
        }

        val pos = path.last()
        return getPerpendicularNeighbours2d(pos.x, pos.y)
            .filter {
                val point = Point(it.first, it.second)
                map[point] != '.' && !path.contains(point)
            }.flatMap {
                getPath(map, path.plus(Point(it.first, it.second)), end)
            }
    }

    private fun parseMap(input: List<String>) =
        input.flatMapIndexed { y, row -> row.toCharArray().mapIndexed { x, c -> Pair(Point(x, y), c) } }.toMap()
}
