package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day3 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2022, "3.txt")

        printResult("part 1") {
            input
                .map { it.chunked(it.length / 2) }
                .sumOf { getPriority(it[0].toSet().intersect(it[1].toSet()).first()) }
        }

        printResult("part 2") {
            input
                .map { it.toSet() }
                .chunked(3)
                .sumOf { getPriority(it[0].intersect(it[1]).intersect(it[2]).first()) }
        }
    }

    private fun getPriority(item: Char) =
        if (item.isLowerCase()) item.code - 'a'.code + 1 else item.code - 'A'.code + 27
}