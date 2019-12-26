package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.v2019.shared.IntCodeComputer
import kotlin.system.measureTimeMillis

object Day21 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getCommaSeparatedValuesAsList(2019, "21.txt").map(String::toLong)

        // Run program.
        val time1 = measureTimeMillis {
            val script = getScriptAsAscii(
                listOf(
                    "NOT A J", // Next 1 is not ground
                    "NOT C J", // Next 3 is not ground

                    "NOT B T",
                    "OR T J", // Next 2 is ground

                    "NOT A T", // Always jump if if next1 is not ground
                    "OR T J",

                    "AND D J", // Next 4 (landing spot ) is ground
                    "WALK"
                )
            )
            val result = IntCodeComputer(input.toMutableList()).runWithInput(script)

            result.first.map { it.toChar() }.forEach(::print)
            println()

            println("Answer part 1: ${result.first.last()}")
        }
        println("Time part 1: ($time1 milliseconds)")

        val time2 = measureTimeMillis {
            val script = getScriptAsAscii(
                listOf(
                    "NOT B T", // Next 2 or 3 is not ground
                    "NOT T T",
                    "AND C T",
                    "NOT T J",

                    "NOT E T", // Dont jump if if next 5,8 is not ground.
                    "NOT T T",
                    "OR H T",
                    "AND T J",

                    "NOT A T", // Always jump if if next 1 is not ground.
                    "OR T J",

                    "AND D J", // Next 4 (landing spot) is ground
                    "RUN"
                )
            )
            val result = IntCodeComputer(input.toMutableList()).runWithInput(script)

            result.first.map { it.toChar() }.forEach(::print)
            println()

            println("Answer part 2: ${result.first.last()}")
        }
        println("Time part 2: ($time2 milliseconds)")
    }

    private fun getScriptAsAscii(code: List<String>): List<Long> {
        return code.flatMap { it.toCharArray().map(Char::toLong).plusElement(10L) }
    }
}