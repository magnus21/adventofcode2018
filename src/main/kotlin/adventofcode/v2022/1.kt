package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day1 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val inputs = FileParser.getFileRows(2022, "1.txt")

        printResult("part 1") { getCalorieSums(inputs).maxByOrNull { it } }
        printResult("part 2") { getCalorieSums(inputs).sortedByDescending { it }.take(3).sum() }
    }

    private fun getCalorieSums(inputs: List<String>): List<Int> {
        return inputs.fold(Pair(0, listOf<Int>())) { acc, calorie ->
            when {
                calorie.isEmpty() -> Pair(0, acc.second.plus(acc.first))
                else -> Pair(acc.first + calorie.toInt(), acc.second)
            }
        }.second
    }
}