package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.v2019.shared.IntCodeComputer
import kotlin.system.measureTimeMillis

object Day17 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getCommaSeparatedValuesAsList(2019, "17.txt").map(String::toLong)

        // Run program.
        val time1 = measureTimeMillis {
            val result = IntCodeComputer(input.toMutableList()).runWithInput(listOf())
                .first.map(Long::toChar)

            var y = 0
            var x = 0
            val map = mutableMapOf<Position, Char>()
            result.forEach {
                if (it.toInt() == 10) {
                    x = 0
                    y++
                } else {
                    map[Position(x++, y)] = it
                }
            }

            val alignmentParametersSum =
                map.entries.filter { isInterSection(it.key, map) }.map { it.key.x * it.key.y }.sum()

            println("Answer part 1: $alignmentParametersSum")

            printMap(map)
        }
        println("Time part 1: ($time1 milliseconds)")

        val time2 = measureTimeMillis {

        }
        println("Time part 2: ($time2 milliseconds)")
    }

    private fun isInterSection(position: Position, map: MutableMap<Position, Char>): Boolean {
        return map[Position(position.x - 1, position.y)] == '#' &&
                map[Position(position.x + 1, position.y)] == '#' &&
                map[Position(position.x, position.y - 1)] == '#' &&
                map[Position(position.x, position.y + 1)] == '#'
    }

    private fun printMap(map: MutableMap<Position, Char>) {

        val xSpan = Pair(map.keys.map { it.x }.min()!!, map.keys.map { it.x }.max()!!)
        val ySpan = Pair(map.keys.map { it.y }.min()!!, map.keys.map { it.y }.max()!!)

        for (y in ySpan.first..ySpan.second) {
            for (x in xSpan.first..xSpan.second) {
                val tile = map[Position(x, y)]
                print(tile!!)
            }
            println()
        }
    }

    private data class Position(var x: Int, var y: Int) {
        override fun toString(): String {
            return "[$x, $y]"
        }
    }

}