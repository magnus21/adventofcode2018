package adventofcode.v2020

import adventofcode.util.AdventOfCodeUtil.generatePairs
import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day9 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2020, "9.txt").map { it.toLong() }

        // Run program.
        val time = measureTimeMillis {

            val part1Answer = part1(input)

            println("Part 1: $part1Answer")
            println("Part 2: ${part2(input, part1Answer)}")
        }
        println("Time: ($time milliseconds)")
    }

    private fun part1(input: List<Long>): Long {
        val preambleSize = 25
        for (start in input.indices) {
            val sums =
                generatePairs(input.drop(start).take(preambleSize)).map { it.first + it.second }.toSet()
            if (!sums.contains(input[start + preambleSize])) {
                return input[start + preambleSize]
            }
        }
        return -1
    }

    private fun part2(input: List<Long>, part1Answer: Long): Long {
        for (start in input.indices) {
            var n = 2
            while (true) {
                val range = input.drop(start).take(n++)
                val sum = range.sum()

                if (sum == part1Answer) {
                    return range.min()!! + range.max()!!
                } else if (sum > part1Answer || start + n > input.size) {
                    break
                }
            }
        }
        return -1
    }
}