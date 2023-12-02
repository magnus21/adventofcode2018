package adventofcode.v2023

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day1 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val inputs = FileParser.getFileRows(2023, "1.txt")

        printResult("part 1") { getCalibrationValueSum1(inputs) }
        printResult("part 2") { getCalibrationValueSum2(inputs) }
    }

    private fun getCalibrationValueSum1(inputs: List<String>): Int {
        return inputs.sumOf {
            it.first(Char::isDigit).digitToInt() * 10 + it.last(Char::isDigit).digitToInt()
        }
    }

    private fun getCalibrationValueSum2(inputs: List<String>): Int {
        return inputs.sumOf { input ->
            val digitIndexes = stringDigits
                .mapIndexed { i, str -> Pair(i + 1, input.indexOf(str)) }
                .filter { it.second != -1 }
                .toMutableList()

            digitIndexes.addAll(
                stringDigits
                    .mapIndexed { i, str -> Pair(i + 1, input.lastIndexOf(str)) }
                    .filter { it.second != -1 }
                    .toMutableList())

            val indexFirstDigit = input.indexOfFirst(Char::isDigit)
            if (indexFirstDigit != -1) {
                digitIndexes.add(Pair(input[indexFirstDigit].digitToInt(), indexFirstDigit));
            }

            val indexLastDigit = input.indexOfLast(Char::isDigit)
            if (indexLastDigit != -1) {
                digitIndexes.add(Pair(input[indexLastDigit].digitToInt(), indexLastDigit));
            }

            val sortedByIndex = digitIndexes.sortedBy { it.second }

            sortedByIndex.first().first * 10 + sortedByIndex.last().first
        }
    }

    private val stringDigits = setOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
}