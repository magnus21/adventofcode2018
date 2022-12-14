package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil.Boundaries
import adventofcode.util.AdventOfCodeUtil.Point
import adventofcode.util.AdventOfCodeUtil.getBoundaries
import adventofcode.util.AdventOfCodeUtil.isWithInBoundaries
import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day14 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val rockPoints = parseMap(FileParser.getFileRows(2022, "14.txt"))
        val boundaries = getBoundaries(rockPoints.map { Pair(it.x, it.y) }.plus(Pair(500, 0)))

        printResult("part 1") { part1(rockPoints, boundaries) }
        printResult("part 2") { part2(rockPoints, boundaries) }
    }

    private fun part1(rockPoints: Set<Point>, boundaries: Boundaries): Int {
        return dropSandAndGetCount(rockPoints, boundaries)
    }

    private fun part2(rockPoints: Set<Point>, boundaries: Boundaries): Int {
        val bottom = boundaries.ymax + 2
        boundaries.ymax = bottom
        boundaries.xmin = Int.MIN_VALUE
        boundaries.xmax = Int.MAX_VALUE

        return dropSandAndGetCount(rockPoints, boundaries, bottom)
    }

    private fun dropSandAndGetCount(rockPoints: Set<Point>, boundaries: Boundaries, bottom: Int? = null): Int {
        val map = rockPoints.associate { Pair(Point(it.x, it.y), it.z) }.toMutableMap()
        do {
            val restPoint = dropOneSand(map, boundaries, bottom)
            if (restPoint != null) {
                map[restPoint] = 0
            }
            //printPointsMap(map.map { Pair(Pair(it.key.x, it.key.y), if (it.value == 1) '#' else 'o') }.associate { it })
            //println("================================")
        } while (restPoint != null)

        return map.values.count { it == 0 }
    }

    private fun dropOneSand(map: Map<Point, Int>, boundaries: Boundaries, bottom: Int? = null): Point? {
        var sand = Point(500, 0)
        if (map[sand] != null) {
            return null
        }

        while (isWithInBoundaries(boundaries, sand)) {
            val center = Point(sand.x, sand.y + 1)
            val left = Point(sand.x - 1, sand.y + 1)
            val right = Point(sand.x + 1, sand.y + 1)

            sand = when {
                bottom != null && sand.y + 1 == bottom -> return sand
                map[center] == null -> center
                map[left] == null -> left
                map[right] == null -> right
                else -> return sand
            }
        }
        return null
    }

    private fun parseMap(rows: List<String>): Set<Point> {
        val rockPaths = rows.map { row ->
            row.split(" -> ").map {
                val point = it.split(",")
                Point(point[0].toInt(), point[1].toInt())
            }
        }

        return rockPaths.flatMap { row ->
            row.drop(1).fold(Pair<Point, Set<Point>>(row[0], setOf())) { acc, p ->
                when (acc.first.x) {
                    p.x -> {
                        val range = if (acc.first.y < p.y) acc.first.y..p.y else p.y..acc.first.y
                        Pair(p, acc.second.plus(range.map { Point(p.x, it, 1) }))
                    }
                    else -> {
                        val range = if (acc.first.x < p.x) acc.first.x..p.x else p.x..acc.first.x
                        Pair(p, acc.second.plus(range.map { Point(it, p.y, 1) }))
                    }
                }
            }.second
        }.toSet()
    }
}