package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day6 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getAsString(2022, "6.txt")

        printResult("part 1") { getMarkerStartIndex(input, 4) }
        printResult("part 2") { getMarkerStartIndex(input, 14) }
    }

    private fun getMarkerStartIndex(input: String, distinctCharsLength: Int): Int? {
        (0 until input.length - distinctCharsLength).forEach {
            if (input.substring(it, it + distinctCharsLength).toSet().size == distinctCharsLength) {
                return it + distinctCharsLength
            }
        }
        return null
    }
}