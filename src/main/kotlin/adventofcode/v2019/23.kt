package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.v2019.shared.IntCodeComputer
import kotlin.system.measureTimeMillis

object Day23 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getCommaSeparatedValuesAsList(2019, "23.txt").map(String::toLong)

        // Run program.
        val time1 = measureTimeMillis {
            val result = IntCodeComputer(input.toMutableList()).runWithInput(listOf())

            result.first.map { it.toChar() }.forEach(::print)
            println()

            println("Answer part 1: ${result.first.last()}")
        }
        println("Time part 1: ($time1 milliseconds)")

        val time2 = measureTimeMillis {
            val result = IntCodeComputer(input.toMutableList()).runWithInput(listOf())

            result.first.map { it.toChar() }.forEach(::print)
            println()

            println("Answer part 2: ${result.first.last()}")
        }
        println("Time part 2: ($time2 milliseconds)")
    }

}