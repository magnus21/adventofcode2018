package adventofcode.v2021

import adventofcode.util.AdventOfCodeUtil.getPerpendicularNeighbours2d
import adventofcode.util.FileParser
import adventofcode.util.Queue
import kotlin.system.measureTimeMillis

object Day9 {

    @JvmStatic
    fun main(args: Array<String>) {
        val caveLocations = parseInput(FileParser.getFileRows(2021, "9.txt"))
        val lowPoints = caveLocations
            .filter { (pos, cavePos) ->
                getPerpendicularNeighbours2d(pos.first, pos.second)
                    .all { caveLocations.getOrDefault(it, CavePosition(-1, -1, 10)).height > cavePos.height }
            }

        val time1 = measureTimeMillis {
            val lowPointRiskLevelsSum = lowPoints
                .map { it.value.height + 1 }
                .sum()

            println("answer part 1: $lowPointRiskLevelsSum")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val basins = lowPoints.values.map { lowPoint ->
                lowPoint.explored = true
                val queue = Queue<CavePosition>()
                queue.enqueue(lowPoint)

                val basinLocation = mutableSetOf<CavePosition>()
                while (queue.isNotEmpty()) {
                    val caveLocation = queue.dequeue()!!
                    basinLocation.add(caveLocation)
                    addNewNeighboursInBasin(caveLocation, caveLocations, queue)
                }
                basinLocation.size
            }

            val answer = basins.sorted().takeLast(3).reduce { acc, i -> acc * i }

            println("answer part 2: $answer")
        }
        println("Time: $time2 ms")
    }

    private fun addNewNeighboursInBasin(
        cavePosition: CavePosition,
        caveLocations: Map<Pair<Int, Int>, CavePosition>,
        queue: Queue<CavePosition>,
    ) {
        val unexploredNeighbours = getPerpendicularNeighbours2d(cavePosition.x, cavePosition.y)
            .map { caveLocations.getOrDefault(it, CavePosition(-1, -1, 9)) }
            .filter { !it.explored && it.height != 9 }

        unexploredNeighbours.forEach {
            it.explored = true
            queue.enqueue(it)
        }
    }

    private fun parseInput(rows: List<String>): Map<Pair<Int, Int>, CavePosition> {
        var y = -1
        return rows.flatMap { row ->
            var x = 0
            y++
            row.toList().map { Pair(Pair(x, y), CavePosition(x++, y, it.digitToInt())) }
        }.toMap()
    }

    data class CavePosition(val x: Int, val y: Int, val height: Int, var explored: Boolean = false)
}