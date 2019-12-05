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
            IntCodeComputer.runWithInput(input.toMutableList(), 5)
        }
        println("Time: ($time milliseconds)")
    }
}