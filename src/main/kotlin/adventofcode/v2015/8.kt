package adventofcode.v2015

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day8 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2015, "8.txt")

        val time1 = measureTimeMillis {

            val answer = input.map { hayStraw -> Pair(hayStraw, findNrOfOccurrences(hayStraw, this::countPart1) + 2) }
            println("Part 1: ${answer.map { it.second }.sum()}")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {

            val answer = input.map { hayStraw -> Pair(hayStraw, findNrOfOccurrences(hayStraw, this::countPart2) + 4) }
            println("Part 2: ${answer.map { it.second }.sum()}")
        }
        println("Time: $time2 ms")
    }

    private fun findNrOfOccurrences(str: String, counter: (String, Int) -> Pair<Int, String>): Int {
        var count = 0
        var hayStraw = str
        while (true) {
            val index = hayStraw.indexOf("\\")
            if (index == -1) {
                return count
            }

            if (index + 1 < hayStraw.length - 1) {
                val pair = counter(hayStraw, index)
                count += pair.first
                hayStraw = pair.second
            }
        }
    }

    private fun countPart1(
        hayStraw: String,
        index: Int
    ): Pair<Int, String> {
        return when (hayStraw[index + 1]) {
            '\\' -> Pair(1, hayStraw.substring(index + 2))
            '\"' -> Pair(1, hayStraw.substring(index + 2))
            'x' -> Pair(3, hayStraw.substring(index + 4))
            else -> Pair(0, hayStraw.substring(index + 1))
        }
    }

    private fun countPart2(
        hayStraw: String,
        index: Int
    ): Pair<Int, String> {
        return when (hayStraw[index + 1]) {
            '\\' -> Pair(2, hayStraw.substring(index + 2))
            '\"' -> Pair(2, hayStraw.substring(index + 2))
            'x' -> Pair(1, hayStraw.substring(index + 4))
            else -> Pair(0, hayStraw.substring(index + 1))
        }
    }
}