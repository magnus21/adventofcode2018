package adventofcode.v2015

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day3 {

    @JvmStatic
    fun main(args: Array<String>) {

        val path = FileParser.getFileRows(2015, "3.txt").first()
        val time1 = measureTimeMillis {
            val positions = getPositions(path)

            println("Part 1: ${positions.distinct().size}")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val positionsSanta = getPositions(path.filterIndexed { i, _ -> i % 2 == 0 })
            val positionsRoboSanta = getPositions(path.filterIndexed { i, _ -> i % 2 != 0 })

            println("Part 2: ${positionsSanta.plus(positionsRoboSanta).distinct().size}")
        }
        println("Time: $time2 ms")
    }

    private fun getPositions(path: String): List<Pair<Int, Int>> {
        return path.fold(listOf(Pair(0, 0))) { positions, c ->
            val prevPos = positions.last()
            val pos = when (c) {
                '^' -> Pair(prevPos.first, prevPos.second - 1)
                'v' -> Pair(prevPos.first, prevPos.second + 1)
                '>' -> Pair(prevPos.first + 1, prevPos.second)
                else -> Pair(prevPos.first - 1, prevPos.second)
            }
            positions.plus(pos)
        }
    }
}