package adventofcode.v2021

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day3 {

    @JvmStatic
    fun main(args: Array<String>) {

        val diagnostics = FileParser.getFileRows(2021, "3.txt")
            .map { it.toCharArray().map { c -> c.code - 48 } }

        val time1 = measureTimeMillis {
            var gammaRate = ""
            var epsilonRate = ""
            (0 until diagnostics.first().size).forEach { i ->
                val mostCommonValue = diagnostics.map { it[i] }.groupBy { it }.maxByOrNull { it.value.size }!!.key
                gammaRate += mostCommonValue
                epsilonRate += if (mostCommonValue == 1) 0 else 1
            }
            println("answer part 1: ${gammaRate.toInt(2) * epsilonRate.toInt(2)}")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val oxygenRating = getFilteredNumbers(0, diagnostics, 1)
                .first().joinToString("").toInt(2)
            val co2Rating = getFilteredNumbers(0, diagnostics, 0)
                .first().joinToString("").toInt(2)

            val lifeSupportRating = oxygenRating * co2Rating
            println("answer part 2: $lifeSupportRating")
        }
        println("Time: $time2 ms")
    }

    private fun getFilteredNumbers(
        index: Int,
        diagnostics: List<List<Int>>,
        tiePick: Int
    ): List<List<Int>> {
        val groupedValues = diagnostics.map { it[index] }.groupBy { it }
        val filterValue = when {
            groupedValues[0]!!.size == groupedValues[1]!!.size -> tiePick
            tiePick == 1 -> groupedValues.maxByOrNull { it.value.size }!!.key
            else -> groupedValues.minByOrNull { it.value.size }!!.key
        }

        val filtered = diagnostics.filter { it[index] == filterValue }

        if (filtered.size == 1) {
            return filtered
        }
        return getFilteredNumbers(index + 1, filtered, tiePick)
    }
}