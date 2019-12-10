package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.v2019.shared.IntCodeComputer
import kotlin.system.measureTimeMillis

object Day9 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getCommaSeparatedValuesAsList(2019, "9.txt").map { it.toLong() }

        // Run program.
        val time1 = measureTimeMillis {
            val result = IntCodeComputer(input.toMutableList()).runWithInput(listOf(1))
            println(result)
        }
        println("Time part 1: ($time1 milliseconds)")

        val time2 = measureTimeMillis {
            val result = IntCodeComputer(input.toMutableList()).runWithInput(listOf(2))
            println(result)
        }
        println("Time part 2: ($time2 milliseconds)")
    }
}