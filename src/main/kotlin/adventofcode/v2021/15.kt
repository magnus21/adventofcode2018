package adventofcode.v2021

import adventofcode.util.AdventOfCodeUtil
import adventofcode.util.FileParser
import adventofcode.util.Queue
import kotlin.system.measureTimeMillis

object Day15 {

    @JvmStatic
    fun main(args: Array<String>) {

        val time1 = measureTimeMillis {
            val riskLevelMap = parseInput(FileParser.getFileRows(2021, "15.txt"))
            val lowestRiskPathSum = findPathWithLowestRisk(riskLevelMap)

            println("answer part 1: $lowestRiskPathSum")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val riskLevelMap = parseInput(FileParser.getFileRows(2021, "15.txt"), 5)
            val lowestRiskPathSum = findPathWithLowestRisk(riskLevelMap)

            println("answer part 2: $lowestRiskPathSum")
        }
        println("Time: $time2 ms")
    }

    private fun findPathWithLowestRisk(riskLevelMap: Map<Position, Int>): Int {
        val boundaries = AdventOfCodeUtil.getBoundaries(riskLevelMap.keys.map { Pair(it.x, it.y) }.toSet())

        val startPosition = Position(boundaries.xmin, boundaries.ymin)
        val endPosition = Position(boundaries.xmax, boundaries.ymax)

        val startPath = CavePath(startPosition)

        val queue = Queue<CavePath>()
        queue.enqueue(startPath)

        val lowestRiskAtPosition = mutableMapOf<Position, Int>()
        lowestRiskAtPosition[startPosition] = -1

        var lowestRiskPathSum = Integer.MAX_VALUE
        while (queue.isNotEmpty()) {
            val cavePath = queue.dequeue()!!

            val newPaths =
                AdventOfCodeUtil.getPerpendicularNeighbours2d(cavePath.lastPosition.x, cavePath.lastPosition.y)
                    .map { Position(it.first, it.second) }
                    .filter { riskLevelMap.contains(it) }
                    .filter {
                        val newPathRiskSoFar = cavePath.riskSoFar + riskLevelMap[it]!!
                        newPathRiskSoFar < lowestRiskAtPosition.getOrDefault(it, Integer.MAX_VALUE) &&
                                newPathRiskSoFar < lowestRiskPathSum
                    }.map { CavePath(it, cavePath.riskSoFar + riskLevelMap[it]!!) }

            newPaths.forEach { lowestRiskAtPosition[it.lastPosition] = it.riskSoFar }

            newPaths
                .filter { endPosition == it.lastPosition }
                .map { path -> path.riskSoFar }
                .filter { it < lowestRiskPathSum }
                .minOrNull()
                ?.also { lowestRiskPathSum = it }

            newPaths.filter { endPosition != it.lastPosition }.forEach { queue.enqueue(it) }

            // Important, otherwise # paths will explode :)
            queue.sortQueue(compareBy { it.riskSoFar })
        }

        return lowestRiskPathSum
    }

    private fun parseInput(rows: List<String>, tileFactor: Int = 1): Map<Position, Int> {
        val map = mutableMapOf<Position, Int>()
        val size = rows.size
        for (xx in 0 until tileFactor) {
            for (yy in 0 until tileFactor) {
                var y = -1
                map.putAll(rows.flatMap { row ->
                    var x = 0
                    y++
                    row.toList().map {
                        val risk = it.digitToInt() + (xx + yy)
                        val adjustedRisk = if (risk > 9) risk - 9 else risk
                        Pair(Position(xx * size + x++, yy * size + y), adjustedRisk)
                    }
                })
            }
        }
        return map
    }

    data class Position(val x: Int, val y: Int)
    data class CavePath(val lastPosition: Position, val riskSoFar: Int = 0)
}