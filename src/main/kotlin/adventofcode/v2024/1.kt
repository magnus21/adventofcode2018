package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.math.abs
import kotlin.time.ExperimentalTime

object Day1 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val inputs = FileParser.getFileRows(2024, "1.txt")

        val inputLists = inputs.map {
            val parts = it.split("   ")
            Pair(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]))
        }
        val leftList = inputLists.map { it.first }.sorted()
        val rightList = inputLists.map { it.second }.sorted()

        printResult("part 1") { part1(leftList, rightList) }
        printResult("part 2") { part2(leftList, rightList) }
    }

    private fun part1(leftList: List<Int>, rightList: List<Int>): Int {
        return leftList.mapIndexed { i, number -> abs(number - rightList[i]) }.sum()
    }

    private fun part2(leftList: List<Int>, rightList: List<Int>): Int {
        val countMap = rightList.groupBy { it }.map { e -> Pair(e.key, e.value.size) }.toMap()
        return leftList.sumOf { number -> number * countMap.getOrDefault(number, 0) }
    }
}