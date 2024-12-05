package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day21 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2022, "18.txt")

        printResult("part 1") { part1(input) }
        printResult("part 2") { part2(input) }
    }

    private fun part1(input: List<String>): Long? {
        return null
    }

    private fun part2(input: List<String>): Long? {

        return null
    }
}