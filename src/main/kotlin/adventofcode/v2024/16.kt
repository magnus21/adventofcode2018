package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.Point
import adventofcode.util.AdventOfCodeUtil.getPerpendicularNeighbours2d
import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import adventofcode.util.Queue
import kotlin.time.ExperimentalTime

object Day16 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {

        val map = parseMap(FileParser.getFileRows(2024, "16.txt"))
        val start = map.filter { it.value == 'S' }.map { it.key }.first()
        val end = map.filter { it.value == 'E' }.map { it.key }.first()

        printResult("part 1") { solve1(map, start, end) }
        printResult("part 2") { solve2(map, start, end) }
    }


    private fun solve1(map: Map<Point, Char>, start: Point, end: Point): Int {
        return findBestPaths(map, start, end).first().getScore()
    }

    private fun solve2(map: Map<Point, Char>, start: Point, end: Point): Int {
        return findBestPaths(map, start, end, true).flatMap { it.moves }.toSet().size
    }

    private fun findBestPaths(
        map: Map<Point, Char>,
        start: Point,
        end: Point,
        findAllBestPaths: Boolean = false
    ): List<ReindeerPath> {

        val queue = Queue<ReindeerPath>()
        queue.enqueue(ReindeerPath(listOf(start), 0, Point(1, 0)))

        val visitedPositions = mutableMapOf<Point, Pair<Int, Point>>()
        val bestPaths = mutableListOf<ReindeerPath>()
        while (queue.isNotEmpty()) {
            val path = queue.dequeue()!!
            if (path.moves.last() == end) {
                if (findAllBestPaths) {
                    bestPaths.add(path)
                } else {
                    return listOf(path)
                }
            } else {
                explorePath(visitedPositions, path, map, queue)
            }
        }

        val minScore = bestPaths.minOf { it.getScore() }
        return bestPaths.filter { it.getScore() == minScore }
    }

    private fun explorePath(
        visitedPositions: MutableMap<Point, Pair<Int, Point>>,
        path: ReindeerPath,
        map: Map<Point, Char>,
        queue: Queue<ReindeerPath>
    ) {
        val pos = path.moves.last()
        val possibleSteps = getPerpendicularNeighbours2d(pos)
            .filter { map[it] != '#' }
            .filter { !path.moves.contains(it) }

        possibleSteps.forEach { step ->
            val direction = Point(step.x - pos.x, step.y - pos.y)
            val newPath = ReindeerPath(
                path.moves.plus(step),
                if (direction != path.direction) path.turnCount + 1 else path.turnCount,
                direction
            )

            val visitedPos = visitedPositions[step]
            if (visitedPos == null || direction != visitedPos.second || newPath.getScore() <= visitedPos.first) {
                queue.enqueue(newPath)
                visitedPositions[step] = Pair(newPath.getScore(), direction)
            }
        }
        queue.sortQueue(compareBy { it.getScore() })
    }

    data class ReindeerPath(
        var moves: List<Point>,
        var turnCount: Int,
        var direction: Point
    ) {
        fun getScore() = moves.size - 1 + 1000 * turnCount
    }

    private fun parseMap(input: List<String>) =
        input.flatMapIndexed { y, row -> row.toCharArray().mapIndexed { x, c -> Pair(Point(x, y), c) } }.toMap()
}