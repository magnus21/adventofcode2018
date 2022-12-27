package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil.Boundaries3D
import adventofcode.util.AdventOfCodeUtil.Point
import adventofcode.util.AdventOfCodeUtil.get3DBoundaries
import adventofcode.util.AdventOfCodeUtil.getOneManhattanDistNeighbours3d
import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day18 {

    private val lavaPoints = mutableSetOf<Point>()
    private val pocketAir = mutableSetOf<Point>()
    private val openAir = mutableSetOf<Point>()

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val points = FileParser.getFileRows(2022, "18.txt")
            .map { row ->
                val parts = row.split(",").map { it.toInt() }
                Point(parts[0], parts[1], parts[2])
            }.toSet()

        printResult("part 1") { part1(points) }
        printResult("part 2") { part2(points) }
    }

    private fun part1(points: Set<Point>): Int {

        val cubeSides = 6
        return points.sumOf {
            val neighbors = getOneManhattanDistNeighbours3d(it.x, it.y, it.z)
            cubeSides - neighbors.intersect(points).size
        }
    }

    private fun part2(points: Set<Point>): Int {

        val bounds = get3DBoundaries(points)
        lavaPoints.addAll(points)

        for (x in bounds.xmin..bounds.xmax) {
            for (y in bounds.ymin..bounds.ymax) {
                for (z in bounds.zmin..bounds.zmax) {
                    val p = Point(x, y, z)
                    if (!lavaPoints.contains(p) && !pocketAir.contains(p) && !openAir.contains(p)) {
                        val explored = mutableSetOf(p)
                        if (connectedToOpenAir(p, explored, bounds)) {
                            openAir.addAll(explored)
                        } else {
                            pocketAir.addAll(explored)
                        }
                    }
                }
            }
        }

        val openSides = part1(points)
        val airPocketsOpenSides = part1(pocketAir)

        return openSides - airPocketsOpenSides
    }

    private fun connectedToOpenAir(
        point: Point,
        explored: MutableSet<Point>,
        bounds: Boundaries3D
    ): Boolean {
        val neighbours = getOneManhattanDistNeighbours3d(point.x, point.y, point.z)
            .filter { !lavaPoints.contains(it) }
            .filter { !explored.contains(it) }

        neighbours.forEach(explored::add)

        if (neighbours.any { pocketAir.contains(it) }) {
            return false
        }
        if (neighbours.any { openAir.contains(it) || outOfBounds(it, bounds) }) {
            return true
        }

        return neighbours.any { connectedToOpenAir(it, explored, bounds) }
    }

    private fun outOfBounds(point: Point, bounds: Boundaries3D): Boolean {
        return point.x <= bounds.xmin || point.x >= bounds.xmax ||
                point.y <= bounds.ymin || point.y >= bounds.ymax ||
                point.z <= bounds.zmin || point.z >= bounds.zmax
    }
}