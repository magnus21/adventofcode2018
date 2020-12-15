package adventofcode.v2020

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day15 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getCommaSeparatedValuesAsList(2020, "15.txt").map { it.toInt() }

        val time = measureTimeMillis {
            println("Part 1: ${getNumber(input, 2020)}")
            println("Part 2: ${getNumber(input, 30000000)}")
        }
        println("Time: ($time milliseconds)")
    }

    private fun getNumber(input: List<Int>, stopCount: Int): Int {
        val memory = input.mapIndexed { i, v -> Pair(v, i) }.toMap().toMutableMap()
        var count = memory.size
        var prevNumber = 0

        while (count < stopCount - 1) {
            val tmpPrev = prevNumber
            prevNumber = if (memory.containsKey(prevNumber)) count - memory[prevNumber]!! else 0
            memory[tmpPrev] = count
            count++
        }

        return prevNumber
    }

}