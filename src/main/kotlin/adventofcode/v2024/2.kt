package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.math.abs
import kotlin.time.ExperimentalTime

object Day2 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2024, "2.txt")
        val reports = input.map { it.split(" ").toList().map { Integer.parseInt(it) } }

        printResult("part 1") { reports.count { isSafe(it) } }
        printResult("part 2") { reports.count { isSafe(it) || isSafe2(it) } }
    }

    private fun isSafe(report: List<Int>): Boolean {
        var previous = report[0]
        val diffs = mutableListOf<Int>()
        for (number in report.drop(1)) {
            diffs.add(previous - number)
            previous = number
        }

        return (diffs.all { it > 0 } || diffs.all { it < 0 }) && diffs.map { abs(it) }.all { it in listOf(1, 2, 3) }
    }

    private fun isSafe2(report: List<Int>): Boolean {
        return getCombos(report).any { isSafe(it) }
    }

    private fun getCombos(report: List<Int>): List<List<Int>> {
        return report.indices
            .map { i -> report.filterIndexed { j, _ -> i != j } }
    }
}