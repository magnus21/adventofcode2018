package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.Point
import adventofcode.util.AdventOfCodeUtil.getPerpendicularNeighbours2d
import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import adventofcode.util.Queue
import kotlin.time.ExperimentalTime

object Day18 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {

        val bytePositions = parseInput(FileParser.getFileRows(2024, "18.txt"))

        printResult("part 1") { solve(bytePositions, 71) }
        printResult("part 2") { solve2(bytePositions, 71) }
    }

    private fun solve(bytePositions: List<Point>, size: Int): Int {
        return (findBestPaths(bytePositions.toSet(), Point(0, 0), Point(size - 1, size - 1), size)?.steps?.size
            ?: 0) - 1
    }

    private fun solve2(bytePositions: List<Point>, size: Int): String {
        var i = 1024
        while (true) {
            println("$i")
            findBestPaths(bytePositions.take(i).toSet(), Point(0, 0), Point(size - 1, size - 1), size)
                ?: return bytePositions[i - 1].toString()
            i++
        }
    }

    private fun findBestPaths(
        bytePositions: Set<Point>,
        start: Point,
        end: Point,
        size: Int
    ): MemoryPath? {
        val map = (0 until size).flatMap { y ->
            (0 until size).map { x -> Pair(Point(x, y), if (bytePositions.contains(Point(x, y))) '#' else '.') }
        }.toMap()

        //printPointsMap(map, boundariesParam = Boundaries(0, 0, size-1, size-1))

        val queue = Queue<MemoryPath>()
        queue.enqueue(MemoryPath(listOf(start)))

        val visitedPositions = mutableMapOf<Point, Int>()
        //val bestPaths = mutableListOf<MemoryPath>()
        while (queue.isNotEmpty()) {
            val path = queue.dequeue()!!
            if (path.steps.last() == end) {
                //if (findAllBestPaths) {
                //    bestPaths.add(path)
                //} else {
                return path
                //}
            } else {
                explorePath(visitedPositions, path, map, queue)
            }
        }
        return null
    }

    private fun explorePath(
        visitedPositions: MutableMap<Point, Int>,
        path: MemoryPath,
        map: Map<Point, Char>,
        queue: Queue<MemoryPath>
    ) {
        val pos = path.steps.last()
        val possibleSteps = getPerpendicularNeighbours2d(pos)
            .filter { map[it] != null && map[it] != '#' }
            .filter { !path.steps.contains(it) }

        possibleSteps.forEach { step ->
            //val direction = Point(step.x - pos.x, step.y - pos.y)
            val newPath = MemoryPath(
                path.steps.plus(step),
                //if (direction != path.direction) path.turnCount + 1 else path.turnCount,
                //direction
            )

            val visitedPosition = visitedPositions[step]
            if (visitedPosition == null || newPath.steps.size < visitedPosition) {
                queue.enqueue(newPath)
                visitedPositions[step] = newPath.steps.size
            }
        }
        queue.sortQueue(compareBy { it.steps.size })
    }

    data class MemoryPath(
        var steps: List<Point>,
        var direction: Point = Point(1, 0)
    )

    private fun parseInput(input: List<String>): List<Point> {
        return input.map {
            val coordinates = it.split(",")
            Point(coordinates[0].toInt(), coordinates[1].toInt())
        }
    }

}