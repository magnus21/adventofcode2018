package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.v2019.shared.IntCodeComputer
import kotlin.system.measureTimeMillis

object Day5 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getCommaSeparatedValuesAsList(2019, "5.txt").map { Integer.valueOf(it) }

        // Run program.
        val time = measureTimeMillis {
            val result1 = IntCodeComputer(input.toMutableList()).runWithInput(listOf(1))
            println(result1.first.takeLast(1))

            val result2 = IntCodeComputer(input.toMutableList()).runWithInput(listOf(5))
            println(result2.first.takeLast(1))
        }
        println("Time: ($time milliseconds)")
    }
}