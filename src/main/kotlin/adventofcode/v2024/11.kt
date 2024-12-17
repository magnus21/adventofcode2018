package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.math.pow
import kotlin.time.ExperimentalTime

object Day11 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val input = parseInput(FileParser.getFileRows(2024, "11.txt"))

        printResult("part 1") { solve(input, 25) }
        printResult("part 2") { solve(input, 75) }
    }

    private fun solve(input: List<Long>, iterations: Int): Long {
        var numbersMap = input.groupBy { it }.map { Pair(it.key, it.value.size.toLong()) }.toMap()
        (1..iterations).forEach { c ->
            numbersMap = numbersMap.entries.flatMap { e ->
                when {
                    e.key == 0L -> {
                        listOf(Pair(1L, e.value))
                    }

                    e.key.toString().length % 2 == 0 -> {
                        val splitFactor = (10.0.pow(e.key.toString().length.toDouble() / 2)).toLong()
                        listOf(Pair(e.key / splitFactor, e.value), Pair(e.key % splitFactor, e.value))
                    }

                    else -> listOf(Pair(e.key * 2024L, e.value))
                }
            }.groupBy { it.first }.map { Pair(it.key, it.value.sumOf { n -> n.second }) }.toMap()
        }

        return numbersMap.map { e -> e.value }.sum()
    }

    private fun parseInput(input: List<String>) =
        input[0].split(" ").map { it.toLong() }
}