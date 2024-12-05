package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day3 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2024, "3.txt")

        printResult("part 1") { part1(input) }
        printResult("part 2") { part2(input) }
    }

    private fun part1(input: List<String>): Int {
        val inputString = input.joinToString { it }
        return findMulsAndCalcSum(inputString)
    }

    private fun part2(input: List<String>): Int {
        val inputString = input.joinToString { it }.fold(Triple("", listOf<Char>(), false)) { acc, c ->
            val accStr = acc.first.plus(c)
            val disabled = if (accStr.takeLast(7) == "don't()") true
            else if (accStr.takeLast(4) == "do()") false
            else acc.third

            val filtered = if (acc.third) acc.second else acc.second.plus(c)

            Triple(acc.first.plus(c), filtered, disabled)
        }.second.joinToString("")

        return findMulsAndCalcSum(inputString)
    }

    private fun findMulsAndCalcSum(inputString: String): Int {
        val operations = "mul\\((\\d{1,3}),(\\d{1,3})\\)".toRegex().findAll(inputString).map {
            Pair(Integer.parseInt(it.groups[1]!!.value), Integer.parseInt(it.groups[2]!!.value))
        }
        return operations.sumOf { it.first * it.second }
    }


}